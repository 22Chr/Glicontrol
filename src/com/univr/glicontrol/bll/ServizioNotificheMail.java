package com.univr.glicontrol.bll;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class ServizioNotificheMail {

    private final String username = "system.glicontrol@gmail.com";
    private final String password = "sull rihb tdac dzpb";

    final Properties props;

    public ServizioNotificheMail() {
        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);

        Transport.send(message);
    }
}
