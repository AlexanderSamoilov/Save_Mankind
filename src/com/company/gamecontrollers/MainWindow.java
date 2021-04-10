/* ***************** *
 * S I N G L E T O N *
 * ***************** */
package com.company.gamecontrollers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.company.gamethread.M_Thread;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamecontent.GameMap;
import com.company.gamecontent.GameMapRenderer;
import com.company.gamegraphics.GraphBugfixes;
import com.company.gamethread.D_Thread;
import com.company.gamethread.V_Thread;
import com.company.gamethread.C_Thread;
import com.company.gametools.Tools;

import static com.company.gamethread.M_Thread.terminateNoGiveUp;

public class MainWindow {
    private static Logger LOG = LogManager.getLogger(MainWindow.class.getName());

    private static MainWindow instance = null;
    public static synchronized MainWindow getInstance() {
        return instance;
    }
    private MainWindow() {
        LOG.debug(getClass() + " singleton created.");
    }

    public static synchronized void init() {
        if (instance != null) {
            terminateNoGiveUp(null,
                    1000,
                    instance.getClass() + " init error. Not allowed to initialize MainWindow twice!"
            );
        }
        instance = new MainWindow();
    }

    // global graphics variables
    //private Canvas canvOber = new Canvas();
    //private Canvas canvUnter = new Canvas();
    //private BufferStrategy bs = null;
    //private Graphics grap = null;

    /*
      EDT = Event Dispatcher Thread
      This is an automatic thread which is implemented in the java.awt.EventDispatchThread
      This thread is responsible for two things:
        - mouse (java.awt.event.MouseAdapter, java.awt.event.MouseEvent)
        - screen graphics (javax.swing.JFrame, javax.swing.JPanel etc.)
      We use EDT at the moment as replacement of D-Thread in our game concept.
      It is an experimental implementation (in future better to implement own graphics completely).
     */
    // static, because EventDispatcherThread is single for the whole JVM
    public static long EDT_ID = -1; // WRITABLE

