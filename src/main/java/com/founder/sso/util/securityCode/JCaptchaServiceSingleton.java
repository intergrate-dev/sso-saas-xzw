/*
package com.founder.sso.util.securityCode;
   
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore; 
import com.octo.captcha.service.image.ImageCaptchaService; 

public class JCaptchaServiceSingleton { 
    private static CustomGenericManageableCaptchaService imageCaptchaService = new CustomGenericManageableCaptchaService( 
            new FastHashMapCaptchaStore(), new GMailEngine(), 180, 100000, 
            75000); 
   
    public static ImageCaptchaService getInstance() { 
        return imageCaptchaService; 
    } 
    
    public static void removeCaptcha(String sessionId){
    	imageCaptchaService.removeCaptcha(sessionId);
    }
}*/
