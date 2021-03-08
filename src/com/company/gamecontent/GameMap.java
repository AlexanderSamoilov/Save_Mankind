/* ***************** *
 * S I N G L E T O N *
 * ***************** */
package com.company.gamecontent;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamegeom._2d.GridRectangle;
import com.company.gamegeom._3d.ParallelepipedOfBlocks;
import com.company.gamecontrollers.MouseController;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamethread.ParameterizedMutexManager;
import com.company.gametools.ConcurrentHashSet;
//import com.company.gametools.Tools;

import static com.company.gamethread.M_Thread.terminateNoGiveUp;
import static com.company.gamecontent.Constants.BLOCK_SIZE;
import static com.company.gamecontent.Constants.INTERSECTION_STRATEGY_SEVERITY;

public class GameMap extends ParallelepipedOfBlocks implements Renderable {

    private static Logger LOG = LogManager.getLogger(GameMap.class.getName());

    // Singleton
    /*
     Lazy thread-safe initialization (possible to catch exception in Main).
     See https://www.geeksforgeeks.org/java-singleton-design-pattern-practices-examples.
     */

    private static GameMap instance = null;
    public static synchronized GameMap getInstance() {
        return instance;
    }

    private GameMap(Point3D_Integer p, Vector3D_Integer v) {
        super(p, v);
        LOG.debug(getClass() + " singleton created.");
        //Tools.printStackTrace(null); // DEBUG
    }

    // TODO what about Units, Buildings? Why Bullets separate of them?
    private HashSet<GameObject>           selectedObjects = null;
    GameMapBlock[][]                      landscapeBlocks = null;
    public ConcurrentHashSet<Bullet>             bullets         = null;

    private static synchronized void initDefaultLandscapeBlockTemplates() {
        LandscapeBlockTemplate.add("SAND", /*true, true, false,*/ "sand_dark_stackable.png");
        LandscapeBlockTemplate.add("DIRT", /*true, true, false,*/ "dirt.png");
        LandscapeBlockTemplate.add("PLATE", /*true, true, false,*/ "plate.png");
        LandscapeBlockTemplate.add("FOREST", /*true, true, false,*/ "forest.png");
        LandscapeBlockTemplate.add("BUSH", /*true, true, false,*/ "bush.png");
        LandscapeBlockTemplate.add("WATER", /*true, true, false,*/ "water_dirt.png");
        LandscapeBlockTemplate.add("HOLE", /*true, true, false,*/ "hole_dirt.png");
        LandscapeBlockTemplate.add("MARSH", /*true, true, false,*/ "marsh_dirt_stackable.png");
        LandscapeBlockTemplate.add("HILL", /*true, true, false,*/ "hill_dirt.png");
    }

    private synchronized void initMapBlocks() {
        //String[][] terrain_map = GameMapGenerator.readMapFromConfig();
        String [][] terrain_map = GameMapGenerator.generateRandomMap(dimInBlocks.x(), dimInBlocks.y());
        for (int x = 0; x < dimInBlocks.x(); x++) {
            for (int y = 0; y < dimInBlocks.y(); y++) {
                try {
                    this.landscapeBlocks[x][y] = new GameMapBlock(x, y, terrain_map[x][y]);
                } catch (Exception e) {
                    LOG.error("Block (" + x + ", " + y + ")");
                    LOG.error("Map size: " + dimInBlocks.x() + "x" + dimInBlocks.y());
                    terminateNoGiveUp(e,
                        1000,
                        getClass() + ": Map initialization failed with " + e.getClass().getSimpleName()
                    );
                }
            }
        }
        // C Lang: free(terrain_map);
    }

