package com.univr.glicontrol.bll;

import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServizioNotificheTest {

    @Test
    void sendEmail() {
        ServizioNotifiche servizioNotifiche = new ServizioNotifiche();

        String destinatario = "anna.martini2004@gmail.com";
        String oggetto = "Test invio email";
        String corpo = "Questa è un'email di test";

        try {
            servizioNotifiche.sendEmail(destinatario, oggetto, corpo);
            System.out.println("Email inviata con successo!");
            assertTrue(true);
        } catch (MessagingException e) {
            e.printStackTrace();
            fail("Invio email fallito: " + e.getMessage());
        }
    }

    @Test
    void sendEmail_shouldThrowMessagingException() {
        // Classe anonima per forzare credenziali errate
        ServizioNotifiche servizioNotifiche = new ServizioNotifiche() {
            @Override
            public void sendEmail(String to, String subject, String body) throws MessagingException {
                // Forziamo un mittente sbagliato che farà fallire l'autenticazione
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                var session = jakarta.mail.Session.getInstance(props, new jakarta.mail.Authenticator() {
                    protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new jakarta.mail.PasswordAuthentication("email.falsa@gmail.com", "passwordSbagliata");
                    }
                });

                var message = new jakarta.mail.internet.MimeMessage(session);
                message.setFrom(new jakarta.mail.internet.InternetAddress("email.falsa@gmail.com"));
                message.setRecipients(jakarta.mail.Message.RecipientType.TO,
                        jakarta.mail.internet.InternetAddress.parse(to));
                message.setSubject(subject);
                message.setText(body);

                // Questo causerà una MessagingException
                jakarta.mail.Transport.send(message);
            }
        };

        assertThrows(MessagingException.class, () -> {
            servizioNotifiche.sendEmail("destinatario@esempio.com", "Oggetto test", "Corpo test");
        });
    }
}