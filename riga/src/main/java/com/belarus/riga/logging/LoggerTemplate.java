package com.belarus.riga.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerTemplate {

    private static final Logger LOGGER = LogManager.getLogger(LoggerTemplate.class);

    public void someMethod() {
        // Примеры логирования
        LOGGER.debug("This is a debug message");
        LOGGER.info("This is an info message");
        LOGGER.warn("This is a warning message");
        LOGGER.error("This is an error message");
        LOGGER.fatal("This is a fatal message");
    }

    public static void main(String[] args) {
        LOGGER.info("Application is starting!");

        LoggerTemplate yourClassInstance = new LoggerTemplate();
        yourClassInstance.someMethod();

        LOGGER.info("Application is shutting down.");
    }
}
