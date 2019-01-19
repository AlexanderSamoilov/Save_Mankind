package com.gamethread;

import com.gamecontent.*;
import com.gamecontrollers.MouseController;
import com.gamegraphics.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;


// FIXME To Class GameManager or GameState
enum Choice {
    EXIT_IMMEDIATELY,
    EXIT,
    PAUSE,
    RESUME,
    CANCEL
}


public class Main {

    //public static Player[] players = new Player[2]; // TODO: move to GameMap?

    // global graphics variables
    //private static Canvas canvOber = new Canvas();
    //private static Canvas canvUnter = new Canvas();
    //private static BufferStrategy bs = null;
    //private static Graphics grap = null;

    private static JFrame frame = new JFrame("Sosochek Konchika Bolshoy");
    private static long threadId = -1;
    private static String LOGFILE = "game.log";

    /* DEBUG */
    private static boolean TRACE_ON = false;
    private static boolean OUT_LOG = true;

    private static MouseRect mouseRectangle = new MouseRect();
    private static MouseController mouseController = new MouseController();

    // public Main();
    private static boolean SIGNAL_TERM_GENERAL = false;

    // Override basic method of EDT of JPanel
    private static JPanel jpOber = new JPanel();
//    {
//        // this is in a JPanel extended class
//        @Override
//        public void paintComponent(Graphics g) {
//            //Main.printMsg("Painting from jpOber.paint[" + Thread.currentThread().getId() + "].");
//            super.paintComponent(g);
//            //g.drawArc(10, 10, getWidth() - 20, getHeight() - 20, 0, 360);
//        }
//    };

    // Override basic method of EDT of JPanel
    private static JPanel jpUnter = new JPanel() {
        @Override
        // This is in a JPanel extended class
        public void paintComponent(Graphics g) {
            //Main.printMsg("Painting from jpUnter.paint[" + Thread.currentThread().getId() + "].");
            super.paintComponent(g);
            GameMap.getInstance().render(g);
        }
    };

    private static void initMap(int [][] terrain_map, int width, int height) {
        try {
            GameMap.getInstance().init(terrain_map, width, height);
        } catch (Exception e) {
            // This can print messages to file
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }

            terminateNoGiveUp(1000, null);
        }

