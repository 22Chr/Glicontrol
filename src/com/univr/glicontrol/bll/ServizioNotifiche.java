package com.univr.glicontrol.bll;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import javafx.scene.control.Alert;

import java.util.Properties;

public class ServizioNotifiche {

    private final String username = "system.glicontrol@gmail.com";
    private final String password = "sull rihb tdac dzpb";

    final Properties props;

    public ServizioNotifiche() {
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

    public void mostraNotifichePromemoriaAssunzioneFarmaci() {
        Alert promemoriaAssunzioneFarmaciAlert = new Alert(Alert.AlertType.INFORMATION);
        promemoriaAssunzioneFarmaciAlert.setTitle("System Notification Service");
        promemoriaAssunzioneFarmaciAlert.setHeaderText("Promemoria di assunzione dei farmaci");
        promemoriaAssunzioneFarmaciAlert.setContentText("Hai alcuni farmaci che risultano essere non ancora assunti.\nRicordati di registrare tutti i tuoi farmaci");
        promemoriaAssunzioneFarmaciAlert.showAndWait();
    }

    public void mancataAderenzaOrariFarmaciTerapia() {
        Alert orariTerapiaNonRispettatiAlert = new Alert(Alert.AlertType.WARNING);
        orariTerapiaNonRispettatiAlert.setTitle("System Notification Service");
        orariTerapiaNonRispettatiAlert.setHeaderText("Orario di assunzione non conforme");
        orariTerapiaNonRispettatiAlert.setContentText("L'orario in cui hai registrato questo farmaco non corrisponde a quello previsto dalla tua terapia.\nSei invitato ad assumere i tuoi farmaci rispettando gli orari stabiliti dai medici");
        orariTerapiaNonRispettatiAlert.showAndWait();
    }
}
