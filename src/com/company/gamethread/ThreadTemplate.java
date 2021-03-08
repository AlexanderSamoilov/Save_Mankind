/* ********************************* *
 * S I N G L E T O N   P A T T E R N *
 * ********************************* */
package com.company.gamethread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gametools.Tools;
import static com.company.gamecontent.Constants.TIME_QUANTUM;

// "Pattern" class for creation of singletons
public abstract class ThreadTemplate extends Thread {
    private static Logger LOG = LogManager.getLogger(ThreadTemplate.class.getName());

    // Semi-Singleton (abstract): "protected" constructor with a parameter
    // Child classes will have "private" constructor like a normal singleton, but with a parameter
    private static ThreadTemplate instance = null;
    public static synchronized ThreadTemplate getInstance() {
        return instance;
    }
    ThreadTemplate(String threadName) {
        super.setName(threadName);
        SIGNAL_TERM = false;
        //LOG.debug(ThreadTemplate.class + " singleton created.");
    }

    private boolean SIGNAL_TERM;

    @Override
    @Deprecated
    public void start() {
        throw new UnsupportedOperationException(
                "void start() method with no arguments is forbidden. Use start(int, long) instead."
        );
        // interrupt(); - is it possible that the method start() of the base class anyway started the run() at this moment?
    }

    public boolean start(int attempts, long timeoutMSec)
    {
        boolean rc = true;
        StackTraceElement [] stackTrace = null;
        while (rc) {
            try {
                LOG.info("Thread " + super.getId() + " is starting");
                super.start();
                LOG.info("Thread " + super.getId() + " started.");
                rc = false;
            } catch (Exception e) {
                stackTrace = e.getStackTrace();
                Tools.timeout(timeoutMSec);
            }
        }

        LOG.warn("Thread " +super.getId() + " failed to start after " + attempts + " attempts.");
        if (stackTrace != null) {
            for (StackTraceElement steElement : stackTrace) {
                LOG.error(steElement.toString());
            }
        }

        return false;
    }

    @Override
    @Deprecated // Derived classes should use repeat() instead.
    public void run() {
        try
        {
            LOG.info("Thread " + super.getId() + " is running");
            while (!interrupted() && !SIGNAL_TERM) {
                // All the functionality is incapsulated here.
                repeat();
                Tools.timeout(TIME_QUANTUM);
            }

            LOG.info("Thread " + super.getId() + " finished.");
        }
        catch (InterruptedException e)
        {
            LOG.error("Thread " + super.getId() + " died!");
            Tools.printStackTrace(e);
        }

        //super.run(); // Thread.run() is empty, so we don't need it.
    }

    // FIXME Init this
    public void repeat() throws InterruptedException {
        // Pure functionality without error handling
        // Is to be used by derived classes
    }

    // TODO: Should we release all mutexes held by a thread before terminating?
    // TODO: What if we call this function when the thread is held by another thread?
    // FIXME Confused ret values
    private boolean terminateThread(long timeoutMsec) {
        SIGNAL_TERM = true;

        // Wait and hope that the thread finish its work normally
        Tools.timeout(timeoutMsec);

        // Still did not exit - it's a pity...
        if (isAlive()) {
            /* Hard termination (See https://stackoverflow.com/questions/671049/how-do-you-kill-a-thread-in-java). */
            try {
                interrupt();
            } catch (Exception e) {
                Tools.printStackTrace(e);
            }

            // TODO: check of we need this
            super.interrupt();
        }

        return !isAlive();
    }

    /*
     We need this static function to avoid such a situation
     when somebody calls <Class>.getInstance().terminateThread()
     when the singleton instance not created yet. In this case it
     will be first created inside getInstance() which is absolutely useless.
     */
    public static boolean terminate(long timeoutMsec) {
        if (instance == null) {
            return true;
        }
        return instance.terminateThread(timeoutMsec);
    }
} // end of class ThreadTemplate
// public class ThreadTemplateImpl extends ThreadTemplate { }
