/* ***************** *
 * S I N G L E T O N *
 * ***************** */
package com.company.gamethread;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamecontrollers.MainWindow;

public class V_Thread extends ThreadTemplate {
    private static Logger LOG = LogManager.getLogger(V_Thread.class.getName());

    // Singleton
    private static final V_Thread instance = new V_Thread("V-Thread");
    public static synchronized V_Thread getInstance() {
        return instance;
    }
    private V_Thread(String threadName) {
        super(threadName);
        LOG.debug(getClass() + " singleton created.");
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