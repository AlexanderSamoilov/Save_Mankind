package com.company.gamethread;

import java.util.concurrent.Semaphore;

// Singleton
public class V_Thread extends Main.ThreadPattern {

    private static final V_Thread instance = new V_Thread("V-Thread");

    public static V_Thread getInstance() {
        return instance;
    }

    private V_Thread(String threadName) {
        super(threadName);
    }

    @Override
    public void repeat() throws InterruptedException {

        Semaphore sem = Main.ParameterizedMutexManager.getInstance().getMutex("C", "recalc");
        sem.acquire();
        // Main.printMsg("-> " + super.getName() + " is drawing. Permits: " + String.valueOf(sem.availablePermits()));
        //GameMap.getInstance().rerandom();
        //GameMap.getInstance().render(); - moved to EDT
        //GameMap.getInstance().print();
        Main.getFrame().repaint(0);
        // Main.printMsg("<- " + super.getName() + " is drawing. Permits: " + String.valueOf(sem.availablePermits()));
        sem.release();
    }

}