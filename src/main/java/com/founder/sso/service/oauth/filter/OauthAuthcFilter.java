//package com.founder.sso.service.oauth.filter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//
//import org.apache.shiro.web.servlet.OncePerRequestFilter;
//import org.apache.shiro.web.util.WebUtils;
//
//import com.founder.sso.service.oauth.OauthClientManager;
//import com.founder.sso.service.oauth.entity.OauthErrorMsg;
//import com.founder.sso.util.WebUtil;
//TODO fengdd 将功能从Shiro的Filter框架中剥离出来
//public class OauthAuthcFilter extends OncePerRequestFilter implements Filter {
//    public static final String DEFAULT_ERROR_KEY_ATTRIBUTE_NAME = "shiroLoginFailure";
//    public static final String DEFAULT_LOGIN_URL = "user/login";
//    // TODO fengdd 最终跳转到用户家园
//    public static final String DEFAULT_SUCCESS_URL = "user/profile";
//
//    @Override
//    protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
//        Map<String, List<String>> mappedParams = WebUtil.preProcessParams(request);
//        // 开始处理回调 完成请求的合法性效验和错误信息效验
//        OauthErrorMsg errorMsg = OauthClientManager.detectErrors(mappedParams);
//        if (errorMsg == null) {
//            // 请求合法继续处理
//            boolean ok = executeLogin(request, response);
//            if (ok == true) {
//                WebUtils.redirectToSavedRequest(request, response, DEFAULT_SUCCESS_URL);
//            } else {
//                forwardFailurePage(request, response, "第三方授权异常-请重试");
//            }
//        } else {
//            forwardFailurePage(request, response, errorMsg.getMessage());
//        }
//    }
//
//    private boolean executeLogin(ServletRequest request, ServletResponse response) {
//        doOauthProcess();
//        doLogin();
//        return true;
//    }
//
//    private void doLogin() {
//    }
//
//    private void doOauthProcess() {
//        // TODO Auto-generated method stub
//
//    }
//
//    protected void forwardFailurePage(ServletRequest request, ServletResponse response, String errorMsg) {
//        request.setAttribute(DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, errorMsg);
//        try {
//            request.getRequestDispatcher(DEFAULT_LOGIN_URL).forward(request, response);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//}
