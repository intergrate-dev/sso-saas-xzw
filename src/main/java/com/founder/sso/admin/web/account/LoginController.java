package com.founder.sso.admin.web.account;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/admin")
public class LoginController {

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login() {
        return "admin/account/login";
    }
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String login(@RequestParam( FormAuthenticationFilter.DEFAULT_USERNAME_PARAM)  String username,Model model) {
        model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
        return "admin/account/login";
    }
    
    /*
     * 进入首页
     */
    @RequestMapping(value="/homepage")
    public String homepage() {
        return "admin/homepage";
    }
    
}
