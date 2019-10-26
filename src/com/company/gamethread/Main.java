package com.company.gamethread;

import com.company.gamegeom.cortegemath.CortegeTest;
import com.company.gamegeom.cortegemath.point.Point3D_Integer;
import com.company.gamegeom.cortegemath.vector.Vector3D_Integer;
import com.company.gamelogger.LogConfFactory;

import com.company.gamecontent.*;
import com.company.gamecontrollers.MouseController;
import com.company.gamegraphics.Sprite;

import com.company.gamegraphics.GraphBugfixes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationFactory;


// FIXME To Class GameManager or GameState
enum Choice {
    EXIT_IMMEDIATELY,
    EXIT,
    PAUSE,
    RESUME,
    CANCEL
}

public class Main {
    /* ATTENTION! */
    /* Don't use System.out.println! Since we use very powerful Thread.suspend() method
    in this software, we must not call anywhere System.out.println(), because it results to deadlock:
    https://stackoverflow.com/questions/36631153/deadlock-with-system-out-println-and-a-suspended-thread
    */
    private static Logger LOG;

    //public static Player[] players = new Player[2]; // TODO: move to GameMap?

    // global graphics variables
    //private static Canvas canvOber = new Canvas();
    //private static Canvas canvUnter = new Canvas();
    //private static BufferStrategy bs = null;
    //private static Graphics grap = null;

