package com.founder.sso.web;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.founder.sso.auth.wechat.util.WechatUtil;
import com.founder.sso.dao.SystemConfigDao;
import com.founder.sso.service.oauth.entity.SystemConfig;
import com.founder.sso.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.shiro.web.util.SavedRequest;
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

import com.founder.sso.admin.service.SubsystemService;
import com.founder.sso.admin.utils.Encodes;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.LocalPrincipal.IdentityType;
import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;
import com.founder.sso.service.LocalPrincipalService;
import com.founder.sso.service.SensitiveWordService;
import com.founder.sso.service.UserService;
import com.founder.sso.service.oauth.OauthClientManager;
import com.founder.sso.service.oauth.entity.OauthProviders;
import com.founder.sso.service.oauth.entity.RemoteUser;
import com.founder.sso.service.oauth.entity.UserOauthBinding;
import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;
import com.founder.sso.util.sensitiveword.SensitivewordUtil;

import static org.apache.shiro.web.filter.mgt.DefaultFilter.user;

/**
 * 外网用户控制器
 * </p>
 * 主要负责外网用户自身的信息维护工作及外网用户的注册工作
 * 
 * @author zhangmc
 *
 */
@Controller
@RequestMapping(value = "/user/connection")
public class ConnectionController extends BaseController {


	@Autowired
	private SubsystemService subsystemService;
	@Autowired
	private UserService userService;
	@Autowired
	private LocalPrincipalService principalService;
	@Autowired
	private SensitiveWordService sensitiveWordservice;
	@Autowired
	private SystemConfigDao systemConfigDao;

	/**
	 * 检查登录的账号是否已经关联了user
	 */
	@RequestMapping(value = "isConnected")
	public String checkUserIsBinding(Model model, String info, HttpServletRequest request) {
		boolean firstLogin = false;
		if (getSession().getAttribute("firstLogin") != null) {
			firstLogin = true;
		}
		this.platFormConfig();
		LoginInfo login = getLoginInfo();
		model.addAttribute("info", info);
		if (login == null) {
			System.out.println("============= ConnectionController checkUserIsBinding, time: " + DateTimeUtils.currentTimeMillis() + ", url: " + ((HttpServletRequest)request).getRequestURI());
			return "/user/login";
		}

		// 如果firstLogin为true，说明是用媒体账号刚刚注册的，引导用户进行关联账号
		// 屏蔽掉第三方账号首次登录，要求绑定新用户操作，绑定操作可以在用户中心进行操作。
		/*if (firstLogin) {
			model.addAttribute("comFrom", OauthProviders.value(getLoginInfo().getRemoteUser().getProvider()).getName());
			getSession().removeAttribute("firstLogin");
			return "/user/connection/inputConnectUser";
		} else {
			return "redirect:/user/myHome";
		}*/
		// request.getSession().setAttribute("login_status", "login");

		User user = login.getUser();
		if (user.getUid() == null || user.getUid() == 0) {
			try {
				org.json.JSONObject json = userService.synRegistToMember(user, "isnull", null);
			} catch (Exception e) {
				System.out.println("------------- 第三方登录成功，已注册前台用户，会员同步注册失败");
				e.printStackTrace();
			}
		}
		request.getSession().removeAttribute("login_status");
		return "redirect:/user/myHome";
	}

