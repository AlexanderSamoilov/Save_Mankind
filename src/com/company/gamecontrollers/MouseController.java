package com.gamecontrollers;

import com.gamecontent.GameMap;
import com.gamethread.Main;
import com.gamethread.MutexManager;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;


// TODO If we not releasing controller for Enemy, than this class must be static
public class MouseController extends MouseAdapter {
    private final int NUM_BUTTONS = 4;

    private static final int NO_BUTTON = 0;
    private static final int L_BUTTON = 1;
    private static final int M_BUTTON = 2;
    private static final int R_BUTTON = 3;

    private final boolean[] buttons = new boolean[NUM_BUTTONS];
//    private final boolean[] lastButtons = new boolean[NUM_BUTTONS];

    private int x      = -1;    // Текущее положение курсора по оси Х
    private int y      = -1;    // Текущее положение курсора по оси Y
    private int last_x = -1;    // Предыдущее, последнее положение курсора по оси Х
    private int last_y = -1;    // Предыдущее, последнее положение курсора по оси Y
//    private int             last_drag_x = -1;    // Предыдущее, последнее положение курсора по оси Х
//    private int             last_drag_y = -1;    // Предыдущее, последнее положение курсора по оси Y

//    private boolean         moving;         // Двигается ли курсор мышки
//    private boolean         disabled;       // Вкл/Выкл контроллера

    public static boolean attackFocus = false;

