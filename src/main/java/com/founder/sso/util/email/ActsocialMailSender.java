package com.founder.sso.util.email;

import java.io.File;
import java.security.Security;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

/**
 * 发邮件的类
 */
public class ActsocialMailSender {
    //从配置文件中读取相应的邮件配置属性
	public static String emailHost = null;
	public static String userName = null;
	public static String password = null;
    
    private static final String mailAuth = "true";
    private static Map<String, Object> proMap = null;
    private static JavaMailSenderImpl instance = null;
    private static VelocityEngine velocityEngine = null;

    static {
        proMap = new HashMap<String, Object>();
        proMap.put("resource.loader", "class");
        proMap.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    }

    public static JavaMailSender getInstance() {
        if (null == instance) {
            synchronized (JavaMailSenderImpl.class) {
                if (null == instance) {
                    instance = new JavaMailSenderImpl();
                    instance.setHost(emailHost);
                    instance.setUsername(userName);
                    instance.setPassword(password);
                    Properties properties = new Properties();
                    properties.setProperty("mail.smtp.auth", mailAuth);
                   
                  /*  properties.setProperty("proxySet", "true");
                    properties.setProperty("socksProxyHost", "isasrv");
                    properties.setProperty("socksProxyPort","80");*/
                    
                    //使用gmail或qq发送邮件是必须设置如下参数的 主要是port不一样
                    if (emailHost.indexOf("smtp.gmail.com")>=0 || emailHost.indexOf("smtp.qq.com")>=0) {
                        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
                        properties.setProperty("mail.smtp.port", "465");
                        properties.setProperty("mail.smtp.socketFactory.port", "465");
                    }
                    instance.setJavaMailProperties(properties);
                }
            }
        }

        return instance;
    }

    public static VelocityEngine getVelocityEngineInstance() {
        if (null == velocityEngine) {
            synchronized (VelocityEngine.class) {
                if (null == velocityEngine) {
                    velocityEngine = new VelocityEngine();
                    for (Map.Entry<String, Object> entry : proMap.entrySet()) {
                        velocityEngine.setProperty(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return velocityEngine;
    }

    /**
     * 发送邮件的方法
     * @param model：设置模板中的变量和值
     * @param subject:主题
     * @param vmfile：模板名称
     * @param mailTo：收件人数组，支持多人
     * @param files：附件名，应该填写完整地址
     */
    public static void sendEmail(final Map<String,Object> model,final String subject,final String vmfile,final String[] mailTo,final String [] files) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            //注意MimeMessagePreparator接口只有这一个回调函数
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage,true,"GBK");
                //这是一个生成Mime邮件简单工具，如果不使用GBK这个，中文会出现乱码
                //如果您使用的都是英文，那么可以使用MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setTo(mailTo);//设置接收方的email地址
                message.setSubject(subject);//设置邮件主题
                message.setFrom(userName);//设置发送方地址
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        ActsocialMailSender.getVelocityEngineInstance(), vmfile, "UTF-8", model);
                //从模板中加载要发送的内容，vmfile就是模板文件的名字
                //注意模板中有中文要加GBK，model中存放的是要替换模板中字段的值
                message.setText(text, true);
                //将发送的内容赋值给MimeMessageHelper,后面的true表示内容解析成html
                //如果您不想解析文本内容，可以使用false或者不添加这项
                FileSystemResource file;
                for(String s:files)//添加附件
                {
                    file = new FileSystemResource(new File(s));//读取附件
                    message.addAttachment(s, file);//向email中添加附件
                }
            }
        };
        ActsocialMailSender.getInstance().send(preparator);//发送邮件
    }
    
    /**
     * 在使用邮箱注册
     * @param model
     * @param mailTo
     */
    public static void sendEmailCode(final Map<String,Object> model,final String[] mailTo){
    	sendEmail(model, "邮箱验证码", "template/sendCode.txt", mailTo, new String[]{});
    }

    /*发送邮件服务*/
    public static void sendMail(String email, String sendCode, String useType) throws Exception {
        System.out.println("");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("code", sendCode);
        String sendContent = VelocityEngineUtils.mergeTemplateIntoString(
                ActsocialMailSender.getVelocityEngineInstance(), "template/sendCode.html", "UTF-8", model);

        String mailSSLPort = "465";
        Properties props = new Properties();
        props.put("mail.smtp.host", ActsocialMailSender.emailHost);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.socketFactory.port", mailSSLPort);
        props.put("mail.smtp.port", mailSSLPort);

        String senSubject = useType.equals("0") ? "星洲网注册验证码" : "星洲网找回密码验证码";
        // String sendContent = "您好！您正在星洲网本次操作的验证码为：" + sendCode + ", 10分钟内有效，请不要泄露给他人。";
        javax.mail.Session session = javax.mail.Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(ActsocialMailSender.userName));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
        message.setSubject(senSubject);
        message.setContent(sendContent, "text/html;charset=gbk");
        message.setSentDate(new Date());
        message.saveChanges();
        Transport transport = null;
        try {
            transport = session.getTransport();
            transport.connect(ActsocialMailSender.userName, ActsocialMailSender.password);
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                transport.close();
            }
        }
    }

    public static void sendMailByGmail(String email, String sendCode, String useType) throws Exception {
        System.out.println("============= ActsocialMailSender sendMail, sendCode: " + sendCode + ", email: " + email);
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        /*props.setProperty("mail.smtp.host", "smtp.gmail.com");*/
        props.put("mail.smtp.host", ActsocialMailSender.emailHost);
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");

        /*final String username = "zhaoyucai2018";
        final String password = "012580zyc";*/
        /*final String username = "foundermaojs";
        final String password = "Founder123";*/

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ActsocialMailSender.userName, ActsocialMailSender.password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(ActsocialMailSender.userName + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(email, false));

        Map<String,Object> model = new HashMap<String,Object>();
        model.put("code", sendCode);
        /*String sendContent = VelocityEngineUtils.mergeTemplateIntoString(
                ActsocialMailSender.getVelocityEngineInstance(), "templates/sendCode.html", "UTF-8", model);*/
        String sendContent = "您好！您正在星洲网本次操作的验证码为：" + sendCode + ", 10分钟内有效，请不要泄露给他人。";
        String sendSubject = useType.equals("0") ? "星洲网注册验证码" : "星洲网找回密码验证码";
        System.out.println("============= ActsocialMailSender sendMail, time: " + System.currentTimeMillis() + ", sendContent: " + sendContent + ", email: " + email);

        msg.setSubject(sendSubject);
        msg.setText(sendContent);
        msg.setSentDate(new Date());
        System.out.println("============= ActsocialMailSender sendMail start, time: " + System.currentTimeMillis() + ", email: " + email);
        Transport.send(msg);
        System.out.println("============= ActsocialMailSender sendMail end, time: " + System.currentTimeMillis()  + ", email: " + email);

    }
}