    @RequestMapping(value = "onece")
    public String onece(Model model, String info, HttpServletRequest request) {
		String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
		String authUrl = basePath + "/user/oauth2/session/new?provider=wechat&access_token=zx-4545dfrgrert4tr4" +
				"&userID=wx-90596059603ert4t4eer&userName=Answer&headImg=http://www.iconfans.org/images/uichina_08.png";
		JSONObject jsonObject = null;
		if (request.getScheme().equals("http")) {
			jsonObject = this.httpGet(authUrl);
		}
		System.out.println("once: " + jsonObject.toString());
		return null;
		//return "redirect:/user/connection/isConnected";
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
	 * 关联user和第三方账号
	 */
	@SuppressWarnings("finally")
	@RequestMapping(value = "doConnection")
	public ResponseEntity<String> doConnected(String identity, String password, HttpServletRequest request) {

		String result = "true";
		JSONObject json = new JSONObject();
		boolean matchResult = false;
		List<LocalPrincipal> localPrincipalList = principalService.findByUsernameOrEmailOrPhone(identity);
		User targetUser = null;
		for (LocalPrincipal localPrincipal : localPrincipalList) {
			String userPwd = localPrincipal.getPassword();
			// 从前台得到的password加密后
			String passwordEntrypt = principalService.entryptPassword(password, localPrincipal.getSalt());
			if (passwordEntrypt.endsWith(userPwd)) {
				targetUser = localPrincipal.getUser();
				matchResult = true;
			}
		}

		// 如果查询到相应的user
		LoginInfo loginInfo = getLoginInfo();
		String comFrom = "";
		String previousAvatar = "";
		String previousNickname = "";
		if (matchResult && loginInfo != null) {

			//previousAvatar = loginInfo.getUser().getFullAvatarSmall();
			previousAvatar = loginInfo.getUser().getFullAvatarSmall(request);
			previousNickname = loginInfo.getUser().getNickname();
			// 绑定user和remoteUser
			RemoteUser remoteUser = loginInfo.getRemoteUser();

			// 查看是否已经绑定，防止重复绑定
			List<UserOauthBinding> findByUserIdAndOauthUid = userService.findByUserIdAndOauthUidAndProvider(
					targetUser.getId(), remoteUser.getUid(), remoteUser.getProvider());
			if (findByUserIdAndOauthUid.size() <= 0) {
				userService.doUserOauthBinding(remoteUser, targetUser, null);
				loginInfo.setNickname(targetUser.getNickname());
				loginInfo.setUsername(targetUser.getUsername());
				loginInfo.setUser(targetUser);
				loginInfo.setUserId(targetUser.getId());
			}
			comFrom = OauthProviders.value(remoteUser.getProvider()).getName();
		} else {
			result = "false";
		}
		try {
			json.put("result", result);
			json.put("previousAvatar", URLEncoder.encode(previousAvatar, "UTF-8"));
			json.put("previousNickname", URLEncoder.encode(previousNickname, "UTF-8"));
			json.put("comFrom", URLEncoder.encode(comFrom, "UTF-8"));
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			ResponseEntity<String> re = new ResponseEntity<String>(json.toString(), HttpStatus.OK);
			return re;
		}
	}

	/**
	 * 关联成功以后跳转页面
	 */
	@RequestMapping(value = "connectionSucess")
	public String connectionSucess(Model model, String previousAvatar, String previousNickname, String comFrom,
			HttpServletRequest request, HttpServletResponse response) {
		SavedRequest savedRequest = WebUtils.getSavedRequest(request);
		if (savedRequest != null)
			try {
				WebUtils.redirectToSavedRequest(request, response, savedRequest.getRequestURI());
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		model.addAttribute("previousAvatar", previousAvatar);
		model.addAttribute("previousNickname", previousNickname);
		model.addAttribute("targetUser", getCurrentUser());
		model.addAttribute("comFrom", comFrom);
		model.addAttribute("subsystemList", subsystemService.findByEnabledTrue());
		return "/user/connection/connectUser";
	}

	/**
	 * 关联user和第三方账号
	 */
	@RequestMapping(value = "register")
	public ResponseEntity<String> register(User user, String password, String registerType) {
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("success", false);
		if (registerType.equals("email")) {
			user.setPhone(null);
			// 检查email格式是否正确
			if (!ValidateUtil.CheckEmail(user.getEmail())) {
				rs.put("email", "邮箱格式不正确");
				return new ResponseEntity<String>(new JSONObject(rs).toString(), HttpStatus.OK);
			}
		} else {
			user.setEmail(null);
			if (!ValidateUtil.CheckPhone(user.getPhone())) {
				rs.put("phone", "手机格式不正确");
				return new ResponseEntity<String>(new JSONObject(rs).toString(), HttpStatus.OK);
			}
		}

		// 检查用户名格式
		String username = user.getUsername().trim();
		if (!ValidateUtil.CheckUsername(username)) {
			rs.put("username", "用户名格式不正确");
			return new ResponseEntity<String>(new JSONObject(rs).toString(), HttpStatus.OK);
		}

		// 敏感词检查
		SensitivewordUtil swUtil = new SensitivewordUtil();
		Set<String> keyWordSet;
		try {
			keyWordSet = sensitiveWordservice.readSensitiveWordTable();
			swUtil.addSensitiveWordToHashMap(keyWordSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (swUtil.isContaintSensitiveWord(user.getUsername(), 2)) {
			rs.put("username", "用户名不能包含敏感词");
			return new ResponseEntity<String>(new JSONObject(rs).toString(), HttpStatus.OK);
		}

		/*user.setNickname(user.getUsername());
		user.setRegisterDate(Clock.DEFAULT.getCurrentDate());
		user = userService.save(user);
		principalService.savePrincipal(user, password);*/
		if(org.springframework.util.StringUtils.isEmpty(user.getUsername())){
			user.setUsername("WEB USER"+user.getId());
			user.setNickname("WEB USER"+user.getId());
		} else{
			user.setNickname(user.getUsername());
		}
		try {
			org.json.JSONObject json = userService.synRegistToMember(user, password, "1");
			if(!org.apache.commons.lang3.StringUtils.isEmpty(json.getString("code")) && json.getString("code").equals("0")){
                rs.put("username", json.getString("msg"));
                return new ResponseEntity<String>(new JSONObject(rs).toString(), HttpStatus.OK);
            }
		} catch (Exception e) {
			e.printStackTrace();
			rs.put("username", "绑定用户失败");
			return new ResponseEntity<String>(new JSONObject(rs).toString(), HttpStatus.OK);
		}
		rs.put("success", true);
		// 注册完之后,将session中的注册码清空
		getSession().setAttribute(registerType + "Code", null);
		return new ResponseEntity<String>(new JSONObject(rs).toString(), HttpStatus.OK);
	}

	/**
	 * 跳过关联，则将自动生成的user、remoteUser、binding保存进数据库
	 * 
	 * @return
	 */
	@RequestMapping(value = "generateUser")
	public String generateUser(Model model,User guser, String password_re, HttpServletRequest request,
			HttpServletResponse response) {
		LoginInfo loginInfo = getLoginInfo();
		RemoteUser remoteUser = getLoginInfo().getRemoteUser();
		UserOauthBinding binding = userService.findBinding(remoteUser.getUid(), remoteUser.getProvider());
		// 防止用户重复操作
		if (binding == null) {
			// 从前台得到的password加密后
			LocalPrincipal localPrincipal = principalService.findPrincipal(guser.getUsername(), IdentityType.USERNAME);
			if (localPrincipal != null) {
				String passwordEntrypt = principalService.entryptPassword(password_re, localPrincipal.getSalt());
				if (StringUtils.isNotEmpty(passwordEntrypt) && passwordEntrypt.equals(localPrincipal.getPassword())) {
					User existedUsername = userService.findByUsername(guser.getUsername());
					User user = null;
					if (existedUsername != null) {
						user = userService.doUserOauthBinding(remoteUser, existedUsername, password_re);
						loginInfo.setNickname(user.getNickname());
						loginInfo.setUsername(user.getUsername());
						loginInfo.setUser(user);
						loginInfo.setUserId(user.getId());
					}

					//判断是否调用会员中心接口
					if(SystemConfigHolder.getConfig("if_to_amuc").equals("1")){
						try {
							if (user.getUid() == null) {
								org.json.JSONObject json = userService.synRegistToMember(user, password_re, "1");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		SavedRequest savedRequest = WebUtils.getSavedRequest(request);
		if (savedRequest != null)
			try {
				WebUtils.redirectToSavedRequest(request, response, savedRequest.getRequestURI());
				return null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		model.addAttribute("previousAvatar", remoteUser.getAvatarSmall());
		model.addAttribute("previousNickname", remoteUser.getNickname());
		model.addAttribute("targetUser", getCurrentUser());
		model.addAttribute("comFrom", OauthProviders.value(remoteUser.getProvider()).getName());
		model.addAttribute("subsystemList", subsystemService.findByEnabledTrue());
		return "/user/connection/connectUser";
	}

	/**
	 * 进入账号绑定的页面
	 */
	@RequestMapping(value = "initBind")
	public String initBind(Model model, boolean beBounded, boolean doBounded, String nickname, String provider) {
		userService.initBind(model, getCurrentUserId());
		if (model.asMap().get("bindMap") == null || getCurrentUser().getProvider()!=null) {
			return "/user/connection/bindReguser";
		}
		model.addAttribute("doBounded", doBounded);
		model.addAttribute("beBounded", beBounded);
		model.addAttribute("nickname", nickname);
		model.addAttribute("provider", provider);
		return "/user/connection/bindAccount";
	}

	/**
	 * 再 绑定一个媒体账号
	 */
	@RequestMapping(value = "create")
	public String newBinding(Model model, ServletRequest request) {

		User user = getCurrentUser();
		RemoteUser remoteUser = OauthClientManager.getRemoteUser(request);
		UserOauthBinding findBinding = userService.findBinding(remoteUser.getUid(), remoteUser.getProvider());

		if (findBinding == null) {

			userService.doUserOauthBinding(remoteUser, user, null);
			return "redirect:/user/connection/initBind?doBounded=true&nickname="
					+ Encodes.urlEncode(remoteUser.getNickname());
		} else {
			return "redirect:/user/connection/initBind?beBounded=true&nickname="
					+ Encodes.urlEncode(remoteUser.getNickname()) + "&provider=" + remoteUser.getProvider();
		}
	}

	/**
	 * 解除绑定
	 */
	@RequestMapping(value = "unBind")
	public String unBind(long bindingId) {

		userService.unBind(bindingId);
		return "redirect:/user/connection/initBind";
	}

	/**
	 * 关联user和第三方账号
	 */
	@RequestMapping(value = "isBinded")
	public ResponseEntity<String> isBinded(HttpServletRequest request, HttpServletResponse response) {
		UserOauthBinding binding = userService.findBinding(request.getParameter("oauthUid"), request.getParameter("provider"));
		Map<String, Object> rs = new HashMap<String, Object>();
		rs.put("code", binding != null ? true : false);
		return new ResponseEntity<String>(new com.founder.sso.util.json.JSONObject(rs).toString(), HttpStatus.OK);
	}

	/**
	 * 解除绑定
	 */
	@RequestMapping(value = "doBindThirdAccount")
	public String doBindThirdAccount(HttpServletRequest request, HttpServletResponse response) {
		try {
			LoginInfo loginInfo = getLoginInfo();
			User user = loginInfo.getUser();
			if (loginInfo == null) {
				System.out.println("============= ConnectionController getLoginInfo null");
			}
			if (user.getUid() == null || user.getUid() == 0) {
				try {
					org.json.JSONObject json = userService.synRegistToMember(user, "isnull", null);
				} catch (Exception e) {
					System.out.println("------------- 第三方登录成功，已注册前台用户，会员同步注册失败");
					e.printStackTrace();
				}
			}
			userService.bindThirAccount(loginInfo, request.getParameter("provider"), request.getParameter("userID"), request.getParameter("userName"));
		} catch (Exception e) {
			System.out.println("============= ConnectionController doBindThirdAccount fail");
			e.printStackTrace();
		}
		return "redirect:/user/connection/initBind";
	}
}
