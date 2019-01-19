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

    private final boolean[] buttons = new boolean[NUM_BUTTONS];
    private final boolean[] lastButtons = new boolean[NUM_BUTTONS];

    private int             x = -1;         // Текущее положение курсора по оси Х
    private int             y = -1;         // Текущее положение курсора по оси Y
    private int             last_x = -1;    // Предыдущее, последнее положение курсора по оси Х
    private int             last_y = -1;    // Предыдущее, последнее положение курсора по оси Y
    private int             last_drag_x = -1;    // Предыдущее, последнее положение курсора по оси Х
    private int             last_drag_y = -1;    // Предыдущее, последнее положение курсора по оси Y

    //private boolean         moving;         // Двигается ли курсор мышки
    //private boolean         disabled;       // Вкл/Выкл контроллера

    private static boolean attackFocus = false;

    public static boolean attackFocusActive() {
        return attackFocus;
    }

    public static void deactivateAttackFocus() {
        attackFocus = false;
    }

    public static void activateAttackFocus() {
        attackFocus = true;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    //public boolean isMoving() { return moving; }

    @Override
    public void mousePressed(MouseEvent e) {
        buttons[e.getButton()] = true;
        if (e.getButton() == MouseEvent.BUTTON3) { // right click
            // TODO: ideally C and V threads should be locked here when we reassignin the targets
            // currently it also works, but from the next round
            GameMap.getInstance().assign(new Integer[]{e.getX(), e.getY()});
        }
        last_x = x;
        last_y = y;
        x = e.getX();
        y = e.getY();
        //GameMap.getInstance().assign(x, y);
        Main.printMsg("pressed:(" + e.getButton() + ") x=" + x + ", y=" + y);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if ((e.getButton() == MouseEvent.BUTTON1)) { // right button stopped dragging
            if (buttons[MouseEvent.NOBUTTON]) { // released dragging
                // assign selection to units
                int rectX = java.lang.Math.min(last_x, e.getX());
                int rectY = java.lang.Math.min(last_y, e.getY());
                int rectWid = java.lang.Math.abs(last_x - e.getX());
                int rectHei = java.lang.Math.abs(last_y - e.getY());
                GameMap.getInstance().select(rectX, rectY, rectWid, rectHei);

                last_drag_x = -1;
                last_drag_y = -1;

                Main.getMouseRect().redefineRect(-1, -1, -1, -1);
                Main.resumeAll();
                buttons[MouseEvent.NOBUTTON] = false;
                Main.printMsg("released_drag(" + e.getButton() + "): x=" + x + ", y=" + y);
            } else { // pressed and released (actually, clicked)
                Main.suspendAll();
                GameMap.getInstance().select(e.getX(), e.getY(), 1, 1);
                Main.resumeAll();
                Main.printMsg("released_press(" + e.getButton() + "): x=" + x + ", y=" + y);
            }
        } else {
            // TODO: add here failback for the case of unexpected suspend state
            Main.printMsg("released(" + e.getButton() + "): x=" + x + ", y=" + y);
        }
        last_x = x;
        last_y = y;
        x = e.getX();
        y = e.getY();
        buttons[e.getButton()] = false;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        buttons[e.getButton()] = false; // maybe not needed
        last_x = x;
        last_y = y;
        x = e.getX();
        y = e.getY();
        //moving = true;
        //Main.printMsg("moved: x=" + x + ", y=" + y);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        buttons[e.getButton()] = true;
        // ignore dragging with any other button except the left button
        // https://stackoverflow.com/questions/17441013/mousedragged-not-returning-appropriate-button-down:
        int b1 = MouseEvent.BUTTON1_DOWN_MASK;
        int b2 = MouseEvent.BUTTON2_DOWN_MASK;
        int b3 = MouseEvent.BUTTON3_DOWN_MASK;
        if ((e.getModifiersEx() & (b1 | b2 | b3)) == b1) {

            Main.printMsg("dragged(" + e.getButton() + ":" + e.getModifiersEx() + "): (" + last_x + "," + last_y + " -> (" + e.getX() + "," + e.getY() + ")");

            /* I want and I WILL use Thread.suspend() anyway! */
            // BUT I must do it very very careful, especially regarding printouts.
            // If I suspend the thread which is executing System.out.println at the moment
            // then it blocks PrintStream object since it is synchronized!
            // As a result, any other thread that is also trying to call System.out.println gets blocked.
            // If the current thread which calls suspend() is trying to System.out.println then it results to deadlock.
            // https://stackoverflow.com/questions/36631153/deadlock-with-system-out-println-and-a-suspended-thread
            Main.suspendAll();

            int rectX = java.lang.Math.min(last_x, e.getX());
            int rectY = java.lang.Math.min(last_y, e.getY());
            int rectWid = java.lang.Math.abs(last_x - e.getX());
            int rectHei = java.lang.Math.abs(last_y - e.getY());

            //Main.getPanelUnter().repaint(0); - how it works without it?! how the lower layer is recovered after rect painting?
            Main.getMouseRect().redefineRect(rectX, rectY, rectWid, rectHei);
            //Main.printMsg("repainted.");

            last_drag_x = e.getX();
            last_drag_y = e.getY();
        } else {
            //Main.printMsg("dragged(" + e.getButton() + ":" + e.getModifiersEx() + "): (" + last_x + "," + last_y + " -> (" + e.getX() + "," + e.getY() + ")");
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Main.printMsg("wheelMoved(" + e.getButton() + "): x=" + e.getX() + ", y=" + e.getY());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        buttons[e.getButton()] = true;
        // IMPORTANT:
        // I moved this oode to mousePressed, because of the strange bug
        // Sometimes (3-5% of cases) I click and nothing happens
        // The logs showed that in this case mousePressed and then mouseReleased were called (with thr same getX, getY)
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
