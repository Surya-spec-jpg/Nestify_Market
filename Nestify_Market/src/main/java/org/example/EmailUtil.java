package org.example;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        final String from = "Your e-mail";
        final String pass = "Your App Password"; // 16-char app password

        Properties props = new Properties();
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.ssl.trust","smtp.gmail.com");

        Session sess = Session.getInstance(props, new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(from, pass);
            }
        });

        Message msg=new MimeMessage(sess);
        msg.setFrom(new InternetAddress(from));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        msg.setSubject(subject);
        msg.setText(body);

        Transport.send(msg);
        System.out.println("Email sent to " + toEmail);
    }
}
