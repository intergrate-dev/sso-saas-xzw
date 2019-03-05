package com.founder.sso.admin.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.founder.sso.admin.entity.SubsystemApp;
import com.founder.sso.admin.service.SubsystemAppService;
import com.founder.sso.util.json.JSONException;
import com.founder.sso.util.json.JSONObject;

@Controller
@RequestMapping(value="/admin/app")
public class SubsystemAppController {
	@Autowired
	private SubsystemAppService subsystemService;
	

	@RequestMapping(value="", method=RequestMethod.GET)
	public String subsystem(Model model){
		model.addAttribute("subsystemList", subsystemService.findAll());
		return "/admin/app/subsystemList";
	}
	
    
    /**
     * 初始化创建或修改管理员 
     */
    @RequestMapping(value="/initUpdate")
    public String initUpdate(Long id, Model model) {
    	//修改用户信息时
    	if(!StringUtils.isEmpty(id)){
    		SubsystemApp subsystem = subsystemService.findOne(id);
    		model.addAttribute("subsystem", subsystem);
    	}
    	
    	return "/admin/app/updateSubsystem";
    }
    /**
     * 检查code是否已经存在
     * @throws JSONException 
     */
    @RequestMapping(value="/isCodeLegal")
    public ResponseEntity<String> checkIsCodeLegal(String code, Long systemId) {
    	//修改用户信息时
    	JSONObject json = subsystemService.findByCode(code, systemId);
    	
    	ResponseEntity<String> re = new ResponseEntity<String>(json.toString(),HttpStatus.OK); 
		return re;
    }
    
    /**
     * 创建或修改子系统信息
     */
    @RequestMapping(value="/save")
    public String save(SubsystemApp subsystem) {
    	subsystemService.save(subsystem);
    	return "/admin/app/updateSubsystem";
    }
    
    /**
     * 创建或修改子系统信息
     */
    @RequestMapping(value="{id}/delete")
    public String delete(@PathVariable("id")Long id, RedirectAttributes attr) {
    	
    	subsystemService.delete(id);
    	attr.addAttribute("info", "success");
    	return "redirect:/admin/app";
    }
}