    // Element #0 - the main game window.
    public final JFrame frame = new JFrame("Main Window") {
//    <editor-fold desc="ATTENTION: Please, don't remove this implementation!">
//
//    I added it to synchronize C-thread with EDT (Swing) thread.
//    Normally EDT thread is working automatically and is drawing asynchronously to the other game threads.
//    This contradicts however with Hayami's game concept where all drawing (which is done by V-Thread)
//    is completely controlled and synchronized with other game threads. For example (it is reproduced better with very small TIME_QUANT):
//
//    DEBUG [16]ConcurrentModificationException has reproduced!
//    DEBUG [16]java.util.HashMap$HashIterator.nextNode(HashMap.java:1442)
//    DEBUG [16]java.util.HashMap$KeyIterator.next(HashMap.java:1466)
//    DEBUG [16]com.company.gamecontent.GameMap.renderObjects(GameMap.java:136)
//    DEBUG [16]com.company.gamecontent.GameMap.render(GameMap.java:109)
//    DEBUG [16]com.company.Main$1.paintComponent(Main.java:96)
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

    // Element #1 - the upper layer of the main window.
    private final JPanel jpOber = new JPanel();
//    {
//        // Override basic method of EDT of JPanel
//        @Override
//        public void paintComponent(Graphics g) {
//            LOG.trace("Painting from jpOber.paint[" + Thread.currentThread().getId() + "].");
//            super.paintComponent(g);
//            //g.drawArc(10, 10, getWidth() - 20, getHeight() - 20, 0, 360);
//        }
//    };

    // Element #2 - the lower layer of the main window.
    private final JPanel jpUnter = new JPanel() {
        // Override basic method of EDT of JPanel
        @Override
        public void paintComponent(Graphics g) {
            LOG.trace("Painting from jpUnter.paint[" + Thread.currentThread().getId() + "].");
            if (EDT_ID == -1) { // redefine it only one time!
                EDT_ID = Thread.currentThread().getId();
                LOG.info("EDT ID assigned: " + EDT_ID);
            }
            super.paintComponent(g);
            GameMapRenderer.render(g);
        }
    };

    // TODO: consider cases when mouse goes outside the Jframe and coordinates are negative
    // FIXME Class-in-Class.
    // FIXME Set this singletone
    // Element #3 - selection rectangle.
    static class MouseRect extends JPanel {

        private int x = -1;
        private int y = -1;
        private int width = 0;
        private int height = 0;

        void redefineRect(int x, int y, int w, int h) {

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

        Rectangle getRect() {
            return new Rectangle(x, y, width, height);
        }
    }
    final MouseRect mouseRectangle = new MouseRect();

    /*
     This should be in the main thread and not in D-Thread, otherwise we will have to synchronize D-Thread
     with the main thread on exiting moment that is complicated (what if before we suspend D-Thread it also does some exiting stuff?
     will not it lead to double exiting with unexpected consequences?)
    */
    public void destroy() {
        // Here we implement releasing of allocated memory for all objects.

        // kill main windows properly
        while (frame.isShowing() || frame.isValid()) {
            frame.dispose();
            LOG.debug("Dispose - retry:" + frame.isActive() + "," + frame.isValid() + "," + frame.isDisplayable()
                    + "," + frame.isEnabled() + "," + frame.isShowing() + "," + frame.isVisible());
            Tools.timeout(100);
        }
        // TODO: Controllers also should be destroyed?
    }

    // DEBUG:
    private void debugMargins(String msg) {
        if (LOG.getLevel().intLevel() <= Level.INFO.intLevel()) {
            return;
        }

        LOG.trace("-------------------" + msg + "------------------");
        LOG.trace("JFrame.getInsets(left,top,right,bottom):" +
                frame.getInsets().left + "," + frame.getInsets().top + "," +
                frame.getInsets().right + "," + frame.getInsets().bottom
        );
        LOG.trace("JFrame.getContentPane().getInsets(left,top,right,bottom):" +
                frame.getContentPane().getInsets().left + "," + frame.getContentPane().getInsets().top + "," +
                frame.getContentPane().getInsets().right + "," + frame.getContentPane().getInsets().bottom
        );
        LOG.trace("JFrame.getBounds(): " + frame.getBounds().width + " x " + frame.getBounds().height);
        LOG.trace("JFrame.getContentPane().getBounds(): " +
                frame.getContentPane().getBounds().width + " x " + frame.getContentPane().getBounds().height
        );
        LOG.trace("DIFF(JFrame.getBounds() - JFrame.getContentPane().getBounds()):" +
                (frame.getBounds().width - frame.getContentPane().getBounds().width) + " x " +
                (frame.getBounds().height - frame.getContentPane().getBounds().height)
        );
        LOG.trace("**********************************************************");
    }

    public void initGraph() {
        // FIXME do not use try for all of them.
        try {
            jpOber.setLayout(new BorderLayout());
            jpOber.setBounds(0,0,GameMap.getInstance().dim.x(), GameMap.getInstance().dim.y());
            jpOber.setOpaque(false);
            mouseRectangle.setOpaque(false);
            jpOber.add(mouseRectangle);

            //jpUnter.setLayout(null);
            jpUnter.setBounds(0,0,GameMap.getInstance().dim.x(), GameMap.getInstance().dim.y());
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
                            GameMap.getInstance().dim.x(),
                            GameMap.getInstance().dim.y()
                    )
            );

            debugMargins("Before JFrame.pack()");

            // https://stackoverflow.com/a/19740999/4807875:
            // Call setResizable(false) BEFORE calling pack() !!
            frame.setResizable(false);

            frame.pack();                             // Сворачиваем окно до размера приложения
            //frame.setResizable(false);
            frame.setFocusable(true);

            debugMargins("After JFrame.pack() / Before JFrame.setLocationRelativeTo()");

            frame.setLocationRelativeTo(null);        // Установка окна в центр экрана

            debugMargins("After JFrame.setLocationRelativeTo() / Before frame.addWindowListener()");

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

            debugMargins("After JFrame.addWindowListener()");

            LOG.debug(" ------ Rectangle API Test  ------ ");

            Rectangle myRect = new Rectangle(1, 1, 2, 2);

            LOG.debug("getMaxX=" + myRect.getMaxX() + " getMaxY=" + myRect.getMaxY());
            LOG.debug("getWidth()=" + myRect.getWidth() + " getHeight()=" + myRect.getHeight());
            LOG.debug(" ------ Make EDT thread to drawing ------ ");

            frame.setVisible(true);

            // Установка фокуса для контроллеров игры
            //frame.requestFocus();

            Tools.timeout(1000);

            LOG.debug(" ------ EDT thread finished drawing ------ ");
            /* Obtain numerical ID of the event dispatcher thread (EDT)
               EDT_ID = java.awt.EventDispatchThread.getId(); - does not work, because it is a private class.
               Thus we use a workaround: wait until EDT draws something and "steal" its ID from paintComponent().
               In the loop below we are waiting until paintComponent() is called and initialize the variable EDT_ID.
             */
            while (EDT_ID == -1) {
                Tools.timeout(1000);
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
               (EDT_ID == M_Thread.threadId) ||
               (EDT_ID == C_Thread.getInstance().getId()) ||
               (EDT_ID == V_Thread.getInstance().getId()) ||
               (EDT_ID == D_Thread.getInstance().getId())
        ) {
            terminateNoGiveUp(null,
                    1000,
                    "Failed to detect EDT thread ID. Perhaps, paintComponent() was called from another thread."
            );
        }

        LOG.info("Graphics: ready.");
    }

    public void initControllers() {
        jpUnter.addMouseListener(MouseController.getInstance());
        jpUnter.addMouseMotionListener(MouseController.getInstance());
        jpUnter.addMouseWheelListener(MouseController.getInstance());
        LOG.info("Controllers: ready.");
    }

}
