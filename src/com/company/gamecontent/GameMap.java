package com.company.gamecontent;

import com.company.gamegeom._2d.GridRectangle;
import com.company.gamegeom._3d.ParallelepipedOfBlocks;

import com.company.gamecontrollers.MouseController;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamethread.Main;
import com.company.gamethread.ParameterizedMutexManager;
import com.company.gamethread.V_Thread;

import java.awt.*;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;
import static com.company.gamecontent.Restrictions.INTERSECTION_STRATEGY_SEVERITY;


public class GameMap implements Renderable {

    private static Logger LOG = LogManager.getLogger(GameMap.class.getName());

    private static GameMap curr = new GameMap();
    private static GameMap next = new GameMap();

    // TODO: make it public final. For that we have to get rid of init() and do everything in the constructor.
    private static ParallelepipedOfBlocks parallelepiped;

    // TODO what about Units, Buildings? Why Bullets separate of them?
    // TODO Guava has Table<R, C, V> (table.get(x, y)). May be create Generic Class?
    HashSet<GameObject>[][]       objectsOnMap    = null;
    HashSet<GameObject>           selectedObjects = null;
    HashMap<Integer,Boolean> [][] visibleMap      = null;
    GameMapBlock[][]              landscapeBlocks = null;
    HashSet<Bullet>               bullets         = null;

    private static boolean initialized = false;

    public static GameMap getNextInstance() {
        // Only calculation thread is allowed to operate with the "next" game state
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C", "D")));
        return next;
    }

