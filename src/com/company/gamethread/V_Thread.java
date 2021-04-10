/* ***************** *
 * S I N G L E T O N *
 * ***************** */
/*
     Lazy thread-safe singleton initialization (possible to catch exception in Main).
     See https://www.geeksforgeeks.org/java-singleton-design-pattern-practices-examples.
 */
package com.company.gamethread;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamecontrollers.MainWindow;

import static com.company.gamethread.M_Thread.terminateNoGiveUp;

public class V_Thread extends ThreadTemplate {
    private static Logger LOG = LogManager.getLogger(V_Thread.class.getName());

    private static V_Thread instance = null;
    public static synchronized V_Thread getInstance() {
        return instance;
    }
    private V_Thread() {
        super("V-Thread");
        LOG.debug(getClass() + " singleton created.");
    }

    static synchronized void init() {
        if (instance != null) {
            terminateNoGiveUp(null,
                    1000,
                    instance.getClass() + " init error. Not allowed to initialize V_Thread twice!"
            );
        }
        instance = new V_Thread();
    }

    @Override
    public void repeat() throws InterruptedException {

        Semaphore sem = ParameterizedMutexManager.getInstance().getMutex("C", "recalc");
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            // It is naturally to have this exception while the thread is dying
            // Don't report about it in case of total termination
            if (! M_Thread.SIGNAL_TERM_GENERAL) throw(e);
        }
//         LOG.trace("-> " + super.getName() + " is drawing. Permits: " + String.valueOf(sem.availablePermits()));
        //GameMap.getInstance().rerandom();
        //GameMap.getInstance().render(); - moved to EDT
        //GameMap.getInstance().print();
        MainWindow.getInstance().frame.repaint(0);
//         LOG.trace("<- " + super.getName() + " is drawing. Permits: " + String.valueOf(sem.availablePermits()));
        try {
            sem.release();
        } catch (Exception e) {
            // It is naturally to have this exception while the thread is dying
            // Don't report about it in case of total termination
            if (! M_Thread.SIGNAL_TERM_GENERAL) throw(e);
        }
    }

}