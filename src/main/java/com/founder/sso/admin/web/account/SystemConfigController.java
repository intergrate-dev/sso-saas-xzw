package com.founder.sso.admin.web.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.sso.dao.SystemConfigDao;
import com.founder.sso.service.oauth.entity.SystemConfig;
import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping(value="/admin")
public class SystemConfigController {
	@Autowired
	private SystemConfigDao systemConfigDao;
	
	@RequestMapping("/findAllSystemConfig")
	public String findAllOAuthConfig(Model model){
		model.addAttribute("systemConfigList",ImmutableList.copyOf(systemConfigDao.findAll()));
		return "/admin/systemConfig/systemConfigList";
	}
	
	@RequestMapping("/initUpdateSystemConfig")
	public String initUpdateOauthConfig(Long id,Model model){
		model.addAttribute("systemConfig",systemConfigDao.findOne(id));
		return "/admin/systemConfig/updateSystemConfig";
	}
	
	@RequestMapping("/updateSystemConfig")
	public ResponseEntity<String> updateOauthConfig(SystemConfig systemConfigTarget){
		SystemConfig systemConfig = systemConfigDao.findOne(systemConfigTarget.getId());
		systemConfig.setSname(systemConfigTarget.getSname());
		systemConfig.setSstatus(systemConfigTarget.getSstatus());
		systemConfig.setSdescribe(systemConfigTarget.getSdescribe());
		systemConfigDao.save(systemConfig);
		ResponseEntity<String> re = new ResponseEntity<String>("{\"result\":\"success\"}",HttpStatus.OK); 
    	return re;
	}
	

}
