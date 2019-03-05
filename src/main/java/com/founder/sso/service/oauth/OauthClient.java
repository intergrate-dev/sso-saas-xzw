package com.founder.sso.service.oauth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.founder.sso.dao.OauthClientConfigDao;
import com.founder.sso.dao.UserDao;
import com.founder.sso.entity.User;
import com.founder.sso.service.UserService;
import com.founder.sso.service.oauth.dao.UserOauthBindingDao;
import com.founder.sso.service.oauth.entity.OauthAccessToken;
import com.founder.sso.service.oauth.entity.OauthClientConfig;
import com.founder.sso.service.oauth.entity.OauthErrorMsg;
import com.founder.sso.service.oauth.entity.OauthProviders;
import com.founder.sso.service.oauth.entity.RemoteUser;
import com.founder.sso.service.oauth.entity.UserOauthBinding;
import com.founder.sso.util.Clock;

/**
 * OauthClient的抽象基类
 * 完成辅助不同供应的Client完成以下操作
 * 从数据库加载响应Client基础配置信息
 * 向{@link OauthClientManager}中注册本Client
 * 
 * @author fengdd
 * 
 */
@Component
public abstract class OauthClient {

    protected OauthClientConfig config;
    //TODO 完成流程debug日志
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private OauthClientConfigDao configDao;
    @Autowired
    private UserOauthBindingDao bindingDao;
    @Autowired
    private UserDao userDao;
    protected static final String redirect_uri_new_binding = "";

    /**
     * 从数据库查找本Client的基本配置信息
     * 如果找不到根据实例信息创建一个默认配置并保存到数据库
     */
    @SuppressWarnings("unused")
    @PostConstruct
    private void initOauthClient() {
        // 子类是否合法提供provider标示
        checkArgument(StringUtils.isNotBlank(getProvider()), "OauthClient.getProvider() returns Blank!");
        config = getProviderConfig(getProvider());
        init(config);
        //把所有的配置都加载进去,在取url的时候判断是否是enabled
        OauthClientManager.registerClient(getProvider(), this);
    }

    private OauthClientConfig getProviderConfig(String provider) {
        OauthClientConfig cfg = configDao.findByProvider(provider);
        // 如果数据库中找不到尝试通过Client子类提供的信息进行构建并持久化到数据库
        if (cfg == null) {
            cfg = getInitConfig();
            checkNotNull(cfg, " Cat NOT find or create a config for the Oauth Client implementation [" + this.getClass().getName() + "]");
            configDao.save(cfg);
        }
        return cfg;
    }

    private OauthClientConfig getInitConfig() {
        checkArgument(StringUtils.isNotBlank(getProvider()), "OauthClient.getProvider() returns Blank!");
        OauthClientConfig cfg = new OauthClientConfig();
        cfg.setProviderName(getProviderName());
        cfg.setProvider(getProvider());
        cfg.setEnabled(false);
        return cfg;
    }

    /**
     * 子类自动注册{@link OauthClientManager#registerClient(String, OauthClient) }
     * 之前对自身进行一些初始化操作</p>
     * 子类可以覆写此方法完成一些初始化任务
     * 
     * @param cfg 从数据库加载的配置信息
     */
    protected void init(OauthClientConfig cfg) {

    }

    /**
     * 获取Oauth供应商的标示 对应于{@link OauthProviders#getValue()}返回的数值</p>
     * 此方法必须返回非空标示用于
     * {@link OauthClientManager#registerClient(String, OauthClient)}
     * 方法的注册key值和提取数据库中配置信息的键值
     * 
     * @return 此Client对应的供应商的标示
     */
    public abstract String getProvider();

    /**
     * 获取Oauth供应商的显示名 对应于{@link OauthProviders#getName()}返回的数值</p>
     * 
     * @return 此Client对应的供应商的显示名
     */
    public abstract String getProviderName();

