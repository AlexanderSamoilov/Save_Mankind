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

import com.company.gamecontent.*;

import static com.company.gamethread.M_Thread.terminateNoGiveUp;

public class C_Thread extends ThreadTemplate {
    private static Logger LOG = LogManager.getLogger(C_Thread.class.getName());

    private static C_Thread instance = null;
    public static synchronized C_Thread getInstance() {
        return instance;
    }
    private C_Thread() {
        super("C-Thread");
        LOG.debug(getClass() + " singleton created.");
    }

    static synchronized void init() {
        if (instance != null) {
            terminateNoGiveUp(null,
                    1000,
                    instance.getClass() + " init error. Not allowed to initialize C_Thread twice!"
            );
        }
        instance = new C_Thread();
    }

    @Override
    public void repeat() throws InterruptedException {

        Semaphore sem = ParameterizedMutexManager.getInstance().getMutex("V", "recalc");
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            // It is naturally to have this exception while the thread is dying
            // Don't report about it in case of total termination
            if (! M_Thread.SIGNAL_TERM_GENERAL) throw(e);
        }

        LOG.trace("-> " + super.getName() + " is calculating. Permits: " + String.valueOf(sem.availablePermits()));

        // recalculate positions of each game object
        // TODO: do it according to our documentation (swap pointers worldCurr, worldNext)

        // NO clone required, although .move() modifies the bullets collection (use ConcurrentHashSet).
        for (Bullet b : GameMap.getInstance().bullets) {
            b.move();
        }

        for (Player pl : Player.players) {
            // NO clone required, although .processTargets() modifies the units collection (use ConcurrentHashSet).
            for (Unit u : pl.units) {
                u.processTargets();
            }
        }

        LOG.trace("<- " + super.getName() + " is calculating. Permits: " + String.valueOf(sem.availablePermits()));
        try {
            sem.release();
        } catch (Exception e) {
            // It is naturally to have this exception while the thread is dying
            // Don't report about it in case of total termination
            if (! M_Thread.SIGNAL_TERM_GENERAL) throw(e);
        }
    }

}