package com.local.event.finder.notifications;

import com.local.event.finder.event.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                emailService,
                "from",
                "noreply@test.com"
        );
    }

    @Test
    void shouldSendEmail() {
        EmailModel email = new EmailModel(
                "user@test.com",
                "Test subject",
                "Test body"
        );

        String result = emailService.sendEmail(email);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();

        assertEquals("noreply@test.com", sent.getFrom());
        assertEquals("user@test.com", sent.getTo()[0]);
        assertEquals("Test subject", sent.getSubject());
        assertEquals("Test body", sent.getText());

        assertEquals(
                "Successfully send email to: user@test.com",
                result
        );
    }

    @Test
    void shouldSendEmailNotification() {
        Event event = mock(Event.class);

        when(event.getTitle()).thenReturn("Birthday");
        when(event.getStartTime())
                .thenReturn(LocalDateTime.of(2026, 6, 10, 18, 0));

        EmailModel email = new EmailModel(
                "user@test.com",
                "ignored",
                "ignored"
        );

        emailService.sendEmailNotification(email, event);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();

        assertEquals("user@test.com", sent.getTo()[0]);
        assertEquals("Event Notification", sent.getSubject());
        assertEquals(
                "Your event Birthday starts tomorrow at 2026-06-10T18:00",
                sent.getText()
        );
    }
}
