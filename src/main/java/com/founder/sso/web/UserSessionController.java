package com.founder.sso.web;

import com.founder.redis.JedisClient;
import com.founder.sso.admin.entity.Subsystem;
import com.founder.sso.admin.service.SubsystemService;
import com.founder.sso.auth.wechat.entity.ResultMessage;
import com.founder.sso.auth.wechat.util.JsapiTicketThread;
import com.founder.sso.auth.wechat.util.SignUtils;
import com.founder.sso.auth.wechat.util.WechatUtil;
import com.founder.sso.dao.SystemConfigDao;
import com.founder.sso.entity.User;
import com.founder.sso.entity.UserSubsystemBind;
import com.founder.sso.service.UserService;
import com.founder.sso.service.UserSubsystemBindService;
import com.founder.sso.service.oauth.dao.UserOauthBindingDao;
import com.founder.sso.service.oauth.entity.OauthProviders;
import com.founder.sso.service.oauth.entity.SystemConfig;
import com.founder.sso.service.oauth.entity.UserOauthBinding;
import com.founder.sso.util.SystemConfigHolder;
import com.founder.sso.util.coder.EncryptUtil;
import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;
import com.founder.sso.util.securityCode.CacheUtil;
import com.google.code.kaptcha.Producer;
import com.google.common.collect.Maps;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import weibo4j.util.URLEncodeUtils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;

//import redis.clients.jedis.JedisCluster;

/**
 * 管理会话
 *
 * @author zhangmc
 */
