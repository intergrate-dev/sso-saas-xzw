package com.founder.sso.auth.wechat.util;

import com.founder.sso.auth.wechat.entity.AccessToken;
import com.founder.sso.auth.wechat.entity.JsapiTicket;
import com.founder.sso.util.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 定时获取微信jsapi_Ticket的线程
 * 
 * @author hanpt
 */
public class JsapiTicketThread implements Runnable {
	private static Logger log = LoggerFactory.getLogger(JsapiTicketThread.class);
	// 第三方用户唯一凭证密钥
	public static JsapiTicket jsapiTicket = null;
	@Override
	public void run() {
		while (true) {
			try {
				AccessToken accessToken = TokenThread.accessToken;
				if(accessToken != null){
					try {
						jsapiTicket = WechatUtil.getJsapiTicket(accessToken);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (null != jsapiTicket) {
						log.info("微信获取jsapi_Ticket成功，有效时长{}秒 ticket:{}", jsapiTicket.getExpiresIn(), jsapiTicket.getTicket());
						// 休眠7000秒
						Thread.sleep((jsapiTicket.getExpiresIn() - 200) * 1000);
					} else {
						// 如果jsapi_Ticket为null，60秒后再获取
						Thread.sleep(60 * 1000);
					}
				}else{
					// 如果access_token为null，60秒后再获取
					Thread.sleep(60 * 1000);
				}
			} catch (InterruptedException e) {
				try {
					Thread.sleep(60 * 1000);
				} catch (InterruptedException e1) {
					log.error("{}", e1);
				}
				log.error("{}", e);
			}
		}
	}
}