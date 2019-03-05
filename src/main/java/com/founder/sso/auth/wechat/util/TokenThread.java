package com.founder.sso.auth.wechat.util;

import com.founder.sso.auth.wechat.entity.AccessToken;
import com.founder.sso.util.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时获取微信access_token的线程
 * 
 * @author hanpt
 */
public class TokenThread implements Runnable {
	private static Logger log = LoggerFactory.getLogger(TokenThread.class);
	// 第三方用户唯一凭证密钥
	public static AccessToken accessToken = null;

	@Override
	public void run() {
		while (true) {
			try {
				try {
					accessToken = WechatUtil.getAccessToken(null, null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (null != accessToken) {
					log.info("微信获取access_token成功，有效时长{}秒 token:{}", accessToken.getExpiresIn(), accessToken.getToken());
					// 休眠7000秒
					Thread.sleep((accessToken.getExpiresIn() - 200) * 1000);
				} else {
					log.info("微信获取access_token失败，60秒后再获取");
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