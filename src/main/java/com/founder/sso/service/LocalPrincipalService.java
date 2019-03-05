package com.founder.sso.service;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.sso.admin.utils.Digests;
import com.founder.sso.admin.utils.Encodes;
import com.founder.sso.dao.LocalPrincipalDao;
import com.founder.sso.dao.UserDao;
import com.founder.sso.entity.LocalPrincipal;
import com.founder.sso.entity.User;

@Service
@Transactional
public class LocalPrincipalService {
    public static final String DEFAULT_HASH_ALGORITHM = "SHA-1";
    // 加密算法的次数
    public static final int DEFAULT_HASH_ITERATIONS = 2;
    // 盐值的长度
    public static final int DEFAULT_SALT_SIZE = 8;

    @Autowired
    LocalPrincipalDao dao;
    
    @Autowired
    UserDao userDao;

    public LocalPrincipal getByUserId(Long userId) {
        return dao.findByUserId(userId);
    }

    /**
     * 根据username/phone/email得到principal
     * @param token
     * @return
     */
    public LocalPrincipal findPrincipal(String token) {
    	//在linux下面，不支持LocalPrincipalDao中的findByIdentities()方法
        List<LocalPrincipal> principalList = dao.findByUsernameOrEmailOrPhone(token,token,token);
        if(principalList.size()>0){
        	return principalList.get(0);
        }
        return null;
    }
    /**
     * 根据username/phone/email、password得到principal
     * @return
     */
    public LocalPrincipal findPrincipal(String identity, String password) {
    	List<LocalPrincipal> principalList = findByUsernameOrEmailOrPhone(identity);
    	for (LocalPrincipal localPrincipal : principalList) {
			String passwordEncrypted = entryptPassword(password, localPrincipal.getSalt());
			if(passwordEncrypted.equals(localPrincipal.getPassword())){
				return localPrincipal;
			}
		}
    	return null;
    }
    
    /**
     * 根据字段查询用户
     */
    public List<LocalPrincipal> findByUsernameOrEmailOrPhone(String value) {
    	return dao.findByUsernameOrEmailOrPhone(value, value, value);
    }

    public LocalPrincipal findPrincipal(String token, LocalPrincipal.IdentityType type) {
        LocalPrincipal principal = null;
        switch (type) {
        case USERNAME:
            principal = dao.findByUsername(token);
            break;
        case PHONE:
            principal = dao.findByPhone(token);
            break;
        case EMAIL:
            principal = dao.findByEmail(token);
            break;
        default:
            break;
        }
        return principal;
    }

    public void updatePassword(LocalPrincipal principal, String currentPassword, String newPassword) throws PrincipalException {
        if (isPasswordMatch(principal, currentPassword)) {
            principal.setPassword(entryptPassword(newPassword, principal.getSalt()));
            dao.save(principal);
        } else {
            throw new PrincipalException("原密码不匹配");
        }
    }

    //修改密码
    public void resetPassword(LocalPrincipal principal, String newPassword) {
    	if(principal!=null){
    		principal.setPassword(entryptPassword(newPassword, principal.getSalt()));
    		dao.save(principal);
    	}
    }
    
    
    //根据phone修改密码
    public void resetPasswordByPhone(String phone, String newPassword) {
    	LocalPrincipal principal = dao.findByPhone(phone);
    	if(principal!=null){
    		resetPassword(principal,newPassword);
    	}
    }
    
    //根据email修改密码
    public void resetPasswordByEmail(String email, String newPassword) {
    	LocalPrincipal principal = dao.findByEmail(email);
    	if(principal!=null){
    		resetPassword(principal,newPassword);
    	}
    }
    
    

    /**
     * 根据明文password和salt生成密文password
     * @param plainPwd
     * @param salt
     * @return
     */
    public String entryptPassword(String plainPwd, String salt) {
        return entryptPassword(plainPwd, salt, DEFAULT_HASH_ALGORITHM, DEFAULT_HASH_ITERATIONS);
    }

    public String entryptPassword(String plainPwd, String salt, String ALGORITHM, int iterations) {
        return Encodes.encodeHex(Digests.sha1(plainPwd.getBytes(), Encodes.decodeHex(salt), iterations));
    }

    public boolean isPasswordMatch(LocalPrincipal principal, String credential) {
        String salt = principal.getSalt();
        String hashedCredential = entryptPassword(credential, salt);
        if (hashedCredential.equals(principal.getPassword())) {
            return true;
        }
        return false;
    }

    /**
     * 注册、修改用户资料时，都需要同步principal，分为
     * 1、注册时，principal为null
     * 2.1、完善资料，principal为null
     * 2.2、完善资料，principal不为null
     * @param user
     * @param plainPwd
     * @return
     */
    public LocalPrincipal savePrincipal(User user, String plainPwd) {
        checkArgument(user != null && user.getId() != null, "User Must set And The User Object Must have a id");
        Map<String, String> identities = user.getIdentities();
        
        LocalPrincipal principal = getByUserId(user.getId());
        if(principal == null){
        	principal = new LocalPrincipal();
        }
        
        if(StringUtils.isNotBlank(plainPwd)){
        	String salt = Encodes.encodeHex(Digests.generateSalt(DEFAULT_SALT_SIZE));
        	principal.setPassword(entryptPassword(plainPwd, salt));
        	principal.setSalt(salt);
        }
        principal.setUser(user);
        for (Map.Entry<String, String> entry : identities.entrySet()) {
            if ("username".equals(entry.getKey())) {
                principal.setUsername(entry.getValue());
                continue;
            }
            if ("phone".equals(entry.getKey())) {
                principal.setPhone(entry.getValue());
                continue;
            }
            if ("email".equals(entry.getKey())) {
                principal.setEmail(entry.getValue());
            }
        }
        LocalPrincipal persisted = dao.save(principal);
        user.setLocalPrincipal(principal);
        userDao.save(user);
        return persisted;
    }

}