    @Override
    public void mousePressed(MouseEvent e) {
        this.buttons[e.getButton()] = true;

        if (e.getButton() == R_BUTTON) {
            // TODO: ideally C and V threads should be locked here when we reassigning the targets
            // currently it also works, but from the next round
            GameMap.getInstance().assign(new Integer[]{e.getX(), e.getY()});
        }

        this.last_x = x;
        this.last_y = y;

        this.x = e.getX();
        this.y = e.getY();

        Main.printMsg("pressed:(" + e.getButton() + ") x=" + x + ", y=" + y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Left button stopped dragging
        if (e.getButton() == L_BUTTON) {
//            if (hasBeenDragged()) {
//                // Assign selection to units
////                int rectX = java.lang.Math.min(last_x, e.getX());
////                int rectY = java.lang.Math.min(last_y, e.getY());
////                int rectWidth = java.lang.Math.abs(last_x - e.getX());
////                int rectHeight = java.lang.Math.abs(last_y - e.getY());
//
////                last_drag_x = -1;
////                last_drag_y = -1;
//
//                // Selection of units by rect-selection
////                GameMap.getInstance().select(rectX, rectY, rectWidth, rectHeight);
//                GameMap.getInstance().select(Main.getMouseRect().getRect());
//
////                // Remove previous rect-selection
////                Main.getMouseRect().redefineRect(-1, -1, -1, -1);
////
////                // Drop mouse drag modifier
////                buttons[NO_BUTTON] = false;
////
////                Main.resumeAll();
//
//                Main.printMsg("released_drag(" + e.getButton() + "): x=" + x + ", y=" + y);
//            } else {
//                // TODO This idea is temporary
//                Main.suspendAll();
//
//                // Selection of units by rect-selection
//                GameMap.getInstance().select(e.getX(), e.getY(), 1, 1);
//
////                Main.resumeAll();
//
//                Main.printMsg("released_press(" + e.getButton() + "): x=" + x + ", y=" + y);
//            }

            // TODO: add here failback for the case of unexpected suspend state
            Main.printMsg("Left Mouse Button released(" + e.getButton() + "): x=" + x + ", y=" + y);

            // TODO Idea about realisation of MouseRect and MouseController
            /*
               May be best solution of realisation set MouseController as child of MouseRect
               Or convert all MouseController manipulations to GameMap logic with Rects only
            */
            if (!hasBeenDragged()) {
                // TODO This idea is temporary
                Main.suspendAll();

                // Point selection of unit by rect-selection
                GameMap.getInstance().select(new Rectangle(e.getX(), e.getY(), 1, 1));
                Main.printMsg("released_press(" + e.getButton() + "): x=" + x + ", y=" + y);
            } else {
                // Selection of units by rect-selection
                GameMap.getInstance().select(Main.getMouseRect().getRect());
                Main.printMsg("released_drag(" + e.getButton() + "): x=" + x + ", y=" + y);
            }

//        } else {
//            // TODO: add here fail back for the case of unexpected suspend state
//            Main.printMsg("released(" + e.getButton() + "): x=" + x + ", y=" + y);
        }

        // Remove previous rect-selection
        Main.getMouseRect().redefineRect(-1, -1, -1, -1);

        // Drop mouse drag modifier
        this.buttons[NO_BUTTON] = false;

        Main.resumeAll();

        this.last_x = x;
        this.last_y = y;

        this.x = e.getX();
        this.y = e.getY();

        this.buttons[e.getButton()] = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // maybe not needed
        buttons[e.getButton()] = false;

        this.last_x = x;
        this.last_y = y;

        this.x = e.getX();
        this.y = e.getY();
        //moving = true;
        //Main.printMsg("moved: x=" + x + ", y=" + y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.buttons[e.getButton()] = true;

        // ignore dragging with any other button except the left button
        // https://stackoverflow.com/questions/17441013/mousedragged-not-returning-appropriate-button-down:
        // FIXME Use isDown() for detect what button was Pressed now
        int left_button_down   = MouseEvent.BUTTON1_DOWN_MASK;
        int middle_button_down = MouseEvent.BUTTON2_DOWN_MASK;
        int right_button_down  = MouseEvent.BUTTON3_DOWN_MASK;

        if ((e.getModifiersEx() & (left_button_down | middle_button_down | right_button_down)) == left_button_down) {
            Main.printMsg("dragged(" + e.getButton() + ":" + e.getModifiersEx() + "): (" + last_x + "," + last_y + " -> (" + e.getX() + "," + e.getY() + ")");

            /* I want and I WILL use Thread.suspend() anyway! */
            // BUT I must do it very very careful, especially regarding printouts.
            // If I suspend the thread which is executing System.out.println at the moment
            // then it blocks PrintStream object since it is synchronized!
            // As a result, any other thread that is also trying to call System.out.println gets blocked.
            // If the current thread which calls suspend() is trying to System.out.println then it results to deadlock.
            // https://stackoverflow.com/questions/36631153/deadlock-with-system-out-println-and-a-suspended-thread
            Main.suspendAll();

            // Detect which of two points (last_x,last_y) and (e.getX(), e.getY())
            // is left-top and which is right-bottom
            int rectX = java.lang.Math.min(last_x, e.getX());
            int rectY = java.lang.Math.min(last_y, e.getY());
            int rectWidth = java.lang.Math.abs(last_x - e.getX());
            int rectHeight = java.lang.Math.abs(last_y - e.getY());

            // How it works without it?! how the lower layer is recovered after rect painting?
//            Main.getPanelUnter().repaint(0);
            Main.getMouseRect().redefineRect(rectX, rectY, rectWidth, rectHeight);

//            Main.printMsg("repainted.");
//            last_drag_x = e.getX();
//            last_drag_y = e.getY();
        }
    }

    public boolean hasBeenDragged() {
        return buttons[NO_BUTTON];
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Main.printMsg("wheelMoved(" + e.getButton() + "): x=" + e.getX() + ", y=" + e.getY());
    }

    // TODO May be delete this
    @Override
    public void mouseClicked(MouseEvent e) {
        this.buttons[e.getButton()] = true;

        // IMPORTANT:
        // I moved this code to mousePressed, because of the strange bug
        // Sometimes (3-5% of cases) I click and nothing happens
        // The logs showed that in this case mousePressed and then mouseReleased were
        // called (with thr same getX, getY)
        // but mouseClicked was not called. It might be a bug of the mouse driver or of AWT
        /*if (e.getButton() == MouseEvent.BUTTON3) { // right click
            // TODO: ideally C and V threads should be locked here when we reassignin the targets
            // currently it also works, but from the next round
            GameMap.getInstance().assign(e.getX(), e.getY());
        }
        */
        Main.printMsg("clicked(" + e.getButton() + "): x=" + e.getX() + ", y=" + e.getY());
    }

    /*
    public boolean isDown(int button) {
        // Фиксирует факт нажатия и удержание кнопки мыши (Кнопка нажата и удерживается)
        return buttons[button-1];
    }

    public boolean wasPressed(int button) {
        // Фиксирует факт нажатия кнопки мыши (Кнопка просто была нажата)
        return isDown(button) && !lastButtons[button-1];
    }

    public boolean wasReleased(int button) {
        // Фиксирует факт отпускания кнопки мыши (Кнопка, после нажатия, отпущена)
        return !isDown(button) && lastButtons[button-1];
    }

    public void turnOff() {
        disabled = true;
    }

    public void turnOn() {
        disabled = false;
    }

    public boolean isDisabled() {
        return disabled;
    }
    */
}
