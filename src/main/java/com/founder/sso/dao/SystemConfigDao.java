package com.founder.sso.dao;

import org.springframework.data.repository.CrudRepository;

import com.founder.sso.service.oauth.entity.SystemConfig;

public interface SystemConfigDao extends CrudRepository<SystemConfig, Long> {
	
	SystemConfig findByScode(String scode);
}