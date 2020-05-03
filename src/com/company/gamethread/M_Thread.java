package com.company.gamethread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.Main;
import com.company.gamecontrollers.MainWindow;
import com.company.gametools.Tools;

public abstract class M_Thread {
    private static Logger LOG = LogManager.getLogger(M_Thread.class.getName());

    public static boolean SIGNAL_TERM_GENERAL = false;

//    private static boolean terminate(long timeoutMsec) {
//        boolean rc = true;
//        try {
//            C_Thread.getInstance().terminate(timeoutMsec);
//            V_Thread.getInstance().terminate(timeoutMsec);
//            D_Thread.getInstance().terminate(timeoutMsec);
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
        try {
            C_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }

            return false;
        }

        try {
            V_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }

            return false;
        }

        try {
            D_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }

            return false;
        }

        // Delete all objects and destroy main window
        MainWindow.destroy();

        return true;
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

    public static void suspendAll() {
        D_Thread.getInstance().suspend();
        LOG.debug("D suspended");

        V_Thread.getInstance().suspend();
        LOG.debug("V suspended");

        C_Thread.getInstance().suspend();
        LOG.debug("C suspended");
        LOG.debug("--- suspended ---");
    }

    // TODO Move this to ThreadPool
    public static void resumeAll() {
        D_Thread.getInstance().resume();
        V_Thread.getInstance().resume();
        C_Thread.getInstance().resume();
        LOG.debug("--- resumed ---");
    }

    // FIXME Instead getters use Class.attr
    public static long getThreadId() {
        return Main.threadId;
    }
}
