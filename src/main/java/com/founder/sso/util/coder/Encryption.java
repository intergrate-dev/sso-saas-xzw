package com.founder.sso.util.coder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.springframework.util.Base64Utils;

public class Encryption {
	
	static String key;
	static String iv;
	static Cipher cipher;
	static String ticketUrl;
	
	/**
	 * 初始化解密器
	 * @param key
	 * 	秘钥
	 * @param url
	 * 	进行ticket有效性验证的url
	 * @throws Exception
	 */
	static void init(String keyCode,String url){
		ticketUrl = url;
		if(keyCode.length()<16)
			throw new RuntimeException("key length must largger then 16.");
		key = keyCode.substring(0,16);
		iv = keyCode.substring(keyCode.length()-16);
		SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
		try {
			cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}

	/**
	 * aes对称解密
	 * @param data
	 * 	加密的数据
	 * @return
	 * 	使用初始秘钥进行解密的数据
	 * @throws Exception
	 */
    public static String descrypt(String data) throws Exception {
        return new String(cipher.doFinal(Base64Utils.encode(data.getBytes())));
    }
    
	/**
	 * 判断ticket是否有效
	 * @param ticket
	 * 	需要验证的ticket
	 * @return
	 * 	如果有效返回true，否则返回false
	 */
	public boolean exists(String ticket){
		String content;
		try {
			content = Request.Post(ticketUrl)
				    .bodyForm(
				    		Form.form()
				    			.add("ticket",  URLEncoder.encode(ticket,"UTF-8"))
				    		.build())
				    .execute().returnContent().asString();
			return "TRUE".equalsIgnoreCase(content);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
