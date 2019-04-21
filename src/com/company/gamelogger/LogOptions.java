package com.company.gamelogger;

import org.apache.logging.log4j.Level;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogOptions {
    /**
     * This class the simple configurator for all loggers in application
     */

    // OFF -> FATAL -> ERROR -> WARN -> INFO -> TRACE -> ALL
    static final Level LEVEL = Level.INFO;

    // If NOT Empty: On match with regexp, message will accept. Mismatching will deny message.
    static final String REGEX = "";

    private static final String DATE = new SimpleDateFormat("HH-mm-ss_yyyy.MM.dd").format( new Date());

    static final boolean  WRITE_FILE = false;
    static final String    FILE_NAME = DATE + ".log";
    static final String    FILE_PATH = "logs";

    // Layout instructions:
    //    https://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
    //    http://logging.apache.org/log4j/2.x/manual/layouts.html
    //
    // WARNING. Keys: %c, %l, %M do program's speed very slow!

    // Example of good format of output:
    //    2019-04-16 22:06:54 [22] gamethread.Main > mainfunc() INFO: The game starts.
    static final String STDOUT_LAYOUT = "%d{yyyy-MM-dd HH:mm:ss} [%-2.2tid] %-30.30c{2} > %20.20M() %5level: %msg%n%throwable";

    // Example of good format of output:
    //    2019-04-16 22:06:54 [22] FATAL: The game dead.
    static final String   FILE_LAYOUT = "%d{yyyy-MM-dd HH:mm:ss} [%-2.2tid] %5level: %msg%n%throwable";

    // Example of good format of output:
    //    [22] DEBUG: x=12, y=16.
    static final String  DEBUG_LAYOUT = "[%-2.2tid] %5level: %msg%n";
}
