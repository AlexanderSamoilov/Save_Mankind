package com.company.gamethread;

import com.company.gamecontent.*;

import java.util.HashSet;
import java.util.concurrent.Semaphore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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

    @Override
    public void repeat() throws InterruptedException {

        Semaphore sem = Main.ParameterizedMutexManager.getInstance().getMutex("V", "recalc");
        sem.acquire();

        LOG.debug("-> " + super.getName() + " is calculating. Permits: " + String.valueOf(sem.availablePermits()));

        // recalculate positions of each game object
        // TODO: do it according to our documentation (swap pointers worldCurr, worldNext)
        if (GameMap.getInstance().getBullets() != null) {
            HashSet<Bullet> bullets = (HashSet<Bullet>)GameMap.getInstance().getBullets().clone();

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

        LOG.debug("<- " + super.getName() + " is calculating. Permits: " + String.valueOf(sem.availablePermits()));
        sem.release();
    }

}