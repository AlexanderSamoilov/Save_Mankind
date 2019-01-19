package com.gamethread;

import com.gamecontent.*;
import com.gamecontrollers.MouseController;
import com.gamegraphics.Sprite;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

// graphics
import java.awt.image.BufferStrategy;

public class Main {

    //public static Player[] players = new Player[2]; // TODO: move to GameMap?

    // global graphics variables
    //private static Canvas canvOber = new Canvas();
    //private static Canvas canvUnter = new Canvas();
    //private static BufferStrategy bs = null;
    //private static Graphics grap = null;
    private static JFrame frame = new JFrame("Sosochek Konchika");
    private static long threadId = -1;
    private static boolean TRACE_ON = false;
    private static String LOGFILE = "game.log";

    // TODO: consider cases when mouse goes outside the Jframe and coordinates are negative
    public static class MouseRect extends JPanel {

        private int x = -1;
        private int y = -1;
        private int wid = 0;
        private int hei = 0;

        public void redefineRect(int x0, int y0, int wid0, int hei0) {

            //Main.printMsg("before(" + "(" + x + "," + y + " -> (" + wid + "," + hei + ")");
            //Main.printMsg("after (" + "(" + x0 + "," + y0 + " -> (" + wid0 + "," + hei0 + ")");

            // vanish old rectangle
            if (x != -1) {
                repaint(new Rectangle(x, y, wid, 1));
                repaint(new Rectangle(x, y + hei, wid, 1));
                repaint(new Rectangle(x, y, 1, hei));
                repaint(new Rectangle(x + wid, y, 1, hei));
            }
            // draw new rectangle
            if (x0 != -1) {
                repaint(new Rectangle(x0, y0, wid0, 1));
                repaint(new Rectangle(x0, y0 + hei0, wid0, 1));
                repaint(new Rectangle(x0, y0, 1, hei0));
                repaint(new Rectangle(x0 + wid0, y0, 1, hei0));
            }
            x = x0;
            y = y0;
            wid = wid0;
            hei = hei0;
            //Main.printMsg("rect repaint done.");
        }

        @Override  // this is in a JPanel extended class
        public void paintComponent(Graphics g) {
            //Main.printMsg("Painting from MouseRect.paint[" + Thread.currentThread().getId() + "].");
            super.paintComponent(g);
            g.setColor(Color.RED);
            //co.drawArc(getx(), gety(), getWidth() - 20, getHeight() - 20, 0, 360);
            g.drawRect(x,y,wid,hei);
        }
    };

    private static JPanel jpOber = new JPanel() {
        @Override  // this is in a JPanel extended class
        public void paintComponent(Graphics g) {
            //Main.printMsg("Painting from jpOber.paint[" + Thread.currentThread().getId() + "].");
            super.paintComponent(g);
            //g.drawArc(10, 10, getWidth() - 20, getHeight() - 20, 0, 360);
        }
    };
    private static JPanel jpUnter = new JPanel() {
        @Override  // this is in a JPanel extended class
        public void paintComponent(Graphics g) {
            //Main.printMsg("Painting from jpUnter.paint[" + Thread.currentThread().getId() + "].");
            super.paintComponent(g);
            GameMap.getInstance().render(g);
        }
    };
    private static MouseRect mouseRectangle = new MouseRect();

    public static JFrame getFrame() {
        return frame;
    }

    public static JPanel getPanelUnter() {
        return jpUnter;
    }

    public static JPanel getPanelOber() {
        return jpOber;
    }

    public static MouseRect getMouseRect() {
        return mouseRectangle;
    }

    // controllers
    private static MouseController mouseController = new MouseController();
    public static MouseController getMouseController() {
        return mouseController;
    }


    public static class ParameterizedMutexManager extends MutexManager<String,Semaphore> {
        public static ParameterizedMutexManager instance = null;
        public static ParameterizedMutexManager getInstance() {
            if (instance == null) {
                instance = new ParameterizedMutexManager();
            }
            return instance;
        }
        private ParameterizedMutexManager() { // must be private, but otherwise I cannot inherit in Main.
            super();
        }
    } // we must derive from a generic type to use a full power of getGenericSuperclass() (avoiding type erasure)

