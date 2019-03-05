package com.founder.sso.test;

import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created by yuan-pc on 2018/11/8.
 */
public class EmailTest {
    /*发送邮件服务*/
    public static void sendMail(String mail_from, String mail_user, String mail_password, String toMail, String senSubject, String sendContent, String mail_host) throws Exception {
        Properties props = new Properties();
        props.put("mail.smtp.host", mail_host);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        //String mailSSLPort = "465";
        String mailSSLPort = "587";


        // 是否使用ssl加密端口
        //props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.socketFactory.port", mailSSLPort);
        props.put("mail.smtp.port",mailSSLPort);

        javax.mail.Session session = javax.mail.Session.getInstance(props);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mail_from));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(mail_from));
        message.setSubject(senSubject);
        message.setContent(sendContent, "text/html;charset=gbk");
        message.setSentDate(new Date());
        message.saveChanges();
        Transport transport = session.getTransport();
        transport.connect(mail_user, mail_password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public static void main(String[] args) {
        /*String mail_host = "smtp.163.com";
        String mail_user = "sinchewmail@163.com";
        String mail_password = "yzk2323";
        //String mail_password = "scw123";
        String mail_from = "sinchewmail@163.com";*/

        String mail_host = "mail.sinchew.com.my";
        String mail_user = "sinchewmail@163.com";
        String mail_password = "123wa@#$esz";
        String mail_from = "webmaster@sinchew.com.my";


        String email = "2491042435@qq.com";

        String sendCode = "105693";
        String sendSubject = "mail theme";
        String sendContent = "sendCode：" + sendCode;
        try {
            sendMail(mail_from, mail_user, mail_password, email, sendSubject, sendContent, mail_host);
            System.out.println("send complete ...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
