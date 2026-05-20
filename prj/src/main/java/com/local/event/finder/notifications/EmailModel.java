package com.local.event.finder.notifications;

public record EmailModel(
        String to,
        String subject,
        String body
) {
}
