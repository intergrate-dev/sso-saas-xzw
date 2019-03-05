package com.founder.sso.admin.service.account;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.Date;

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

import com.founder.sso.admin.entity.AdminAccount;
import com.founder.sso.admin.utils.Encodes;

public class AdminDbRealm extends AuthorizingRealm {
    @Autowired
    private AccountService accountService;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
        AdminAccount account = accountService.findByLoginanme(token.getUsername());
        if (account != null&&account.isEnabled()) {
            byte[] slat = Encodes.decodeHex(account.getSlat());
            return new SimpleAuthenticationInfo(new LoginInfo(account, token.getHost()), account.getPassword(),
                    ByteSource.Util.bytes(slat), getName());
        }
        return null;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        LoginInfo loginInfo = (LoginInfo) principals.getPrimaryPrincipal();
        AdminAccount account = loginInfo.getAccount();
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(account.getRoleList());
        info.addStringPermissions(account.getPermissionList());
        return info;
    }

    @PostConstruct
    public void initCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(AccountService.HASH_ALGORITHM);
        matcher.setHashIterations(AccountService.HASH_INTERATIONS);
        setCredentialsMatcher(matcher);
    }

    /**
     * 登录信息类用于封装登录时间、IP等信息
     * 
     * 同时直接封装登录名和用户名简化获取操作
     * 
     * @author fengdd
     * 
     */
    public static class LoginInfo implements Serializable{
        private static final long serialVersionUID = -8923280317871304920L;
        private long accountId;
        private String loginname;
        private String realname;
        private Date loginTime;
        private String loginIp;
        private AdminAccount account;

        public LoginInfo(AdminAccount loginAccount, String ip) {
            checkNotNull(loginAccount, "LoginAccount Must NOT be null");
            checkArgument(isNoneEmpty(ip), "LoginIp Must NOT be Blank");
            this.account = loginAccount;
            this.accountId=loginAccount.getId();
            this.loginname = loginAccount.getLoginname();
            this.realname = loginAccount.getRealname();
            this.loginTime = new Date();
            this.loginIp = ip;
        }

        public String getLoginname() {
            return loginname;
        }

        public String getRealname() {
            return realname;
        }

        public Date getLoginTime() {
            return loginTime;
        }

        public String getLoginIp() {
            return loginIp;
        }

        public AdminAccount getAccount() {
            return account;
        }

       public long getAccountId() {
            return accountId;
        }

    }
}