        printMsg("Initialized map " + width + "x" + height);
    }

    // TODO Move to some inits()
    private static void initObjects() {
        // Initialising Player tank resources
        HashMap<Resource,Integer> testPlayerTankResources = new HashMap<Resource,Integer>();
        testPlayerTankResources.put(Resource.MASS, 500);
        testPlayerTankResources.put(Resource.ENERGY, 1000);

        // Initialising Player test tank Sprite
        Sprite testPlayerTankSprite = new Sprite("tank_composed.png");

        // This is a test. Initialising tanks for Player.
        // We try calculate how much tanks we can place on map by width
        int testNumTanks = GameMap.getInstance().getWidth() / 2 - 1;
//        int testNumTanks = 1;

        ArrayList<Unit> testPlayerUnits = new ArrayList<Unit>();
        for (int i=0; i <= testNumTanks - 1; i++) {
            testPlayerUnits.add(
                    new Unit(
                            new Weapon(
                                    100, 200, 15, 6, 7
                            ),
                            5, testPlayerTankSprite, 2*i + 1, 1, 0, 1, 2, 1,
                            testPlayerTankResources, 500, 6, 25, 15, 5, 5, 5
                    )
            );
        }

        // Initialising Enemy tank resources
        HashMap<Resource,Integer> testEnemyTankResources = new HashMap<Resource,Integer>();
        testEnemyTankResources.put(Resource.MASS, 500);
        testEnemyTankResources.put(Resource.ENERGY, 1000);

        // Initialising Enemy test tank Sprite
        Sprite testEnemyTankSprite = new Sprite("e_tank_composed.png");



        // This is a test. Initialising tanks for Player.
        // We try calculate how much tanks we can place on map by width
        int testEnemyTanks = GameMap.getInstance().getWidth() / 2 - 1;
//        int testEnemyTanks = 1;
        ArrayList<Unit> testEnemyUnits = new ArrayList<Unit>();
        for (int i=0; i <= testEnemyTanks - 1; i++) {
            testEnemyUnits.add(
                    new Unit(
                            new Weapon(
                                    300, 150, 10, 8, 5
                            ),
                            7, testEnemyTankSprite, Restrictions.getMaxX() - 2 - 2*i,
                            Restrictions.getMaxY() - 2, 0, 1, 1, 1,
                            testEnemyTankResources, 500, 7, 25, 15, 5, 5, 5
                    )
            );
        }

        // Initialising all Player resources
        HashMap<Resource,Integer> testPlayerResources = new HashMap<Resource,Integer>();
        testPlayerResources.put(Resource.MASS, 5000);
        testPlayerResources.put(Resource.ENERGY, 10000);

        // Initialising all Enemy resources
        HashMap<Resource,Integer> testEnemyResources = new HashMap<Resource,Integer>();
        testEnemyResources.put(Resource.MASS, 5000);
        testEnemyResources.put(Resource.ENERGY, 10000);

        // TODO Static creation
        // Initialising Player
        new Player(
                Race.RaceType.HUMANS,"Toughie", testPlayerResources, null, testPlayerUnits
        );

//        Player testPlayer = new Player(
//                Race.RaceType.HUMANS,"Toughie", testPlayerResources, null, testPlayerUnits
//        );

        // Initialising Enemy
        new Player(
                Race.RaceType.ROBOTS,"JavaBot", testEnemyResources, null, testEnemyUnits
        );

//        Player testEnemy = new Player(
//                Race.RaceType.ROBOTS,"JavaBot", testEnemyResources, null, testEnemyUnits
//        );

    }

    public static void initGraph() {
        // FIXME do not use try for all of them.
        try {
            jpOber.setLayout(new BorderLayout());
            jpOber.setBounds(0,0,GameMap.getInstance().getWidthAbs(), GameMap.getInstance().getHeightAbs());
            jpOber.setOpaque(false);
            mouseRectangle.setOpaque(false);
            jpOber.add(mouseRectangle);

            //jpUnter.setLayout(null);
            jpUnter.setBounds(0,0,GameMap.getInstance().getWidthAbs(), GameMap.getInstance().getHeightAbs());
            jpUnter.setOpaque(true);

            // Создаём окно, в котором будет запускаться игра
            frame.setLayout(null); // setting layout to null so we can make panels overlap
            frame.add(jpOber);     // Добавляем холст на наш фрейм
            frame.add(jpUnter);     // Добавляем холст на наш фрейм
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Определяем размер приложения
            frame.setPreferredSize(new Dimension(GameMap.getInstance().getWidthAbs(), GameMap.getInstance().getHeightAbs()));
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
                    destroy();
                    terminateNoGiveUp(
                            1000,
                            "Exiting game, because the main windows was closed on user's demand."
                    );
                }
            });

            Main.printMsg(" ------ Make EDT thread to drawing ------ ");
            frame.setVisible(true);

            // Установка фокуса для контроллеров игры
            //frame.requestFocus();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}

            Main.printMsg(" ------ EDT thread finished drawing ------ ");
            //System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
            destroy();
            terminateNoGiveUp(
                    1000,
                    "Failed to initialize game graphics. The game will be terminated now."
            );
        }
    }

    // TODO: consider cases when mouse goes outside the Jframe and coordinates are negative
    // FIXME Class-in-Class.
    // FIXME Set this singletone
    public static class MouseRect extends JPanel {

        private int x = -1;
        private int y = -1;
        private int width = 0;
        private int height = 0;

        public void redefineRect(int x, int y, int w, int h) {

            //Main.printMsg("before(" + "(" + x + "," + y + " -> (" + wid + "," + hei + ")");
            //Main.printMsg("after (" + "(" + x0 + "," + y0 + " -> (" + wid0 + "," + hei0 + ")");

            // TODO Do right this
            // vanish old rectangle
            if (x != -1) {
                repaint(new Rectangle(this.x, this.y, width, 1));
                repaint(new Rectangle(this.x, this.y + height, width, 1));
                repaint(new Rectangle(this.x, this.y, 1, height));
                repaint(new Rectangle(this.x + width, this.y, 1, height));
            }

            // draw new rectangle
            if (x != -1) {
                repaint(new Rectangle(x, y, w, 1));
                repaint(new Rectangle(x, y + h, w, 1));
                repaint(new Rectangle(x, y, 1, h));
                repaint(new Rectangle(x + w, y, 1, h));
            }

            this.x      = x;
            this.y      = y;
            this.width  = w;
            this.height = h;
            //Main.printMsg("rect repaint done.");
        }

        @Override  // this is in a JPanel extended class
        public void paintComponent(Graphics g) {
            //Main.printMsg("Painting from MouseRect.paint[" + Thread.currentThread().getId() + "].");
            super.paintComponent(g);
            g.setColor(Color.RED);

            //co.drawArc(getx(), gety(), getWidth() - 20, getHeight() - 20, 0, 360);
            g.drawRect(x, y, width, height);
        }

        public Rectangle getRect() {
            return new Rectangle(x, y, width, height);
        }
    };

    // FIXME Instead getters use Class.attr
    public static JFrame getFrame() {
        return frame;
    }

    // FIXME Instead getters use Class.attr
    public static JPanel getPanelUnter() {
        return jpUnter;
    }

    // FIXME Instead getters use Class.attr
    public static JPanel getPanelOber() {
        return jpOber;
    }

    // FIXME Instead getters use Class.attr
    public static MouseRect getMouseRect() {
        return mouseRectangle;
    }

    // FIXME Instead getters use Class.attr
    public static MouseController getMouseController() {
        return mouseController;
    }

    // FIXME Instead getters use Class.attr
    public static long getThreadId() {
        return threadId;
    }

    // FIXME Class-in-Class.
    // We must derive from a generic type to use a full power of getGenericSuperclass()
    // (avoiding type erasure)
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
    }

    /* ATTENTION! */
    /* Don't use System.out.println! Since we use very powerful Thread.suspend() method
    in this software, we must not call anywhere System.out.println(), because it results to deadlock:
    https://stackoverflow.com/questions/36631153/deadlock-with-system-out-println-and-a-suspended-thread
     */
    // FIXME Move to Tools.Class
    public static void printMsg(String str) {
        if (TRACE_ON && !OUT_LOG) {
            // Write logs in file
            final File file = new File(LOGFILE);

            try (final Writer writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write("[" + Thread.currentThread().getId() + "]" + str + "\n");
            } catch (Exception e) {
                System.exit(2);
            }
        }

        System.out.print("DEBUG  " + str + "\n");
    }

    // FIXME Move to Tools.Class
    public static boolean in_range(int left, int val, int right, boolean strict) {
        if (strict) {
            return (left < val && val < right);
        }

        return (left <= val && val <= right);
    }

    // FIXME Move to Tools.Class
    public static void timeout(long timeoutMSec) {
        /* TODO: what happens if we pass negative timeout into sleep()? */
        try {
            Thread.sleep(timeoutMSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // This is not a big trouble that we were not able to do sleep, so it is not a reason to interrupt the method on this
        }
    }

    // "Pattern" class for creation of singletones
    // FIXME Class-in-Class.
    public static class ThreadPattern extends Thread {
        protected final String name;
        protected long TIME_QUANT = 80;
        protected boolean SIGNAL_TERM;
        private static ThreadPattern instance = null;

        public static ThreadPattern getInstance() {
            return instance;
        }

        protected ThreadPattern(String threadName) {
            name = threadName;
            SIGNAL_TERM = false;
        }

        @Override @Deprecated
        public void start() {
            throw new UnsupportedOperationException(
                    "void start() method with no arguments is forbidden. Use start(int, long) instead."
            );
            // interrupt(); - is it possible that the method start() of the base class anyway started the run() at this moment?
        }

        public boolean start(int attempts, long timeoutMSec)
        {
            boolean rc = true;
            StackTraceElement [] stackTrace = null;
            while (rc) {
                try {
                    printMsg("Thread " + super.getId() + " is starting");
                    super.start();
                    printMsg("Thread " + super.getId() + " started.");
                    rc = false;
                } catch (Exception e) {
                    stackTrace = e.getStackTrace();
                    timeout(timeoutMSec);
                }
            }

            printMsg("Thread " +super.getId() + " failed to start after " + attempts + " attempts.");
            if (stackTrace != null) {
                for (StackTraceElement steElement : stackTrace) {
                    printMsg(steElement.toString());
                }
            }

            return false;
        }

        @Override
        @Deprecated // Derived classes should use repeat() instead.
        public void run() {
            try
            {
                printMsg("Thread " + super.getId() + " is running");
                while (!interrupted() && !SIGNAL_TERM) {
                    // All the functionality is incapsulated here.
                    repeat();
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

        // FIXME Init this
        public void repeat() throws InterruptedException {
            // Pure functionality without error handling
            // Is to be used by derived classes
        }

        // TODO: Should we release all mutexes held by a thread before terminating?
        // TODO: What if we call this function when the thread is held by another thread?
        // FIXME Confused ret values
        public boolean terminate(long timeoutMsec) {
            SIGNAL_TERM = true;

            // Wait and hope that the thread finish its work normally
            timeout(timeoutMsec);

            // Still did not exit - it's a pity...
            if (isAlive()) {
                /* Hard termination (See https://stackoverflow.com/questions/671049/how-do-you-kill-a-thread-in-java). */
                try {
                    interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TODO: check of we need this
                super.interrupt();
            }

            return isAlive();
        }
    } // end of class ThreadPattern

    // public class ThreadPatternImpl extends ThreadPattern { }

    /*
    This should be in the main thread and not in D-Thread, otherwise we will have to synchronize D-Thread
    with the main thread on exiting moment that is complicated (what if before we suspend D-Thread it also does some exiting stuff?
    will not it lead to double exiting with unexpected consequences?)
    */

    private static void destroy() {
        // Here we implement releasing of allocated memory for all objects.
        frame.dispose();
    }

//    private static boolean terminate(long timeoutMsec) {
//        boolean rc = true;
//        try {
//            C_Thread.getInstance().terminate(timeoutMsec);
//            V_Thread.getInstance().terminate(timeoutMsec);
//            D_Thread.getInstance().terminate(timeoutMsec);
//            destroy(); // delete all objects
//            SIGNAL_TERM_GENERAL = true;
//            rc = false;
//        } catch (Exception e) {
//            for (StackTraceElement steElement : e.getStackTrace()) {
//                printMsg(steElement.toString());
//            }
//        }
//        return rc;
//    }

    // TODO Check logic here!
    private static boolean terminate(long timeoutMsec) {
        try {
            C_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }

            return false;
        }

        try {
            V_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }

            return false;
        }

        try {
            D_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                printMsg(steElement.toString());
            }

            return false;
        }

        // Delete all objects
        destroy();

        SIGNAL_TERM_GENERAL = true;
        return true;
    }

    public static void terminateNoGiveUp(long timeoutMsec, String terminateMsg) {
        if (terminateMsg != null && !terminateMsg.equals("")){
            printMsg(terminateMsg);
        }

        printMsg(" --- total terminate! ---");

        ////ErrWindow ew = displayErrorWindow("The game was interrupted due to exception. Exiting...
        // (if this window does not disappear for a long time, kill the game process manually from OS.)");
        while (!terminate(timeoutMsec)) {
            // Just for safety we set here some small timeout unconfigurable
            // to avoid brutal rush of terminate() requests
            timeout(10);
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

    // TODO Move this to ThreadPool
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
        terminate(1000);

        // TODO: add all of this
        // The window with 4 buttons: Exit Immediately, Exit, Pause, Cancel
//        Choice choice = handlePauseWindow();
//        if (choice == Choice.EXIT_IMMEDIATELY) {
//            terminate(0);
//        } else if (choice == Choice.EXIT) {
//            terminate(1000);
//        } else if (choice == Choice.PAUSE) {
//            // Just keep being suspended...
//        } else if (choice == Choice.RESUME) {
//            resumeAll();
//        } else if (choice == Choice.CANCEL) {
//            // Do nothing
//        } else {
//            // TODO: Display a new window
//            //ErrWindow ew = displayErrorWindow("No such choice: " + choice.toString() + ". Exiting...(if this window does not disappear for a long time, kill the game process manually from OS.)");
//            terminateNoGiveUp(1000, null);
//            //ew.close();
//        }
    }

    // Main thread of the game. Starts other game thread in the correct order. Exist if and only if all other threads exited.
    public static void main(String[] args) throws InterruptedException {
        // Init logging
        File logFile = new File(LOGFILE);
        if (logFile.exists()) {
            logFile.delete();
        }

        printMsg("The game starts.");

        // Remember ID of the main thread to give it to other threads
        threadId = Thread.currentThread().getId();

        // ----> Initing GameMap
        // TODO -> INIT()
        // TODO -> Map.init()
        int width = Restrictions.MAX_X;
        int height = Restrictions.MAX_Y;
        int [][] map = new int[width][height];

        // Fill map with random numbers of textures (initing in future)
        for(int i=0; i<width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = ((i * i + j * j) / 5) % 6;
            }
        }

        initMap(map, width, height);
        initObjects();
        initGraph();

        // ----> Initing Controller
        // TODO -> Controller.Init()
        jpUnter.addMouseListener(getMouseController());
        jpUnter.addMouseMotionListener(getMouseController());
        jpUnter.addMouseWheelListener(getMouseController());

        Main.printMsg("players:" + Player.getPlayers().length);

        if (D_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000, null);
        }

        if (V_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000, null);
        }

        // Wait for D-Thread to get ready (at the same time D-Thread is waiting for V-Thread to get ready)
        ((Semaphore) ParameterizedMutexManager.getInstance().getMutex(
                "D", "getReady")
        ).acquire();

        // Wait for V-Thread to get ready
        ((Semaphore) ParameterizedMutexManager.getInstance().getMutex(
                "V", "getReady")
        ).acquire();

        // 4. Initialize and start C-Thread.
        if (C_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000, null);
        }

        // FIXME Move dis to class ThreadPool
        int deadStatus = 0;

        // Endless loop of the main thread. Here we just catch signals and do nothing more.
        while(!SIGNAL_TERM_GENERAL) {
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

        if (deadStatus == 0) {
            // If no one thread died then we wait for all threads to exit normally {
            // End of the game (wrap up actions)
            // We expect not just InterruptedException, but general Exception, because MutexManager can throw many times of exception from inside *_Thread
            try {
                C_Thread.getInstance().join(); // wait the C-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                C_Thread.getInstance().terminate(1000);
            }

            try {
                V_Thread.getInstance().join(); // wait the V-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                V_Thread.getInstance().terminate(1000);
            }

            try {
                D_Thread.getInstance().join(); // wait the D-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                D_Thread.getInstance().terminate(1000);
            }
        }

        // For sure
        // TODO Do check this sure
        terminateNoGiveUp(1000, null);
        printMsg("The game exited.");
        System.exit(deadStatus);
    }
}