    private static JFrame frame = new JFrame("Sosochek Konchika Mnogothreadovyj") {


//    <editor-fold desc="ATTENTION: Please, don't remove this implementation!">
//
//    ATTENTION: Please, don't remove this implementation!
//    I added it to synchronize C-thread with EDT (Swing) thread.
//    Normally EDT thread is working automatically and is drawing asynchronously to the other game threads.
//    This contradicts however with Hayami's game concept where all drawing (which is done by V-Thread)
//    is completely controlled and synchronized with other game threads. For example (it is reproduced better with very small TIME_QUANT):
//
//    DEBUG [16]ConcurrentModificationException has reproduced!
//    DEBUG [16]java.util.HashMap$HashIterator.nextNode(HashMap.java:1442)
//    DEBUG [16]java.util.HashMap$KeyIterator.next(HashMap.java:1466)
//    EBUG [16]com.company.gamecontent.GameMap.renderObjects(GameMap.java:136)
//    DEBUG [16]com.company.gamecontent.GameMap.render(GameMap.java:109)
//    DEBUG [16]com.company.gamethread.Main$1.paintComponent(Main.java:96)
//    DEBUG [16]javax.swing.JComponent.paint(JComponent.java:1056)
//    DEBUG [16]javax.swing.JComponent.paintChildren(JComponent.java:889)
//    DEBUG [16]javax.swing.JComponent.paint(JComponent.java:1065)
//    DEBUG [16]javax.swing.JComponent.paintChildren(JComponent.java:889)
//    DEBUG [16]javax.swing.JComponent.paint(JComponent.java:1065)
//    DEBUG [16]javax.swing.JLayeredPane.paint(JLayeredPane.java:586)
//    DEBUG [16]javax.swing.JComponent.paintChildren(JComponent.java:889)
//    DEBUG [16]javax.swing.JComponent.paintToOffscreen(JComponent.java:5217)
//    DEBUG [16]javax.swing.RepaintManager$PaintManager.paintDoubleBuffered(RepaintManager.java:1579)
//    DEBUG [16]javax.swing.RepaintManager$PaintManager.paint(RepaintManager.java:1502)
//    DEBUG [16]javax.swing.RepaintManager.paint(RepaintManager.java:1272)
//    DEBUG [16]javax.swing.JComponent.paint(JComponent.java:1042)
//    DEBUG [16]java.awt.GraphicsCallback$PaintCallback.run(GraphicsCallback.java:39)
//    DEBUG [16]sun.awt.SunGraphicsCallback.runOneComponent(SunGraphicsCallback.java:79)
//    DEBUG [16]sun.awt.SunGraphicsCallback.runComponents(SunGraphicsCallback.java:116)
//    DEBUG [16]java.awt.Container.paint(Container.java:1978)
//    DEBUG [16]java.awt.Window.paint(Window.java:3906)
//    DEBUG [16]javax.swing.RepaintManager$4.run(RepaintManager.java:842)
//    DEBUG [16]javax.swing.RepaintManager$4.run(RepaintManager.java:814)
//    DEBUG [16]java.security.AccessController.doPrivileged(Native Method)
//    DEBUG [16]java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:74)
//    DEBUG [16]javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:814)
//    DEBUG [16]javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:789)
//    DEBUG [16]javax.swing.RepaintManager.prePaintDirtyRegions(RepaintManager.java:738)
//    DEBUG [16]javax.swing.RepaintManager.access$1200(RepaintManager.java:64)
//    DEBUG [16]javax.swing.RepaintManager$ProcessingRunnable.run(RepaintManager.java:1732)
//    DEBUG [16]java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:311)
//    DEBUG [16]java.awt.EventQueue.dispatchEventImpl(EventQueue.java:758)
//    DEBUG [16]java.awt.EventQueue.access$500(EventQueue.java:97)
//    DEBUG [16]java.awt.EventQueue$3.run(EventQueue.java:709)
//    DEBUG [16]java.awt.EventQueue$3.run(EventQueue.java:703)
//    DEBUG [16]java.security.AccessController.doPrivileged(Native Method)
//    DEBUG [16]java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:74)
//    DEBUG [16]java.awt.EventQueue.dispatchEvent(EventQueue.java:728)
//    DEBUG [16]java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:205)
//    DEBUG [16]java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:116)
//    DEBUG [16]java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:105)
//    DEBUG [16]java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
//    DEBUG [16]java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:93)
//    DEBUG [16]java.awt.EventDispatchThread.run(EventDispatchThread.java:82)
//
//    This stack trace appears because C-Thread and EDT are trying to modify the GameMap.objectsOnMap matrix at the same time.
//
//    Thus we really need synchronous drawing by EDT and therefore we use .paintImmediately as shown below
//    in order to force EDT to write everything synchronously and only when we order it.
//
//    Moreover when we implement the game driver not in Java there will be no EDT and there will be valid D-V-C thread concept
//    in which V-Thread performing all drawing is fully controlled by us and nothing is done automatically in background.
//
//    The drawback of the synchronous EDT writing is performance degradation on small TIME_QUANT.
//
//    </editor-fold>

        // this is in a JPanel extended class
        @Override
        public void repaint(long tm) {
            LOG.trace("Painting from jpOber.paint[" + Thread.currentThread().getId() + "].");

//            for (Component c : getComponents()) {
//                LOG.trace("+ " + c.toString());
//            }

            JRootPane jRootPane = getRootPane();
            super.getRootPane().paintImmediately(jRootPane.getVisibleRect());
            //g.drawArc(10, 10, getWidth() - 20, getHeight() - 20, 0, 360);
        }
    };

    private static long threadId = -1;
    private static long EDT_ID = -1;

    private static MouseRect mouseRectangle = new MouseRect();
    private static MouseController mouseController;

    // public Main();
    public static boolean SIGNAL_TERM_GENERAL = false;

    // Override basic method of EDT of JPanel
    private static JPanel jpOber = new JPanel();
//    {
//        // this is in a JPanel extended class
//        @Override
//        public void paintComponent(Graphics g) {
//            LOG.trace("Painting from jpOber.paint[" + Thread.currentThread().getId() + "].");
//            super.paintComponent(g);
//            //g.drawArc(10, 10, getWidth() - 20, getHeight() - 20, 0, 360);
//        }
//    };

    // Override basic method of EDT of JPanel
    private static JPanel jpUnter = new JPanel() {
        @Override
        // This is in a JPanel extended class
        public void paintComponent(Graphics g) {
            LOG.trace("Painting from jpUnter.paint[" + Thread.currentThread().getId() + "].");
            if (EDT_ID == -1) { // redefine it only one time!
                EDT_ID = Thread.currentThread().getId();
            }
            super.paintComponent(g);
            GameMap.getInstance().render(g);
        }
    };

