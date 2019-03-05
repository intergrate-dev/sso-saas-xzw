package com.founder.sso.web;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.sso.util.HttpClientUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.SensitiveWord;
import com.founder.sso.entity.User;
import com.founder.sso.service.LocalPrincipalService;
import com.founder.sso.service.PrincipalException;
import com.founder.sso.service.SensitiveWordService;
import com.founder.sso.service.UserService;
import com.founder.sso.service.oauth.entity.OauthToken;
import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;
import com.founder.sso.util.sensitiveword.SensitivewordUtil;

/**
 * 公共API类 
 * @author zhangmc
 */
@Controller
public class APIController {

	@Autowired
	private UserService userService;
	@Autowired
	private LocalPrincipalService principalService;
	
	@Autowired
	private SensitiveWordService sensitivewordservice;
    
	/**
	 * 1、根据id得到用户信息，返回json对象
	 * @param id
	 * @return
	 * @throws Exception
	 */
	//@RequestMapping("/API/user/{id}/info")
	@RequestMapping("/api/user/info")
	public ResponseEntity<String> getUserInfo(@RequestParam("uid")Long uid,HttpServletResponse response, HttpServletRequest request) throws Exception{
		response.setHeader("Access-Control-Allow-Origin","*");
		JSONObject json = userService.findUserInfoById(uid, request);
		//加密处理
//		String jsonStr = DESCoder.encrypt(json.toString());
		ResponseEntity<String> re = new ResponseEntity<String>(json.toString(),HttpStatus.OK);
		return re;
	}
	
	/**
	 * 2、注册用户 
	 * @throws Exception
	 */
//	@RequestMapping("/API/user/new")
	/*public ResponseEntity<String> register(User user, String password){
		//TODO 需要将password进行解密
		JSONObject json = userService.registerUser(user, password);
		String jsonStr = json.toString();
		//加密处理
//		String jsonStr = DESCoder.encrypt(json.toString());
		ResponseEntity<String> re = new ResponseEntity<String>(jsonStr,HttpStatus.OK); 
		return re;
	}*/
	
	/**
	 * 3、更新资料、密码、头像、冻结用户
	 * @throws Exception
	 */
//	@RequestMapping("/API/user/update")
	/*public ResponseEntity<String> update(User user, String password){
		//TODO 需要将password进行解密
		JSONObject json = userService.updateUser(user, password);
		String jsonStr = json.toString();
		//加密处理
//		String jsonStr = DESCoder.encrypt(json.toString());
		ResponseEntity<String> re = new ResponseEntity<String>(jsonStr,HttpStatus.OK); 
		return re;
	}*/
	

	/**
	 * 4、登录认证
	 * 根据用户名/手机号/邮箱 + 密码 认证用户是否存在，
	 * 如果存在将返回用户信息、需要同步登录的子系统信息，用于子系统认证登录；
	 * 子系统拿到之后可以进行其他系统的同步登录和同步退出。
	 * @throws JSONException 
	 * @throws Exception
	 */
//	@RequestMapping(value = "/API/user/authen")
	public ResponseEntity<String> authenUser(String identity, String password, String systemCode, HttpServletRequest request) throws JSONException{

		JSONObject json  = userService.authenUser(identity, password, systemCode, request);

		String jsonStr = json.toString();
		//加密处理
//		String jsonStr = DESCoder.encrypt(json.toString());
		ResponseEntity<String> re = new ResponseEntity<String>(jsonStr,HttpStatus.OK); 
		return re;
	}
	
	/**
	 * 5、同步登录
	 * @throws JSONException 
	 * @throws Exception
	 */
	@SuppressWarnings("finally")
//	@RequestMapping(value = "/user/API/session/synchronizedLogin", method = RequestMethod.GET)
	public ResponseEntity<String> synchronizedLogin(String passport, ServletRequest request){

		Subject subject = SecurityUtils.getSubject();
		JSONObject json = new JSONObject();
		try {
			JSONObject object = new JSONObject(passport);
			Object o = object.get("id");
			if(o==null){
				json.put("ret", "1002");
				json.put("msg", "passport格式错误");
				
			}else {
				Long userId = Long.parseLong(o.toString());
				json  = userService.synchronizedLogin(userId, request, subject);
			}
		} catch (JSONException e) {
			json.put("ret", "1002");
			json.put("msg", "passport格式错误");
			e.printStackTrace();
		}catch (NumberFormatException e){
			json.put("ret", "1002");
			json.put("msg", "id不能转化成Long类型");
		}finally{
			
			String jsonStr = json.toString();
			//加密处理
//			String jsonStr = DESCoder.encrypt(json.toString());
			ResponseEntity<String> re = new ResponseEntity<String>(jsonStr,HttpStatus.OK); 
			return re;
		}

	}
	
