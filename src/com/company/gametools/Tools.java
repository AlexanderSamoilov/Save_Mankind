package com.company.gametools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
 This class contains miscellaneous helper functions and wrappers for standard Java API.
 */
public abstract class Tools {
    private static Logger LOG = LogManager.getLogger(Tools.class.getName());

    public static void timeout(long timeoutMSec) {
        /* TODO: what happens if we pass negative timeout into sleep()? */
        try {
            Thread.sleep(timeoutMSec);
        } catch (InterruptedException e) {
            // This is not a big trouble that we were not able to do sleep, so it is not a reason to interrupt the method on this
        }
    }

    public static void printStackTrace(Exception e) {
        StackTraceElement [] stackTrace = null;
        if (e != null) {
            stackTrace = e.getStackTrace();
        } else { // if the Exception parameter is null then we print the current stack trace
            stackTrace = Thread.currentThread().getStackTrace();
        }

        if (LOG != null) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                LOG.info(stackTraceElement.toString());
            }
        } else { // If the logger is not working for some reason, at least print to console
            for (StackTraceElement stackTraceElement : stackTrace) {
                System.out.println(stackTraceElement.toString());
            }
        }
    }
}
