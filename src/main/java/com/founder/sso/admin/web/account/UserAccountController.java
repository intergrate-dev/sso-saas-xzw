package com.founder.sso.admin.web.account;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.shiro.authc.credential.Md5CredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.founder.sso.admin.utils.CollectionsUtil;
import com.founder.sso.admin.utils.PageUtil;
import com.founder.sso.admin.utils.ServletsUtil;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.User;
import com.founder.sso.service.LocalPrincipalService;
import com.founder.sso.service.UserService;
import com.founder.sso.util.HttpClientUtil;
import com.founder.sso.util.SystemConfigHolder;
import com.google.common.collect.Maps;

@Controller
public class UserAccountController {
	
	private static Map<String, String> sortTypes = Maps.newLinkedHashMap();
	private static final String DEFAULT_PASSWORD=SystemConfigHolder.getConfig("default_password");
	//搜索条件的参数的开头前缀
	private static final String SEARCH_PREFIX = "search_";
	//重定向到获取用户列表的请求
	private static final String REDIRECT_LIST = "redirect:users";
	@Autowired
	private UserService userService;
	@Autowired
    private LocalPrincipalService principalService;
	
	static {
		sortTypes.put("id", "自动");
	}
	
	@RequestMapping("admin/users")
	public String getUserList(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
			@RequestParam(value = "pageSize", defaultValue = PageUtil.PAGE_SIZE) int pageSize,
			@RequestParam(value = "sortType", defaultValue = "id") String sortType, 
			@RequestParam(value = "find_GTE_registerDate",required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date find_GTE_registerDate,
			@RequestParam(value = "find_LTE_registerDate",required=false) @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss") Date find_LTE_registerDate,
			Model model,ServletRequest request) {
		
		Map<String, Object> searchParams = ServletsUtil.getParametersStartingWith(request, SEARCH_PREFIX);
		//手动添加注册的开始注册时间参数、结束注册时间参数，不然getParametersStartingWith不能将日历控件转换为Date
		if(find_GTE_registerDate != null){
			searchParams.put("GTE_registerDate", find_GTE_registerDate);
		}
		if(find_LTE_registerDate != null){
			searchParams.put("LTE_registerDate", find_LTE_registerDate);
		}

		Page<User> users = userService.getUsers(searchParams, pageNumber, pageSize, sortType);

		model.addAttribute("userList", users);
		model.addAttribute("sortType", sortType);
		model.addAttribute("beginSize", pageSize*(pageNumber-1)+1);
		model.addAttribute("sortTypes", sortTypes);
		model.addAttribute("pageSize", pageSize);
		// 将搜索条件编码成字符串，用于排序，分页的URL
		model.addAttribute("searchParams", ServletsUtil.encodeParameterStringWithPrefix(searchParams, SEARCH_PREFIX));
		return "admin/user/userList";
	}
	
	@RequestMapping("admin/deleteUser")
	public ModelAndView deleteUser(String userIds,ServletRequest request,RedirectAttributes attr){
		userService.deleteByIds(userIds);
		attr.addFlashAttribute("info", "success");
		//调用amuc同步接口
  		List<NameValuePair> params = new ArrayList<NameValuePair>();
  		params.add(new BasicNameValuePair("ssoid",userIds));
  		String res = new HttpClientUtil().callAmucAPI("/api/member/del.do", params);
		//重定向查询用户列表时带上查询的参数
		Map<String, String[]> searchParams = request.getParameterMap(); 
		return new ModelAndView(REDIRECT_LIST, searchParams);  
	}
	

    /**
     * 查看个人资料
     */
    @RequestMapping("admin/profile")
    public String viewProfile(long userId, Model model) {
        User user = userService.findUserById(userId);
        model.addAttribute("user", user);
        return "/admin/user/userProfile";
    }
	
	//TODO  zhangmc 这里非常实用实用路径参数 做Restful
	@RequestMapping("admin/initUpdateProfile")
	public String initUpdateUser(Long userId,Model model){
		User user = userService.findUserById(userId);
		model.addAttribute("user", user);
		return "admin/user/updateUser";
	}
	
	@RequestMapping("admin/updateProfile")
	public String updateProfile(User newUser, @DateTimeFormat(pattern="yyyy-MM-dd") Date birthdayPicker){
		User oldUser = userService.findUserById(newUser.getId());
		oldUser.setNickname(newUser.getNickname());
		userService.updateUser(oldUser);
		//同步principal
		principalService.savePrincipal(oldUser, null);
		return "redirect:/admin/profile?userId=" + oldUser.getId() ;
	}
	
	/**
	 * 冻结或恢复用户
	 * @param user
	 * @return
	 */
	@RequestMapping("admin/changeUserStatus")
	public ModelAndView changeUserStatus(boolean actived,String userIds,ServletRequest request,RedirectAttributes attr){
		userService.freezeOrRecoverUser(actived, userIds);
		attr.addFlashAttribute("info", "success");
		//重定向查询用户列表时带上查询的参数
		Map<String, String[]> searchParams = request.getParameterMap(); 
		return new ModelAndView(REDIRECT_LIST, searchParams);  
	}
	
	/**
	 * 重置密码
	 * @param user
	 * @return
	 */
	@RequestMapping("admin/resetPassword")
	public ModelAndView resetPassword(String userIds,ServletRequest request,RedirectAttributes attr){
	    List<Long> ids = CollectionsUtil.convetDotStringToList(userIds);
	    for (Long id : ids) {
	        LocalPrincipal principal = principalService.getByUserId(id);
	        principalService.resetPassword(principal, md5(DEFAULT_PASSWORD));
        }
	    attr.addFlashAttribute("info", "success");
		//重定向查询用户列表时带上查询的参数
		Map<String, String[]> searchParams = request.getParameterMap();
		return new ModelAndView(REDIRECT_LIST, searchParams);  
	}
	
	
    /**
     * 查看头像
     */
    @RequestMapping(value = "admin/face", method = RequestMethod.GET)
    public String face(Model model, String info, long userId) {
        
    	User user = userService.findUserById(userId);
    	userService.initViewFace(model, user, info);
    	
    	return "/admin/user/memberFace";
    }
    
    /**
     * 重置此用户的头像
     */
    @RequestMapping(value = "admin/resetFace")
    public String resetFace(long userId){
    	
    	User user = userService.findUserById(userId);
    	user.resetFace(userService.setDefaultImg());
    	userService.updateUser(user);
        return "redirect:/admin/face?userId=" + userId;
    }
	
    private String md5(String s){
    	try{
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		byte[] bytes = md.digest(s.getBytes("utf-8"));
    		char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    		StringBuilder ret = new StringBuilder(bytes.length * 2);
    		for (int i = 0; i < bytes.length; i++){
    			ret.append(HEX_DIGITS[(bytes[i] >> 4 & 0xF)]);
    			ret.append(HEX_DIGITS[(bytes[i] & 0xF)]);
    		}
    		return ret.toString();
    	}
    	catch (Exception e){
    		throw new RuntimeException(e);
    	}
    }
}
