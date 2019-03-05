package com.founder.sso.admin.web.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.founder.sso.dao.OauthClientConfigDao;
import com.founder.sso.service.oauth.OauthClient;
import com.founder.sso.service.oauth.OauthClientManager;
import com.founder.sso.service.oauth.entity.OauthClientConfig;
import com.google.common.collect.ImmutableList;

@Controller
@RequestMapping(value="/admin")
public class OauthConfigController {
	@Autowired
	private OauthClientConfigDao oauthClientConfigDao;
	
	@RequestMapping("/findAllOauthConfig")
	public String findAllOAuthConfig(Model model){
		model.addAttribute("oauthConfigList",ImmutableList.copyOf(oauthClientConfigDao.findAll()));
		return "/admin/oauthConfig/oauthConfigList";
	}
	
	@RequestMapping("/initUpdateOauthConfig")
	public String initUpdateOauthConfig(Long id,Model model){
		model.addAttribute("oauthConfig",oauthClientConfigDao.findOne(id));
		return "/admin/oauthConfig/updateOauthConfig";
	}
	
	@RequestMapping("/updateOauthConfig")
	public ResponseEntity<String> updateOauthConfig(OauthClientConfig oauthConfigTarget){
		OauthClientConfig oauthConfig = oauthClientConfigDao.findOne(oauthConfigTarget.getId());
		oauthConfig.setAppId(oauthConfigTarget.getAppId());
		oauthConfig.setSecretKey(oauthConfigTarget.getSecretKey());
		oauthConfig.setDescription(oauthConfigTarget.getDescription());
		oauthConfig.setEnabled(oauthConfigTarget.isEnabled());
		oauthClientConfigDao.save(oauthConfig);
		
		//下面更新内存中的OauthClient里面的OauthClientConfig信息
		String provider = oauthConfig.getProvider();
		OauthClient client = OauthClientManager.getClient(provider);
		client.setConfig(oauthConfig);
		OauthClientManager.registerClient(provider, client);
		ResponseEntity<String> re = new ResponseEntity<String>("{\"result\":\"success\"}",HttpStatus.OK); 
    	return re;
	}
	

}
