/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gamelogger;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Level;

/**
 * This class the simple configurator for all loggers in application
 */

enum LogOptions {
    ; // utility class

    // OFF -> FATAL -> ERROR -> WARN -> INFO -> TRACE -> ALL
    static final Level LEVEL = Level.DEBUG;

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
    // static final String STDOUT_LAYOUT = "%d{yyyy-MM-dd HH:mm:ss} [%10tid] %-30.30c{2} > %20.20M() %5level: %msg%n%throwable";
    static final String STDOUT_LAYOUT = "[%5tid] %5level: %msg%n%throwable";

    // Example of good format of output:
    //    2019-04-16 22:06:54 [22] FATAL: The game dead.
    // static final String   FILE_LAYOUT = "%d{yyyy-MM-dd HH:mm:ss} [%10tid] %5level: %msg%n%throwable";
    static final String   FILE_LAYOUT = "[%5tid] %5level: %msg%n%throwable";

    // Example of good format of output:
    //    [22] DEBUG: x=12, y=16.
    // static final String  DEBUG_LAYOUT = "%d{yyyy-MM-dd HH:mm:ss} [%10tid] %5level: %msg%n";
    static final String  DEBUG_LAYOUT = "[%5tid] %5level: %msg%n%throwable";
}
