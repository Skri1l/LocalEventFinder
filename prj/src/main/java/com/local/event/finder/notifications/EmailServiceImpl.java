package com.local.event.finder.notifications;

import com.local.event.finder.logging.AppLogger;
import com.local.event.finder.logging.LoggerFactory;
import com.local.event.finder.user.UserController;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final static AppLogger log = LoggerFactory.getLogger(EmailServiceImpl.class);


    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public String sendEmail(EmailModel emailModel) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(from);
        mailMessage.setTo(emailModel.to());
        mailMessage.setSubject(emailModel.subject());
        mailMessage.setText(emailModel.body());

        mailSender.send(mailMessage);
        log.info("Email sent");
        return String.format("Successfully send email to: %s", emailModel.to());
    }
}