    public static GameMap getCurrentInstance() {
        // Only visualization thread is allowed to operate with the "current" game state
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "V")));
        return curr;
    }

    public static void switchRoles() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));
        GameMap swap = next;
        next = curr;
        curr = swap;

        // clone:
        /*
        for (int i = 0; i < getDim().x(); i++) {
            for (int j = 0; j < getDim().y(); j++) {
                curr.objectsOnMap[i][j] = next.objectsOnMap[i][j];
            }
        }
        for (GameObject selectedObj : (HashSet<GameObject>)curr.selectedObjects.clone()) {
            if (! next.selectedObjects.contains(selectedObj)) {
                curr.selectedObjects.remove(selectedObj);
            }
        }
        for (GameObject selectedObj : next.selectedObjects) {
            if (! curr.selectedObjects.contains(selectedObj)) {
                curr.selectedObjects.add(selectedObj);
            }
        }
        for (Bullet bul : (HashSet<Bullet>)curr.bullets.clone()) {
            if (! next.bullets.contains(bul)) {
                curr.bullets.remove(bul);
            }
        }
        for (Bullet bul : next.bullets) {
            if (! curr.bullets.contains(bul)) {
                curr.bullets.add(bul);
            }
        }*/
    }

    private GameMap() {}

    private static void initInstance(GameMap inst, int[][] map, int width, int height) throws EnumConstantNotPresentException {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));
        // TODO What about collections and Maps?
        inst.objectsOnMap = new HashSet[width][height];
        inst.visibleMap   = new HashMap[width][height];
        inst.landscapeBlocks = new GameMapBlock[width][height];

        // TODO What is i and j?
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                // Declaring landscape blocks
                try {
                    inst.landscapeBlocks[i][j] = new GameMapBlock(i, j, map[i][j]);
                } catch (Exception e) {
                    LOG.error("Block (" + i + ", " + j + ")");
                    LOG.error("Map size: " + width + "x" + height);
                    Main.terminateNoGiveUp(e,
                            1000,
                            inst.getClass() + ": Map initialization failed with " + e.getClass().getSimpleName()
                    );
                }

                // Declaring map with default objects
                inst.objectsOnMap[i][j] = new HashSet<GameObject>();

                // Declaring visibility map for blocks
                // TODO: currently everything is visible for everybody - the logic is to be designed
                inst.visibleMap[i][j] = new HashMap<Integer, Boolean>();
                for (int k = 0; k <= Restrictions.MAX_PLAYERS - 1; k++) {
                    inst.visibleMap[i][j].put(k, true);
                }
            }
        }

        inst.selectedObjects = new HashSet<GameObject>();
        inst.bullets = new HashSet<Bullet>();
    }

    public static void init(int[][] map, int width, int height) throws EnumConstantNotPresentException {

        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));

        if (initialized) {
            Main.terminateNoGiveUp(null,
                    1000,
                    GameMap.class + " init error. Not allowed to initialize the map twice!"
            );
        }

        // Validating map
        boolean width_validation = (width <= 0) || (width > Restrictions.MAX_X);
        boolean height_validation = (height <= 0) || (height > Restrictions.MAX_Y);
        if (width_validation || height_validation) {
            Main.terminateNoGiveUp(null,
                    1000,
                    GameMap.class + " init error. Width and Height are beyond the restricted boundaries."
            );
        }

        // MAX_Z because we don't support 3D so far
        parallelepiped = new ParallelepipedOfBlocks(new Point3D_Integer(0, 0, 0), new Vector3D_Integer(width, height, Restrictions.MAX_Z));

        initInstance(curr, map, width, height);
        initInstance(next, map, width, height);

        initialized = true;
    }

    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

        // Redraw map blocks and Objects on them
        // TODO What about collections and Maps?
        // FIXME Can't move render landscapeBlocks into function - bad realisation
        for (int i = 0; i < getDim().x(); i++) {
            for (int j = 0; j < getDim().y(); j++) {

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
        for (int i = 0; i < getDim().x(); i++) {
            for (int j = 0; j < getDim().y(); j++) {
                this.renderObjects(g, objectsOnMap[i][j]);
            }
        }

        // TODO: fix ConcurrentModificationException
        this.renderBullets(g);
    }

    private void renderBullets(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

        // TODO: fix ConcurrentModificationException
        if (bullets == null) {
            return;
        }

        for (Bullet b : bullets) {
            b.render(g);
        }
    }

    private void renderObjects(Graphics g, HashSet<GameObject> gameObjSet) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

        // FIXME Exception in thread "AWT-EventQueue-0" java.util.ConcurrentModificationException\
        if (gameObjSet.size() == 0) {
            return;
        }

        try {
            LOG.trace("--->");
            for (GameObject gameObj : gameObjSet) {
                gameObj.render(g);
            }
            LOG.trace("<---");
        } catch (ConcurrentModificationException e) {
            LOG.error("ConcurrentModificationException has reproduced!");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                LOG.error(stackTraceElement.toString());
            }
            V_Thread.getInstance().terminate(1000);
        }
    }

    // IMPORTANT: This method is static, because it operates with both "current" and "next" map states.
    // The problem is that when the user selects units on the map he relies on the picture he is seeing.
    // But this picture is produced by the V-Thread while C-thread might already move/create/destroy some units
    // which is still not visible on the screen, because C-Thread is calculating "for future".
    // But we must assure for the user that he selects/assigns exactly what he is seeing on the screen!
    // This is why this method must work this way:
    // 1. Check which units of "current" map were selected (using .getCurrentInstance()).
    // 2. Find the same units in the "next" map and select them there (using .getNextInstance()).
    //    During this action C-Thread must be locked!!
    //    Q: V-Thread also must be locked?
    // TODO: we call here many times getNextInstance() and the thread check is done many times. Maybe improve this.
    public static void select(Rectangle mouseRect) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("D")));

        /* crop the selection rectangle to take into account
           the case when the mouse cursor is outside the map
         */
        Rectangle mouseRectCropped = crop(mouseRect);

        if (getNextInstance().selectedObjects == null) {
            getNextInstance().selectedObjects = new HashSet<GameObject>();
        }

        // First deselect all selected objects
        // TODO Why we Clone them?
        getNextInstance().deselect((HashSet<GameObject>)getNextInstance().selectedObjects.clone());

        // Check objects in Rect-Selector of "curr" and add them to selectedObjects of "next"
        GridRectangle gridRect = new GridRectangle(mouseRectCropped);
        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                if (getCurrentInstance().objectsOnMap[i][j].size() == 0) {
                    continue;
                }

                boolean in_the_middle = gridRect.isMiddleBlock(i, j);

                for (GameObject objectOnMap : getCurrentInstance().objectsOnMap[i][j]) {
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
                        // NOTE: The object will not be selected if he changed the ownership
                        // exactly between game state switch (the object is "mine" on the "current" state
                        // but already not "mine" on the "next" state)
                        continue;
                    }

                    // FIXME: this will be very very slow, I should not commit it!! Only temporarily!!
                    boolean objectExistsOnFutureMapState = false;
                    for (int ii = 0; ii < getDim().x(); ii++) {
                        for (int jj = 0; jj < getDim().y(); jj++) {
                            if (getCurrentInstance().objectsOnMap[ii][jj].contains(objectOnMap)) {
                                objectExistsOnFutureMapState = true;
                                break;
                            }
                        }
                        if (objectExistsOnFutureMapState) { // found it
                            break;
                        }
                    }
                    // FIXME

                    if (objectExistsOnFutureMapState) {
                        objectOnMap.select();
                        getNextInstance().selectedObjects.add(objectOnMap);
                    }
                }
            }
        }
    }

    // C-Thread may call this method if the object gets killed/destroyed.
    // D-thread may call this method on mouse event.
    public static void deselect(HashSet<GameObject> objects) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        if (getNextInstance().selectedObjects == null) {
            return;
        }

        for (GameObject selectedObj : objects) {
            selectedObj.deselect();
            // NOTE: Should not be a problem if it does not exist already (if C-Thread did it for "next" faster)
            getNextInstance().selectedObjects.remove(selectedObj);
        }
    }

    // C-Thread may call this method if the object gets detected and targeted.
    // D-thread may call this method on mouse event.
    public static void assign(Point3D_Integer point) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        if (getNextInstance().selectedObjects == null || getNextInstance().selectedObjects.size() == 0) {
            LOG.debug("--- return");
            return;
        }

        for (GameObject selectedObj : getNextInstance().selectedObjects) {
            // TODO: that is wrong if we allow moveable Buildings
            if (selectedObj instanceof Unit) {
                LOG.debug("--- assignUnit");
                assignUnit(selectedObj, point);
            }
        }
    }

    // QUESTION What this do? May be rename?
    // QUESTION Is this super.method() for GameObject.Unit.setTargets()? Why?
    private static void assignUnit(GameObject selectedObj, Point3D_Integer point){
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        // FIXME: We must take floor(), not just divide!
        Point2D_Integer block = point.to2D().divInt(BLOCK_SIZE);

        // TODO: currently we don't consider "visibility" of the point by the player/enemy.
        HashSet<GameObject> objectsOnBlock = getNextInstance().objectsOnMap[block.x()][block.y()];

        boolean block_visible = getNextInstance().isBlockVisibleForMe(block);
        boolean is_me         = (objectsOnBlock.size() == 1) && objectsOnBlock.contains(selectedObj);
        boolean nobody        = objectsOnBlock.size() == 0;

        // Anyway select enemy unit and attack
        if (block_visible && !is_me && !nobody) {
            // FIXME Use Map, collections or generators. After refactor if
            // FIXME This algorithm is not optimal for big number of units on block
            // In case of several object on the block we choose the first randomly
            // found from them to attack
            for (GameObject objOnBlock : objectsOnBlock) {
                if (objOnBlock.contains(point.to2D()) && objOnBlock != selectedObj) {
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
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        GridRectangle gridRect = new GridRectangle(gameObj.getRect());

        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                GameMap.getNextInstance().validateBlockCoordinates(i, j);
                this.objectsOnMap[i][j].add(gameObj);
            }
        }
    }

    // TODO Code Duplicate. Collections or method fixBlockPositionOnMap()
    // QUESTION What is this?
    // QUESTION Rename to erase()?
    public void eraseObject(GameObject gameObj) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        GridRectangle gridRect = new GridRectangle(gameObj.getRect());

        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                GameMap.getNextInstance().validateBlockCoordinates(i, j);
                // TODO: check what if does not exist
                this.objectsOnMap[i][j].remove(gameObj);
            }
        }
    }

    // FIXME Remove Getter. See comment above dim, abs_dim declaration.
    public static Vector3D_Integer getDim() {
        return parallelepiped.dimInBlocks;
    }

    public static Vector3D_Integer getAbsDim() {
        return parallelepiped.dim;
    }

    public void validateBlockCoordinates(int grid_x, int grid_y) {
        if (! getRect().contains(grid_x, grid_y)) {
            Main.terminateNoGiveUp(null,
                    1000, "Block (" + grid_x + "," + grid_y +
                    " is outside of map " + getDim().x() + " x " + getDim().y()
            );
        }
    }

    // Checks if the area "givenRect" is occupied by some GameObject.
    public boolean occupied(Rectangle givenRect) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));
        return occupied(givenRect, null);
    }

    // Checks if the area "givenRect" where the given GameObject wants to move to/appear is occupied by some other GameObject.
    // Depending of INTERSECTION_STRATEGY_SEVERITY we decide how strictly we consider "occupied".
    // TODO: check not only intersection, but also containing (inclusion).
    public boolean occupied(Rectangle givenRect, GameObject exceptObject) {
        // Theoretically we can allow this method to with in V thread as well, because it is not supposed
        // to modify anything. But it should never happen, because we are going to call it only from M- and C-Thread
        // when we are locating new objects or moving existing objects on the map.
        // That is this method is supposed to work only with .getNextInstance() of GameMap.
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

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
                GameMap.getNextInstance().validateBlockCoordinates(i, j);

                HashSet<GameObject> objectsOnBlock = GameMap.getNextInstance().objectsOnMap[i][j];
                if (objectsOnBlock.size() == 0) {
                    continue;
                }

                for (GameObject objOnBlock : objectsOnBlock) {
                    // Is me?
                    if ((exceptObject != null) && (objOnBlock == exceptObject)) {
                        continue;
                    }

                    if (INTERSECTION_STRATEGY_SEVERITY > 1) {
                        LOG.trace("INTERSECTS: i=" + i + ", j=" + j + ", thisObject=" + this + ", objOnBlock=" + objOnBlock);

                        // Severity 2: Multiple objects on the same block are forbidden even if they actually don't intersect
                        return true;
                    }
                    // ELSE: Severity 1: Multiple objects on the same block are allowed when they don't intersect

                    Rectangle objOnBlockRect = objOnBlock.getRect();

                    // DEBUG
                    LOG.trace("Check 1: (" + givenRect.x + "," + givenRect.y + "," + givenRect.width + "," + givenRect.height);
                    LOG.trace("Check 2: (" + objOnBlockRect.getX() + "," + objOnBlockRect.getY() + "," + objOnBlockRect.getWidth() + "," + objOnBlockRect.getHeight());
                    LOG.trace("Check 3: " + objOnBlock.getAbsLoc() + "," + objOnBlock.getAbsSize());

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
    public boolean isBlockVisibleForMe(Point2D_Integer b) {
//        LOG.debug("get: " + i + "." + j + "." + Player.getMyPlayerId());
        return visibleMap[b.x()][b.y()].get(0);
    }

    public void registerBullet(Bullet b) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        // TODO: check validity of bullet coordinates, return and process bad result
        bullets.add(b);
    }

    // TODO Move it in Spawner class
    public void destroyBullet(Bullet b) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        // TODO; check if exists
        bullets.remove(b);
        b.unsetDestinationPoint();

        // delete - -TODO: move to another class!
//        b = null;
    }

    static Rectangle getRect() {
        return parallelepiped.getAbsBottomRect();
    }

    // Crops the given rectangle with the map rectangle (is used to avoid going outside the map)
    static Rectangle crop(Rectangle rect) {
        // TODO: taking into account Swing bug with drawRect() I would recommend also to check how
        // properly works this .intersect method.
        Rectangle croppedRect = new Rectangle(rect);
        Rectangle.intersect(croppedRect, GameMap.getRect(), croppedRect);
        return croppedRect;
    }

    boolean contains(ParallelepipedOfBlocks ppd) {
        return parallelepiped.contains(ppd);
    }

    boolean contains(Point3D_Integer point) {
        return parallelepiped.contains(point);
    }

    /* DEBUG */
    public void show() {
        for (int i = 0; i < getDim().x(); i++) {
            for (int j = 0; j < getDim().y(); j++) {
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
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));
        for(int i=0; i < getDim().x(); i++)
            for (int j=0; j < getDim().y(); j++)
            {
                landscapeBlocks[i][j].changeNature(); // pseudo-random
            }
    }
}
