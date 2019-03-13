package com.founder.sso.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

import com.founder.sso.service.oauth.entity.UserOauthBinding;
import com.founder.sso.util.securityCode.CacheUtil;
import com.restfb.json.JsonObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.shiro.session.Session;
import org.joda.time.DateTimeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.founder.sso.admin.entity.Subsystem;
import com.founder.sso.admin.service.SubsystemService;
import com.founder.sso.dao.SystemConfigDao;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.entity.UserSubsystemBind;
import com.founder.sso.service.LocalPrincipalService;
import com.founder.sso.service.PrincipalException;
import com.founder.sso.service.SensitiveWordService;
import com.founder.sso.service.UserService;
import com.founder.sso.service.UserSubsystemBindService;
import com.founder.sso.service.oauth.entity.SystemConfig;
import com.founder.sso.user.UserManager;
import com.founder.sso.util.Clock;
import com.founder.sso.util.HttpClientUtil;
import com.founder.sso.util.StringUtil;
import com.founder.sso.util.SystemConfigHolder;
import com.founder.sso.util.ValidateUtil;
import com.founder.sso.util.email.ActsocialMailSender;
import com.founder.sso.util.msg.ActsocialMsgSender;
import com.founder.sso.util.sensitiveword.SensitivewordUtil;

//import redis.clients.jedis.JedisCluster;
import com.founder.redis.JedisClient;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/*import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;*/
/**
 * 外网用户控制器</p>
 * 主要负责外网用户自身的信息维护工作及外网用户的注册工作
 *
 */
@Controller
@RequestMapping(value = "/user")
public class UserController extends BaseController{
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    
    @Autowired
    private LocalPrincipalService principalService;
    
	@Autowired
	private SubsystemService subsystemService;
    
	@Autowired
	private SensitiveWordService sensitivewordservice;
	
	@Autowired
	private UserSubsystemBindService userthirdbindservice;
	
	@Autowired
	private SystemConfigDao systemConfigDao;
	
	//@Autowired
	//private JedisCluster jedisCluster;
	
	@Autowired
	JedisClient jedisClient;

	@Autowired
	CacheUtil cacheUtil;
	
