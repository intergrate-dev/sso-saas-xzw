package com.founder.sso.test.gmail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by yuan-pc on 2019/1/3.
 */
public class GmailTest {
    private static Logger log = LoggerFactory.getLogger(GmailTest.class);

    /*private static String USER_NAME = "yuan.zk123"; // GMail user name (just the part before "@gmail.com")
    private static String PASSWORD = "yzk123AS"; // GMail password*/

    private static String USER_NAME = "foundermaojs";
    private static String PASSWORD = "Founder123";

    public static void main(String[] args) {
        String from = USER_NAME;
        String pass = PASSWORD;
        String subject = "mail theme";
        String body = "secureCode: 676800";
        sendFromGMail(from, pass, Arrays.asList("2491042435@qq.com"), subject, body);
    }

    private static void sendFromGMail(String from, String pass, List<String> to, String subject, String body) {
        try {
            String host = "smtp.gmail.com";
            Properties props = System.getProperties();
            props.put("mail.smtp.host", host);
            //props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.debug", "true");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");

            Session session = Session.getDefaultInstance(props);
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));
            InternetAddress[] toAddress = new InternetAddress[to.size()];

            // To get the array of addresses
            for (int i = 0; i < to.size(); i++) {
                toAddress[i] = new InternetAddress(to.get(i));
            }

            for (int i = 0; i < toAddress.length; i++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            }

            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            log.info("send mail complete ... ");
        } catch (Exception e) {
            log.error("sendFromGMail ", e);
        }
    }
}
