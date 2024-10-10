package com.example.gpq.Services;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailServiceImpl {

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            // Vérifier si l'adresse e-mail est null ou vide
            if (toEmail == null || toEmail.isEmpty()) {
                System.out.println("Adresse e-mail vide ou nulle.");
                return;
            }

            // Vérifier le format de l'adresse e-mail
            InternetAddress.parse(toEmail);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("telnetgpq@gmail.com", "gbpc tgjl bteo szop");
                }
            });

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("telnetgpq@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Sent");

        } catch (AddressException e) {
            System.out.println("Erreur d'adresse e-mail: " + e.getMessage());
        } catch (MessagingException e) {
            System.out.println("Erreur de messagerie: " + e.getMessage());
        }
    }
}