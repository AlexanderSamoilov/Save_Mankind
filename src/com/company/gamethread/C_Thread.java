package com.company.gamethread;

import com.company.gamecontent.*;

import java.util.HashSet;
import java.util.concurrent.Semaphore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Singleton
public class C_Thread extends Main.ThreadPattern {
    private static Logger LOG = LogManager.getLogger(C_Thread.class.getName());

    private static final C_Thread instance = new C_Thread("C-Thread");
    public static C_Thread getInstance() {
        return instance;
    }
    private C_Thread(String threadName) {
        super(threadName);
    }

    public void recalc() {
        // recalculate positions of each game object
        if (GameMap.getNextInstance().getBullets() != null) {
            HashSet<Bullet> bullets = (HashSet<Bullet>)GameMap.getNextInstance().getBullets().clone();

            for (Bullet b : bullets) {
                b.move();
            }
        }

        // TODO bad realisation. We must use collections to iterate all units
        // TODO May be != null check move in getPlayers(), getUnits()?
        if (Player.getPlayers() != null) {
            for (Player pl : Player.getPlayers()) {
                if (pl.getUnits() != null) {
                    for (Unit u : pl.getUnits()) {
                        u.processTargets();
                    }
                }
            }
        }
    }

    @Override
    public void repeat() throws InterruptedException {

        recalc(); // calculate next game state

        Semaphore sem = ParameterizedMutexManager.getInstance().getMutex("V", "recalc");
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            // It is naturally to have this exception while the thread is dying
            // Don't report about it in case of total termination
            if (Main.SIGNAL_TERM_GENERAL != true) throw(e);
        }

        LOG.trace("-> " + super.getName() + " is calculating. Permits: " + String.valueOf(sem.availablePermits()));

        GameMap.switchRoles();

        LOG.trace("<- " + super.getName() + " is calculating. Permits: " + String.valueOf(sem.availablePermits()));
        try {
            sem.release();
        } catch (Exception e) {
            // It is naturally to have this exception while the thread is dying
            // Don't report about it in case of total termination
            if (Main.SIGNAL_TERM_GENERAL != true) throw(e);
        }
    }

}