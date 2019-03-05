package com.founder.sso.service;

import javax.annotation.PostConstruct;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.founder.sso.admin.utils.Encodes;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.LoginInfo;
import com.founder.sso.entity.User;

public class UserShiroRealm extends AuthorizingRealm {
    @Autowired
    private LocalPrincipalService principalService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        //这里应该根据identity + password去得到principal，排除用户A的username=用户B的email等极端情况
        LocalPrincipal principal = principalService.findPrincipal(token.getUsername(), String.valueOf(token.getPassword()));
        if (principal != null && principal.getUser().isActived()) {
            User user = principal.getUser();
            byte[] slat = Encodes.decodeHex(principal.getSalt());
            return new SimpleAuthenticationInfo(new LoginInfo(user, token.getHost(), LocalPrincipal.LOGIN_TYPE),
                    principal.getPassword(), ByteSource.Util.bytes(slat), getName());
        }
        return null;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //TODO 普通用户的权限和组
        info.addRoles(null);
        return info;
    }

    @PostConstruct
    public void initCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(LocalPrincipalService.DEFAULT_HASH_ALGORITHM);
        matcher.setHashIterations(LocalPrincipalService.DEFAULT_HASH_ITERATIONS);
        setCredentialsMatcher(matcher);
    }

}