@Controller
public class UserSessionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserSessionController.class);

    @Autowired
    private SubsystemService subsystemService;
    @Autowired
    private UserSubsystemBindService userthirdbindservice;

    @Autowired
    private SystemConfigDao systemConfigDao;

    @Autowired
    JedisClient jedisClient;

    @Autowired
    Producer producer;

    @Autowired
    CacheUtil cacheUtil;

    @Autowired
    UserService userService;

    @Autowired
    private UserOauthBindingDao userOauthBindingDao;

    private final static JSONObject WECHAT_CONFIG = new JSONObject();
    private final static String WECHAT_APPID = "wechat_appid";
    private final static String WECHAT_SECRETKEY = "wechat_secretKey";

    /**
     * 如果某一子系统本身没有登录功能，登录时需要重定向到SSO的登录页面，
     * sso在登录后，会跳转回给定的参数redirectUrl
     * 这一步将redirectUrl放入session
     */
    @RequestMapping(value = "/user/login", method = RequestMethod.GET)
    public String login(String redirectUrl, String siteId,
                        HttpServletRequest request, HttpServletResponse response) {
        System.out.println("============= UserSessionController login RequestMethod.GET, time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest) request).getRequestURI());
        if (StringUtils.isNoneBlank(redirectUrl)) {
            request.getSession().setAttribute("redirectUrl", redirectUrl);
        }

        if (StringUtils.isNoneBlank(siteId)) {
            request.getSession().setAttribute("siteId", siteId);
        }
        this.platFormConfig();

		/*List<SystemConfig> systemConfigs = (List<SystemConfig>)systemConfigDao.findAll();
        for(SystemConfig systemConfig:systemConfigs){
    		request.setAttribute(systemConfig.getScode(), systemConfig.getSstatus());
    	}*/
    	
		/*String login_reg_view = SystemConfigHolder.getConfig("login_reg_view");
		logger.info("login:-->{}", login_reg_view);*/
        // return "/user/login"+login_reg_view;
        this.handlerByRememberMe(request, response);
        return "/user/login";
    }

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public String fail(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String username, Model model, HttpServletRequest request, HttpServletResponse response) {
        System.out.println("============= UserSessionController login RequestMethod.POST, time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest) request).getRequestURI());
        User user = getCurrentUser();
        if (user == null) {
            List<SystemConfig> systemConfigs = (List<SystemConfig>) systemConfigDao.findAll();
            for (SystemConfig systemConfig : systemConfigs) {
                model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
            }
            this.platFormConfig();
            model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
            String login_reg_view = SystemConfigHolder.getConfig("login_reg_view");
            // return "/user/login"+login_reg_view;
            this.handlerByRememberMe(request, response);
            return "/user/login";
        } else {
            return "redirect:/user/myHome";
        }
    }

    @RequestMapping(value = "/user/login_app", method = RequestMethod.GET)
    public String login_app(String redirectUrl, String siteId, Model model, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isNoneBlank(redirectUrl)) {
            request.getSession().setAttribute("redirectUrl", redirectUrl);
        }
        List<SystemConfig> systemConfigs = (List<SystemConfig>) systemConfigDao.findAll();
        for (SystemConfig systemConfig : systemConfigs) {
            model.addAttribute(systemConfig.getScode(), systemConfig.getSstatus());
        }
        if (StringUtils.isNoneBlank(siteId)) {
            request.getSession().setAttribute("siteId", siteId);
        }
        return "/user/login_app";
    }

    @RequestMapping(value = "/user/login_app", method = RequestMethod.POST)
    public String fail_app(@RequestParam(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM) String username, @RequestParam(required = false) String anyUrl, HttpServletResponse response, Model model) {
        User user = getCurrentUser();
        if (user == null) {
            model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
            return "/user/login_app";
        } else if (anyUrl != null) {
            return "redirect:" + anyUrl;
        } else {
            String amuc_home = SystemConfigHolder.getConfig("amuc_home"); //移动版会员中心首页
            return "redirect:" + amuc_home;
        }
    }

    @RequestMapping(value = "/user/ssoLogin")
    public String ssoLogin(Model model, HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(required = true) String redirectUrl, @RequestParam(required = true) String code,
                           @RequestParam(required = false) String isAppType, @RequestParam(required = false) String anyUrl, String siteId) throws ClientProtocolException, IOException {

        System.out.println("-----------------------------------进入ssoLogin--------------------------------------------");
        System.out.println("当前时间（毫秒为单位）：" + System.currentTimeMillis());
        System.out.println("redirectUrl: " + redirectUrl + ", code: " + code + ", isAppType: " + isAppType + ", anyUrl: " + anyUrl);
        System.out.println("ssoLogin: " + request.getContextPath());

        boolean isCy = false;
        Subsystem subsystem = null;

        if (code.equals(OauthProviders.SOHU_CY.getValue())
                && redirectUrl.equals(request.getContextPath() + "/user/changyan/success")) {
            isCy = true;
        } else {
            subsystem = subsystemService.findByCode(code);
        }
        System.out.println("---------------------------- ssoLogin, isCy: " + isCy);
        if (!isCy && (subsystem == null || !subsystem.getConvertRedirectUrl().equals(redirectUrl))) {
            String referer = request.getHeader("Referer");
            if (!StringUtils.isEmpty(referer)) {
                model.addAttribute("toPage", referer);
            } else {
                String login_reg_view = SystemConfigHolder.getConfig("login_reg_view");
                this.platFormConfig();
                //model.addAttribute("toPage", request.getContextPath() + "/user/login"+login_reg_view);
                model.addAttribute("toPage", request.getContextPath() + "/user/login");
            }
            return "/user/ssoLoginFail";
        }
        User user = getCurrentUser();
        if (user != null) {
            //登录成功以后删除验证码信息
            String sessID = request.getSession().getId();
            //JCaptchaServiceSingleton.removeCaptcha(sessID);
            System.out.println("---------------------------- ssoLogin success, remove securityCode(sessID): " + sessID);
            if (!StringUtils.isEmpty(sessID)) {
                request.getSession().removeAttribute("securityCode::" + sessID);
            }
            //第三方账号已经登录过，但没有绑定本地用户，引导去绑定页面
            if (user.getId() == null && StringUtils.isBlank(user.getEmail()) && StringUtils.isBlank(user.getPhone())) {
                WebUtils.issueRedirect(request, response, "/user/connection/isConnected");
                return null;
            }
            //List<Subsystem> subsystemList = subsystemService.findByEnabledTrueAndCodeNot(code);
            List<Subsystem> subsystemList = subsystemService.findByEnabledTrue();
            model.addAttribute("subsystemList", subsystemList);

            if (user.getUid() == null || user.getUid() == 0) {
                try {
                    // TODO 账户登录、第三方（Head）
                    org.json.JSONObject json = userService.synRegistToMember(user, "isnull", null);
                } catch (Exception e) {
                    System.out.println("-------------子系统第三方登录成功，已注册前台用户，会员同步注册失败");
                    e.printStackTrace();
                }
            }

            Map<String, String> data = user.getIdentities();
            data.put("createTime", String.valueOf(System.currentTimeMillis()));
            data.put("_random", UUID.randomUUID().toString());
            data.put("code", code);
            String gp_accessToken = cacheUtil.getCachedCode("gp_accessToken::" + ((HttpServletRequest) request).getSession().getId(), ((HttpServletRequest) request));
            data.put("token", gp_accessToken);

            // 判断该子系统是否具有子账号，没有的话，无需加入map,有的话，取出子账号加入map
            String suffix = null;
            if (subsystem != null) {

                int subaccount = subsystem.getSubaccount();
                if (subaccount == 1) {// 存在子账号
                    List<UserSubsystemBind> utblist = userthirdbindservice
                            .findByUseridAndSubsystemid(user.getId().intValue(), subsystem.getId().intValue());
                    if (utblist != null && !utblist.isEmpty()) {
                        data.put("subaccount", utblist.get(0).getName());
                    }
                }
                String msg = new JSONObject(data).shuffle2String();
                //遍历其余子系统并将其重定向地址进行封装
                int subLen = subsystemList.size();
                if (subLen != 0) {   //有其他子系统存在
                    List<String> reUrlList = new ArrayList<String>();
                    for (Subsystem subsys : subsystemList) {
                        String reUrl = subsys.getRedirectUrl();   //重定向地址
                        String ecpType = subsys.getEncryptType();  //加密类型
                        String reTicket = "";
                        if ("none".equals(ecpType)) {
                            reTicket = URLEncodeUtils.encodeURL(msg);
                        } else if ("aes".equals(ecpType)) {
                            try {
                                reTicket = URLEncodeUtils.encodeURL(EncryptUtil.aesEncrypt(subsys.getConvertSecretKey(), msg.getBytes("utf-8")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        cacheTicket(reTicket);
                        System.out.println("=========== reUrl+ticket: " + reUrl + "ticket=" + reTicket);
                        reUrlList.add(reUrl + "ticket=" + reTicket);
                    }
                    model.addAttribute("reUrlList", reUrlList);
                }
                if ("none".equals(subsystem.getEncryptType())) {
                    suffix = URLEncodeUtils.encodeURL(msg);
                } else {
                    try {
                        suffix = URLEncodeUtils.encodeURL(EncryptUtil.aesEncrypt(subsystem.getConvertSecretKey(), msg.getBytes("utf-8")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cacheTicket(suffix);
                }
            }

            if (suffix != null) {
                model.addAttribute("toPage", redirectUrl + "ticket=" + suffix);
            }
            if (!model.containsAttribute("toPage")) {
                model.addAttribute("toPage", redirectUrl);
            }
            if (!model.containsAttribute("anyUrl")) {
                model.addAttribute("anyUrl", anyUrl);
                request.getSession().setAttribute("anyUrl", anyUrl);
            }
            if (isAppType != null && isAppType.equals("app")) { //访问移动版会员中心页面
                String amuc_home = SystemConfigHolder.getConfig("amuc_home"); //移动版会员中心首页
                model.addAttribute("toAmucCenter", amuc_home);
                model.addAttribute("isAppType", isAppType);
            }
            System.out.println("-----------------------------------ssoLogin账号密码验证--通过--------------------------------------------");
            System.out.println("当前时间（毫秒为单位）：" + System.currentTimeMillis());
            return isCy ? "/user/cyLogin" : "/user/ssoLogin";
        } else {
            System.out.println("---------------------------- ssoLogin, currentUser is null ...");
            WebUtils.saveRequest(request);
            this.platFormConfig();
            cacheUtil.cacheCode("toSsoLogin::", request.getSession().getId(), "1", request);
            //cacheUtil.cacheCode("redirectUrl::", request.getSession().getId(), redirectUrl, request);
            try {
                if (isAppType == null) {
                    request.getSession().setAttribute("siteId", siteId);
                    WebUtils.issueRedirect(request, response, "/user/login");
                } else if (isAppType.equals("app")) {
                    request.getSession().setAttribute("siteId", siteId);
                    WebUtils.issueRedirect(request, response, "/user/login_app?anyUrl=" + anyUrl);
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("-----------------------------------ssoLogin账号密码验证--未通过--------------------------------------------");
                System.out.println("当前时间（毫秒为单位）：" + System.currentTimeMillis());
                if (isAppType == null) {
                    request.getSession().setAttribute("siteId", siteId);
                    return "/user/login";
                } else {
                    request.getSession().setAttribute("siteId", siteId);
                    return "user/login_app";
                }
            }
        }
    }

    private void cacheTicket(final String ticket) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
				/*jedisCluster.set(ticket, "");
				jedisCluster.expire(ticket, 5 * 60);*/
                jedisClient.set(ticket, "", 5 * 60);
            }
        });

        t.start();
        try {
            t.join(1000);
            if (t.isAlive()) {
                getSession().setAttribute(ticket, "");
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        getSession().removeAttribute(ticket);
                    }
                }).start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/ticket/isLegel", method = RequestMethod.POST)
    public ResponseEntity<String> isLegel(Model model, @RequestParam(required = true) String ticket) {
        System.out.println("-----------------------------------进入ticket/isLegel--------------------------------------------");
        System.out.println("当前时间（毫秒为单位）：" + System.currentTimeMillis());
        System.out.println("ticket:" + ticket);

        Boolean result = false;
        if (jedisClient.exists(ticket)) {
            jedisClient.del(ticket);
            result = true;
        }
        ResponseEntity<String> re = new ResponseEntity<String>(result.toString(), HttpStatus.OK);
        System.out.println("-----------------------------------结束ticket/isLegel--------------------------------------------");
        System.out.println("当前时间（毫秒为单位）：" + System.currentTimeMillis());
        System.out.println("返回结果：" + re);
        return re;
    }

    private boolean existTicket(final String ticket) {
        ExecutorService exec = Executors.newSingleThreadExecutor();
        Future<Boolean> f = exec.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                Boolean result = false;
                if (jedisClient.exists(ticket)) {
                    jedisClient.del(ticket);
                    result = true;
                }
                return result;
            }
        });
        try {
            return f.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        if (getSession().getAttribute(ticket) != null) {
            getSession().removeAttribute(ticket);
            return true;
        }
        return false;
    }

    /**
     * sso同步退出
     */
    @RequestMapping(value = "/user/ssoLogout")
    public String ssoLogout(Model model, HttpServletRequest request, @RequestParam(required = true) String from,
                            @RequestParam(required = true) String code, @RequestParam(required = false) String isAppType, @RequestParam(required = false) String anyUrl) {
        System.out.println("anyUrl: " + anyUrl + " from: " + from + " isAppType:" + isAppType);
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        session.stop();
        model.addAttribute("subsystemList", subsystemService.findByEnabledTrue());
        Subsystem subsystem = subsystemService.findByCode(code);
        if (subsystem == null || !from.startsWith(subsystem.getConvertHomePage())) {
            String referer = request.getHeader("Referer");
            if (!StringUtils.isEmpty(referer)) {
                model.addAttribute("toPage", referer);
            } else {
                String login_reg_view = SystemConfigHolder.getConfig("login_reg_view");
                //model.addAttribute("toPage", request.getContextPath() + "/user/login"+login_reg_view);
                model.addAttribute("toPage", request.getContextPath() + "/user/login");
            }
            return "/user/ssoLogoutFail";
        }
        model.addAttribute("toPage", from);
        model.addAttribute("anyUrl", anyUrl);
        getSubject().logout();
        if (isAppType != null && isAppType.equals("app")) { //访问移动版会员中心页面
            String amuc_home = SystemConfigHolder.getConfig("amuc_home"); //移动版会员中心首页
            model.addAttribute("toAmucCenter", amuc_home);
            model.addAttribute("isAppType", isAppType);
        }
        this.platFormConfig();
        if (!StringUtils.isEmpty(request.getParameter("uid"))) {
            User user = userService.findUserById(Long.parseLong(request.getParameter("uid")));
            if (user != null) {
                model.addAttribute("uid", user.getId());
                //被绑定用户
                if (StringUtils.isEmpty(user.getProvider())) {
                    List<UserOauthBinding> authBindings = userOauthBindingDao.findByUserId(user.getId());
                    if (authBindings != null && authBindings.size() > 0) {
                        User thirdPlatUser = userService.findByOauthUidAndProvider(authBindings.get(0).getOauthUid(), authBindings.get(0).getProvider());
                        System.out.println("-------------------------- ssoLogout, authBinding uid: " + thirdPlatUser.getUid() + ", nickName: " + thirdPlatUser.getNickname() +
                                ", provider: " + thirdPlatUser.getProvider() + " ---------------------");
                        if (thirdPlatUser != null) {
                            user.setProvider(thirdPlatUser.getProvider());
                        }
                    }
                }
                model.addAttribute("provider", user.getProvider());
				/*model.addAttribute("oauthId", user.getOauthUid());*/
                model.addAttribute("sso_token", request.getParameter("sso_token"));
                System.out.println("--------------------------ssoLogout, parameter uid: " + request.getParameter("uid") + ", nickName: " + user.getNickname() +
                        ", provider: " + user.getProvider() + ", sso_token: " + request.getParameter("sso_token"));
            }
        }
        return "/user/synchronizeLogout";
    }


    /**
     * 同步退出其他系统
     */
    @RequestMapping(value = "/user/toSynchroLogout")
    public String toSynchroLogout(Model model, HttpServletRequest request, HttpServletResponse response) {
        if (getSubject() != null) {
            getSubject().logout();
        }
		/*Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		session.stop();*/

        request.getSession().setAttribute("login_status", "loginout");
        request.getSession().setAttribute("provider", request.getParameter("provider"));
        //HttpSession session = request.getSession(false);
        //session.invalidate();

        // A8B1108F0F57705151054D2BF443741C
        // String provider = request.getParameter("provider");
        // request.getSession().setAttribute("provider", provider);
        // request.getSession().setAttribute(provider + "_access_token", request.getParameter("access_token"));
		/*Cookie[] cookies=request.getCookies();
		for(Cookie ck: cookies){*/
			/*Cookie cookie = new Cookie("SID", null);
			cookie.setMaxAge(0);
			cookie.setPath("/");
			cookie.setDomain("google.com");
			response.addCookie(cookie);*/
        //}

		/*Enumeration<String> attributeNames = request.getSession().getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String s = attributeNames.nextElement();
			System.out.println("s: " + s);
		}*/
        this.platFormConfig();
        model.addAttribute("subsystemList", subsystemService.findByEnabledTrue());
        model.addAttribute("toPage", request.getContextPath() + "/user/login");
        return "/user/synchronizeLogout";
    }

    /**
     * 验证码
     */
    @RequestMapping("/securityCode/get")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //获取图片验证码
        //BufferedImage image = sysCaptchaService.getCaptcha(uuid);
        String sessionId = request.getSession().getId();
        //生成文字验证码
        String code = producer.createText();
        if (!StringUtils.isEmpty(code)) {
            code = code.length() > 4 ? code.substring(0, 4) : code;
        }
        cacheUtil.cacheCode("securityCode::", sessionId, code, request);
        BufferedImage image = producer.createImage(code);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        IOUtils.closeQuietly(out);
    }

    /**
     * 验证输入的的安全验证码是否正确
     */
    @SuppressWarnings("finally")
    @RequestMapping(value = "/securityCode/check")
    public ResponseEntity<String> createSecurityCode(HttpServletRequest request, String inputCode) throws IOException {
        String sessID = request.getSession().getId();

        JSONObject json = new JSONObject();
        String result = "false";
        try {
            //boolean isResponseCorrect = JCaptchaServiceSingleton.getInstance().validateResponseForID(sessID, inputCode);
            String securityCode = cacheUtil.getCachedCode("securityCode::" + sessID, request);
            if (!StringUtils.isEmpty(securityCode) && securityCode.equals(inputCode)) {
                result = "true";
            }
        } catch (Exception e) {
            result = "tryAgin";
            e.printStackTrace();
        } finally {
            try {
                json.put("result", result);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            ResponseEntity<String> re = new ResponseEntity<String>(json.toString(), HttpStatus.OK);
            return re;
        }
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
        if (getSession().getAttribute("wechat_secretKey") == null) {
            SystemConfig config = systemConfigDao.findByScode("WECHAT_SECRETKEY");
            getSession().setAttribute("wechat_secretKey", config != null ? config.getSstatus() : "");
        }
    }

    private void handlerByRememberMe(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String checked = request.getParameter("checked");
        String codeName = "";
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(username) || StringUtils.isEmpty(username)) {
            return;
        }
        try {
            codeName = URLEncoder.encode(username, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Integer maxAge = 7 * 24 * 60 * 60;
        Cookie nameCookie = new Cookie("username", codeName);
        Cookie passwordCookie = new Cookie("password", password);
        Cookie checkedCookie = new Cookie("checked", checked);
        nameCookie.setPath(request.getContextPath() + "/");
        passwordCookie.setPath(request.getContextPath() + "/");
        checkedCookie.setPath(request.getContextPath() + "/");
        if (checked != null && "yes".equals(checked)) {
            nameCookie.setMaxAge(maxAge);
            passwordCookie.setMaxAge(maxAge);
            checkedCookie.setMaxAge(maxAge);
        } else {
            nameCookie.setMaxAge(0);
            passwordCookie.setMaxAge(0);
            checkedCookie.setMaxAge(0);
        }
        response.addCookie(nameCookie);
        response.addCookie(passwordCookie);
        response.addCookie(checkedCookie);
    }

    private void getWechatConfig() {
        try {
            if (!WECHAT_CONFIG.has(WECHAT_APPID) || StringUtils.isEmpty(WECHAT_CONFIG.getString(WECHAT_APPID))) {
                if (!jedisClient.exists(WECHAT_APPID)) {
                    SystemConfig config = systemConfigDao.findByScode("WECHAT_APPID");
                    jedisClient.set(WECHAT_APPID, config != null ? config.getSstatus() : "", 36000);
                }
                if (!jedisClient.exists(WECHAT_SECRETKEY)) {
                    SystemConfig config = systemConfigDao.findByScode("WECHAT_SECRETKEY");
                    jedisClient.set(WECHAT_SECRETKEY, config != null ? config.getSstatus() : "", 36000);
                }
                WECHAT_CONFIG.put(WECHAT_APPID, jedisClient.get(WECHAT_APPID));
                WECHAT_CONFIG.put(WECHAT_SECRETKEY, jedisClient.get(WECHAT_SECRETKEY));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //return WECHAT_CONFIG;
    }

    @RequestMapping(value = "/auth/twitter/callback", method = RequestMethod.GET)
    public String authTwitterCallback(String code, String state, Model model, HttpServletRequest request, HttpServletResponse response) throws JSONException {
        String authUrl;
        try {
            model.addAttribute("toPage", request.getContextPath() + "/user/connection/isConnected");
            this.getWechatConfig();
            JSONObject jsonObject = WechatUtil.getAuthorizationAccessToken(WECHAT_CONFIG.getString(WECHAT_APPID),
                    WECHAT_CONFIG.getString(WECHAT_SECRETKEY), code);
            if (jsonObject.has("errcode")) {
                model.addAttribute("errmsg", "微信认证授权失败！");
                logger.error("errmsg: {}", jsonObject.getString("errmsg"));
                return "/user/wechatLoginFail";
            }
            JSONObject authUserInfo = WechatUtil.getAuthorizationUserInfo(jsonObject.getString("access_token"), jsonObject.getString("openid"));
            if (authUserInfo.has("errcode")) {
                model.addAttribute("errmsg", "获取用户信息失败！");
                logger.error("errmsg: {}", authUserInfo.getString("errmsg"));
                return "/user/wechatLoginFail";
            }

            logger.info("authUserInfo: {}", authUserInfo.toString());
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";

            authUrl = basePath + "/user/oauth2/session/new?provider=wechat&access_token=" + authUserInfo.getString("access_token") +
                    "&userID=" + authUserInfo.getString("openid") + "&userName=" + authUserInfo.getString("nickname") + "&headImg=" +
                    authUserInfo.getString("headimgurl");
            logger.info("authUrl: {}", authUrl);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errmsg", "获取用户信息失败！");
            logger.error("errmsg: {}", "微信授权登录回调接口异常");
            return "/user/wechatLoginFail";
        }

        model.addAttribute("toPage", authUrl);
        return "/user/wechatLoginSuccess";
    }

    public JSONObject httpGet(String url)  {
        JSONObject result = new JSONObject();
        CloseableHttpClient httpCilent = HttpClients.createDefault();//Creates CloseableHttpClient instance with default configuration.
        HttpGet httpGet = new HttpGet(url);
        try {
            String srtResult = "";
            CloseableHttpResponse httpResponse = httpCilent.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                srtResult = EntityUtils.toString(httpResponse.getEntity());//获得返回的结果
                result.put("result", srtResult);
            } else if (httpResponse.getStatusLine().getStatusCode() == 400) {
                //..........
            } else if (httpResponse.getStatusLine().getStatusCode() == 500) {
                //.............
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                httpCilent.close();//释放资源
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @RequestMapping(value = "/auth/wxJsHandler", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> authWxJsHandler(Model model, HttpServletRequest request, HttpServletResponse response) {
        JSONObject json = new JSONObject();
        ResultMessage message = new ResultMessage();
        String url = request.getParameter("url");
        url = url.replaceAll("%3A", ":")
                .replaceAll("%2F", "/")
                .replaceAll("%26", "&")
                .replaceAll("%3F", "?")
                .replaceAll("%3D", "=")
                .replaceAll("%2D", "-");
        JsapiTicketThread.jsapiTicket.getTicket();
        Map<String, String> map = SignUtils.sign(JsapiTicketThread.jsapiTicket.getTicket(), url);
        //Map<String,String> map = SignUtils.sign(weiXinUtil.getJsapiTicket(getAccessToken(request)).getTicket(), url);

        //TODO
        map.put("appId", null);
        message.setData(map);
        message.setStatus("0");

        //return new ResponseEntity<String>(json.toString(),HttpStatus.OK);
        return new ResponseEntity<String>(new JSONObject(message).toString(), HttpStatus.OK);
    }
}
