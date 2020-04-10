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


public class GameMap extends ParallelepipedOfBlocks implements Renderable {

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

    public static synchronized GameMap getInstance() {
        return instance;
    }

    private static synchronized Vector3D_Integer getMapDimensions() {
        // TODO: read it from the game config. If not available - use default values.
        int width = Restrictions.MAX_X;
        int height = Restrictions.MAX_Y;
        int depth = Restrictions.MAX_Z; // MAX_Z because we don't support 3D so far

        // Validation of the map sizes.
        boolean width_ok = (width <= 0) || (width > Restrictions.MAX_X);
        boolean height_ok = (height <= 0) || (height > Restrictions.MAX_Y);
        boolean depth_ok = (depth <= 0) || (depth > Restrictions.MAX_Z);
        if (width_ok || height_ok || depth_ok) {
            Main.terminateNoGiveUp(null,
               1000,
               GameMap.class +
               " init error. width=" + width + ", height=" + height + ", depth=" + depth +
               " - beyond the restricted boundaries."
            );
        }

        return new Vector3D_Integer(width, height, depth);
    }

    private synchronized int [][] getMapLandscape() {
        int [][] terrain_map = new int[getDim().x()][getDim().y()];
        // Fill map with random numbers of textures (get it from the game config in the future)
        for (int i = 0; i < getDim().x(); i++) {
            for (int j = 0; j < getDim().y(); j++) {
                terrain_map[i][j] = ((i * i + j * j) / 7) % 8;
            }
        }
        return terrain_map;
    }

    private GameMap() {
        super( // Initialization of the map geometry (ParallelepipedOfBlocks)
                new Point3D_Integer(0, 0, 0),
                getMapDimensions() // static computation before super(): https://stackoverflow.com/a/17769207/4807875
        );
        init();
    }

    /*
       This constructor has no parameters.
       width, height and other parameters are taken either from a configuration file
       or from the default values list. The reason is that Java does not support well
       singletons with parameters. There is one workaround https://stackoverflow.com/a/39731434/4807875,
       but it is not worth on my opinion to use it when it is not quite necessary in our situation.
     */
    private synchronized void init() throws EnumConstantNotPresentException {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));

        // Check if this method was already called once (this is why we use "synchronized")
        if (initialized) {
            Main.terminateNoGiveUp(null,
                1000,
                getClass() + " init error. Not allowed to initialize the map twice!"
            );
        }

        // TODO What about collections and Maps?
        this.landscapeBlocks = new GameMapBlock[getDim().x()][getDim().y()];
        this.objectsOnMap = new HashSet[getDim().x()][getDim().y()];
        this.visibleMap = new HashMap[getDim().x()][getDim().y()];

        // TODO What is i and j?
        int[][] terrain_map = getMapLandscape();
        for (int i = 0; i < getDim().x(); i++) {
            for (int j = 0; j < getDim().y(); j++) {
                // Declaring landscape blocks
                try {
                    this.landscapeBlocks[i][j] = new GameMapBlock(i, j, terrain_map[i][j]);
                } catch (Exception e) {
                    LOG.error("Block (" + i + ", " + j + ")");
                    LOG.error("Map size: " + getDim().x() + "x" + getDim().y());
                    Main.terminateNoGiveUp(e,
                                1000,
                                getClass() + ": Map initialization failed with " + e.getClass().getSimpleName()
                    );
                }

                // Declaring map with default objects
                this.objectsOnMap[i][j] = new HashSet<GameObject>();

                // Declaring visibility map for blocks
                // TODO: currently everything is visible for everybody - the logic is to be designed
                this.visibleMap[i][j] = new HashMap<Integer, Boolean>();
                for (int k = 0; k <= Restrictions.MAX_PLAYERS - 1; k++) {
                    this.visibleMap[i][j].put(k, true);
                }
            }
        }
        // free(terrain_map); - this line will be active in C implementation

        this.selectedObjects = new HashSet<GameObject>();
        this.bullets = new HashSet<Bullet>();

        initialized = true;

        LOG.info("Initialized map " + getDim().x() + "x" + getDim().y());
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

    public void select(Rectangle mouseRect) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("D")));

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
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("D")));

        if (selectedObjects == null) {
            return;
        }

        for (GameObject selectedObj : objects) {
            selectedObj.deselect();
            this.selectedObjects.remove(selectedObj);
        }
    }

    public void assign(Point3D_Integer point) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

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
    private void assignUnit(GameObject selectedObj, Point3D_Integer point){
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        // FIXME: We must take floor(), not just divide!
        Point2D_Integer block = point.to2D().divInt(BLOCK_SIZE);

        // TODO: currently we don't consider "visibility" of the point by the player/enemy.
        HashSet<GameObject> objectsOnBlock = objectsOnMap[block.x()][block.y()];

        boolean block_visible = isBlockVisibleForMe(block);
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
                GameMap.getInstance().validateBlockCoordinates(i, j);
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
                GameMap.getInstance().validateBlockCoordinates(i, j);
                // TODO: check what if does not exist
                this.objectsOnMap[i][j].remove(gameObj);
            }
        }
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

    Rectangle getRect() {
        return getAbsBottomRect();
    }

    // Crops the given rectangle with the map rectangle (is used to avoid going outside the map)
    Rectangle crop(Rectangle rect) {
        // TODO: taking into account Swing bug with drawRect() I would recommend also to check how
        // properly works this .intersect method.
        Rectangle croppedRect = new Rectangle(rect);
        Rectangle.intersect(croppedRect, GameMap.getInstance().getRect(), croppedRect);
        return croppedRect;
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
