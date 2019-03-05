package com.founder.sso.test;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

/**
 * Created by yuan-pc on 2018/12/26.
 */
public class TokenTest {
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/androidpublisher");

    public static void main(String[] args) {
        //webpage client
        String filePath = "E:/xxxxx/coding/xy-branch/xzw-projects/sso-saas-xzw/src/main/resources/client_secret_331174060034.json";
        //service account--create secret
        String filePath1 = "E:/xxxxx/coding/xy-branch/xzw-projects/sso-saas-xzw/src/main/resources/newsapp-account.json";
        try {
            // 根据Service Account文件构造认证实例 GoogleCredential
            GoogleCredential credential = GoogleCredential
                    .fromStream(new FileInputStream(filePath))// 加载服务帐户认证文件
                    .createScoped(SCOPES);

            // 刷新token
            credential.refreshToken();

            // 获取token
            System.out.println("google accessToken: " + credential.getAccessToken());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
