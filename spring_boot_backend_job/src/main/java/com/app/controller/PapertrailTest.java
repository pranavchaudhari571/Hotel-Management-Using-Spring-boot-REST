package com.app.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PapertrailTest {

    private static final Logger logger = LoggerFactory.getLogger(PapertrailTest.class);

    public static void main(String[] args) {
        // Log a simple message at different levels
        logger.debug("This is a DEBUG message");
        logger.info("This is an INFO message");
        logger.warn("This is a WARN message");
        logger.error("This is an ERROR message");

        // Simulate an HTTP log (this would simulate a request like the one you mentioned)
        simulateHttpLog();
    }

    public static void simulateHttpLog() {
        logger.info("GET / HTTP/1.1");
        logger.info("Host: logs5.papertrailapp.com:16696");
        logger.info("Connection: keep-alive");
        logger.info("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/133.0.0.0");
        logger.info("Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp");
        logger.info("Cookie: AMCVS_8D6867C25245AEFB0A490D4C%40AdobeOrg=1; papertrail_session_id=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");
    }
}
