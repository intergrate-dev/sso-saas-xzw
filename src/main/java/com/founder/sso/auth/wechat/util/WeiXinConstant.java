package com.founder.sso.auth.wechat.util;

public class WeiXinConstant {

	public final static String access_token_url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	public final static String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
	public final static String url_send = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
	public final static String menu_create_url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	
	//网页授权获取用户信息：1.请求授权，获取code
	public static String  authorization_code_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
	//网页授权获取用户信息：2.通过code换取网页授权access_token
	public static String  authorization_access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=APPSECRET&code=CODE&grant_type=authorization_code";
	//网页授权获取用户信息：3.拉取用户信息(需scope为 snsapi_userinfo)
	public static String  authorization_userinfo_url = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	//直接获取用户信息
	public static String  userinfo_url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	//多媒体下载接口
	/*public static String download_media="http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=ACCESS_TOKEN&media_id=MEDIA_ID";
	//腾讯QQ登陆-第一步：获取Authorization Code
	public static String authorizationCodeForQQ="https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&state=test";
	//腾讯QQ登陆-第二步：通过Authorization Code获取Access Token
	public static String getAccessTokenForQQ="https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=CLIENT_ID&client_secret=CLIENT_SECRET&code=CODE&redirect_uri=REDIRECT_URI";
	//腾讯QQ登陆-第三步：通过Access Token获取用户OpenID
	public static String getOpenIDForQQ="https://graph.qq.com/oauth2.0/me?access_token=ACCESS_TOKEN";
	//腾讯QQ登陆-第四步：获取网站登录用户信息，目前可获取用户在QQ空间的昵称、头像信息及黄钻信息。
	public static String getQQInfoByOpenID="https://graph.qq.com/user/get_user_info?oauth_consumer_key=OAUTH_CONSUMER_KEY&access_token=ACCESS_TOKEN&openid=OPENID&format=json";
	*/
	/*public static final String SCOPE_SNSAPI_BASE  = "snsapi_base";
	public static final String SCOPE_SNSAPI_USERINFO  = "snsapi_userinfo";
	public final static String MCH_ID = "1256531401";//商户号
	public final static String API_KEY = "gbyscbs20160707wxdzggbdscbs27756";//API密钥
	public final static String SIGN_TYPE = "MD5";//签名加密方式
	public final static String TOKEN = "weixinCourse";//服务号的配置token
	public final static String appKeyForQQ="54a1284f0edf36441cfd5fec2b3326d8";//腾讯QQ登陆appKey
	public final static String redirect_uri="http%3A%2F%2Fweixin.crtp.com.cn%2fmember%2finfo.do";//腾讯QQ登陆后跳转地址
	public final static String appKeyForSina="2726624704";//新浪微博登录appKey
	public final static String appSecretForSina="33dfc5f33f453ac247410bb96227933f";//新浪微博登录appSecret*/
	

	/*//微信支付统一接口的回调action
	 public final static String NOTIFY_URL = "http://weixin.crtp.com.cn/order/paySuccess.do";
	//微信支付成功支付后跳转的地址
	 public final static String SUCCESS_URL = "http://weixin.crtp.com.cn/order/toPaySuccess.do";


	 //获取token接口(GET)
	 public final static String TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	 //刷新access_token接口（GET）
	 public final static String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
	// 菜单创建接口（POST）
	 public final static String MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	// 菜单查询（GET）
	 public final static String MENU_GET_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	// 菜单删除（GET）
	public final static String MENU_DELETE_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";

	//微信支付统一接口(POST)
	public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	//微信退款接口(POST)
	public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	//订单查询接口(POST)
	public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
	//关闭订单接口(POST)
	public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
	//退款查询接口(POST)
	public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
	//对账单接口(POST)
	public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
	//短链接转换接口(POST)
	public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
	//接口调用上报接口(POST)
	public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";
	//本机存放的PKCS12证书文件
	public final static String P12_PATH = "D:/apiclient_cert.p12";
	//微信导出地址
	public final static String SERVER_HOST = "edu.cciph.com.cn";*/
}
