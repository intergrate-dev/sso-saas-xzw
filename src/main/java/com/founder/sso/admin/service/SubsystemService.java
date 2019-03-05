package com.founder.sso.admin.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.founder.sso.admin.dao.SubsystemDao;
import com.founder.sso.admin.entity.Subsystem;
import com.founder.sso.util.json.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

@Service
public class SubsystemService {

	@Autowired
	private SubsystemDao subsystemDao;

	/**
	 * 查询所有的数据
	 * @return
	 */
	public List<Subsystem> findAll(){
		return ImmutableList.copyOf(subsystemDao.findAll());
	}
	/**
	 * 查询所有正常的系统
	 * @return
	 */
	public List<Subsystem> findByEnabledTrue(){
		return subsystemDao.findByEnabledTrue();
	}
	/**
	 * 查询除此之外的，所有正常的系统
	 * @return
	 */
	public List<Subsystem> findByEnabledTrueAndCodeNot(String exceptSystemCode){
		return subsystemDao.findByEnabledTrueAndCodeNot(exceptSystemCode);
	}
	
	public JSONObject findByCode(String code, Long systemId){
		Map<String,Object> json = Maps.newHashMap();
		List<Subsystem> systemList = subsystemDao.findByCode(code);
		String result = "true";
    	if(systemList!=null && systemList.size()>0){
    		for (Subsystem subsystem : systemList) {
				if(subsystem.getId()!=systemId){
					result = "false";
					break;
				}
			}
    	}
   		json.put("result", result);
   		return new JSONObject(json);
	}
	
	public Subsystem findByCode(String code){
		List<Subsystem> systemList = subsystemDao.findByCode(code);
		if(systemList!=null&&!systemList.isEmpty())
			return systemList.get(0);
		return null;
	}
	
	/**
	 * 查询某一Subsystem
	 * @return
	 */
	public Subsystem findOne(Long id){
		return subsystemDao.findOne(id);
	}
	
	/**
	 * 保存
	 * @return
	 */
	public Subsystem save(Subsystem subsystem){
		//新增和修改两种情况
		//根据subsystemid 去查询数据库里是否有记录，没有记录就是新增，有记录就是修改
		Long subsystemid = subsystem.getId();
		Subsystem  sub_old = null;
		if(subsystemid == null|| subsystemid <= 0){
			sub_old = null;
		}else{
			sub_old = subsystemDao.findOne(subsystemid);
			if(sub_old!=null&&!sub_old.getCode().equals(subsystem.getCode())){
				return subsystem;
			}
		}
		if(sub_old != null){
			//修改时,code和secretkey保持数据库内已有的值不变
			subsystem.setSecretKey(sub_old.getSecretKey());
			subsystem.setCode(sub_old.getCode());
		}else{
			subsystem.setSecretKey(UUID.randomUUID().toString().replaceAll("-", ""));
		}
		return subsystemDao.save(subsystem);
	}
	
	/**
	 * 删除
	 * @return
	 */
	public void delete(Long id){
		 subsystemDao.delete(id);
	}

	
	
	
	public void setSubsystemDao(SubsystemDao subsystemDao) {
		this.subsystemDao = subsystemDao;
	}
	
	
}
