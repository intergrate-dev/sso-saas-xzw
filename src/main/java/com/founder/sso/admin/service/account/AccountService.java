package com.founder.sso.admin.service.account;

import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.sso.admin.dao.AdminAccountDao;
import com.founder.sso.admin.entity.AdminAccount;
import com.founder.sso.admin.utils.Digests;
import com.founder.sso.admin.utils.Encodes;
import com.google.common.collect.ImmutableList;

@Service
public class AccountService {
    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 2;
    //盐值的长度
    private static final int SALT_SIZE = 8;
    //默认密码
    private static final String DEFAULT_PASSWORD = "123456";
    //默认状态是否是正常状态
    private static final boolean IS_ENABLED = true;
    //默认的PERMISSIONS
    private static final String PERMISSIONS = ",";
    
    private AdminAccountDao accountDao;
    
    /**
     * 查询所有管理员
     * 
     * @return
     */
    public List<AdminAccount> findAll() {
        return ImmutableList.copyOf(accountDao.findAll());
    }
    /**
     * 查询所有管理员
     * @return
     */
    public AdminAccount findById(Long id) {
    	return accountDao.findOne(id);
    }

    /**
     * 通过用户名去查询一个管理员
     * 
     * @param loginanme
     * @return 相应管理员对象 不存在返回null
     */
    public AdminAccount findByLoginanme(String loginname) {
        return accountDao.findByLoginname(loginname);
    }

    /**
     * 更新用户信息 如果更新超级管理员的信息需要超级管理员身份
     * 
     * @param account 待更新的用户信息封装
     * @return 更新后的用户信息
     */
    public AdminAccount updateInfo(AdminAccount account) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 新建/更新管理员账号
     * @return 保存后 的管理员
     */
    public AdminAccount updateAdmin(AdminAccount account) {
    	//1.如果没有id，说明是新建的管理员账号
    	if(StringUtils.isEmpty(account.getId())){
    		//设置默认密码123456
    		account.setPlainPassword(DEFAULT_PASSWORD);
    		entryptPassword(account);
    		//是否是正常状态
    		account.setEnabled(IS_ENABLED);
    		account.setPermissions(PERMISSIONS);
    		return accountDao.save(account);
    	
    	//2.有id，说明是修改管理员账号
    	}else {
    		//得到修改之前的对象
    		AdminAccount primaryAccount = findById(account.getId());
    		//2.1修改超级管理员时，只能修改电话、邮箱、密码
    		if(account.isSuperAdmin()){
    			primaryAccount.setEmail(account.getEmail());
    			primaryAccount.setMobile(account.getMobile());
    			return accountDao.save(primaryAccount);
    		}else {
	    		account.setLoginname(primaryAccount.getLoginname());
	    		account.setPassword(primaryAccount.getPassword());
	    		account.setSlat(primaryAccount.getSlat());
	    		account.setEnabled(primaryAccount.isEnabled());
	    		account.setPermissions(primaryAccount.getPermissions());
	    		return accountDao.save(account);
    		}
    	}
    }

    /**
     * 删除一个管理员账户
     * 
     * @param targetId
     */
    public void deleteAdmin(long targetId) {
    	accountDao.delete(targetId);
    }

    /**
     * 冻结管理员账号 目标对象是超级管理员需要超级管理员身份
     */
    public void disableAdmin(Long targetId) {
        //TODO check last enabled SuperAdmin
        AdminAccount targetAccount = accountDao.findOne(targetId);
        targetAccount.setEnabled(false);
        accountDao.save(targetAccount);
    }

    /**
     * 激活管理员账号 如果更新超级管理员的信息需要超级管理员身份
     */
    public void enableAdmin(Long targetId) {
        //TODO SuperAdmin Check
        AdminAccount targetAdmin = accountDao.findOne(targetId);
        targetAdmin.setEnabled(true);
        accountDao.save(targetAdmin);
    }

    /**
     * 查询指定ID的管理员是否是超级管理员
     * 
     * @param id
     * @return
     */
    public boolean isSuperAdmin(long id) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 创建一个超级管理员，只有超级管理员可以创建超级管理员
     * 
     * @return
     */
    public AdminAccount createSuperAdmin() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 删除一个超级管理员
     * 
     */
    public void deleteSuperAdmin(String targetId) {
        // TODO Auto-generated method stub

    }
    /**
     * 检测是否为最后一个激活状态的超级用户
     * 至少保证一个超级用户以保证系统的安全
     * @param targetId
     * @return
     */
    public boolean isLastEnabledSuperAdmin(String targetId) {
        //TODO fengdd 待实现
        return false;
    }
    
	 /**
     * 重置密码
     */
	public void resetPassword(Long targetId){
		AdminAccount account = findById(targetId);
		account.setPassword(getEntryptPassword(account.getSlat(),DEFAULT_PASSWORD));
		accountDao.save(account);
	}
	
	/**
	 * 修改密码
	 */
	public String updatePassword(Long targetId,String oldPassword,String newPassword){
		AdminAccount account = findById(targetId);
		String salt = account.getSlat();
		//从前台来的密码加密之后
		String passwordFromUser = getEntryptPassword(salt,oldPassword);
		//从前台得到的密码加密之后与 数据库中查询的一致
		if(passwordFromUser.equals(account.getPassword())){
			account.setPassword(getEntryptPassword(salt,newPassword));
			accountDao.save(account);
			return "success";
		}else {
			return "old_password_is_wrong"; 
		}
		
	}
	
	/**
	 * 设定安全的密码，生成随机的salt并经过2次 sha-1 hash
	 */
	private void entryptPassword(AdminAccount account) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
		account.setSlat(Encodes.encodeHex(salt));
		byte[] hashPassword = Digests.sha1(account.getPlainPassword().getBytes(), salt, HASH_INTERATIONS);
		account.setPassword(Encodes.encodeHex(hashPassword));
	}
	
	/**
	 * 根据salt和明文密码，得到加密之后的密码
	 */
	private String getEntryptPassword(String salt,String password) {
		byte[] hashPassword = Digests.sha1(password.getBytes(),Encodes.decodeHex(salt), HASH_INTERATIONS);
		return Encodes.encodeHex(hashPassword);
	}
    @Autowired
    public void setAccountDao(AdminAccountDao accountDao) {
        this.accountDao = accountDao;
    }
    

}