    /**
     * 进入个人主页
     */
    @RequestMapping(value = "myHome")
    public String myHome(Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			List<Subsystem> sysList = subsystemService.findByEnabledTrue();
			long userId = getCurrentUserId();

			for (Subsystem subsystem : sysList) {
                 List<UserSubsystemBind>  utblist = userthirdbindservice.findByUseridAndSubsystemid((int)userId, subsystem.getId().intValue());
                 if(utblist !=null && utblist.size() > 0){
                     subsystem.setUsername(utblist.get(0).getName());
                     subsystem.setUserthirdbindid(utblist.get(0).getId());
                 }
            }
			this.platFormConfig();
			model.addAttribute("subsystemList", sysList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "/user/myHome";
    }
    
    /**
     * 查看头像
     */
    @RequestMapping(value = "face", method = RequestMethod.GET)
    public String face(Model model, String info) {
        
    	User user = getCurrentUser();
    	userService.initViewFace(model, user, info);
    	//设置此字段，是为了把上传头像的功能扩展到admin模块
    	model.addAttribute("module", "user");
    	model.addAttribute("userId", user.getUid());
		model.addAttribute("appIfApiUrl", SystemConfigHolder.getConfig("app_if_api_url"));
		return "/user/myFace";
    }
    
    /**
     * 上传未经处理的头像
     * picType:图片的格式,包含"."   支持.jpg, .png, .gif, jpeg
     */
    @RequestMapping(value = "initCutFace")
    public String initCutFace(@RequestParam(value = "file") MultipartFile file,String picType, ModelMap model, String module, HttpServletRequest request) {
		System.out.println("============= UserController initCutFace start , time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
		String result = userService.initCutFace(file, picType, model, module, request);
		System.out.println("============= UserController initCutFace complete , time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
        if(result.equals("failpic")){
			System.out.println("================================= initCutFace to user/face  =================");
        	return "/user/face?info=failpic";
        }
        //同步到会员（与端保持一致）
		/*ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		try {
			List<FileItem> items = upload.parseRequest(request);
			MultipartEntity entity = getMultipartEntity(items);
			//String synResult = userService.synUpload(entity, request);
		} catch (Exception e) {
			e.printStackTrace();

		}*/
		System.out.println("============= UserController initCutFace end, to user/cutFace, time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
        return "/user/cutFace";
    }
    
    /**
     * 上传处理过的头像
     */
    @RequestMapping(value = "cutFace")
    public String cutFace(int x1,int y1,int x2, int y2, String cutFace, String avatarSmall, String avatarMiddle, String avatarLarge, HttpServletRequest request) {
    	User user = getCurrentUser();
    	
    	String result = userService.cutFace(user, x1, y1, x2, y2, cutFace, avatarSmall, avatarMiddle, avatarLarge, request);
    	/* 下面是更新 内存shiro中的principal信息。不知道采用这种方式是否会引起其他问题。*/
		LoginInfo principal = getLoginInfo();
		principal.setUser(user);
    	return "redirect:/user/face?info=" + result;
    }
    
    /**
     * 查看个人资料
     */
    @RequestMapping(value = "profile", method = RequestMethod.GET)
    public String viewProfile(Model model) {
    	User user = getCurrentUser();
    	model.addAttribute("user", user);
    	return "/user/userProfile";
    }

    /**
	 * 初始化修改资料
	 */
	@RequestMapping("/initUpdateProfile")
	public String initUpdateProfile(Model model){
		User user = userService.findUserById(getLoginInfo().getUserId());
		model.addAttribute("user", user);
		return "user/updateProfile";
	}
    /**
     * 用户更新自身信息
     * 
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "updateProfile")
    public String updateProfile(User newUser,@DateTimeFormat(pattern="yyyy-MM-dd") Date birthdayPicker, String updatePhone, String updateEmail) throws Exception {
    	User oldUser = userService.findUserById(getLoginInfo().getUserId());
		oldUser.setNickname(newUser.getNickname());
		/*if("yes".equals(updatePhone)){
			oldUser.setPhone(newUser.getPhone());
			if(!ValidateUtil.CheckPhone(newUser.getPhone())){
				return "redirect:/user/profile";
			}
		}*/
		if("yes".equals(updateEmail)){
			oldUser.setEmail(newUser.getEmail());
			if(!ValidateUtil.CheckEmail(newUser.getEmail())){
				return "redirect:/user/profile";
			}
		}
		
		userService.updateUser(oldUser);
		//同步principal
		principalService.savePrincipal(oldUser, null);
		
		if(SystemConfigHolder.getConfig("if_to_amuc").equals("1")){
      		List<NameValuePair> params = new ArrayList<NameValuePair>();
      		params.add(new BasicNameValuePair("nickname",newUser.getNickname()));
      		params.add(new BasicNameValuePair("mobile",oldUser.getPhone()));
      		new HttpClientUtil().callAmucAPI("/api/member/syn/updateNickname.do", params);
        }
		
		/* 下面是更新 内存shiro中的principal信息。不知道采用这种方式是否会引起其他问题。*/
		LoginInfo principal = getLoginInfo();
		principal.setUser(oldUser);
		principal.setNickname(oldUser.getNickname());
		return "redirect:/user/profile";
    }
    /**
     * 进入修改密码的页面
     */
    @RequestMapping(value = "password/initUpdatePassword")
    public String initUpdatePassword(Model model) {
    	
    	LocalPrincipal principal = principalService.getByUserId(getCurrentUserId());
    	//如果之前没设置过密码
    	if(principal == null || StringUtils.isBlank(principal.getPassword())){
    		model.addAttribute("hasPassword",false);
    	}else {
    		model.addAttribute("hasPassword",true);
    	}
    	return "/user/updatePassword";
    }
    
    /**
     * 进入修改密码的页面，访问个人中心页面时的修改密码页面，不需要装饰。
     */
    @RequestMapping(value = "password/initUpdatePassword2")
    public String initUpdatePassword2(Model model) {
    	
    	LocalPrincipal principal = principalService.getByUserId(getCurrentUserId());
    	//如果之前没设置过密码
    	if(principal == null || StringUtils.isBlank(principal.getPassword())){
    		model.addAttribute("hasPassword",false);
    	}else {
    		model.addAttribute("hasPassword",true);
    	}
    	return "/user/updatePassword2";
    }
    
    /**
     * 进入修改密码的页面，访问个人中心页面时的修改密码页面，不需要装饰。（移动版会员中心的修改密码页面）
     */
    @RequestMapping(value = "password/initUpdatePassword_app")
    public String initUpdatePassword_app(String anyUrl,Model model) {
    	
    	LocalPrincipal principal = principalService.getByUserId(getCurrentUserId());
    	//如果之前没设置过密码
    	if(principal == null || StringUtils.isBlank(principal.getPassword())){
    		model.addAttribute("hasPassword",false);
    	}else {
    		model.addAttribute("hasPassword",true);
    	}
    	model.addAttribute("anyUrl",anyUrl);
    	return "/user/updatePassword_app";
    }
    
    /**
     * 修改密码
     */
    @RequestMapping(value = "password/change")
    public ResponseEntity<String> changePassword(String currentPassword, String newPassword, boolean hasPassword, Model model) {
    	
    	String result = "success";
    	if(StringUtils.isBlank(currentPassword)){
    		result = "old password can not be null.";
    	}else if(StringUtils.isBlank(newPassword)){
    		result = "new password can not be null.";
    	}else {
    		
    	}
    	LocalPrincipal principal = principalService.getByUserId(getCurrentUserId());
    	//如果之前没设置过密码
    	if(principal == null || StringUtils.isBlank(principal.getPassword())){
    		result="can not update password,please build local user";
    	}else{
    		try {
				principalService.updatePassword(principal, currentPassword, newPassword);
			} catch (PrincipalException e) {
				result = "old_password_is_wrong";
				e.printStackTrace();
			}
    	}
    	Map<String,Object> rs = new HashMap<String, Object>();
    	rs.put("result", result);
    	ResponseEntity<String> re = new ResponseEntity<String>(new com.founder.sso.util.json.JSONObject(rs).toString(),HttpStatus.OK); 
    	return re;
    }

    /**
     * 测试邮箱页面
     */
    @RequestMapping(value = "email/test")
    public String initTestEmail(Model model) {
    	try{
    		List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    		for(SystemConfig systemConfig:systemConfigs){
    			if(systemConfig.getScode().equals("emailHost"))
    				model.addAttribute("emailHost", systemConfig.getSstatus());
    			if(systemConfig.getScode().equals("email_userName"))
    				model.addAttribute("userName", systemConfig.getSstatus());
    			if(systemConfig.getScode().equals("email_password"))
    				model.addAttribute("password", systemConfig.getSstatus());
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return "/user/findpwd/testEmail";
    }
    
    /**
     * 测试邮箱页面
     */
    @RequestMapping(value = "email/sendCode", method=RequestMethod.POST)
    public ResponseEntity<String> testEmailSend(String sendEmail,Model model) {
    	try{
    		List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    		String emailHost = "";
    		String email_userName = "";
    		String email_password = "";
    		for(SystemConfig systemConfig:systemConfigs){
    			if(systemConfig.getScode().equals("emailHost"))
    				emailHost = systemConfig.getSstatus();
    			if(systemConfig.getScode().equals("email_userName"))
    				email_userName = systemConfig.getSstatus();
    			if(systemConfig.getScode().equals("email_password"))
    				email_password = systemConfig.getSstatus();
    		}	
    		SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(email_userName);
            mail.setTo(new String[]{sendEmail});
            mail.setSubject("这是测试邮件，请勿回复！");
            mail.setSentDate(new Date());// 邮件发送时间
            mail.setText("这是一封测试邮件。如果您已收到此邮件，说明您的邮件服务器已设置成功。请勿回复，请勿回复，请勿回复，重要的事说三遍！");
            JavaMailSenderImpl sender = createMailSender(emailHost,email_userName,email_password);
            sender.send(mail);

    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return new ResponseEntity<String>("", HttpStatus.OK);
    }
    
    /**
     * 修改邮箱配置
     */
    @RequestMapping(value = "email/updateEmailConfig", method=RequestMethod.POST)
    public ResponseEntity<String> updateEmailConfig(String emailHost,String userName,String password,Model model) {
    	try{
    		SystemConfig config1 = systemConfigDao.findByScode("emailHost");
    		config1.setSstatus(emailHost);
    		systemConfigDao.save(config1);
    		SystemConfig config2 = systemConfigDao.findByScode("email_userName");
    		config2.setSstatus(userName);
    		systemConfigDao.save(config2);
    		SystemConfig config3 = systemConfigDao.findByScode("email_password");
    		config3.setSstatus(password);
    		systemConfigDao.save(config3);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	ResponseEntity<String> re = new ResponseEntity<String>("{\"result\":\"success\"}",HttpStatus.OK);
    	return re;
    }
    
    public JavaMailSenderImpl createMailSender(String emailHost,String userName,String password){
    	JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    	try{
    		mailSender.setHost(emailHost);
    		mailSender.setUsername(userName);
    		mailSender.setPassword(password);
            Properties properties = new Properties();
            properties.setProperty("mail.smtp.auth", "true");
            //使用gmail或qq发送邮件是必须设置如下参数的 主要是port不一样
            if (emailHost.indexOf("smtp.gmail.com")>=0 || emailHost.indexOf("smtp.qq.com")>=0) {
                properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                properties.setProperty("mail.smtp.socketFactory.fallback", "false");
                properties.setProperty("mail.smtp.port", "465");
                properties.setProperty("mail.smtp.socketFactory.port", "465");
            }
            mailSender.setJavaMailProperties(properties);
    	}catch(Exception e){
    		
    	}
        
        return mailSender;
    }
    /**
     * 显示密码找回
     * 
     * @return
     */
    @RequestMapping(value = "password/find")
    public String initFindPassword(String byType, Model model) {
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
    	model.addAttribute("byType", byType);
        return "/user/findpwd/findpwd_s1";
    }
    
    /**
     * 密码找回s2：输入邮箱，（已发送验证码）
     * @return
     */
    @RequestMapping(value = "password/find/inputCode", method=RequestMethod.POST)
    public String inputCode(String byType, String phone, String email, Model model) {
    	
    	if("phone".equals(byType)){
    		model.addAttribute("value", phone);
    		model.addAttribute("isPhone", true);
    	}else {
    		model.addAttribute("value", email);
    		model.addAttribute("isPhone", false);
    	}
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
    	model.addAttribute("byType", byType);
    	return "/user/findpwd/findpwd_s2";
    }
    /**
     * 密码找回s3：进行身份验证
     * @return
     */
    @RequestMapping(value = "password/find/authentication", method=RequestMethod.POST)
    public String authentication(String byType, String value, boolean isPhone, String inputCode, Model model, HttpServletRequest request) {
		String codeName = "";
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
		if("email".equals(byType)){
			codeName = "emailcode::" + value ;
		}
		else{
			codeName = "phonecode::" + value;
		}
    	model.addAttribute("isPhone", isPhone);
    	model.addAttribute("value", value);
    	model.addAttribute("byType", byType);
		model.addAttribute("ctx", basePath);
		//String sessionCode = getCachedCode(codeName);
		String sessionCode = cacheUtil.getCachedCode(codeName, request);
		if(StringUtils.isBlank(sessionCode)){
			model.addAttribute("errorMsg", "验证码不能为空！");
			return "/user/findpwd/validPwd_fail";
		}else if(!inputCode.trim().equalsIgnoreCase(sessionCode.trim())){
			model.addAttribute("errorMsg", "验证码不正确！");
			return "/user/findpwd/validPwd_fail";
		}
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
    	return "/user/findpwd/findpwd_s3";
    }
    
    /**
     * 密码找回s4：重置密码
     * @return
     */
    @RequestMapping(value = "password/find/resetPwd", method=RequestMethod.POST)
    public String resetPwd(String byType, String value, boolean isPhone, String password,String isAppType, Model model) {
        principalService.resetPasswordByEmail(value, password);
//    	return "redirect:/user/password/find/success?isPhone="+isPhone+"&byType="+byType;
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
    	model.addAttribute("isPhone", isPhone);
    	model.addAttribute("byType", byType);
    	if("web".equals(isAppType)){ //web端
    		model.addAttribute("isAppType", "web");
    		model.addAttribute("successType", "restPwd");
    		return "/user/success_web";
    	}else if(isAppType == null){ //app端
    		return "/user/findpwd/findpwd_s4";
    	}else{
    		model.addAttribute("isAppType", "app");
    		model.addAttribute("successType", "restPwd"); //重置密码
    		return "/user/success_app";
    	}
    }

	/**
	 * 密码找回s4：重置密码
	 * @return
	 */
	@RequestMapping(value = "password/find/resetPassword", method=RequestMethod.POST)
	public ResponseEntity<String> resetPassword(String value, boolean isPhone, String password, String inputCode, String regisType, HttpServletRequest request) throws Exception {
		String codeName = "";
		if("email".equals(regisType)){
			codeName = "emailcode::" + value ;
		}
		else{
			codeName = "phonecode::" + value;
		}
		JSONObject json = new JSONObject();
		String sessionCode = cacheUtil.getCachedCode(codeName, request);
		json.put("success", true);
		String msg = null;
		if(StringUtils.isBlank(sessionCode)){
			json.put("sucess", false);
			msg = "验证码不能为空！";
		}else if(!inputCode.trim().equalsIgnoreCase(sessionCode.trim())){
			json.put("success", false);
			msg = "验证不正确！";
		}
		if (msg != null) {
			json.put("msg", URLEncoder.encode(msg, "UTF-8"));
		}
		principalService.resetPasswordByEmail(value, password);
		ResponseEntity<String> re = new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		return re;
	}
	/**
	 * 重置密码成功跳转
	 * @return
	 */
	@RequestMapping(value = "resetPwd/postHandle", method=RequestMethod.GET)
	public String postHandle(Model model, String isPhone, String byType, String isAppType) {
		List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
		for(SystemConfig systemConfig:systemConfigs){
			model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
		}
		model.addAttribute("isPhone", isPhone);
		model.addAttribute("byType", byType);
		if("web".equals(isAppType)){ //web端
			model.addAttribute("isAppType", "web");
			model.addAttribute("successType", "restPwd");
			return "/user/success_web";
		}else if(isAppType == null){ //app端
			return "/user/findpwd/findpwd_s4";
		}else{
			model.addAttribute("isAppType", "app");
			model.addAttribute("successType", "restPwd"); //重置密码
			return "/user/success_app";
		}
	}



    /**
     * 显示密码找回页面（移动端）
     * 
     * @return
     */
    @RequestMapping(value = "forgetPwd_app", method = RequestMethod.GET)
    public String initForgetPwd_app(Model model) {
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
        return "/user/forgetPwd_app";
    }
    /**
     * 显示密码找回页面（悦享web）
     * 
     * @return
     */
    @RequestMapping(value = "forgetPwd_web", method = RequestMethod.GET)
    public String initForgetPwd_web(Model model) {
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
        return "/user/forgetPwd_web";
    }

    /**
     * 显示注册页面
     * 
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String initRegister(String siteId,Model model) {
    	if (StringUtils.isNoneBlank(siteId)) {
    		model.addAttribute("siteId", siteId);
		}
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
    	String login_reg_view = SystemConfigHolder.getConfig("login_reg_view"); 
        return "/user/register"+login_reg_view;
    }
    
    /**
     * 显示注册页面-移动端页面
     * 
     * @return
     */
    @RequestMapping(value = "register_app", method = RequestMethod.GET)
    public String initRegister_app(String siteId,Model model) {
    	if (StringUtils.isNoneBlank(siteId)) {
    		model.addAttribute("siteId", siteId);
		}
    	List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}
        return "/user/register_app";
    }

    /**
     * 注册
     * 
     * @return
     * @throws Exception 
     */
    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String register(User user, String password, String registerType,String isAppType,String siteId,Model model) throws Exception {
		System.out.println("register, start ...... ");
    	//先把siteId和配置参数放进去
    	model.addAttribute("siteId", siteId);
		/*List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
    	for(SystemConfig systemConfig:systemConfigs){
    		model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}*/
    	
        if(registerType != null && registerType.equals("email") ){
        	user.setPhone(null);
        	//检查email格式是否正确
        	if(!ValidateUtil.CheckEmail(user.getEmail())){
        		System.out.println("register, email format is error! ");
        		model.addAttribute("email", user.getEmail());
        		if(isAppType == null){ //进入移动端的注册页面
        			String login_reg_view = SystemConfigHolder.getConfig("login_reg_view"); 
        			return "/user/register"+login_reg_view;
        		}else{ //进入WEB端的注册页面
        			return "/user/register_app";
        		}
        	}
        }else {
        	user.setEmail(null);
        	if(!ValidateUtil.CheckPhone(user.getPhone())){
        		model.addAttribute("phone", user.getPhone());
        		//return "/user/register";
        		if(isAppType == null){ //进入移动端的注册页面
        			String login_reg_view = SystemConfigHolder.getConfig("login_reg_view"); 
        			return "/user/register"+login_reg_view;
        		}else{ //进入WEB端的注册页面
        			return "/user/register_app";
        		}
        	}
        }

        try{
	        user.setRegisterDate(Clock.DEFAULT.getCurrentDate());
	        user = userService.save(user);
			System.out.println("register, user save complete ... ");
			if(org.springframework.util.StringUtils.isEmpty(user.getUsername())){
				user.setUsername("WEB USER"+user.getId());
				user.setNickname("WEB USER"+user.getId());
			} else{
				user.setNickname(user.getUsername());
			}
			System.out.println("============= UserController register, user'nickname: " + user.getNickname());

	        //注册完之后,将session中的注册码清空
	        getSession().setAttribute(registerType + "Code", null);
	        //判断是否调用会员中心接口
	        if(SystemConfigHolder.getConfig("if_to_amuc").equals("1")){

				JSONObject json = userService.synRegistToMember(user, password, siteId);
				if(!StringUtils.isEmpty(json.getString("code")) && json.getString("code").equals("0")){
					/*if(null != user.getId()){
						userService.delete(user.getId());
					}*/
					model.addAttribute("errMsg", json.getString("msg"));
					return "/user/fail_register";
				}

				/*if(user.getUsername()==null||user.getUsername().equals("")){
					String name=registerType.equals("web")?registerType+user.getId():"APP"+user.getId();
					user.setUsername(name);
					user.setNickname(name);
					user = userService.save(user);
					principalService.savePrincipal(user, password);
				}*/
				System.out.println("register, user and principal save complete ... ");
	        }
        }catch (Exception e){
        	e.printStackTrace();
			System.out.println("register, register fail ... ");
        	if(null != user.getId()){
				userService.delete(user.getId());
			}
			model.addAttribute("errMsg", "该邮箱已被注册");
			return "/user/fail_register";
        	/*userService.delete(user.getId());
        	if(isAppType == null){ //进入移动端的注册页面
        		String login_reg_view = SystemConfigHolder.getConfig("login_reg_view"); 
        		return "/user/register"+login_reg_view;
    		}else{ //进入WEB端的注册页面
    			return "/user/register_app";
    		}*/
        }

		System.out.println("register, register success ... ");
		if(isAppType == null){ //进入移动端页面
			return "/user/registerSuccess";
		}else{
			model.addAttribute("isAppType", "app");
			model.addAttribute("successType", "register"); //重置密码
			return "/user/success_app";
		}
        
    }


	/**
     * 检查用户名、邮箱是否合法；在注册、修改资料时都可使用
     * 
     * * 当会员修改信息时，能够根邮箱 从数据库查出user，需要排除本会员信息
     * @return true 表示数据合法，没有被注册；false表示非法，已被注册。
     */
    @RequestMapping(value = "identities/isLegal")
    public ResponseEntity<String> isLegal(String value, String field) {

        JSONObject json = new JSONObject();
        User user = userService.getUserByField(value, field);
        String result = "false";
        //1、如果连这样的user都查不到，说明合法
        if (user == null) {
            result = "true";
        //2、如果能查询到user
        }else {
        	Long userId = getCurrentUserId();
        	//2.1、如果查询出来的user是自己
        	if(userId != null && user.getId().equals(userId)){
        		result = "true";
    		//2.2、如果查询出来的user不是自己
        	}else {
        		result = "false";
        	}
        }
        try {
            json.put("result", result);
        } catch (JSONException e) {
            logger.warn("注册账号检查用户名、邮箱是否合法时,json构建出错：" + e.toString());
            e.printStackTrace();
        }
        ResponseEntity<String> re = new ResponseEntity<String>(json.toString(), HttpStatus.OK);
        return re;
    }

     /**
      * 发送手机/邮箱验证码
      * type:phone 手机；email 邮箱
      * value：邮箱
      */
    @RequestMapping(value = "sendCode")
    public ResponseEntity<String> sendCode(String type, String value, String useType, HttpServletRequest request) {
    	//Session session = getSession();
    	String code = StringUtil.getRandom(6);
    	//session.setAttribute(type + "Code", code);
    	System.out.println(type+ "===================== 号码为" + value + "的验证码：" + code + " =======================");
		com.restfb.json.JsonObject json = new JsonObject();
		List<SystemConfig> systemConfigs = (List<SystemConfig>) systemConfigDao.findAll();
    	if("phone".equals(type)) {
			// 检查email格式是否正确
			if (!ValidateUtil.CheckPhone(value)) {
				json.put("code", "fail");
				json.put("msg", "手机号码格式不正确");
				return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
			}


			User u = userService.getUserByField(value, "phone");

			if (useType.equals("0")) {
				//是否已经注册
				if (u != null) {
					json.put("code", "fail");
					json.put("msg", "该号码已被注册！");
					return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
				}
			} else {
				//找回密码
				if (u == null) {
					json.put("code", "fail");
					json.put("msg", "该号码已被注册！");
					return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
				}
			}

			//TODO 发送手机验证码
			String SERVER = "";
			String PORT = "";
			String ACCOUNT_SID = "";
			String AUTH_TOKEN = "";
			String AppId = "";
			for (SystemConfig systemConfig : systemConfigs) {
				if (systemConfig.getScode().equals("SERVER"))
					SERVER = systemConfig.getSstatus();
				if (systemConfig.getScode().equals("PORT"))
					PORT = systemConfig.getSstatus();
				if (systemConfig.getScode().equals("ACCOUNT_SID"))
					ACCOUNT_SID = systemConfig.getSstatus();
				if (systemConfig.getScode().equals("AUTH_TOKEN"))
					AUTH_TOKEN = systemConfig.getSstatus();
				if (systemConfig.getScode().equals("AppId"))
					AppId = systemConfig.getSstatus();
			}
			Properties prop = new Properties();
			FileOutputStream oFile = null;
			try {
				///保存属性到properties文件
				oFile = new FileOutputStream("msgsend.properties", true);
				prop.setProperty("SERVER", SERVER);
				prop.setProperty("PORT", PORT);
				prop.setProperty("ACCOUNT_SID", ACCOUNT_SID);
				prop.setProperty("AUTH_TOKEN", AUTH_TOKEN);
				prop.setProperty("AppId", AppId);
				prop.store(oFile, "The New properties file");
			} catch (Exception e) {
				System.out.println(e);
			} finally {
				if (oFile != null) {
					try {
                        oFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
				}
			}
			cacheUtil.cacheCode("phonecode::", value, code, request);
			logger.info("cacheCode: {}", new Object[]{code});
			ActsocialMsgSender msgSender = new ActsocialMsgSender();
			msgSender.sendMsgCode(value, code);

		}else if("email".equals(type)){
			// 检查email格式是否正确
			if (!ValidateUtil.CheckEmail(value)) {
				json.put("code", "fail");
				json.put("msg", "email格式不正确！");
				return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
			}

			if (value.indexOf("@qq.") != -1 && !ValidateUtil.CheckEmail_QQ(value)) {
				json.put("code", "fail");
				json.put("msg", "qq邮箱无效！");
				return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
			}

			User u = userService.getUserByField(value, "email");
			if (useType.equals("0")) {
				//是否已经注册
				if (u != null) {
					json.put("code", "fail");
					json.put("msg", "该邮箱已被注册！");
					return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
				}
			} else{
				//找回密码
				if (u == null) {
					json.put("code", "fail");
					json.put("msg", "找回密码或者修改邮箱失败，该邮箱未注册！");
					return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
				}
			}

    		for(SystemConfig systemConfig:systemConfigs){
    			if(systemConfig.getScode().equals("emailHost"))
    				ActsocialMailSender.emailHost = systemConfig.getSstatus();
    			if(systemConfig.getScode().equals("email_userName"))
    				ActsocialMailSender.userName = systemConfig.getSstatus();
    			if(systemConfig.getScode().equals("email_password"))
    				ActsocialMailSender.password = systemConfig.getSstatus();
    		}
    		cacheUtil.cacheCode("emailcode::", value, code, request);
    		Map<String,Object> model = new HashMap<String,Object>();
	        model.put("code", code);
	        // ActsocialMailSender.sendEmailCode(model, new String[]{value});
			try {
				if (this.isOnlineOrigin(request)) {
					ActsocialMailSender.sendMail(value, code, useType);
				} else {
					ActsocialMailSender.sendMailByGmail(value, code, useType);
				}
			} catch (Exception e) {
				e.printStackTrace();
				json.put("code", "fail");
				json.put("msg", "发送邮件失败！");
				return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
			}
		}
		json.put("code", "success");
		json.put("msg", "验证码已发送");
    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    }

	private boolean isOnlineOrigin(HttpServletRequest request) {
		return request.getHeader("Origin").equals("http://localhost") || request.getHeader("Origin").equals("http://127.0.0.1")
				|| request.getHeader("Origin").equals("https://www.crion.top") || request.getHeader("Origin").equals("http://www.crion.top");
	}

	/*private void cacheCode(final String prefix, final String value, final String code) {
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				String codeTime = SystemConfigHolder.getConfig("codeTime"); //redis中验证码有效期
				jedisClient.set(prefix+value, code, Integer.parseInt(codeTime));
				System.out.println("============= cacheCode into jedis, emial: " + prefix+value + ", code: " + jedisClient.get(prefix+value) + " ======================");
			}
		});
		
		t.start();
		try {
			t.join(1000);
			if(t.isAlive()){
				getSession().setAttribute(prefix+value, code);
				System.out.println("============ cacheCode into session, emial: " + prefix+value + ", code: " + getSession().getAttribute(prefix+value) + " ======================");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(60*1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						getSession().removeAttribute(prefix+value);
						System.out.println("============= cacheCode remove from session, key: " + prefix+value + " ======================");
					}
				}).start();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}*/
    
    /**
     * 检查验证码是否输入正确。
     */
    @RequestMapping(value = "checkCode")
    public ResponseEntity<String> checkCode(String type,String value, String inputCode, HttpServletRequest request) {

        JSONObject json = new JSONObject();
        String result = "diffrent";
        //  处理手机注册及邮箱注册 手机注册比对验证码 
    	/*Session session = getSession();
    	String codeName = type + "Code";
    	String sessionCode = (String) session.getAttribute(codeName);*/
        String codeName = "";
        if("email".equals(type))
        	codeName = "emailcode::" + value ;
        else
        	codeName = "phonecode::" + value;
        
        String sessionCode = cacheUtil.getCachedCode(codeName, request);
    	if(StringUtils.isBlank(sessionCode)){
    		result = "codeIsNull";
    	} else if(inputCode.trim().equalsIgnoreCase(sessionCode.trim())){
    		result = "same";
    	}
    	
        try {
            json.put("result", result);
        } catch (JSONException e) {
            logger.warn("检查验证码是否输入正确时,json构建出错：" + e.toString());
            e.printStackTrace();
        }
        ResponseEntity<String> re = new ResponseEntity<String>(json.toString(), HttpStatus.OK);
        return re;
    }

	/*private String getCachedCode(final String codeName) {
		ExecutorService exec = Executors.newSingleThreadExecutor();  
		Future<String> f = exec.submit(new Callable<String>() {

			@Override
			public String call() throws Exception {
				System.out.println("============ getCachedCode from redis, emial: " + codeName + ", code: " + jedisClient.get(codeName));
				return jedisClient.get(codeName);
			}
		});
		try {
			String code = f.get(1, TimeUnit.SECONDS);
			System.out.println("============ getCachedCode form future, emial: " + codeName + ", code: " + code + " ======================");
			return code;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
		Object attribute = getSession().getAttribute(codeName);
		System.out.println("============ getCachedCode from session, emial: " + codeName + ", code: " + (String) attribute + " ======================");
		return attribute ==null?null:getSession().removeAttribute(codeName).toString();
	}*/
   
    /**
     * 敏感词检查
     * @throws Exception 
     */
    @RequestMapping(value = "identities/isSensitiveword", method = RequestMethod.GET)
    public ResponseEntity<String> isSensitiveword(String value) throws Exception {

        JSONObject json = new JSONObject();
        String result = "false";
       
        try {
        	SensitivewordUtil swUtil = new SensitivewordUtil();
      		Set<String> keyWordSet = sensitivewordservice.readSensitiveWordTable();
      		swUtil.addSensitiveWordToHashMap(keyWordSet);
      		if(swUtil.isContaintSensitiveWord(value, 2)){
      			result = "true";
      		}
            json.put("result", result);
        } catch (JSONException e) {
            logger.warn("敏感词检查时,json构建出错：" + e.toString());
            e.printStackTrace();
        }
        ResponseEntity<String> re = new ResponseEntity<String>(json.toString(), HttpStatus.OK);
        return re;
    }
    /**
     * 绑定报社旧系统中的第三方账户页面初始化
     */
    @RequestMapping(value = "initBindThirdAccount/{id}", method = RequestMethod.GET)
    public String initBindThirdAccount(@PathVariable String id, Model model) {
    	
    	long userId = getCurrentUserId();
    	model.addAttribute("systemid", id);
    	model.addAttribute("userid", userId);
    	return "/user/bindThirdAccount";
    }
    /**
     * 绑定报社旧系统中的第三方账户
     */
    @SuppressWarnings("static-access")
	@RequestMapping(value = "BindThirdAccount", method = RequestMethod.POST)
    public String bindThirdAccount(String userid, String username, String pwd, String subsystemid, Model model){
    	
    	System.out.println(userid);
    	System.out.println(username);
    	System.out.println(pwd);
    	System.out.println(subsystemid);
    	
    	logger.debug("userid={},username={},pwd={},subsystemid={}",
    			userid, username, pwd, subsystemid);
    	
    	int UserId = Integer.parseInt(userid);
    	int SubSystemId = Integer.parseInt(subsystemid);
    	//1.验证用户名密码是否正确
    	Subsystem sub = subsystemService.findOne(Long.parseLong(subsystemid));
    	String authcurl = sub.getAuthcUrl();
    	
    	List<UserSubsystemBind> utbs =  userthirdbindservice.findByUseridAndSubsystemid(UserId, SubSystemId);
    	if(utbs != null && utbs.size() > 0 ){
    		System.out.println("已存在用户和该子系统的绑定记录");
        	
    		model.addAttribute("beBounded", "true");
    		model.addAttribute("doBounded", "true");
    		model.addAttribute("nickname", username);
    		model.addAttribute("systemid", subsystemid);
        	return "/user/bindThirdAccount";//重定向到页面
    	}else{
    		//远程调用，根据授权地址验证用户名密码是否正确
    		UserManager um = new UserManager();
    		boolean authcAccount = um.AuthcAccount(authcurl, username, pwd);
    		authcAccount = true;
    		if(authcAccount){
        		//验证通过，将用户数据写入user_third_binding表
        		UserSubsystemBind userthirdbind = new UserSubsystemBind();
        		userthirdbind.setName(username);
        		userthirdbind.setBindtime(Clock.DEFAULT.getCurrentDate());
        		userthirdbind.setSubsystemid(SubSystemId);
        		userthirdbind.setUserid(UserId);
        		userthirdbindservice.save(userthirdbind);
        	}
    	}
    	return "redirect:/user/myHome";
    }
    /**
     * 解除绑定  报社旧系统中的第三方账户
     */
    @RequestMapping(value = "unBindThirdAccount")
    public String unBindThirdAccount(long userThirdBindId){
    	
    	userthirdbindservice.unBindThirdAccount(userThirdBindId);
    	return "redirect:/user/myHome";
    	
    }

	/**
	 * 发送短信
	 * @param email
	 * @param request
	 * @return
	 */
	@RequestMapping("/email/send")
	@ResponseBody
	public ResponseEntity<String> emailSend(String email, HttpServletRequest request) {
		com.restfb.json.JsonObject json = new JsonObject();
		Map<String, String> pMap = new HashedMap();
		// 检查email格式是否正确
		if (!ValidateUtil.CheckEmail(email)) {
			json.put("code", "fail");
			json.put("msg", "email格式不对");
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}

		//是否已经注册
		if (userService.getUserByField(email, "email") != null) {
			json.put("code", "fail");
			json.put("msg", "该邮箱已被注册！");
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}

		List<SystemConfig> systemConfigs = (List<SystemConfig>) systemConfigDao.findAll();
		for (SystemConfig systemConfig : systemConfigs) {
			if (systemConfig.getScode().equals("emailHost")) {
				pMap.put("mail_host", systemConfig.getSstatus());
			}
			if (systemConfig.getScode().equals("email_userName")) {
				pMap.put("mail_user", systemConfig.getSstatus());
				pMap.put("mail_from", systemConfig.getSstatus());
			}
			if (systemConfig.getScode().equals("email_password")) {
				pMap.put("mail_password", systemConfig.getSstatus());
			}
		}
		pMap.put("email", email);
		return new ResponseEntity<String>(userService.sendMail(pMap).toString(), HttpStatus.OK);
	}

	/**
	 * 关联user和第三方账号
	 */
	@RequestMapping(value = "isLogOut")
	public ResponseEntity<String> isLogOut(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			Cookie ck = cookies[i];
			String domain = ck.getDomain();
			System.out.println("domain: " + ck.getDomain() + ",name: " + ck.getName() + ", value: " + ck.getValue());

		}
		HttpSession session = request.getSession();
		Enumeration<String> enumers = session.getAttributeNames();
		while (enumers.hasMoreElements()){
			System.out.println("enumer: " + enumers.nextElement());
		}
		Object attribute = request.getSession().getAttribute("login_status");
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("success", true);
		Session session1 = getSubject().getSession();
		rs.put("code", attribute != null ? false : true);
		return new ResponseEntity<String>(new com.founder.sso.util.json.JSONObject(rs).toString(), HttpStatus.OK);
	}

	private void platFormConfig() {
		if (getSession().getAttribute("fb_appid") == null) {
			SystemConfig config = systemConfigDao.findByScode("FACEBOOK_APPID");
			getSession().setAttribute("fb_appid", config != null ? config.getSstatus() : "");
		}
		if (getSession().getAttribute("gp_clientid") == null) {
			SystemConfig config = systemConfigDao.findByScode("GOOGLEPLUS_CLIENTID");
			getSession().setAttribute("gp_clientid", config != null ? config.getSstatus() : "");
		}
		if (getSession().getAttribute("twitter_appid") == null) {
			SystemConfig config = systemConfigDao.findByScode("TWITTER_APPID");
			getSession().setAttribute("twitter_appid", config != null ? config.getSstatus() : "");
		}
		if (getSession().getAttribute("wechat_appid") == null) {
			SystemConfig config = systemConfigDao.findByScode("WECHAT_APPID");
			getSession().setAttribute("wechat_appid", config != null ? config.getSstatus() : "");
		}
	}

	/**
	 * 注销用户
	 */
	@RequestMapping(value = "deleteUser", method=RequestMethod.POST)
	public ResponseEntity<String> deleteUser(String email, HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> rs = new HashMap<String, Object>();
		try {
			//String email = URLDecoder.decode(request.getParameter("email"), "UTF-8");
			User user = userService.findByEmail(email);
			if (user != null) {
                userService.delete(user.getId());
                if (user.getUid() != 0) {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("uid",String.valueOf(user.getUid())));
					String result = new HttpClientUtil().callAmucAPI("/api/member/syn/deleteMember.do", params);
				}
            }
			rs.put("code", true);
		} catch (Exception e) {
			rs.put("code", false);
			e.printStackTrace();
		}
		return new ResponseEntity<String>(new com.founder.sso.util.json.JSONObject(rs).toString(), HttpStatus.OK);
	}

	//privacyStatement
	/**
	 * 绑定报社旧系统中的第三方账户页面初始化
	 */
	@RequestMapping(value = "privacy/statement", method = RequestMethod.GET)
	public String privacyStatement(HttpServletRequest request, HttpServletResponse response, Model model) {
		model.addAttribute("ctx", request.getContextPath());
		return "/user/privacyStatement";
	}

	private MultipartEntity getMultipartEntity(List<FileItem> items) throws IOException {
		Iterator<FileItem> iter = items.iterator();
		MultipartEntity entity = new MultipartEntity();
		int i = 1;
		while (iter.hasNext()) {
			FileItem item = iter.next();
			if (!item.isFormField() && item.getSize() > 0) {
				entity.addPart("file" + i++, new InputStreamBody(item.getInputStream(), item.getName()));
			}
		}
		return entity;
	}

	/**
	 * 用户是否存在
	 */
	@RequestMapping(value = "checkUserExit", method = RequestMethod.POST)
	public ResponseEntity<String> checkUserExit(HttpServletRequest request, HttpServletResponse response) {
		User user = userService.getUserByField(request.getParameter("value"), request.getParameter("field"));
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("code", user != null ? true : false);
		return new ResponseEntity<String>(new com.founder.sso.util.json.JSONObject(rs).toString(), HttpStatus.OK);
	}
}