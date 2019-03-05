package com.founder.sso.util.securityCode;

import com.founder.redis.JedisClient;
import com.founder.sso.util.SystemConfigHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.*;

/**
 * Created by yuan-pc on 2018/12/10.
 * 图片验证码、邮件验证码、手机号验证码缓存
 */
@Component
public class CacheUtil {

    @Autowired
    JedisClient jedisClient;

    public void cacheCode(final String prefix, final String value, final String code, HttpServletRequest request) {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                String codeTime = SystemConfigHolder.getConfig("codeTime"); //redis中验证码有效期
                jedisClient.set(prefix + value, code, Integer.parseInt(codeTime));
                System.out.println("============= cacheCode into jedis, emial: " + prefix + value + ", code: " + jedisClient.get(prefix + value) + " ======================");
            }
        });

        t.start();
        try {
            t.join(1000);
            if (t.isAlive()) {
                //getSession().setAttribute(prefix+value, code);
                request.getSession().setAttribute(prefix + value, code);
                System.out.println("============ cacheCode into session, emial: " + prefix + value + ", code: " + request.getSession().getAttribute(prefix + value) + " ======================");
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5 * 60 * 1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (request.getSession() != null && request.getSession().getAttribute(prefix + value) != null) {
                            request.getSession().removeAttribute(prefix + value);
                            System.out.println("============= cacheCode remove from session, key: " + prefix + value + " ======================");
                        }
                    }
                }).start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getCachedCode(final String codeName, HttpServletRequest request) {
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
        // Object attribute = getSession().getAttribute(codeName);
        String attribute = (String) request.getSession().getAttribute(codeName);
        //return attribute == null ? null : request.getSession().removeAttribute(codeName).toString();
        if (!StringUtils.isEmpty(attribute)) {
            request.getSession().removeAttribute(codeName);
            System.out.println("============ getCachedCode from session, emial: " + codeName + ", code: " + (String) attribute + " ======================");
        }
        return attribute;
    }
}

