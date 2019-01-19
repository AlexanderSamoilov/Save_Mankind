package com.gamecontent;

import com.gamecontrollers.MouseController;
import com.gamethread.Main;

import javax.imageio.IIOException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameMap {

    HashSet<GameObject> [][] objects = null;
    HashSet<GameObject> selectedObjects = null;
    HashMap<Integer,Boolean> [][] visible = null;
    GameMapBlock[][] blocks = null;
    HashSet<Bullet> bullets = null;

    // https://habr.com/post/27108/
    private static volatile GameMap instance;
    private static boolean initialized = false;

    public static GameMap getInstance() {
        if (instance == null) {
            synchronized (GameMap.class) {
                if (instance == null)
                    instance = new GameMap();
            }
        }
        return instance;
    }

    private GameMap() {}

    public void init(int[][] map, int wid, int len) throws IllegalArgumentException, ArrayIndexOutOfBoundsException, IIOException, IOException {
        if (initialized) {
            throw new IllegalArgumentException("Not allowed to initialize the map twice!");
        } else {
            // 2 - validation
            if ((wid <= 0) || (wid > Restrictions.getMaxX()) || (len <= 0) || (len > Restrictions.getMaxY())) {
                throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries.");
            }

            blocks = new GameMapBlock[wid][len];

            for(int i=0; i<wid;i++)
                for (int j=0;j<len;j++)
                {
                    try {
                        blocks[i][j] = new GameMapBlock(i, j, map[i][j]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Main.printMsg("ArrayIndexOutOfBoundsException during map initialization!" + "i=" + i + ", j=" + j + ", wid=" + wid + ", len=" + len);
                        throw e;
                    }
                }
                initialized = true;

            // 3 - default
            objects = new HashSet[wid][len];
            for(int i=0; i<wid;i++) {
                for (int j = 0; j < len; j++) {
                    objects[i][j] = new HashSet<GameObject>();
                }
            }
            selectedObjects = new HashSet<GameObject>();
            visible = new HashMap[wid][len];
            // TODO: currently everything is visible for everybody - the logic is to be designed
            for(int i=0; i<wid;i++) {
                for (int j = 0; j < len; j++) {
                    visible[i][j] = new HashMap<Integer, Boolean>();
                    for (int k = 0; k<=Restrictions.getMaxPlayers() - 1; k++) {
                        visible[i][j].put(k, true);
                        //Main.printMsg("put: " + i + "." + j + "." + k);
                    }
                }
            }
            bullets = new HashSet<Bullet>();
        }
    }

    public HashSet<Bullet> getBullets() {
        return bullets; // TODO: make unmodifiable
    }

    public boolean isBlockVisibleForMe(int i, int j) {
        // TODO: check that i,j are within allowed boundaries
        //Main.printMsg("get: " + i + "." + j + "." + Player.getMyPlayerId());
        return visible[i][j].get(0);
    }

    public void registerObject(GameObject go) {
        int idxX = go.loc[0] / Restrictions.getBlockSize();
        int idxY = go.loc[1] / Restrictions.getBlockSize();
        for (int i = idxX; i <= idxX + go.size[0]; i++) {
            for (int j = idxY; j <= idxY + go.size[1]; j++) {
                // TODO: remove these temporary defense after implement safe check of map bounds:
                int i_fixed = (i == GameMap.getInstance().getWid()) ? i-1 : i;
                int j_fixed = (j == GameMap.getInstance().getLen()) ? j-1 : j;
                objects[i_fixed][j_fixed].add(go);
                //Main.printMsg("IN: " + go + ": (" + i + "," + j + ").");
                //Main.printMsg("IN: " + go + ": (" + go.loc[0] + "," + go.loc[1] + ").");
            }
        }
    }

    public void eraseObject(GameObject go) {
        int idxX = go.loc[0] / Restrictions.getBlockSize();
        int idxY = go.loc[1] / Restrictions.getBlockSize();
        for (int i = idxX; i <= idxX + go.size[0]; i++) {
            for (int j = idxY; j <= idxY + go.size[1]; j++) {
                // TODO: remove these temporary defense after implement safe check of map bounds:
                int i_fixed = (i == GameMap.getInstance().getWid()) ? i-1 : i;
                int j_fixed = (j == GameMap.getInstance().getLen()) ? j-1 : j;
                objects[i_fixed][j_fixed].remove(go); // TODO: check what if does not exist
                //Main.printMsg("IN: " + go + ": (" + i + "," + j + ").");
                //Main.printMsg("IN: " + go + ": (" + go.loc[0] + "," + go.loc[1] + ").");
            }
        }
    }

    public void registerBullet(Bullet b) {
        // TODO: check validity of bullet coordinates, return and process bad result
        bullets.add(b);
    }

    public void destroyBullet(Bullet b) {
        // TODO; check if exists
        bullets.remove(b);
        b.unsetDestinationPoint();
        b = null; // delete - -TODO: move to another class!
    }

    // test
    public void rerandom() {
        for(int i=0; i<getWid();i++)
            for (int j=0;j<getLen();j++)
            {
                blocks[i][j].changeNature(); // pseudo-random
            }
    }

    public void render(Graphics g) { // TODO: replace "objects", move GameObject to GameMapBlock
        //Main.getGraphDriver().fillRect(0,0, getWidAbs(), getLenAbs());
        // redraw map blocks
        for (int i = 0; i < getWid(); i++) {
            for (int j = 0; j < getLen(); j++) {
                // for debugging
                //boolean occupied = objects[i][j].size() != 0;
                boolean occupied = false;
                blocks[i][j].render(g, occupied);
            }
        }
        // redraw game objects
        for (int i = 0; i < getWid(); i++) {
            for (int j = 0; j < getLen(); j++) {
                if (objects[i][j].size() != 0) {
                    //Main.printMsg("RENDER MAP: x=" + i + ", y=" + j + ", obj=" + this);
                    for (GameObject go : objects[i][j]) { // TODO: Fix ConcurrentModificationException (Main.java 91 paintComponent)
                        go.render(g);
                    }
                }
            }
        }

        if (bullets != null) {
            for (Bullet b : bullets) { // TODO: fix ConcurrentModificationException
                b.render(g);
            }
        }

        //Main.getPanelUnter().show();
        //Main.getBufferStrategy().show();
        //Main.getFrame().repaint();
    }

    public void select(int rectX, int rectY, int rectWid, int rectHei) {

        // first unselect all
        for (GameObject so : (HashSet<GameObject>)selectedObjects.clone()) {
            //Main.printMsg("selectedObject0: " + so);
            so.unselect(); // unselect in GameObject class
            selectedObjects.remove(so); // unselect on the map
        }

        for (int i = 0; i < getWid(); i++) {
            for (int j = 0; j < getLen(); j++) {
                if (objects[i][j].size() != 0) {
                    for (GameObject go : objects[i][j]) {
                        Rectangle objectRect = new Rectangle(go.loc[0], go.loc[1], go.size[0] * Restrictions.getBlockSize(), go.size[1] * Restrictions.getBlockSize());
                        Rectangle selectRect = new Rectangle(rectX, rectY, rectWid, rectHei);
                        if (objectRect.intersects(selectRect) || selectRect.contains(objectRect)) {
                            if (go.getPlayerId() == 0) { // I can select only my own units, not enemy or ally
                                go.select(); // select in GameObject class
                                selectedObjects.add(go); // select on the map
                                //Main.printMsg("selected(" + i + "," + j + "): " + go);
                            } else {
                                //Main.printMsg("not my object(" + i + "," + j + "): " + go);
                            }
                        }
                    }
                } else {
                    //Main.printMsg("null(" + i + "," + j + "): " + objects[i][j].size());
                }
            }
        }
        for (GameObject so : selectedObjects) {
            //Main.printMsg("selectedObject1: " + so);
        }
    }

    // TODO: unset target point if the object there is destroyed? or just shoot?
    public void assign(Integer[] vect) { // should be called by D-Thread
        if (selectedObjects.size() == 0) {
            Main.printMsg("NO SELECTED OBJECTS.");
            return;
        }
        for (GameObject go : selectedObjects) {
            //Main.printMsg("check=" + go);
            if (go instanceof Unit) { // TODO: that is wrong if we allow moveable Buildings
                // FIX IT: multiple objects may stay on block?
                // FIX IT: we must take floor(), not just divide!
                int i = vect[0] / Restrictions.getBlockSize();
                int j = vect[1] / Restrictions.getBlockSize();
                // TODO: currently we don't consider "visibility" of the point by the player/enemy.
                boolean visible = isBlockVisibleForMe(i, j);

                //Main.printMsg("visible=" + visible);
                if (visible) {
                    HashSet<GameObject> objectsOnBlock = objects[i][j];
                    // FUN: remove the condition (objectOnBlock == go) and units will be able to shoot itself
                    if ((objectsOnBlock.size() == 0) || ((objectsOnBlock.size() == 1) && (objectsOnBlock.contains(go)))) { // we pointed on an unallocated block on map or on the block of itself
                        if (MouseController.attackFocusActive()) { // not implemented yet (always false)
                            ((Unit) go).setAttackPoint(vect);
                            MouseController.deactivateAttackFocus();
                        } else {
                            go.setDestinationPoint(vect);
                        }
                    } else { // there is another game object on the block
                        for (GameObject thatGO : objectsOnBlock) {
                            Rectangle objectRect = new Rectangle(thatGO.loc[0], thatGO.loc[1], thatGO.size[0] * Restrictions.getBlockSize(), thatGO.size[1] * Restrictions.getBlockSize());
                            if (objectRect.contains(vect[0], vect[1])) {
                                ((Unit) go).setAttackObject(thatGO);
                                break; // in case of several object on the block we choose the first randomly found from them to attack
                            }
                        }
                    }
                } else { // invisible for me
                    if (MouseController.attackFocusActive()) { // not implemented yet (always false)
                        ((Unit) go).setAttackPoint(vect);
                        MouseController.deactivateAttackFocus();
                    } else {
                        go.setDestinationPoint(vect);
                    }
                }
            }
        }
    }

    public int getWid() {
        return blocks.length;
    }

    public int getLen() {
        return blocks[0].length;
    }

    public int getWidAbs() {
        return getWid() * Restrictions.getBlockSize();
    }

    public int getLenAbs() {
        return getLen() * Restrictions.getBlockSize();
    }

    public void print() {
        for (int i = 0; i < getWid(); i++) {
            for (int j = 0; j < getLen(); j++) {
                int plId = -1;
                GameObject target = null;
                if (objects[i][j].size() != 0) {
                    for (GameObject go : objects[i][j]) {
                        plId = go.getPlayerId();
                        target = ((Unit) (go)).getAttackObject();
                        Main.printMsg("(" + i + "," + j + ")[" + plId + "]:" + go + " -> " + target);
                    }
                }
            }
        }
    }
}