	/**
	 * 6、同步退出
	 * @throws JSONException 
	 * @throws Exception
	 */
//	@RequestMapping(value = "/user/API/session/synchronizedLogout", method = RequestMethod.GET)
	public ResponseEntity<String> synchronizedLogout() throws JSONException{
		
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		JSONObject json  = new JSONObject();
		json.put("ret", "0");
		json.put("msg", "success");
		
		String jsonStr = json.toString();
		//加密处理
//		String jsonStr = DESCoder.encrypt(json.toString());
		ResponseEntity<String> re = new ResponseEntity<String>(jsonStr,HttpStatus.OK); 
		return re;
	}

	/**
	 * 7、
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/API/user/sensitiveword")
	public void sensitiveword() throws Exception{
		
		SensitivewordUtil filter = new SensitivewordUtil();
		Set<String> keyWordSet = sensitivewordservice.readSensitiveWordTable();
		filter.addSensitiveWordToHashMap(keyWordSet);
		
		
		String string = "法轮功太多的伤感情怀福音也许只局限于饲养基地福音会 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
				+ "然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
				+ "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
		
		System.out.println("待检测语句字数：" + string.length());
		long beginTime = System.currentTimeMillis();
		boolean lb = filter.isContaintSensitiveWord(string, 2);
		long endTime = System.currentTimeMillis();
		System.out.println("语句中包含敏感词的个数为：" + lb);
		System.out.println("总共消耗时间为：" + (endTime - beginTime));
		
		
	}
	
	/**
	 * 8、
	 * 修改密码
	 * @throws JSONException 
	 * @throws PrincipalException 
	 */
	@RequestMapping("/api/user/updatePassword")
	public ResponseEntity<String> updatePassword(@RequestParam(required=true) Long uid, 
	          @RequestParam(required=true) String password,
	          @RequestParam(required=true) String newPassword,HttpServletResponse response) throws JSONException, PrincipalException{
		response.setHeader("Access-Control-Allow-Origin","*");
		JSONObject json = new JSONObject();
		password = password.trim();
		newPassword = newPassword.trim();
		User user=userService.findUserById(uid);
		LocalPrincipal localPrincipal = user.getLocalPrincipal();
		if(password.equals(newPassword)){
			json.put("msg", "新旧密码相同！");
		}else if(user==null){
			json.put("msg", "根据ID找不到用户！");
		}else if(!user.isActived()){
			json.put("msg", "用户被禁用");
		}else if(localPrincipal==null){
			json.put("msg", "第三方账号不能更新密码");
		}else if(!principalService.isPasswordMatch(localPrincipal, password)){
			json.put("msg", "旧密码不正确");
		}else{
			principalService.updatePassword(localPrincipal, password, newPassword);
			json.put("msg", "success");
			
		}
		System.out.println(json.toString());
		return new ResponseEntity<String>(json.toString(),HttpStatus.OK);
	}
	
	/**
	 * 9、
	 * 修改密码
	 * @throws JSONException 
	 */
	@RequestMapping(value = "/api/user/getPortrait", method = RequestMethod.GET)
	@ResponseBody
	public String getPortrait(@RequestParam(value = "uid") Long uid,HttpServletResponse response)throws Exception {
		response.setHeader("Access-Control-Allow-Origin","*");
		JSONObject json = new JSONObject();
		User user=userService.findUserById(uid);
		if(user==null){
			json.put("msg", "根据ID找不到用户！");
		}else if(!user.isActived()){
			json.put("msg", "用户被禁用");
		}else if(user.getAvatarMiddle()==null||"".equals(user.getAvatarMiddle())){
			json.put("msg", "用户头像地址为空");
		}else{
			json.put("portrait", user.getAvatarMiddle());
			json.put("msg", "success");
		}
		return json.toString();
	}
	/**
	 * 会员修改昵称同步接口
	 * @param id 会员的sso标识
	 * @param nickname 会员的昵称
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/user/modify", method=RequestMethod.POST)
    public ResponseEntity<String> modify(@RequestParam(value="id",required=false)Long id,
    		HttpServletResponse response,
    		@RequestParam(value="nickname",required=false)String nickname) throws Exception{
    	response.setHeader("Access-Control-Allow-Origin","*");
    	User user=userService.findUserById(id);
    	user.setNickname(nickname);
    	userService.updateUser(user);
  		ResponseEntity<String> result = new ResponseEntity<String>("",HttpStatus.OK); 
  		return result;
    }

	/**
	 * 会员修改昵称同步接口
	 * @param id 会员的sso标识
	 * @param nickname 会员的昵称
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/api/user/uploadImage", method=RequestMethod.POST)
	public ResponseEntity<String> uploadImage(HttpServletRequest request,
										 HttpServletResponse response) throws Exception{
		/*List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("nickname",newUser.getNickname()));
		params.add(new BasicNameValuePair("mobile",oldUser.getPhone()));
		new HttpClientUtil().callAmucAPI("/api/member/syn/updateNickname.do", params);*/
		ResponseEntity<String> result = new ResponseEntity<String>("",HttpStatus.OK);
		return result;

		/*HttpPost httpPost = new HttpPost("");
		Map<String, String> apiCase = new HashedMap();
		new HttpClientUtil().sendFormByHttpPost(httpPost, apiCase);
		ResponseEntity<String> result = new ResponseEntity<String>("",HttpStatus.OK);
		return result;*/
	}
}
