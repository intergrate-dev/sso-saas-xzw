package com.founder.sso.admin.web.account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class EmailController {

	@RequestMapping("/initSendEmail")
	public String initSendEmail(){
		
		return "/admin/email/sendEmail";
	}
	
	@RequestMapping("/initSetEmail")
	public String initSetemail(){
		
		return "/admin/email/setEmail";
	}
}
