package com.gamethread;

import com.gamecontent.Bullet;
import com.gamecontent.GameMap;
import com.gamecontent.Player;
import com.gamecontent.Unit;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

// Singleton
public class C_Thread extends Main.ThreadPattern {

    private static final C_Thread instance = new C_Thread("C-Thread");
    public static C_Thread getInstance() {
        return instance;
    }
    private C_Thread(String threadName) {
        super(threadName);
    }

    @Override
    public void repeat() throws InterruptedException {

        Main.ParameterizedMutexManager.getInstance().getMutex("V", "recalc").acquire();
        Main.printMsg(super.getName() + " is calculating.");
        // recalculate positions of each game object
        // TODO: do it according to our documentation (swap pointers worldCurr, worldNext)
        if (GameMap.getInstance().getBullets() != null) {
            HashSet<Bullet> blts = (HashSet<Bullet>)GameMap.getInstance().getBullets().clone();
            for (Bullet b : blts) {
                b.move();
            }
        }
        if (Player.getPlayers() != null) {
            for (Player pl : Player.getPlayers()) {
                if (pl.getUnits() != null) {
                    for (Unit u : pl.getUnits()) {
                        u.processTargets();
                    }
                }
            }
        }
        ((Semaphore) Main.ParameterizedMutexManager.getInstance().getMutex("V", "recalc")).release();
    }

}