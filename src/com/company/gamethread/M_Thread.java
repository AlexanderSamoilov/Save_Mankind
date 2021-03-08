/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gamethread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.Main;
import com.company.gamecontrollers.MainWindow;
import com.company.gametools.Tools;

/*
 The only sense of this class is to move thread control from Main to a separate file.
 */

public enum M_Thread {
    ; // utility class

    private static Logger LOG = LogManager.getLogger(M_Thread.class.getName());

    public static boolean SIGNAL_TERM_GENERAL = false;
    public static int deadStatus = 0;

    public static void start() {
        if (D_Thread.getInstance().start(100, 10)) {
            M_Thread.terminateNoGiveUp(null,1000, null);
        }

        if (V_Thread.getInstance().start(100, 10)) {
            M_Thread.terminateNoGiveUp(null,1000, null);
        }

        // Wait for D-Thread to get ready (at the same time D-Thread is waiting for V-Thread to get ready)
        try {
            (ParameterizedMutexManager.getInstance().getMutex("D", "getReady")).acquire();
        } catch (Exception e) {
            M_Thread.terminateNoGiveUp(e,1000, "Failed to get mutex for D-Thread.");
        }

        // Wait for V-Thread to get ready
        try {
            (ParameterizedMutexManager.getInstance().getMutex("V", "getReady")).acquire();
        } catch (Exception e) {
            M_Thread.terminateNoGiveUp(e,1000, "Failed to get mutex for V-Thread.");
        }

        // 4. Initialize and start C-Thread.
        if (C_Thread.getInstance().start(100, 10)) {
            M_Thread.terminateNoGiveUp(null,1000, null);
        }
    }

    public static void repeat() {

        // Endless loop of the main thread. Here we just catch signals and do nothing more.
        while(!SIGNAL_TERM_GENERAL) {
            Tools.timeout(1000);
            // NOTE: I have an idea of some intelligent implementation - to respawn the thread if it dies due to really unexpected events, like
            // Java internal bug or an external influence (for example, if OS killed the thread)
            // I did not implement it, because at the moment I don't know how is it possible to notify the parent thread
            // immediately when the child thread dies. Probably, it is even not possible.
            // Tracking of the threads state in a loop with isAlive() is NOT safe, because between two loop iterations
            // some small time passes, so there is very low chance that during this time another threads try to get access
            // to mutexes associated with the dead thread.
            // I left the investigation of this question for the future, but oin case I find the solution
            // I leave here the list of actions which must be done if some thread is not alive and we want to respawn it:
            //  - block all threads
            //  - clear cache for dead thread AND think about what to do with still helded locks associated with dead thread
            //  - respawn dead thread
            //  - update cache for it taking into account new threadID?
            // Here is something to think about: https://stackoverflow.com/questions/12521776/what-happens-to-the-lock-when-thread-crashes-inside-a-synchronized-block.
            // I suppose it is possible to implement it, but to be very sure it is better to add also one more check in the loop
            // which checks if some of thread is in the waiting state very long (more than a given timeout seconds)
            // and exit the game or kill/respawn the thread in this case, BUT with the obligatory logging message about which lock
            // and from which other thread the given thread waited and could not acquire.
            //}

            if (!D_Thread.getInstance().isAlive()) {
                deadStatus++;
            }
            if (!V_Thread.getInstance().isAlive()) {
                deadStatus++;
            }
            if (!C_Thread.getInstance().isAlive()) {
                deadStatus++;
            }

            if (deadStatus > 0) {
                LOG.error(deadStatus + " of game threads were unexpectedly terminated. To ensure the correct game flow we must exit. Please, restart the game.");
                break;
            }
        }

        if (deadStatus == 0) {
            // If no one thread died then we wait for all threads to exit normally {
            // End of the game (wrap up actions)
            // We expect not just InterruptedException, but general Exception, because MutexManager can throw many times of exception from inside *_Thread
            try {
                C_Thread.getInstance().join(); // wait the C-thread to finish
            } catch (Exception eOuter) {
                Tools.printStackTrace(eOuter);
                C_Thread.terminate(1000);
            }

            try {
                V_Thread.getInstance().join(); // wait the V-thread to finish
            } catch (Exception eOuter) {
                Tools.printStackTrace(eOuter);
                V_Thread.terminate(1000);
            }

            try {
                D_Thread.getInstance().join(); // wait the D-thread to finish
            } catch (Exception eOuter) {
                Tools.printStackTrace(eOuter);
                D_Thread.terminate(1000);
            }
        }
    }

//    private static boolean terminate(long timeoutMsec) {
//        boolean rc = true;
//        try {
//            C_Thread.terminate(timeoutMsec);
//            V_Thread.terminate(timeoutMsec);
//            D_Thread.terminate(timeoutMsec);
//            destroy(); // delete all objects
//            SIGNAL_TERM_GENERAL = true;
//            rc = false;
//        } catch (Exception e) {
//            for (StackTraceElement steElement : e.getStackTrace()) {
//                LOG.error(steElement.toString());
//            }
//        }
//        return rc;
//    }

    // TODO Check logic here!
    private static boolean terminate(long timeoutMsec) {
        SIGNAL_TERM_GENERAL = true;
        boolean res;
        try {
            res = C_Thread.terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }
            return false;
        }

        try {
            res = res || V_Thread.terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }
            return false;
        }

        try {
            res = res || D_Thread.terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }
            return false;
        }

        // Delete all objects and destroy main window
        MainWindow.getInstance().destroy();

        return res;
    }

    public static void terminateNoGiveUp(Exception exception, long timeoutMsec, String terminateMsg) {

        LOG.info(" --- total terminate! ---");
        Tools.printStackTrace(exception);

        if (terminateMsg != null && !terminateMsg.equals("")){
            LOG.fatal(terminateMsg);
        }

        // ErrWindow ew = displayErrorWindow("The game was interrupted due to exception. Exiting...
        // (if this window does not disappear for a long time, kill the game process manually from OS.)");
        while (!terminate(timeoutMsec)) {
            // Just for safety we set here some small timeout unconfigurable
            // to avoid brutal rush of terminate() requests
            Tools.timeout(10);
            LOG.debug(" --- trying terminate! ---");
        }

        ////ew.close();
        System.exit(1);
    }

    public static void suspendChilds() {
        D_Thread.getInstance().suspend();
        LOG.debug("D suspended");

        V_Thread.getInstance().suspend();
        LOG.debug("V suspended");

        C_Thread.getInstance().suspend();
        LOG.debug("C suspended");
        LOG.debug("--- suspended ---");
    }

    public static void resumeChilds() {
        D_Thread.getInstance().resume();
        V_Thread.getInstance().resume();
        C_Thread.getInstance().resume();
        LOG.debug("--- resumed ---");
    }

    public static long getThreadId() {
        return Main.threadId;
    }
}
