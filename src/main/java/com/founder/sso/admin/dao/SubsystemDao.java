package com.founder.sso.admin.dao;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.founder.sso.admin.entity.Subsystem;

public interface SubsystemDao extends PagingAndSortingRepository<Subsystem, Long> {
	
	public List<Subsystem> findByEnabledTrue();
	public List<Subsystem> findByEnabledTrueAndCodeNot(String Code);
	public List<Subsystem> findByCode(String code);
}