    /*
       The init() method has no parameters.
       width, height and other parameters are taken either from a configuration file
       or from the default values list. The reason is that Java does not support well
       singletons with parameters. There is one workaround https://stackoverflow.com/a/39731434/4807875,
       but it is not worth on my opinion to use it when it is not quite necessary in our situation.
       The init() method is implemented synchronized in order to prevent being called in parallel.
     */
    public static synchronized void init() throws EnumConstantNotPresentException {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("M"))); // Arrays.asList("M")

        // Prevent duplicated call of the init() method.
        // Let imagine we called init() first time and it failed for some reason,
        // the instance was created, but the class fields were not initialized completely.
        // So let us be strict and give only one chance to init the map.
        if (instance != null) {
            terminateNoGiveUp(null,
                1000,
                instance.getClass() + " init error. Not allowed to initialize the map twice!"
            );
        }

        // Initialization of the map geometry (ParallelepipedOfBlocks) happens in GameMapGenerator.readMapDimensionsFromConfig()
        // It will work inside a default constructor as well as far as readMapDimensionsFromConfig() is static
        // See https://stackoverflow.com/a/17769207/4807875
        instance = new GameMap(
                new Point3D_Integer(0, 0, 0),
                GameMapGenerator.readMapDimensionsFromConfig()
        );

        instance.landscapeBlocks = new GameMapBlock[instance.dimInBlocks.x()][instance.dimInBlocks.y()];
        initDefaultLandscapeBlockTemplates();
        instance.initMapBlocks();
        instance.selectedObjects = new HashSet<>(); // HashSet<GameObject>
        instance.bullets = new ConcurrentHashSet<>(); // ConcurrentHashSet<Bullet>

        LOG.info("Initialized map " + instance.dimInBlocks.x() + "x" + instance.dimInBlocks.y());
    }

    public void select(Rectangle mouseRect) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("D"))); // Arrays.asList("D")

        /* crop the selection rectangle to take into account
           the case when the mouse cursor is outside the map
         */
        Rectangle mouseRectCropped = crop(mouseRect);

        if (selectedObjects == null) {
            selectedObjects = new HashSet<>(); // HashSet<GameObject>
        }

        // First deselect all selected objects
        this.deselectAll();

        // Check objects in Rect-Selector and add them to selectedObjects
        GridRectangle gridRect = new GridRectangle(mouseRectCropped);
        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                if (landscapeBlocks[i][j].objectsOnBlock.size() == 0) {
                    continue;
                }

                boolean in_the_middle = gridRect.isMiddleBlock(i, j);

                for (GameObject objectOnMap : landscapeBlocks[i][j].objectsOnBlock) {
                    Rectangle objectOnMapRect = objectOnMap.getAbsBottomRect();

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
                    if (objectOnMap.owner.id > 0) {
                        continue;
                    }

                    objectOnMap.select();
                    this.selectedObjects.add(objectOnMap);
                }
            }
        }
    }

    private void deselectAll() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("D"))); // Arrays.asList("D")

        if (selectedObjects == null) {
            return;
        }

        for (GameObject selectedObj : selectedObjects) {
            selectedObj.deselect();
        }
        this.selectedObjects.clear();
    }

    public void assign(Point3D_Integer point) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        if (selectedObjects == null || selectedObjects.size() == 0) {
            return;
        }

        for (GameObject selectedObj : selectedObjects) {
            // TODO: think about, whether we allow movable Buildings
            if (selectedObj instanceof Unit) {
                assignUnit(selectedObj, point);
            }
        }
    }

    // Q: What does it do? May be we rename it?
    // Q: Is this super.method() for GameObject.Unit.setTargets()? Why?
    private void assignUnit(GameObject selectedObj, Point3D_Integer point){
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        Point2D_Integer block = point.to2D().divInt(BLOCK_SIZE);

        // TODO: currently we don't consider "visibility" of the point by the player/enemy.
        HashSet<GameObject> objectsOnBlock = landscapeBlocks[block.x()][block.y()].objectsOnBlock;

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
        if (MouseController.getInstance().attackFocus) {
            ((Unit) selectedObj).setTargetPoint(point);

            // FIXME Move it in MouseController until reasons clarified
            MouseController.getInstance().attackFocus = false;

            return;
        }

        // Move to point
        selectedObj.setDestinationPoint(point);
    }

    // TODO Code Duplicate. Collections or method fixBlockPositionOnMap()
    void registerObject(GameObject gameObj) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        GridRectangle gridRect = new GridRectangle(gameObj.getAbsBottomRect());

        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                validateBlockCoordinates(i, j);
                landscapeBlocks[i][j].objectsOnBlock.add(gameObj);
            }
        }
    }

    // TODO Code Duplicate. Collections or method fixBlockPositionOnMap()
    void eraseObject(GameObject gameObj) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        GridRectangle gridRect = new GridRectangle(gameObj.getAbsBottomRect());

        for (int i = gridRect.left; i <= gridRect.right; i++) {
            for (int j = gridRect.top; j <= gridRect.bottom; j++) {
                validateBlockCoordinates(i, j);
                // TODO: check what if does not exist
                landscapeBlocks[i][j].objectsOnBlock.remove(gameObj);
            }
        }
    }

    private void validateBlockCoordinates(int grid_x, int grid_y) {
        if (! getAbsBottomRect().contains(grid_x, grid_y)) {
            terminateNoGiveUp(null,
                    1000, "Block (" + grid_x + "," + grid_y +
                    " is outside of map " + dimInBlocks.x() + " x " + dimInBlocks.y()
            );
        }
    }

    // Checks if the area "givenRect" is occupied by some GameObject.
    /*public boolean occupied(Rectangle givenRect) {
        return occupied(givenRect, null);
    }*/

    // Checks if the area "givenRect" where the given GameObject wants to move to/appear is occupied by some other GameObject.
    // Depending of INTERSECTION_STRATEGY_SEVERITY we decide how strictly we consider "occupied".
    // TODO: check not only intersection, but also containing (inclusion).
    boolean occupied(Rectangle givenRect, GameObject exceptObject) {
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
                validateBlockCoordinates(i, j);

                HashSet<GameObject> objectsOnBlock = landscapeBlocks[i][j].objectsOnBlock;
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

                    Rectangle objOnBlockRect = objOnBlock.getAbsBottomRect();

                    // DEBUG
                    LOG.trace("Check 1: (" + givenRect.x + "," + givenRect.y + "," + givenRect.width + "," + givenRect.height);
                    LOG.trace("Check 2: (" + objOnBlockRect.x + "," + objOnBlockRect.y + "," + objOnBlockRect.width + "," + objOnBlockRect.height);
                    LOG.trace("Check 3: " + objOnBlock.loc + "," + objOnBlock.dim);

                    if (givenRect.intersects(objOnBlockRect)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // TODO: check that i,j are within allowed boundaries
    private boolean isBlockVisibleForMe(Point2D_Integer b) {
//        LOG.debug("get: " + i + "." + j + "." + Player.playerId());
        return landscapeBlocks[b.x()][b.y()].visible.get(0);
    }

    void registerBullet(Bullet b) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        // TODO: check validity of bullet coordinates, return and process bad result
        bullets.add(b);
    }

    void eraseBullet(Bullet b) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        if (!bullets.contains(b)) {
            /* DEBUG */
            terminateNoGiveUp(null,
                1000,
                "Player #" + b.shooter.owner.id + ": " +
                " the object #" + b + " does not exist."
            );
        }
        bullets.remove(b);
    }

    // Crops the given rectangle with the map rectangle (is used to avoid going outside the map)
    Rectangle crop(Rectangle rect) {
        // TODO: taking into account Swing bug with drawRect() I would recommend also to check how
        // properly works this .intersect method.
        Rectangle croppedRect = new Rectangle(rect);
        Rectangle.intersect(croppedRect, getAbsBottomRect(), croppedRect);
        return croppedRect;
    }

    /* DEBUG */
    /*
    public void show() {
        for (int i = 0; i < dimInBlocks.x(); i++) {
            for (int j = 0; j < dimInBlocks.y(); j++) {
                int plId = -1;
                GameObject target = null;
                if (landscapeBlocks[i][j].objectsOnBlock.size() != 0) {
                    for (GameObject currObj : landscapeBlocks[i][j].objectsOnBlock) {
                        plId = currObj.playerId;
                        target = ((Unit) (currObj)).getTargetObject();
                        LOG.debug("(" + i + "," + j + ")[" + plId + "]:" + currObj + " -> " + target);
                    }
                }
            }
        }
    }*/

    // Randomising landscapeBlocks
    /*
    public void rerandom() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));
        for(int i=0; i < dimInBlocks.x(); i++)
            for (int j=0; j < dimInBlocks.y(); j++)
            {
                landscapeBlocks[i][j].changeNature(); // pseudo-random
            }
    }*/
}
