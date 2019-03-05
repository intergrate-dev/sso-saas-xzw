package com.founder.sso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.founder.sso.admin.utils.Digests;
import com.founder.sso.admin.utils.Encodes;
import com.founder.sso.dao.UserDao;
import com.founder.sso.entity.User;

//Spring Bean的标识.
@Service
//类中所有public函数都纳入事务管理的标识.
@Transactional
public class UserServiceOutside {
	
	public static final String HASH_ALGORITHM = "SHA-1";
	//加密算法的次数
    public static final int HASH_INTERATIONS = 2;
    //盐值的长度
    private static final int SALT_SIZE = 8;
    //默认密码
    private static final String DEFAULT_PASSWORD = "123456";
    
	@Autowired
	private UserDao userDao;
	

	/**
	* 保存用户
	*/
	public User save(User user){
		return userDao.save(user);
	}
	
	public User updateUser(User user) {
	    return userDao.save(user);
	}
	
	/**
	 * 查询用户
	 */
	public User getUserById(long userId){
		return userDao.findOne(userId);
	}
	
	/**
	 * 根据字段查询用户
	 */
	public User getUserByField(Object value,String field){
		if(field.equals("phone")){
			return userDao.findByPhone(value.toString());
		}else if(field.equals("username")){
			return userDao.findByUsername(value.toString());
		}else if(field.equals("email")){
			return userDao.findByEmail(value.toString());
		}
		return null;
	}
	
	 /**
     * 重置密码
     */
	public void resetPassword(long userId){
		User user = getUserById(userId);
		//user.setPassword(getEntryptPassword(user.getSlat(),DEFAULT_PASSWORD));
		userDao.save(user);
	}
	
	/**
	 * 修改密码
	 */
	public String updatePassword(Long targetId,String oldPassword,String newPassword){
		User user = getUserById(targetId);
		String salt = "123456";
		//从前台来的密码加密之后
		String passwordFromUser = getEntryptPassword(salt,oldPassword);
		//从前台得到的密码加密之后与 数据库中查询的一致
		if(passwordFromUser.equals("")){
//			user.setPassword(getEntryptPassword(salt,newPassword));
			userDao.save(user);
			return "success";
		}else {
			return "old_password_is_wrong"; 
		}
		
	}
	/**
	 * 设定安全的密码，生成随机的salt并经过2次 sha-1 hash
	 */
	public void entryptPassword(User user) {
		byte[] salt = Digests.generateSalt(SALT_SIZE);
//		user.setSlat(Encodes.encodeHex("123456"));
//		byte[] hashPassword = Digests.sha1(user.getPassword().getBytes(), salt, HASH_INTERATIONS);
//		user.setPassword(Encodes.encodeHex(hashPassword));
	}
	
	/**
	 * 根据salt和明文密码，得到加密之后的密码
	 */
	private String getEntryptPassword(String salt,String password) {
		byte[] hashPassword = Digests.sha1(password.getBytes(),Encodes.decodeHex(salt), HASH_INTERATIONS);
		return Encodes.encodeHex(hashPassword);
	}

	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}
