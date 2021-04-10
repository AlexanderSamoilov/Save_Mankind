package com.company;

import com.company.gamethread.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;

import com.company.gamecontrollers.MainWindow;
import com.company.gamemath.cortegemath.CortegeTest;
import com.company.gamelogger.LogConfFactory;
import com.company.gamecontent.*;

public class Main {

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
             Trigger GameMap singleton creation.
             We have to do it in order to control when the map is created(initialized).
             */
            GameMap.init();
        } catch (Exception e) {
            M_Thread.terminateNoGiveUp(e,1000, "Failed GameMap creation!");
        }
    }

    private static void initObjects() {
        GameMapGenerator.generateRandomUnitsOnMap();
        LOG.info("The game objects have been initialized!");
    }

    // Main thread of the game. Starts other game thread in the correct order. Exist if and only if all other threads exited.
    public static void main(String[] args) {
        System.out.println(" ------------------------------ START ------------------------------ ");
        initLoggers();
        CortegeTest.main(null);
        initMap();
        initObjects();

        LOG.debug("players:" + Player.players.size());

        // Start child threads (D, C, V).
        M_Thread.start();

        // Main game loop (child threads are working).
        M_Thread.repeat();

        // Terminate remaining threads if still are running
        M_Thread.terminateNoGiveUp(null,1000, "Some threads did not terminate normally.");

        LOG.info("The game exited.");
        System.exit(M_Thread.deadStatus ? 1 : 0);
    }

}
