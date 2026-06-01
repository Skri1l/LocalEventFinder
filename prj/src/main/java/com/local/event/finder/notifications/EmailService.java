package com.local.event.finder.notifications;

import com.local.event.finder.event.Event;

public interface EmailService {

    String sendEmail(EmailModel emailModel);

    void sendEmailNotification(EmailModel emailModel, Event event);
}
