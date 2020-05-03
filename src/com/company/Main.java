package com.company;

import com.company.gamethread.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

import com.company.gamecontrollers.MainWindow;
import com.company.gamemath.cortegemath.CortegeTest;
import com.company.gamelogger.LogConfFactory;
import com.company.gamecontent.*;
import com.company.gametools.Tools;

public class Main {

    // Remember ID of the main thread to give it to other threads
    // PROBLEM: For WJ DDA I moved thread related code to M_Thread.java,
    // but I could not move also this variable into it, because then
    // it can be modified from other threads
    public static final long threadId = Thread.currentThread().getId();

    /* ATTENTION! */
    /* Don't use System.out.println! Since we use very powerful Thread.suspend() method
    in this software, we must not call anywhere System.out.println(), because it results to deadlock:
    https://stackoverflow.com/questions/36631153/deadlock-with-system-out-println-and-a-suspended-thread
    */
    private static Logger LOG;

    private static void initLoggers() {
        ConfigurationFactory.setConfigurationFactory(new LogConfFactory());
        LOG = LogManager.getLogger(Main.class.getName());
        LOG.info("The loggers are ready!");
    }

    private static void initMap() {
        try {
            /*
             Call GameMap singleton constructor explicitly (the map is created here).
             We have to do it in order to control when the map is created(initialized).
             Despite GameMap singleton instance is "static final", it is not called on the class loading stage
             (I see this from the stack trace printed out from the GameMap() singleton constructor).
             */
            GameMap.getInstance();
        } catch (Exception e) {
            M_Thread.terminateNoGiveUp(e,1000, null);
        }
    }

    // TODO Move to some inits()
    private static void initObjects() {
        GameMapGenerator.generateRandomUnits();
        LOG.info("The game objects have been initialized!");
    }

    // Main thread of the game. Starts other game thread in the correct order. Exist if and only if all other threads exited.
    public static void main(String[] args) throws InterruptedException {

        initLoggers();
        CortegeTest.testOperators();
        MainWindow.initControllers();
        initMap();
        initObjects();
        MainWindow.initGraph();

        LOG.debug("players:" + Player.getPlayers().length);

        if (D_Thread.getInstance().start(100, 10)) {
            M_Thread.terminateNoGiveUp(null,1000, null);
        }

        if (V_Thread.getInstance().start(100, 10)) {
            M_Thread.terminateNoGiveUp(null,1000, null);
        }

        // Wait for D-Thread to get ready (at the same time D-Thread is waiting for V-Thread to get ready)
        try {
            (ParameterizedMutexManager.getInstance().getMutex("D", "getReady")).acquire();
        } catch (Exception e) {
            M_Thread.terminateNoGiveUp(e,1000, "Failed to get mutex for D-Thread.");
        }

        // Wait for V-Thread to get ready
        try {
            (ParameterizedMutexManager.getInstance().getMutex("V", "getReady")).acquire();
        } catch (Exception e) {
            M_Thread.terminateNoGiveUp(e,1000, "Failed to get mutex for V-Thread.");
        }

        // 4. Initialize and start C-Thread.
        if (C_Thread.getInstance().start(100, 10)) {
            M_Thread.terminateNoGiveUp(null,1000, null);
        }

        // FIXME Move dis to class ThreadPool
        int deadStatus = 0;

        // Endless loop of the main thread. Here we just catch signals and do nothing more.
        while(!M_Thread.SIGNAL_TERM_GENERAL) {
            Tools.timeout(1000);
            // NOTE: I have an idea of some intelligent implementation - to respawn the thread if it dies due to really unexpected events, like
            // Java internal bug or an external influence (for example, if OS killed the thread)
            // I did not implement it, because at the moment I don't know how is it possible to notify the parent thread
            // immediately when the child thread dies. Probably, it is even not possible.
            // Tracking of the threads state in a loop with isAlive() is NOT safe, because between two loop iterations
            // some small time passes, so there is very low chance that during this time another threads try to get access
            // to mutexes associated with the dead thread.
            // I left the investigation of this question for the future, but oin case I find the solution
            // I leave here the list of actions which must be done if some thread is not alive and we want to respawn it:
            //  - block all threads
            //  - clear cache for dead thread AND think about what to do with still helded locks associated with dead thread
            //  - respawn dead thread
            //  - update cache for it taking into account new threadID?
            // Here is something to think about: https://stackoverflow.com/questions/12521776/what-happens-to-the-lock-when-thread-crashes-inside-a-synchronized-block.
            // I suppose it is possible to implement it, but to be very sure it is better to add also one more check in the loop
            // which checks if some of thread is in the waiting state very long (more than a given timeout seconds)
            // and exit the game or kill/respawn the thread in this case, BUT with the obligatory logging message about which lock
            // and from which other thread the given thread waited and could not acquire.
            //}

            if (!D_Thread.getInstance().isAlive()) {
                deadStatus++;
            }
            if (!V_Thread.getInstance().isAlive()) {
                deadStatus++;
            }
            if (!C_Thread.getInstance().isAlive()) {
                deadStatus++;
            }

            if (deadStatus > 0) {
                LOG.error(deadStatus + " of game threads were unexpectedly terminated. To ensure the correct game flow we must exit. Please, restart the game.");
                break;
            }
        }

        if (deadStatus == 0) {
            // If no one thread died then we wait for all threads to exit normally {
            // End of the game (wrap up actions)
            // We expect not just InterruptedException, but general Exception, because MutexManager can throw many times of exception from inside *_Thread
            try {
                C_Thread.getInstance().join(); // wait the C-thread to finish
            } catch (Exception eOuter) {
                Tools.printStackTrace(eOuter);
                C_Thread.getInstance().terminate(1000);
            }

            try {
                V_Thread.getInstance().join(); // wait the V-thread to finish
            } catch (Exception eOuter) {
                Tools.printStackTrace(eOuter);
                V_Thread.getInstance().terminate(1000);
            }

            try {
                D_Thread.getInstance().join(); // wait the D-thread to finish
            } catch (Exception eOuter) {
                Tools.printStackTrace(eOuter);
                D_Thread.getInstance().terminate(1000);
            }
        }

        // For sure
        // TODO Do check this sure
        M_Thread.terminateNoGiveUp(null,1000, null);
        LOG.warn("The game exited.");
        System.exit(deadStatus);
    }

}