    /**
     * 获取Oauth认证地址
     * 地址中已经完善自身的配置信息及标示
     * 
     * @return Oauth认证地址
     */
    public abstract String getAuthorizeUrl();

    /**
     * 获取指定标示绑定的地址，其实和getAuthorizeUrl()得到的url一样，只是return_uri不一样
     * 
     * @param provider 供应商标示
     * @return 如果不存在此供应商则返回 当前锚点
     */
    public abstract String getBindingUrl(); 


    /**
     * 检测服务商返回的错误信息
     * 
     * @param mappedValues
     * @return
     */
    public abstract OauthErrorMsg detectErrorMsg(ServletRequest request);


    /**
     * 处理用户的授权码
     * 
     * @param mappedValues
     * @return
     */
    public abstract String parseAuthcCode(ServletRequest request);

    /**
     * 处理用户授权Code 返回OauthAccessToken
     * 
     * @param authcCode 授权码
     * @return OauthAccessToken
     * @throws OauthErrorMsg
     */
    public abstract OauthAccessToken exchangeAccessToken(String authcCode) throws OauthErrorMsg;
    
    /**
     * 处理用户授权Code 返回OauthAccessToken 此方法是用来再次绑定第三方账号的
     */
    public abstract OauthAccessToken exchangeAccessTokenForBinding(String authcCode) throws OauthErrorMsg;

    /**
     * 根据AccessToken向供应商拉取远程用户信息
     * 客户端实现在此步骤根据自身需求将信息进行保存
     * 
     * @param accessToken
     * @return 远程用户信息
     */
    public abstract RemoteUser fetchRemoteUser(OauthAccessToken accessToken);

    /**
     *  根据remoteuser得到User
     *  1、如果之前已经绑定，从数据库中查询即可
     *  2、如果之前没有绑定，说明是刚注册的账号，构建出一个user（此时并不保存到数据库）。下一步会提示用户是绑定一个user还是随机生成一个user
     * @return
     */
    public User getLocalUser(RemoteUser ru) {
        User localUser = null;
        UserOauthBinding binding = bindingDao.findByOauthUidAndProvider(ru.getUid(), getProvider());
        if (binding != null) {
        	log.info("binding username is:"+binding.getUser().getUsername());
            return binding.getUser();
        } else {
        	log.info("has not binding username,remote uid is: {}.",ru.getUid());
        	localUser = userDao.findByOauthUidAndProvider(ru.getUid(), getProvider());
        	if(localUser==null){
        		log.info("has not registe remote uid is: {}.",ru.getUid());
        		localUser = userDao.save(buildLocalUser(ru));
        		Subject subject = SecurityUtils.getSubject();
        		 Session session = subject.getSession(false);
        		 if (session != null) {
        			 session.setAttribute("firstLogin",true);
        		}
        	}
            return localUser;
        }

    }

    
    /**
     * 从数据库中查询不到绑定的信息，也就是说此用户是刚注册进来
     * 根据远程用户得到一个本地用户，此用户信息不全
     * @param remoteUser
     * @return
     */
    protected User buildLocalUser(RemoteUser remoteUser) {
    	// 通过UserService创建一个本地用户
    	User user = new User();
    	user.setNickname(remoteUser.getNickname());
    	user.setUsername(remoteUser.convertUserName());
    	user.setActived(true);
    	user.setRegisterDate(Clock.DEFAULT.getCurrentDate());
    	user.setProvider(remoteUser.getProvider());
    	user.setOauthUid(remoteUser.getUid());
    	UserService.generateFace(user, remoteUser.getAvatarLarge());
    	System.out.println("build user as:"+user.getOauthUid()+"\t"+user.getAvatarLarge()+"\t"+user.getNickname());
    	return user;
    }
    
    

    public OauthClientConfig getConfig() {
        return config;
    }

    public void setConfig(OauthClientConfig config) {
        this.config = config;
    }

    public abstract RemoteUser getRemoteUser(ServletRequest request);
}
