package com.founder.sso.admin.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.founder.sso.admin.entity.AdminAccount;

public interface AdminAccountDao extends PagingAndSortingRepository<AdminAccount, Long> {
     AdminAccount findByLoginname(String loginname);
     AdminAccount findByLoginnameAndEnabledIsTrue(String loginname);
     @Query("select account from AdminAccount account where account.enabled=true and account.roles like '%SUPER_ADMIN%'")
     List<AdminAccount> findEnabledSuperAdmins();

}
