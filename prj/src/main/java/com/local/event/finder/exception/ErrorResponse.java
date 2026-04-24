package com.local.event.finder.exception;

public record ErrorResponse(
        int status,
        String message
) {
}
