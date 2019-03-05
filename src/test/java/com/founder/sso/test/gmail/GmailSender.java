package com.founder.sso.test.gmail;

import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * 使用Gmail发送邮件
 *
 * @author Winter Lau
 */
public class GmailSender {

    public static void main(String[] args) throws AddressException, MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        //props.put("mail.smtp.auth", "true");

        final String username = "zhaoyucai2018";
        final String password = "012580zyc";
        /*final String username = "foundermaojs";
        final String password = "Founder123";*/

        Session session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse("309865@qq.com", false));
        msg.setSubject("mail subject");
        msg.setText("send Content");
        msg.setSentDate(new Date());
        Transport.send(msg);

        System.out.println("Message sent.");
    }
}