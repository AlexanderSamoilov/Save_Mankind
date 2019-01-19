package com.gamecontent;

import com.gamecontrollers.MouseController;
import com.gamethread.Main;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class GameMap {

    private static final GameMap instance = new GameMap();
    // TODO what about Units, Buildings? Why Bullets separate of them?
    // TODO Guava has Table<R, C, V> (table.get(x, y)). May be create Generic Class?
    HashSet<GameObject>[][]       objectsOnMap    = null;

    HashSet<GameObject>           selectedObjects = null;
    HashMap<Integer,Boolean> [][] visibleMap      = null;
    GameMapBlock[][]              landscapeBlocks = null;
    HashSet<Bullet>               bullets         = null;

    private static boolean initialized = false;

    public static GameMap getInstance() {
        return instance;
    }

    private GameMap() {}

    public void init(int[][] map, int width, int height) throws EnumConstantNotPresentException {
        if (initialized) {
            Main.terminateNoGiveUp(
                    1000,
                    getClass() + " init error. Not allowed to initialize the map twice!"
            );
        }

        // Validating map
        boolean width_validation = (width <= 0) || (width > Restrictions.MAX_X);
        boolean height_validation = (height <= 0) || (height > Restrictions.MAX_Y);
        if (width_validation || height_validation) {
            Main.terminateNoGiveUp(
                    1000,
                    getClass() + " init error. Width and Height are beyond the restricted boundaries."
            );
        }

        this.landscapeBlocks = new GameMapBlock[width][height];

        // TODO What about collections and Maps?
        this.objectsOnMap = new HashSet[width][height];
        this.visibleMap   = new HashMap[width][height];

        // TODO What is i and j?
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Declaring landscape blocks
                try {
                    this.landscapeBlocks[i][j] = new GameMapBlock(i, j, map[i][j]);
                } catch (EnumConstantNotPresentException e) {
                    Main.printMsg("Block (" + i + ", " + j + ")");
                    Main.printMsg("Map size: " + width + "x" + height);
                    Main.terminateNoGiveUp(
                            1000,
                            getClass() + " init error. Blocks array out of bounds"
                    );
                }

                // Declaring map with default objects
                this.objectsOnMap[i][j] = new HashSet<GameObject>();

                // Declaring visibility map for blocks
                // TODO: currently everything is visible for everybody - the logic is to be designed
                this.visibleMap[i][j] = new HashMap<Integer, Boolean>();
                for (int k = 0; k <= Restrictions.getMaxPlayers() - 1; k++) {
                    this.visibleMap[i][j].put(k, true);
                }
            }
        }

        this.selectedObjects = new HashSet<GameObject>();
        this.bullets = new HashSet<Bullet>();

        initialized = true;
    }

    public void render(Graphics g) {
        //Main.getGraphDriver().fillRect(0,0, getWidthAbs(), getLenAbs());

        // Redraw map blocks and Objects on them
        // TODO What about collections and Maps?
        // FIXME Can't move render landscapeBlocks into function - bad realisation
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                /* DEBUG */
                boolean occupied = objectsOnMap[i][j].size() != 0;
//                boolean occupied = false;
                this.landscapeBlocks[i][j].render(g, occupied);
                this.renderObjects(g, objectsOnMap[i][j]);
            }
        }

        // TODO: fix ConcurrentModificationException
        this.renderBullets(g);
    }

    private void renderBullets(Graphics g) {
        // TODO: fix ConcurrentModificationException
        if (bullets == null) {
            return;
        }

        for (Bullet b : bullets) {
            b.render(g);
        }
    }

    private void renderObjects(Graphics g, HashSet<GameObject> gameObjSet) {
        // FIXME Exception in thread "AWT-EventQueue-0" java.util.ConcurrentModificationException\
        if (gameObjSet.size() == 0) {
            return;
        }

        for (GameObject gameObj : gameObjSet) {
            gameObj.render(g);
        }
    }

    public void select(Rectangle mouseRect) {
        if (selectedObjects == null) {
            selectedObjects = new HashSet<GameObject>();
        }

        // First deselect all selected objects
        // TODO Why we Clone them?
        this.deselect((HashSet<GameObject>)selectedObjects.clone());

        // Check objects in Rect-Selector and add them to selectObjects
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                if (objectsOnMap[i][j].size() == 0) {
                    continue;
                }

                for (GameObject objectOnMap : objectsOnMap[i][j]) {
                    Rectangle objectOnMapRect = objectOnMap.getRect();
                    // TODO Why is this working without contains()?
                    if (
                            !objectOnMapRect.intersects(mouseRect)
//                                    && !mouseRect.contains(objectOnMapRect)
                    ) {
                        continue;
                    }

                    // TODO go.isMine()
                    // TODO I can select Enemy objects!
                    // If this Objects not mine or neutral
                    if (objectOnMap.getPlayerId() > 0) {
                        continue;
                    }

                    objectOnMap.select();
                    this.selectedObjects.add(objectOnMap);
                }
            }
        }
    }

    private void deselect(HashSet<GameObject> objects) {
        if (selectedObjects == null) {
            return;
        }

        for (GameObject selectedObj : objects) {
            selectedObj.deselect();
            this.selectedObjects.remove(selectedObj);
        }
    }

    public void assign(Integer[] point) {
        if (selectedObjects == null || selectedObjects.size() == 0) {
            return;
        }

        for (GameObject selectedObj : selectedObjects) {
            // TODO: that is wrong if we allow moveable Buildings
            if (selectedObj instanceof Unit) {
                this.assignUnit(selectedObj, point);
            }
        }
    }

    // TODO May be move it to Unit?
    private void assignUnit(GameObject selectedObj, Integer[] point){
        // FIXME: We must take floor(), not just divide!
        int block_x = point[0] / Restrictions.BLOCK_SIZE;
        int block_y = point[1] / Restrictions.BLOCK_SIZE;

        // TODO: currently we don't consider "visibility" of the point by the player/enemy.
        HashSet<GameObject> objectsOnBlock = objectsOnMap[block_x][block_y];

        boolean block_visible = isBlockVisibleForMe(block_x, block_y);
        boolean is_me         = (objectsOnBlock.size() == 1) && objectsOnBlock.contains(selectedObj);
        boolean nobody        = objectsOnBlock.size() == 0;

        // Anyway select enemy unit and attack
        if (block_visible && !is_me && !nobody) {
            // FIXME Use Map, collections or generators. After refactor if
            // FIXME This algorithm is not optimal for big number of units on block
            // In case of several object on the block we choose the first randomly
            // found from them to attack
            for (GameObject objOnBlock : objectsOnBlock) {
                if (objOnBlock.contains(point) && objOnBlock != selectedObj) {
                    if (((Unit) selectedObj).setTargetObject(objOnBlock)) {
                        return;
                    }
                }
            }
        }

        // Attack point
        if (MouseController.attackFocus) {
            ((Unit) selectedObj).setTargetPoint(point);

            // FIXME Move it in MouseController until reasons clarified
            MouseController.attackFocus = false;

            return;
        }

        // Move to point
        selectedObj.setDestinationPoint(point);
    }

    // TODO Code Duplicate. Collections or method fixBlockPositionOnMap()
    // TODO What is this?
    public void registerObject(GameObject gameObj) {
        // Left-top coordinate of object
        int obj_x = gameObj.loc[0] / Restrictions.BLOCK_SIZE;
        int obj_y = gameObj.loc[1] / Restrictions.BLOCK_SIZE;
        for (int i = obj_x; i <= obj_x + gameObj.size[0]; i++) {
            for (int j = obj_y; j <= obj_y + gameObj.size[1]; j++) {
                // TODO: remove these temporary defense after implement safe check of map bounds:
                int i_fixed = (i == GameMap.getInstance().getWidth()) ? i-1 : i;
                int j_fixed = (j == GameMap.getInstance().getHeight()) ? j-1 : j;
                this.objectsOnMap[i_fixed][j_fixed].add(gameObj);
            }
        }
    }

    // TODO Code Duplicate. Collections or method fixBlockPositionOnMap()
    // TODO What is this?
    public void eraseObject(GameObject gameObj) {
        // Left-top coordinate of object
        int obj_x = gameObj.loc[0] / Restrictions.BLOCK_SIZE;
        int obj_y = gameObj.loc[1] / Restrictions.BLOCK_SIZE;
        for (int i = obj_x; i <= obj_x + gameObj.size[0]; i++) {
            for (int j = obj_y; j <= obj_y + gameObj.size[1]; j++) {
                // TODO: remove these temporary defense after implement safe check of map bounds:
                int i_fixed = (i == GameMap.getInstance().getWidth()) ? i-1 : i;
                int j_fixed = (j == GameMap.getInstance().getHeight()) ? j-1 : j;

                // TODO: check what if does not exist
                this.objectsOnMap[i_fixed][j_fixed].remove(gameObj);
            }
        }
    }

    // FIXME lndScapeBlocks.width. Remove Getter
    public int getWidth() {
        // Block = lndScapeBlocks.(x,y)
        return this.landscapeBlocks.length;
    }

    // FIXME lndScapeBlocks.height. Remove Getter
    public int getHeight() {
        return this.landscapeBlocks[0].length;
    }

    public int getWidthAbs() {
        return this.getWidth() * Restrictions.BLOCK_SIZE;
    }

    public int getHeightAbs() {
        return this.getHeight() * Restrictions.BLOCK_SIZE;
    }

    // TODO: make unmodifiable
    // TODO Remove getters. Use Class.attr
    public HashSet<Bullet> getBullets() {
        return bullets;
    }

    // TODO: check that i,j are within allowed boundaries
    public boolean isBlockVisibleForMe(int i, int j) {
        //Main.printMsg("get: " + i + "." + j + "." + Player.getMyPlayerId());
        return visibleMap[i][j].get(0);
    }

    public void registerBullet(Bullet b) {
        // TODO: check validity of bullet coordinates, return and process bad result
        bullets.add(b);
    }

    // TODO Move it in Spawner class
    public void destroyBullet(Bullet b) {
        // TODO; check if exists
        bullets.remove(b);
        b.unsetDestinationPoint();

        // delete - -TODO: move to another class!
//        b = null;
    }

    /* DEBUG */
    public void show() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                int plId = -1;
                GameObject target = null;
                if (objectsOnMap[i][j].size() != 0) {
                    for (GameObject currObj : objectsOnMap[i][j]) {
                        plId = currObj.getPlayerId();
                        target = ((Unit) (currObj)).getTargetObject();
                        Main.printMsg("(" + i + "," + j + ")[" + plId + "]:" + currObj + " -> " + target);
                    }
                }
            }
        }
    }

    /* TEST-01 */
    // Randomising landscapeBlocks
    public void rerandom() {
        for(int i=0; i<getWidth();i++)
            for (int j=0;j<getHeight();j++)
            {
                landscapeBlocks[i][j].changeNature(); // pseudo-random
            }
    }
}
