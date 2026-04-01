package com.local.event.finder.logging;

public final class LoggerFactory {

    private LoggerFactory() {}

    public static AppLogger getLogger(Class<?> clazz) {
        return new Slf4jLogger(clazz);
    }
}
