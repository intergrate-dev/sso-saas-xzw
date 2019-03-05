package com.founder.sso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.founder.sso.dao.UserSubsystemBindDao;
import com.founder.sso.entity.UserSubsystemBind;

//Spring Bean的标识.
@Service
// 类中所有public函数都纳入事务管理的标识.
@Transactional
public class UserSubsystemBindService {

	@Autowired
	private UserSubsystemBindDao userSubsystembinddao;

	/**
	 * 保存userThirdBind
	 */
	public UserSubsystemBind save(UserSubsystemBind userthirdbind) {
		return userSubsystembinddao.save(userthirdbind);
	}

	/**
	 * 根据userid、子系统id查询已绑定的用户记录
	 * @param userid
	 * @param subsystemid
	 * @return
	 */
	public List<UserSubsystemBind> findByUseridAndSubsystemid(int userid, int subsystemid){
		
		return userSubsystembinddao.findByUserIdAndSubsystemId(userid,subsystemid);
	}
	
	/**
	 * 解除绑定
	 * @param userid
	 */
	public void unBindThirdAccount(long userid){
		userSubsystembinddao.delete(userid);
	}
	
}
