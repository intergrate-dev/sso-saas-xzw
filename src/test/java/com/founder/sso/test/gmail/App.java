package com.founder.sso.test.gmail;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        MailMail mm = (MailMail) context.getBean("mailMail");
        mm.sendMail("Yiibai Mook", "This is text content");
    }
}