    /* ATTENTION! */
    /* Don't use System.out.println! Since we use very powerful Thread.suspend() method
    in this software, we must not call anywhere System.out.println(), because it results to deadlock:
    https://stackoverflow.com/questions/36631153/deadlock-with-system-out-println-and-a-suspended-thread
     */
    public static void printMsg(String str) {
        if (TRACE_ON) {
            final File file = new File(LOGFILE);
            try (final Writer writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write("[" + Thread.currentThread().getId() + "]" + str + "\n");
            } catch (Exception e) {
                // Don't do it!!
                //System.out.println("Cannot write to the game log.");
                System.exit(2);
            }
        }
    }

    public static void timeout(long timeoutMSec) {
        /* TODO: what happens if we pass negative timeout into sleep()? */
        try {
            Thread.sleep(timeoutMSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // This is not a big trouble that we were not able to do sleep, so it is not a reason to interrupt the method on this
        }
    }

    public static long getThreadId() {
        return threadId;
    }
    // "Pattern" class for creation of singletones
    public static class ThreadPattern extends Thread {
        protected final String name;
        protected long TIME_QUANT = 200;
        protected boolean SIGNAL_TERM_FROM_PARENT;

        private static ThreadPattern instance = null;
        public static ThreadPattern getInstance() {
            return instance;
        }
        protected ThreadPattern(String threadName) {
            name = threadName;
            SIGNAL_TERM_FROM_PARENT = false;
        }

        @Override @Deprecated
        public void start() {
            throw new UnsupportedOperationException("void start() method with no arguments is forbidden. Use start(int, long) instead.");
            // interrupt(); - is it possible that the method start() of the base class anyway started the run() at this moment?
        }

        public boolean start(int attempts, long timeoutMSec)
        {
            boolean rc = true;
            StackTraceElement [] ste = null;
            while (rc) {
                try {
                    printMsg("Thread " + super.getId() + " is starting");
                    super.start();
                    printMsg("Thread " + super.getId() + " started.");
                    rc = false;
                } catch (Exception e) {
                    ste = e.getStackTrace();
                    timeout(timeoutMSec);
                }
            }
            if (rc) {
                printMsg("Thread " +super.getId() + " failed to start after " + attempts + " attempts.");
                for (StackTraceElement steElement : ste) {
                    printMsg(steElement.toString());
                }
            }
            return rc;
        }

        @Override
        @Deprecated // Derived classes should use repeat() instead.
        public void run()
        {
            try
            {
                printMsg("Thread " + super.getId() + " is running");
                while (!interrupted() && !SIGNAL_TERM_FROM_PARENT) {
                    repeat(); // All the functionality is incapsulated here.
                    timeout(TIME_QUANT);
                }
                printMsg("Thread " + super.getId() + " finished.");
            }
            catch (InterruptedException e)
            {
                printMsg("Thread " + super.getId() + " died!");
                e.printStackTrace();
            }
            //super.run(); // Thread.run() is empty, so we don't need it.
        }

        public void repeat() throws InterruptedException {
            // Pure functionality without error handling
            // Is to be used by derived classes
        }

        // TODO: Should we release all mutexes held by a thread before terminating?
        // TODO: What if we call this function when the thread is held by another thread?
        public boolean terminate(long timeoutMsec) throws InterruptedException {
            SIGNAL_TERM_FROM_PARENT = true;
            timeout(timeoutMsec); // wait and hope that the thread finish its work normally

            // still did not exit - it's a pity...
            if (isAlive()) {
                /* Hard termination (See https://stackoverflow.com/questions/671049/how-do-you-kill-a-thread-in-java). */
                interrupt();
                super.interrupt(); // TODO: check of we need this
            } else {
                return false;
            }

            if (isAlive()) { // failed to interrupt
                return true;
            }
            return false;
        }
    } // end of class ThreadPattern

    //public class ThreadPatternImpl extends ThreadPattern { }

    private static enum Choice {
        CHOICE_EXIT_IMMEDIATELY,
        CHOICE_EXIT,
        CHOICE_PAUSE,
        CHOICE_RESUME,
        CHOICE_CANCEL
    }

    // public Main();
    static boolean SIGNAL_TERM_GENERAL = false;

    /*
    This should be in the main thread and not in D-Thread, otherwise we will have to synchronize D-Thread
    with the main thread on exiting moment that is complicated (what if before we suspend D-Thread it also does some exiting stuff?
    will not it lead to double exiting with unexpected consequences?)
    */

    private static void destroy() {
        // Here we implement releasing of allocated memory for all objects.
        frame.dispose();
    }

    private static boolean terminate(long timeoutMsec) {
        boolean rc = true;
        try {
            C_Thread.getInstance().terminate(timeoutMsec);
            V_Thread.getInstance().terminate(timeoutMsec);
            D_Thread.getInstance().terminate(timeoutMsec);
            destroy(); // delete all objects
            SIGNAL_TERM_GENERAL = true;
            rc = false;
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
        }
        return rc;
    }

    public static void terminateNoGiveUp(long timeoutMsec) {
        printMsg(" --- total terminate! ---");
        ////ErrWindow ew = displayErrorWindow("The game was interrupted due to exception. Exiting...(if this window does not disappear for a long time, kill the game process manually from OS.)");
        while (terminate(timeoutMsec)) {
            timeout(10); // just for safety we set here some small timeout unconfigurable to avoid brutal rush of terminate() requests
        }
        ////ew.close();
        destroy();
        System.exit(1);
    }

    public static void suspendAll() {
        D_Thread.getInstance().suspend();
        //Main.printMsg("D suspended");
        V_Thread.getInstance().suspend();
        //Main.printMsg("V suspended");
        C_Thread.getInstance().suspend();
        //Main.printMsg("C suspended");
        Main.printMsg("--- suspended ---");
    }

    public static void resumeAll() {
        D_Thread.getInstance().resume();
        V_Thread.getInstance().resume();
        C_Thread.getInstance().resume();
        Main.printMsg("--- resumed ---");
    }
    // This function handles ESC key press (it runs in a special unnamed thread automatically by Java mechanisms)
    // I think that this must be handled in the main thread, because it is the highest priority action - game state managing.
    public void escKeyHandling() {
        suspendAll();
        Main.Choice choice = Choice.CHOICE_EXIT; //// = handlePauseWindow(); // The window with 4 buttons: Exit Immediately, Exit, Pause, Cancel

        if (choice == Choice.CHOICE_EXIT_IMMEDIATELY) {
            terminate(0);
        } else if (choice == Choice.CHOICE_EXIT) {
            terminate(1000);
        } else if (choice == Choice.CHOICE_PAUSE) {
            // Just keep being suspended...
        } else if (choice == Choice.CHOICE_RESUME) {
            resumeAll();
        } else if (choice == Choice.CHOICE_CANCEL) {
            // Do nothing
        } else {
            // TODO: Display a new window
            //ErrWindow ew = displayErrorWindow("No such choice: " + choice.toString() + ". Exiting...(if this window does not disappear for a long time, kill the game process manually from OS.)");
            terminateNoGiveUp(1000);
            //ew.close();
        }
    }

    private static void initMap(int [][] map, int wid, int len) {
        try {
            GameMap.getInstance().init(map, wid, len);
        } catch (IllegalArgumentException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("IllegalArgumentException during map initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        } catch (ArrayIndexOutOfBoundsException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("ArrayIndexOutOfBoundsException during map initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        } catch (IIOException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("IIOException during map initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        } catch (IOException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("IOException during map initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        }
        printMsg("Initialized map " + wid + "x" + len);
    }

    private static void initObjects() {
        // Player
        HashMap<Resource,Integer> testPlayerTankResources = new HashMap<Resource,Integer>();
        testPlayerTankResources.put(Resource.MASS, 500);
        testPlayerTankResources.put(Resource.ENERGY, 1000);
        Sprite testPlayerTankSprite = null;
        try {
            testPlayerTankSprite = new Sprite("tank_composed.png");
        } catch (IIOException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("IIOException during initial player's unit set initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        } catch (IOException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("IOException during initial player's unit set initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        }

        int testNumTanks = GameMap.getInstance().getWid() / 2 - 1;
        ArrayList<Unit> testPlayerUnits = new ArrayList<Unit>();
        for (int i=0; i <= testNumTanks - 1; i++) {
            testPlayerUnits.add(new Unit(new Weapon(100, 200, 15, 6, 7), 5, testPlayerTankSprite, 2*i + 1, 1, 0, 1, 2, 1, testPlayerTankResources, 500, 6, 25, 15, 5, 5, 5));
        }

        HashMap<Resource,Integer> testPlayerResources = new HashMap<Resource,Integer>();
        testPlayerResources.put(Resource.MASS, 5000);
        testPlayerResources.put(Resource.ENERGY, 10000);

        Player testPlayer = new Player(Race.RaceType.HUMANS,"Toughie", testPlayerResources, null, testPlayerUnits);

        // Enemy
        HashMap<Resource,Integer> testEnemyTankResources = new HashMap<Resource,Integer>();
        testEnemyTankResources.put(Resource.MASS, 500);
        testEnemyTankResources.put(Resource.ENERGY, 1000);

        Sprite testEnemyTankSprite = null;
        try {
            testEnemyTankSprite = new Sprite("e_tank_composed.png");
        } catch (IIOException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("IIOException during initial enemy's unit set initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        } catch (IOException e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }
            Main.printMsg("IOException during initial enemy's unit set initialization. The game will be terminated now.");
            terminateNoGiveUp(1000);
        }

        int testEnemyTanks = GameMap.getInstance().getWid() / 2 - 1;
        ArrayList<Unit> testEnemyUnits = new ArrayList<Unit>();
        for (int i=0; i <= testEnemyTanks - 1; i++) {
            testEnemyUnits.add(new Unit(new Weapon(300, 150, 10, 8, 5), 7, testEnemyTankSprite, Restrictions.getMaxX() - 2 - 2*i, Restrictions.getMaxY() - 2, 0, 1, 1, 1, testEnemyTankResources, 500, 7, 25, 15, 5, 5, 5));
        }

        HashMap<Resource,Integer> testEnemyResources = new HashMap<Resource,Integer>();
        testEnemyResources.put(Resource.MASS, 5000);
        testEnemyResources.put(Resource.ENERGY, 10000);

        Player testEnemy = new Player(Race.RaceType.ROBOTS,"JavaBot", testEnemyResources, null, testEnemyUnits);

    }

    public static void initGraph() {
        try {

            jpOber.setLayout(new BorderLayout());
            jpOber.setBounds(0,0,GameMap.getInstance().getWidAbs(), GameMap.getInstance().getLenAbs());
            jpOber.setOpaque(false);
            mouseRectangle.setOpaque(false);
            jpOber.add(mouseRectangle);

            //jpUnter.setLayout(null);
            jpUnter.setBounds(0,0,GameMap.getInstance().getWidAbs(), GameMap.getInstance().getLenAbs());
            jpUnter.setOpaque(true);

            // Создаём окно, в котором будет запускаться игра
            frame.setLayout(null); // setting layout to null so we can make panels overlap
            frame.add(jpOber);     // Добавляем холст на наш фрейм
            frame.add(jpUnter);     // Добавляем холст на наш фрейм
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            // Определяем размер приложения
            frame.setPreferredSize(new Dimension(GameMap.getInstance().getWidAbs(), GameMap.getInstance().getLenAbs()));
            frame.pack();                             // Сворачиваем окно до размера приложения
            frame.setResizable(false);
            frame.setFocusable(true);
            frame.setLocationRelativeTo(null);        // Установка окна в центр экрана
            //frame.setUndecorated(true);
            //frame.setOpacity(0.50f);

            // Лучше написать слушателя событий, который может контролировать приложение
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    /* Для закрытия окна будем использовать наши методы класса */
                    Main.printMsg("Exiting game, because the main windows was closed on user's demand.");
                    destroy();
                    terminateNoGiveUp(1000);
                }
            });


            Main.printMsg(" ------ Make EDT thread to drawing ------ ");
            frame.setVisible(true);
            //frame.requestFocus();                     // Установка фокуса для контроллеров игры
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            Main.printMsg(" ------ EDT thread finished drawing ------ ");
            //System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            Main.printMsg("Failed to initialize game graphics. The game will be terminated now.");
            destroy();
            terminateNoGiveUp(1000);
        }
    }

    // Main thread of the game. Starts other game thread in the correct order. Exist if and only if all other threads exited.
    public static void main(String[] args) throws InterruptedException {
        // init logging
        File logFile = new File(LOGFILE);
        if (logFile.exists()) {
            logFile.delete();
        }

        printMsg("The game starts.");
        threadId = Thread.currentThread().getId(); // remember ID of the main thread to give it to other threads
        // 1. Initialize map and units of all players.
        //map
        int wid = Restrictions.getMaxX();
        int len = Restrictions.getMaxY();
        int [][] map = new int[wid][len];
        for(int i=0; i<wid;i++) {
            for (int j = 0; j < len; j++) {
                //map[i][j] = (i * i + j * j) % 6; // pseudo-random
                map[i][j] = ((i * i + j * j) / 5) % 6;
            }
        }

        initMap(map, wid, len);

        // units
        initObjects();

        // graphics initialization
        initGraph();

        // controllers initialization
        jpUnter.addMouseListener(getMouseController());
        jpUnter.addMouseMotionListener(getMouseController());
        jpUnter.addMouseWheelListener(getMouseController());

        Main.printMsg("players:" + Player.getPlayers().length);
        // 2. Initialize and start D-Thread.
        if (D_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000);
        }
        // 3. Initialize and start V-Thread.
        if (V_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000);
        }

        // wait for D-Thread to get ready (at the same time D-Thread is waiting for V-Thread to get ready)
        ((Semaphore) ParameterizedMutexManager.getInstance().getMutex("D", "getReady")).acquire();
        // wait for V-Thread to get ready
        ((Semaphore) ParameterizedMutexManager.getInstance().getMutex("V", "getReady")).acquire();

        // 4. Initialize and start C-Thread.

        if (C_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000);
        }

        int deadStatus = 0;
        while(!SIGNAL_TERM_GENERAL) { // Endless loop of the main thread. Here we just catch signals and do nothing more.
            timeout(1000);
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
                printMsg(deadStatus + " of game threads were unexpectedly terminated. To ensure the correct game flow we must exit. Please, restart the game.");
                break;
            }
        }

        if (deadStatus == 0) { // If no one thread died then we wait for all threads to exit normally {
            // End of the game (wrap up actions)
            // We expect not just InterruptedException, but general Exception, because MutexManager can throw many times of exception from inside *_Thread
            try {
                C_Thread.getInstance().join(); // wait the C-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                try {
                    C_Thread.getInstance().terminate(1000);
                } catch (Exception eInner) {
                    eInner.printStackTrace();
                }
            }

            try {
                V_Thread.getInstance().join(); // wait the V-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                try {
                    V_Thread.getInstance().terminate(1000);
                } catch (Exception eInner) {
                    eInner.printStackTrace();
                }
            }

            try {
                D_Thread.getInstance().join(); // wait the D-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                try {
                    D_Thread.getInstance().terminate(1000);
                } catch (Exception eInner) {
                    eInner.printStackTrace();
                }
            }
        }

        // For sure
        terminateNoGiveUp(1000);
        printMsg("The game exited.");
        System.exit(deadStatus);
    }
}
