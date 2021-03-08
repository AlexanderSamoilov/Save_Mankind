/* ***************** *
 * S I N G L E T O N *
 * ***************** */
package com.company.gamethread;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamecontent.*;

public class C_Thread extends ThreadTemplate {
    private static Logger LOG = LogManager.getLogger(C_Thread.class.getName());

    // Singleton
    private static final C_Thread instance = new C_Thread("C-Thread");
    public static synchronized C_Thread getInstance() {
        return instance;
    }
    private C_Thread(String threadName) {
        super(threadName);
        LOG.debug(getClass() + " singleton created.");
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
        if (GameMap.getInstance().bullets != null) {
            // NO clone required, although .move() modifies the bullets collection (use ConcurrentHashSet).
            for (Bullet b : GameMap.getInstance().bullets) {
                b.move();
            }
        }

        // TODO May be != null check move in getPlayers(), getUnits()?
        if (Player.players != null) {
            for (Player pl : Player.players) {
                if (pl.units != null) {
                    // NO clone required, although .processTargets() modifies the units collection (use ConcurrentHashSet).
                    for (Unit u : pl.units) {
                        u.processTargets();
                    }
                }
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