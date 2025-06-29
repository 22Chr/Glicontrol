package com.univr.glicontrol.bll;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;

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

    public void notificaMancataAderenzaOrariFarmaciTerapia() {
        Alert orariTerapiaNonRispettatiAlert = new Alert(Alert.AlertType.WARNING);
        orariTerapiaNonRispettatiAlert.setTitle("System Notification Service");
        orariTerapiaNonRispettatiAlert.setHeaderText("Orario di assunzione non conforme");
        orariTerapiaNonRispettatiAlert.setContentText("L'orario in cui hai registrato questo farmaco non corrisponde a quello previsto dalla tua terapia.\nSei invitato ad assumere i tuoi farmaci rispettando gli orari stabiliti dai medici");
        orariTerapiaNonRispettatiAlert.showAndWait();
    }

    public void notificaSospensioneFarmacoTerapia(Paziente paziente) {
        Alert sospensioneFarmacoTerapiaAlert = new Alert(Alert.AlertType.WARNING);
        sospensioneFarmacoTerapiaAlert.setTitle("System Notification Service");
        sospensioneFarmacoTerapiaAlert.setHeaderText("Sospensione farmaci");
        sospensioneFarmacoTerapiaAlert.setContentText("Il paziente " + paziente.getNome() + " non assume i farmaci prescritti da più di 3 giorni.\nSi consiglia di contattare il paziente per dei chiarimenti");
        sospensioneFarmacoTerapiaAlert.showAndWait();
    }

    public void notificaPromemoriaRegistrazioneGlicemia() {
        Alert promemoriaRegistrazioneGlicemiaAlert = new Alert(Alert.AlertType.INFORMATION);
        promemoriaRegistrazioneGlicemiaAlert.setTitle("System Notification Service");
        promemoriaRegistrazioneGlicemiaAlert.setHeaderText("Promemoria di registrazione della glicemia");
        promemoriaRegistrazioneGlicemiaAlert.setContentText("Ricorda di registrare i tuoi valori glicemici");
        promemoriaRegistrazioneGlicemiaAlert.showAndWait();
    }

    public void notificaLivelliGlicemici(Paziente paziente, int indiceGlicemico) {
        Alert livelliGlicemiciAlert = new Alert(Alert.AlertType.WARNING);
        livelliGlicemiciAlert.setTitle("System Notification Service");

        if (indiceGlicemico < 0) {
            livelliGlicemiciAlert.setHeaderText("Anomalia nei livelli glicemici preprandiali");
        } else {
            livelliGlicemiciAlert.setHeaderText("Anomalia nei livelli glicemici postprandiali");
        }

        Label textLabel;
        switch (Math.abs(indiceGlicemico)) {
            case 1:
                    livelliGlicemiciAlert.setContentText("Il paziente " + paziente.getNome() + " " + paziente.getCognome() + " (" + paziente.getCodiceFiscale() + ") ha registrato un livello glicemico lievemente alterato");
                    livelliGlicemiciAlert.getDialogPane().setStyle("-fx-background-color: #ffdd00;");
                    break;
            case 2: livelliGlicemiciAlert.setContentText("Il paziente " + paziente.getNome() + " " + paziente.getCognome() + " (" + paziente.getCodiceFiscale() + ") ha registrato un livello glicemico moderatamente critico.\nSi consiglia di tenere monitorato il paziente");
                    livelliGlicemiciAlert.getDialogPane().setStyle("-fx-background-color: #ff9900;");
                    break;

            case 3: textLabel = new Label("Il paziente " + paziente.getNome() + " " + paziente.getCognome() + " (" + paziente.getCodiceFiscale() + ") ha registrato un livello glicemico critico.\nSi consiglia di rivedere la dieta e/o la terapia farmacologica");
                    textLabel.setStyle("-fx-text-fill: white;");
                    livelliGlicemiciAlert.getDialogPane().setContent(textLabel);
                    livelliGlicemiciAlert.getDialogPane().setStyle("-fx-background-color: #ff0000;");
                    break;
            case 4: textLabel = new Label("Il paziente " + paziente.getNome() + " " + paziente.getCognome() + " (" + paziente.getCodiceFiscale() + ") ha registrato un livello glicemico estremamente critico.\nÈ necessario un intervento medico immediato");
                    textLabel.setStyle("-fx-text-fill: white;");
                    livelliGlicemiciAlert.getDialogPane().setContent(textLabel);
                    livelliGlicemiciAlert.getDialogPane().setStyle("-fx-background-color: #6b0c8a;");
                    break;
        }

        livelliGlicemiciAlert.showAndWait();
    }

    public void notificaPresenzaNotificheNonVisualizzate() {
        Alert presenzaNotificheNonVisualizzateAlert = new Alert(Alert.AlertType.INFORMATION);
        presenzaNotificheNonVisualizzateAlert.setTitle("System Notification Service");
        presenzaNotificheNonVisualizzateAlert.setHeaderText("Sono presenti delle notifiche non ancora visualizzate");
        presenzaNotificheNonVisualizzateAlert.setContentText("Apri il centro notifiche per visionarne il contenuto");
        presenzaNotificheNonVisualizzateAlert.showAndWait();
    }
}
