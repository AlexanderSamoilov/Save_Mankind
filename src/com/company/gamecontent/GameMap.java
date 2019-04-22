package com.company.gamecontent;

import com.company.gamegeom.Parallelepiped.GridRectangle;
import com.company.gamegeom.Parallelepiped;

import com.company.gamecontrollers.MouseController;
import com.company.gamethread.Main;
import com.company.gamethread.V_Thread;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;
import static com.company.gamecontent.Restrictions.INTERSECTION_STRATEGY_SEVERITY;


public class GameMap {
    private static Logger LOG = LogManager.getLogger(GameMap.class.getName());

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
                    this.landscapeBlocks[i][j] = new GameMapBlock(i * BLOCK_SIZE, j * BLOCK_SIZE, map[i][j]);
                } catch (EnumConstantNotPresentException e) {
                    LOG.debug("Block (" + i + ", " + j + ")");
                    LOG.debug("Map size: " + width + "x" + height);
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
        // Redraw map blocks and Objects on them
        // TODO What about collections and Maps?
        // FIXME Can't move render landscapeBlocks into function - bad realisation
        for (int i = 0; i < getMaxX(); i++) {
            for (int j = 0; j < getMaxY(); j++) {

                /* For debug purpose: We draw only those blocks which are not occupied, otherwise
                there will be white space there, because the whole picture is "erased" on each step.
                To remove/add marking of the occupied blocks with white color please comment/uncomment "if".
                 */
                if (objectsOnMap[i][j].size() == 0) {
                    this.landscapeBlocks[i][j].render(g);
                }
            }
        }

        // Rendering objects on a blocks
        for (int i = 0; i < getMaxX(); i++) {
            for (int j = 0; j < getMaxY(); j++) {
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

        try {
            LOG.debug("--->");
            for (GameObject gameObj : gameObjSet) {
                gameObj.render(g);
            }
            LOG.debug("<---");
        } catch (ConcurrentModificationException e) {
            LOG.error("ConcurrentModificationException has reproduced!");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                LOG.error(stackTraceElement.toString());
            }
            V_Thread.getInstance().terminate(1000);
        }
    }

    public void select(Rectangle mouseRect) {

        /* crop the selection rectangle to take into account
           the case when the mouse cursor is outside the map
         */
        Rectangle mouseRectCropped = crop(mouseRect);

        if (selectedObjects == null) {
            selectedObjects = new HashSet<GameObject>();
        }

        // First deselect all selected objects
        // TODO Why we Clone them?
        this.deselect((HashSet<GameObject>)selectedObjects.clone());

        // Check objects in Rect-Selector and add them to selectedObjects
        GridRectangle gridRect = new GridRectangle(mouseRectCropped);
        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                if (objectsOnMap[i][j].size() == 0) {
                    continue;
                }

                boolean in_the_middle = gridRect.isMiddleBlock(i, j);

                for (GameObject objectOnMap : objectsOnMap[i][j]) {
                    Rectangle objectOnMapRect = objectOnMap.getRect();

                    // TODO Why is this working without contains()?
                    if (
                            !in_the_middle && // all middle block are contained (no need to check intersection)
                            !mouseRectCropped.intersects(objectOnMapRect)
//                          && !mouseRectCropped.contains(objectOnMapRect)
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
                assignUnit(selectedObj, point);
            }
        }
    }

    // QUESTION What this do? May be rename?
    // QUESTION Is this super.method() for GameObject.Unit.setTargets()? Why?
    private void assignUnit(GameObject selectedObj, Integer[] point){
        // FIXME: We must take floor(), not just divide!
        int block_x = point[0] / BLOCK_SIZE;
        int block_y = point[1] / BLOCK_SIZE;

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

        GridRectangle gridRect = new GridRectangle(gameObj.getRect());

        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                GameMap.getInstance().validateBlockCoordinates(i, j);
                this.objectsOnMap[i][j].add(gameObj);
            }
        }
    }

    // TODO Code Duplicate. Collections or method fixBlockPositionOnMap()
    // QUESTION What is this?
    // QUESTION Rename to erase()?
    public void eraseObject(GameObject gameObj) {

        GridRectangle gridRect = new GridRectangle(gameObj.getRect());

        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                GameMap.getInstance().validateBlockCoordinates(i, j);
                // TODO: check what if does not exist
                this.objectsOnMap[i][j].remove(gameObj);
            }
        }
    }

    // FIXME lndScapeBlocks.width. Remove Getter
    public int getMaxX() {
        // Block = lndScapeBlocks.(x,y)
        return this.landscapeBlocks.length;
    }

    // FIXME lndScapeBlocks.height. Remove Getter
    public int getMaxY() {
        return this.landscapeBlocks[0].length;
    }

    // TODO: We don't support 3D so far
    public int getMaxZ() {
        return Restrictions.getMaxZ();
    }

    public int getAbsMaxX() {
        return this.getMaxX() * BLOCK_SIZE;
    }

    public int getAbsMaxY() {
        return this.getMaxY() * BLOCK_SIZE;
    }

    public int getAbsMaxZ() {
        return this.getMaxZ() * BLOCK_SIZE;
    }

    public void validateBlockCoordinates(int grid_x, int grid_y) {
        if (
                (grid_x < 0) || (grid_x > getMaxX() - 1) ||
                (grid_y < 0) || (grid_y > getMaxY() - 1)
        ) {
            Main.terminateNoGiveUp(
                    1000, "Block (" + grid_x + "," + grid_y +
                    " is outside of map " + getMaxX() + " x " + getMaxY()
            );
        }
    }

    // Checks if the area "givenRect" is occupied by some GameObject.
    public boolean occupied(Rectangle givenRect) {
        return occupied(givenRect, null);
    }

    // Checks if the area "givenRect" where the given GameObject wants to move to/appear is occupied by some other GameObject.
    // Depending of INTERSECTION_STRATEGY_SEVERITY we decide how strictly we consider "occupied".
    // TODO: check not only intersection, but also containing (inclusion).
    public boolean occupied(Rectangle givenRect, GameObject exceptObject) {
        // With intersection severity level INTERSECTION_STRATEGY_SEVERITY=0 of two game objects is allowed.
        // Multiple units can use the same place (unreal, but let it be).

        if (INTERSECTION_STRATEGY_SEVERITY == 0) {
            return false;
        }

        GridRectangle gridRect = new GridRectangle(givenRect);

        // Check if we intersect another object
        // 1 - obtain the list of the map blocks which are intersected by the line of the object
        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {

                // TODO: We could introduce occupied(i, j, exceptMe) and call it in the loop
                GameMap.getInstance().validateBlockCoordinates(i, j);

                HashSet<GameObject> objectsOnBlock = GameMap.getInstance().objectsOnMap[i][j];
                if (objectsOnBlock.size() == 0) {
                    continue;
                }

                for (GameObject objOnBlock : objectsOnBlock) {
                    // Is me?
                    if ((exceptObject != null) && (objOnBlock == exceptObject)) {
                        continue;
                    }

                    if (INTERSECTION_STRATEGY_SEVERITY > 1) {
                        LOG.debug("INTERSECTS: i=" + i + ", j=" + j + ", thisObject=" + this + ", objOnBlock=" + objOnBlock);

                        // Severity 2: Multiple objects on the same block are forbidden even if they actually don't intersect
                        return true;
                    }
                    // ELSE: Severity 1: Multiple objects on the same block are allowed when they don't intersect

                    Rectangle objOnBlockRect = objOnBlock.getRect();

                    // DEBUG
//                    LOG.debug("Check 1: (" + thisObjRect.x + "," + thisObjRect.y + "," + thisObjRect.width + "," + thisObjRect.height);
//                    LOG.debug("Check 2: (" + objOnBlockRect.getX() + "," + objOnBlockRect.getY() + "," + objOnBlockRect.getWidth() + "," + objOnBlockRect.getHeight());
//                    LOG.debug("Check 3: (" + objOnBlock.getAbsLoc()[0] + "," + objOnBlock.getAbsLoc()[1] + "," + objOnBlock.getAbsSize()[0] + "," + objOnBlock.getAbsSize()[1]);

                    if (givenRect.intersects(objOnBlockRect)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // TODO: make unmodifiable
    // TODO Remove getters. Use Class.attr
    public HashSet<Bullet> getBullets() { return bullets; }

    // TODO: check that i,j are within allowed boundaries
    public boolean isBlockVisibleForMe(int i, int j) {
//        LOG.debug("get: " + i + "." + j + "." + Player.getMyPlayerId());
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

    // TODO Rename it to get()
    Parallelepiped getParallelepiped() {
        return new Parallelepiped(0, 0, 0, getMaxX(), getMaxY(), getMaxZ());
    }

    Rectangle getRect() {
        return getParallelepiped().getAbsBottomRect();
    }

    // Crops the given rectangle with the map rectangle (is used to avoid going outside the map)
    Rectangle crop(Rectangle rect) {
        // TODO: taking into account Swing bug with drawRect() I would recommend also to check how
        // properly works this .intersect method.
        Rectangle croppedRect = new Rectangle(rect);
        Rectangle.intersect(croppedRect, GameMap.getInstance().getRect(), croppedRect);
        return croppedRect;
    }

    boolean contains(Parallelepiped ppd) {
        return getParallelepiped().contains(ppd);
    }

    boolean contains(Integer[] point) {
        return getParallelepiped().contains(point);
    }

    /* DEBUG */
    public void show() {
        for (int i = 0; i < getMaxX(); i++) {
            for (int j = 0; j < getMaxY(); j++) {
                int plId = -1;
                GameObject target = null;
                if (objectsOnMap[i][j].size() != 0) {
                    for (GameObject currObj : objectsOnMap[i][j]) {
                        plId = currObj.getPlayerId();
                        target = ((Unit) (currObj)).getTargetObject();
                        LOG.debug("(" + i + "," + j + ")[" + plId + "]:" + currObj + " -> " + target);
                    }
                }
            }
        }
    }

    /* TEST-01 */
    // Randomising landscapeBlocks
    public void rerandom() {
        for(int i=0; i < getMaxX(); i++)
            for (int j=0; j < getMaxY(); j++)
            {
                landscapeBlocks[i][j].changeNature(); // pseudo-random
            }
    }
}
