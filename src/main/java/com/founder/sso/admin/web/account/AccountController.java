package com.founder.sso.admin.web.account;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.founder.sso.admin.entity.AdminAccount;
import com.founder.sso.admin.service.account.AccountService;
import com.founder.sso.admin.service.account.AdminDbRealm;

@Controller
@RequestMapping(value="/admin/account")
public class AccountController {

	private static Logger logger = LoggerFactory.getLogger(AccountController.class);
	
	//重定向到获取用户列表的请求
	private static final String REDIRECT_LIST = "redirect:/admin/account/";
    @Autowired
    private AccountService accountService;
    
    @RequestMapping(value="/",method=RequestMethod.GET)
    public String listAll(Model model) {
        List<AdminAccount> accountList = accountService.findAll();
        model.addAttribute("accountList", accountList);
        model.addAttribute("inlineAccount", getLoginInfo().getAccount());
        return "/admin/account/accountList";
    }

    /**
     * 得到当前用户
     * @return
     */
    public static AdminDbRealm.LoginInfo getLoginInfo(){
    	return (AdminDbRealm.LoginInfo)SecurityUtils.getSubject().getPrincipal();
    }
    
    /*
     * 冻结用户
     */
    @RequestMapping(value="{id}/disable")
    public String disableAccount(@PathVariable("id")Long id,RedirectAttributes attr) {
    	accountService.disableAdmin(id);
    	attr.addFlashAttribute("info", "success");
    	return REDIRECT_LIST;
    }
    
    /*
     * 恢复用户
     */
    @RequestMapping(value="{id}/enable")
    public String enableAccount(@PathVariable("id")Long id,RedirectAttributes attr) {
        accountService.enableAdmin(id);
		attr.addFlashAttribute("info", "success");
        return REDIRECT_LIST;
    }
    
    /*
     * 删除用户 
     */
    @RequestMapping(value="{id}/delete")
    public String deleteAccount(@PathVariable("id")Long id,RedirectAttributes attr) {
    	accountService.deleteAdmin(id);
		attr.addFlashAttribute("info", "success");
    	return REDIRECT_LIST;
    }
    
    /*
     * 重置密码 
     */
    @RequestMapping(value="{id}/resetPassword")
    public String resetPassword(@PathVariable("id")Long id,RedirectAttributes attr) {
    	accountService.resetPassword(id);
		attr.addFlashAttribute("info", "resetPasswordSuccess");
    	return "redirect:/admin/account/initUpdate?id="+id;
    }
    
    /*
     * 初始化创建或修改管理员 
     */
    @RequestMapping(value="initUpdate")
    public String initUpdate(Long id,Model model) {
    	//修改用户信息时
    	if(!StringUtils.isEmpty(id)){
    		AdminAccount account = accountService.findById(id);
    		model.addAttribute("account", account);
    	}
    	return "/admin/account/updateAccount";
    }
    
    /*
     * 创建或修改管理员 
     */
    @RequestMapping(value="updateAccount")
    public void updateAccount(AdminAccount account) {
    	accountService.updateAdmin(account);
    }
    
    /*
     * 初始化修改密码 
     */
    @RequestMapping(value="initUpdatePassword")
    public String initUpdatePassword() {
    	return "/admin/account/updatePassword";
    }
    /*
     * 修改密码 
     */
    @RequestMapping(value="updatePassword")
    public ResponseEntity<String> updatePassword(Long id,String oldPassword,String newPassword) {
    	JSONObject json = new JSONObject(); 
    	String result = accountService.updatePassword(id, oldPassword, newPassword);
    	try {
			json.put("result", result);
    	} catch (JSONException e) {
    		logger.warn("修改密码时，构建json对象出错" + e.toString());
    		e.printStackTrace();
    	} 
    	ResponseEntity<String> re = new ResponseEntity<String>(json.toString(),HttpStatus.OK); 
    	return re;
    }
    
    /*
     * 检查此用户名是否存在
     */
    @RequestMapping(value="isExistLoginName")
    public ResponseEntity<String> isExistLoginName(String loginname,Model model) {
    	AdminAccount account = accountService.findByLoginanme(loginname);
    	JSONObject json = new JSONObject(); 
    	try {
	    	if(account == null || account.getId() == null){
				json.put("result", "no");
	    	}else {
    			json.put("result", "yes");
	    	}
    	} catch (JSONException e) {
    		logger.warn("检查用户是否存在时，构建json对出错" + e.toString());
    		e.printStackTrace();
    	} 
    	ResponseEntity<String> re = new ResponseEntity<String>(json.toString(),HttpStatus.OK); 
    	return re;
    }
    
    
}
