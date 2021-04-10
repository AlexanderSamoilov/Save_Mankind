/* ***************** *
 * S I N G L E T O N *
 * ***************** */
/*
     Lazy thread-safe singleton initialization (possible to catch exception in Main).
     See https://www.geeksforgeeks.org/java-singleton-design-pattern-practices-examples.
 */
package com.company.gamethread;

import com.company.gamecontrollers.MainWindow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.company.gamethread.M_Thread.terminateNoGiveUp;

public class D_Thread extends ThreadTemplate {
    private static Logger LOG = LogManager.getLogger(D_Thread.class.getName());

    private static D_Thread instance = null;
    public static synchronized D_Thread getInstance() {
        return instance;
    }
    private D_Thread() {
        super("D-Thread");
        LOG.debug(getClass() + " singleton created.");
    }

    static synchronized void init() {
        if (instance != null) {
            terminateNoGiveUp(null,
                    1000,
                    instance.getClass() + " init error. Not allowed to initialize D_Thread twice!"
            );
        }
        instance = new D_Thread();
    }

    @Override
    // Temporarily don't implement own D-Thread, use Java EDT-Thread (from java.awt).
    public void repeat() /*throws InterruptedException*/ {

//        LOG.trace(super.getName() + " is managing the dashboard.");
    }

}