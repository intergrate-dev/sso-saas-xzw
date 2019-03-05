package com.founder.sso.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.founder.sso.entity.User;
import com.founder.sso.service.oauth.entity.UserOauthBinding;

public interface UserDao extends PagingAndSortingRepository<User, Long> , JpaSpecificationExecutor<User>{
	User findByUsername(String username);
	User findByPhone(String phone);
	User findByEmail(String email);
	Set<User> findByPhoneOrEmail(String phone, String email);
    User findByUsernameAndActivedIsTrue(String username);

    /**
     * 批量修改User对象
     * @param value 修改的值
     * @param User id集合
     * @return 返回修改的个数
     */
    @Modifying 
    @Query("update User u set u.actived = ?1 where u.id in (?2)") 
    int freezeOrRecoverUser(boolean value, List<Long> userIds);
    User findByNickname(String nickname);
	User findByOauthUidAndProvider(String oauthId, String provider);
    
}