    private static void initLoggers() {
        ConfigurationFactory.setConfigurationFactory(new LogConfFactory());
        LOG = LogManager.getLogger(Main.class.getName());

        LOG.info("The loggers are ready!");
    }

    private static void initMap(int [][] terrain_map, int width, int height) {
        try {
            GameMap.getInstance().init(terrain_map, width, height);
        } catch (Exception e) {
            terminateNoGiveUp(e,1000, null);
        }

        LOG.info("Initialized map " + width + "x" + height);
    }

    // TODO Move to some inits()
    private static void initObjects() {
        // Initialising Player tank resources
        HashMap<Resource,Integer> testPlayerTankResources = new HashMap<Resource,Integer>();
        testPlayerTankResources.put(Resource.MASS, 500);
        testPlayerTankResources.put(Resource.ENERGY, 1000);

        // Initialising Player test tank Sprite
        Sprite testPlayerTankSprite = new Sprite("light_tank_1.png");

        // This is a test. Initialising tanks for Player.
        // We try calculate how much tanks we can place on map by width
//        int testNumTanks = GameMap.getInstance().getMaxX() / 2 - 1;
        int testNumTanks = 2;

        ArrayList<Unit> testPlayerUnits = new ArrayList<Unit>();
        for (int i=0; i <= testNumTanks - 1; i++) {
            testPlayerUnits.add(
                    new Unit(
                            new Weapon(
                                    10, 150, 15, 6, 7
                            ),
                            2, testPlayerTankSprite,
                            new Point3D_Integer(4*i + 1, 1, 0),
                            new Vector3D_Integer(3, 3, 3),
                            testPlayerTankResources, 5000, 600, 10, 45,25, 15, 5, 5, 5
                    )
            );
        }

        // Initialising Enemy tank resources
        HashMap<Resource,Integer> testEnemyTankResources = new HashMap<Resource,Integer>();
        testEnemyTankResources.put(Resource.MASS, 500);
        testEnemyTankResources.put(Resource.ENERGY, 1000);

        // Initialising Enemy test tank Sprite
        Sprite testEnemyTankSprite = new Sprite("light_tank_2.png");



        // This is a test. Initialising tanks for Player.
        // We try calculate how much tanks we can place on map by width
//        int testEnemyTanks = GameMap.getInstance().getMaxX() / 2 - 1;
        int testEnemyTanks = 2;
        ArrayList<Unit> testEnemyUnits = new ArrayList<Unit>();
        for (int i=0; i <= testEnemyTanks - 1; i++) {
            testEnemyUnits.add(
                    new Unit(
                            new Weapon(
                                    30, 150, 10, 8, 5
                            ),
                            3, testEnemyTankSprite,
                            new Point3D_Integer(Restrictions.MAX_X - 2 - 2*i,Restrictions.MAX_DIM.y() - 2, 0),
                            new Vector3D_Integer(1, 1, 1),
                            testEnemyTankResources, 500, 7, 15, 45, 25, 15, 5, 5, 5
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

        LOG.info("The game objects have been initialized!");
    }

    public static void debugMargins(JFrame jFrame, String msg) {
        if (LOG.getLevel().intLevel() <= Level.INFO.intLevel()) {
            return;
        }

        LOG.debug("-------------------" + msg + "------------------");
        LOG.debug("JFrame.getInsets(left,top,right,bottom):" + jFrame.getInsets().left + "," + jFrame.getInsets().top
                + "," + jFrame.getInsets().right + "," + jFrame.getInsets().bottom);
        LOG.debug("JFrame.getContentPane().getInsets(left,top,right,bottom):" + jFrame.getContentPane().getInsets().left + "," + jFrame.getContentPane().getInsets().top
                + "," + jFrame.getContentPane().getInsets().right + "," + jFrame.getContentPane().getInsets().bottom);

        LOG.debug("JFrame.getBounds(): " + jFrame.getBounds().width + " x " + jFrame.getBounds().height);
        LOG.debug("JFrame.getContentPane().getBounds(): " + jFrame.getContentPane().getBounds().width + " x " + jFrame.getContentPane().getBounds().height);
        LOG.debug("DIFF(JFrame.getBounds() - JFrame.getContentPane().getBounds()):" +
                (jFrame.getBounds().width - jFrame.getContentPane().getBounds().width) + " x " +
                (jFrame.getBounds().height - jFrame.getContentPane().getBounds().height));
        LOG.debug("**********************************************************");
    }

    public static void initGraph() {
        // FIXME do not use try for all of them.
        try {
            jpOber.setLayout(new BorderLayout());
            jpOber.setBounds(0,0,GameMap.getInstance().getAbsDim().x(), GameMap.getInstance().getAbsDim().y());
            jpOber.setOpaque(false);
            mouseRectangle.setOpaque(false);
            jpOber.add(mouseRectangle);

            //jpUnter.setLayout(null);
            jpUnter.setBounds(0,0,GameMap.getInstance().getAbsDim().x(), GameMap.getInstance().getAbsDim().y());
            jpUnter.setOpaque(true);

            // Создаём окно, в котором будет запускаться игра
            frame.setLayout(null); // setting layout to null so we can make panels overlap
            frame.add(jpOber);     // Добавляем холст на наш фрейм
            frame.add(jpUnter);     // Добавляем холст на наш фрейм
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //frame.setUndecorated(true);
            //frame.setOpacity(0.50f);

            // Определяем размер приложения

            // frame.setPreferredSize(...) gives bad result!
            // The frame bounds became cut (less than the bounds of the contained JPanel).
            // Empirically I found that calling of .setPreferredSize to the contained JRootPane solves it.
            // Both .getRootPane() and .getContentPane() give good result.
            frame.getRootPane().setPreferredSize(new Dimension(
                    GameMap.getInstance().getAbsDim().x(),
                    GameMap.getInstance().getAbsDim().y()
                    )
            );

            debugMargins(frame,"Before JFrame.pack()");

            // https://stackoverflow.com/a/19740999/4807875:
            // Call setResizable(false) BEFORE calling pack() !!
            frame.setResizable(false);

            frame.pack();                             // Сворачиваем окно до размера приложения
            //frame.setResizable(false);
            frame.setFocusable(true);

            debugMargins(frame,"After JFrame.pack() / Before JFrame.setLocationRelativeTo()");

            frame.setLocationRelativeTo(null);        // Установка окна в центр экрана

            debugMargins(frame,"After JFrame.setLocationRelativeTo() / Before frame.addWindowListener()");

            // Лучше написать слушателя событий, который может контролировать приложение
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    /* Для закрытия окна будем использовать наши методы класса */
                    destroy();
                    terminateNoGiveUp(null,
                            1000,
                            "Exiting game, because the main windows was closed on user's demand."
                    );
                }
            });

            debugMargins(frame,"After JFrame.addWindowListener()");

            LOG.debug(" ------ Rectangle API Test  ------ ");

            Rectangle myRect = new Rectangle(1, 1, 2, 2);

            LOG.debug("getMaxX=" + myRect.getMaxX() + " getMaxY=" + myRect.getMaxY());
            LOG.debug("getWidth()=" + myRect.getWidth() + " getHeight()=" + myRect.getHeight());
            LOG.debug(" ------ Make EDT thread to drawing ------ ");

            frame.setVisible(true);

            // Установка фокуса для контроллеров игры
            //frame.requestFocus();

            timeout(1000);

            LOG.debug(" ------ EDT thread finished drawing ------ ");
            /* Obtain numerical ID of the event dispatcher thread (EDT)
               EDT_ID = java.awt.EventDispatchThread.getId(); - does not work, because it is a private class.
               Thus we use a workaround: wait until EDT draws something and "steal" its ID from paintComponent().
               In the loop below we are waiting until paintComponent() is called and initialize the variable EDT_ID.
             */
             while (EDT_ID == -1) {
                 timeout(1000);
             }
             LOG.info(" ------ EDT thread finished drawing. EDT thread ID detected: " + EDT_ID + " ------ ");

        } catch (Exception e) {
            destroy();
            terminateNoGiveUp(e,
                    1000,
                    "Failed to initialize game graphics. The game will be terminated now."
            );
        }

        if (
               (EDT_ID == Main.threadId) ||
               (EDT_ID == C_Thread.getInstance().getId()) ||
               (EDT_ID == V_Thread.getInstance().getId()) ||
               (EDT_ID == D_Thread.getInstance().getId())
        ) {
            terminateNoGiveUp(null,1000, "Failed to detect EDT thread ID. Perhaps, paintComponent() was called from another thread.");
        }

        LOG.info("The graphics are ready!");
    }

