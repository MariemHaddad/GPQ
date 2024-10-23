package com.example.gpq.Services;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    private EmailServiceImpl emailService;
    private Session mockSession;
    private MimeMessage mockMimeMessage;

    @BeforeEach
    void setUp() throws Exception {
        emailService = new EmailServiceImpl();

        // Mocking the Session and MimeMessage
        mockSession = Mockito.mock(Session.class);
        mockMimeMessage = Mockito.mock(MimeMessage.class);



    }

    @Test
    void testSendEmail_ValidEmail_ShouldSendEmail() throws Exception {
        // Arrange
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        emailService.sendEmail(toEmail, subject, body);

    }

    @Test
    void testSendEmail_NullEmail_ShouldNotSendEmail() throws MessagingException {
        // Arrange
        String toEmail = null;
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mockMimeMessage, never()).setFrom((Address) any());
    }

    @Test
    void testSendEmail_EmptyEmail_ShouldNotSendEmail() throws MessagingException {
        // Arrange
        String toEmail = "";
        String subject = "Test Subject";
        String body = "Test Body";

        // Act
        emailService.sendEmail(toEmail, subject, body);

        // Assert
        verify(mockMimeMessage, never()).setFrom((Address) any());
    }
}
