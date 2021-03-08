/* ***************** *
 * S I N G L E T O N *
 * ***************** */
package com.company.gamethread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class D_Thread extends ThreadTemplate {
    private static Logger LOG = LogManager.getLogger(D_Thread.class.getName());

    // Singleton
    private static final D_Thread instance = new D_Thread("D-Thread");
    public static synchronized D_Thread getInstance() {
        return instance;
    }
    private D_Thread(String threadName) {
        super(threadName);
        LOG.debug(getClass() + " singleton created.");
    }

    @Override
    // Temporarily don't implement own D-Thread, use Java EDT-Thread (from java.awt).
    public void repeat() /*throws InterruptedException*/ {

//        LOG.trace(super.getName() + " is managing the dashboard.");
    }

}