    public static void initControllers() {
        mouseController = new MouseController();

        LOG.info("The controllers are ready!");
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

//            LOG.trace("before(" + "(" + x + "," + y + " -> (" + wid + "," + hei + ")");
//            LOG.trace("after (" + "(" + x0 + "," + y0 + " -> (" + wid0 + "," + hei0 + ")");

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
        }

        @Override  // this is in a JPanel extended class
        public void paintComponent(Graphics g) {
            LOG.trace("Painting from MouseRect.paint[" + Thread.currentThread().getId() + "].");

            super.paintComponent(g);
            g.setColor(Color.RED);

//            co.drawArc(getX(), getY(), getWidth() - 20, getHeight() - 20, 0, 360);
            GraphBugfixes.drawRect(g, new Rectangle(x, y, width, height));
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

    public static long getEDTId() {
        return EDT_ID;
    }

    // FIXME Move to Tools.Class
    public static void timeout(long timeoutMSec) {
        /* TODO: what happens if we pass negative timeout into sleep()? */
        try {
            Thread.sleep(timeoutMSec);
        } catch (InterruptedException e) {
            // This is not a big trouble that we were not able to do sleep, so it is not a reason to interrupt the method on this
        }
    }

    public static void printStackTrace(Exception e) {
        StackTraceElement [] stackTrace = null;
        if (e != null) {
            stackTrace = e.getStackTrace();
        } else { // if the Exception parameter is null then we print the current stack trace
            stackTrace = Thread.currentThread().getStackTrace();
        }

        if (LOG != null) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                LOG.info(stackTraceElement.toString());
            }
        } else { // If the logger is not working for some reason, at least print to console
            for (StackTraceElement stackTraceElement : stackTrace) {
                System.out.println(stackTraceElement.toString());
            }
        }
    }

    // "Pattern" class for creation of singletones
    // FIXME Class-in-Class.
    public static class ThreadPattern extends Thread {

        protected long TIME_QUANT = 80;
        protected boolean SIGNAL_TERM;
        private static ThreadPattern instance = null;

        public static ThreadPattern getInstance() {
            return instance;
        }

        protected ThreadPattern(String threadName) {
            super.setName(threadName);
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
                    LOG.info("Thread " + super.getId() + " is starting");
                    super.start();
                    LOG.info("Thread " + super.getId() + " started.");
                    rc = false;
                } catch (Exception e) {
                    stackTrace = e.getStackTrace();
                    timeout(timeoutMSec);
                }
            }

            LOG.warn("Thread " +super.getId() + " failed to start after " + attempts + " attempts.");
            if (stackTrace != null) {
                for (StackTraceElement steElement : stackTrace) {
                    LOG.error(steElement.toString());
                }
            }

            return false;
        }

        @Override
        @Deprecated // Derived classes should use repeat() instead.
        public void run() {
            try
            {
                LOG.info("Thread " + super.getId() + " is running");
                while (!interrupted() && !SIGNAL_TERM) {
                    // All the functionality is incapsulated here.
                    repeat();
                    timeout(TIME_QUANT);
                }

                LOG.info("Thread " + super.getId() + " finished.");
            }
            catch (InterruptedException e)
            {
                LOG.error("Thread " + super.getId() + " died!");
                printStackTrace(e);
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
                    printStackTrace(e);
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

        // kill main windows properly
        while (frame.isShowing() || frame.isValid()) {
            frame.dispose();
            LOG.debug("Dispose - retry:" + frame.isActive() + "," + frame.isValid() + "," + frame.isDisplayable()
                    + "," + frame.isEnabled() + "," + frame.isShowing() + "," + frame.isVisible());
            timeout(100);
        }
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
//                LOG.error(steElement.toString());
//            }
//        }
//        return rc;
//    }

    // TODO Check logic here!
    private static boolean terminate(long timeoutMsec) {
        SIGNAL_TERM_GENERAL = true;
        try {
            C_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }

            return false;
        }

        try {
            V_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }

            return false;
        }

        try {
            D_Thread.getInstance().terminate(timeoutMsec);
        } catch (Exception e) {
            for (StackTraceElement steElement : e.getStackTrace()) {
                LOG.error(steElement.toString());
            }

            return false;
        }

        // Delete all objects and destroy main window
        destroy();

        return true;
    }

    public static void terminateNoGiveUp(Exception exception, long timeoutMsec, String terminateMsg) {

        LOG.info(" --- total terminate! ---");
        printStackTrace(exception);

        if (terminateMsg != null && !terminateMsg.equals("")){
            LOG.fatal(terminateMsg);
        }

        // ErrWindow ew = displayErrorWindow("The game was interrupted due to exception. Exiting...
        // (if this window does not disappear for a long time, kill the game process manually from OS.)");
        while (!terminate(timeoutMsec)) {
            // Just for safety we set here some small timeout unconfigurable
            // to avoid brutal rush of terminate() requests
            timeout(10);
            LOG.debug(" --- trying terminate! ---");
        }

        ////ew.close();
        System.exit(1);
    }

    public static void suspendAll() {
        D_Thread.getInstance().suspend();
        LOG.debug("D suspended");

        V_Thread.getInstance().suspend();
        LOG.debug("V suspended");

        C_Thread.getInstance().suspend();
        LOG.debug("C suspended");
        LOG.debug("--- suspended ---");
    }

    // TODO Move this to ThreadPool
    public static void resumeAll() {
        D_Thread.getInstance().resume();
        V_Thread.getInstance().resume();
        C_Thread.getInstance().resume();
        LOG.debug("--- resumed ---");
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
//            terminateNoGiveUp(null, 1000, null);
//            //ew.close();
//        }
    }

    // Main thread of the game. Starts other game thread in the correct order. Exist if and only if all other threads exited.
    public static void main(String[] args) throws InterruptedException {
        // ---->> Init logging
        initLoggers();

        CortegeTest.testOperators();

        // ---->> Init Controllers
        initControllers();

        // Remember ID of the main thread to give it to other threads
        threadId = Thread.currentThread().getId();

        // ----> Initing GameMap
        // TODO -> Map.init()
        int width = Restrictions.MAX_X;
        int height = Restrictions.MAX_Y;
        int [][] map = new int[width][height];

        // Fill map with random numbers of textures (initing in future)
        for(int i=0; i<width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = ((i * i + j * j) / 7) % 8;
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

        LOG.debug("players:" + Player.getPlayers().length);

        if (D_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(null,1000, null);
        }

        if (V_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(null,1000, null);
        }

        // Wait for D-Thread to get ready (at the same time D-Thread is waiting for V-Thread to get ready)
        try {
            ((Semaphore) ParameterizedMutexManager.getInstance().getMutex(
                    "D", "getReady")
            ).acquire();
        } catch (Exception e) {
            terminateNoGiveUp(e,1000, "Failed to get mutex for D-Thread.");
        }

        // Wait for V-Thread to get ready
        try {
            ((Semaphore) ParameterizedMutexManager.getInstance().getMutex(
                    "V", "getReady")
            ).acquire();
        } catch (Exception e) {
            terminateNoGiveUp(e,1000, "Failed to get mutex for V-Thread.");
        }

        // 4. Initialize and start C-Thread.
        if (C_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(null,1000, null);
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
                printStackTrace(eOuter);
                C_Thread.getInstance().terminate(1000);
            }

            try {
                V_Thread.getInstance().join(); // wait the V-thread to finish
            } catch (Exception eOuter) {
                printStackTrace(eOuter);
                V_Thread.getInstance().terminate(1000);
            }

            try {
                D_Thread.getInstance().join(); // wait the D-thread to finish
            } catch (Exception eOuter) {
                printStackTrace(eOuter);
                D_Thread.getInstance().terminate(1000);
            }
        }

        // For sure
        // TODO Do check this sure
        terminateNoGiveUp(null,1000, null);
        LOG.warn("The game exited.");
        System.exit(deadStatus);
    }
}
