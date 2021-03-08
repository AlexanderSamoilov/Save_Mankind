/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gamecontent;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamecontrollers.MainWindow;
import com.company.gamegeom._2d.GridMatrixHorizontal;
import com.company.gamegeom._2d.GridMatrixVertical;
import com.company.gamegeom._2d.ParallelogramHorizontal;
import com.company.gamegeom._2d.ParallelogramVertical;
import com.company.gamegeom._3d.ParallelepipedOfBlocks;
import com.company.gamegraphics.GraphBugfixes;
import com.company.gamegraphics.GraphExtensions;
import com.company.gamemath.cortegemath.cortege.Cortege2D_Integer;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector2D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;

import static com.company.gametools.MathTools.calcNextPointOnRay;
import static com.company.gametools.MathTools.sectionContains;
import static com.company.gamethread.M_Thread.terminateNoGiveUp;
import static com.company.gamecontent.Constants.INTERSECTION_STRATEGY_SEVERITY;

public enum UnitMovementAlgorithms {
    ; // utility class

    private static Logger LOG = LogManager.getLogger(UnitMovementAlgorithms.class.getName());

    // ALGORITHM FOR ANALYSING OF IMPEDIMENTS ON THE WAY OF THE OBJECT WITH RECTANGLE SHAPE
    public static Vector3D_Integer calcWayTillFirstImpediment(GameObject go, Vector3D_Integer dv) {

        Vector3D_Integer dv_opt = null;

        // Step 1. Detect all map(grid) blocks which are intersected or contained by two parallelograms
        // which are represented by the movement (translation) of two "forward"-edges of the object rectangle.
        // In case the movement is completely horizontal or vertical there will be only one parallelogram.
        // pgmHoriz - parallelogram represented by the movement of the top/bottom edge
        // pgmVert - parallelogram represented by the movement of the right/left edge

        Graphics currentGraphics = MainWindow.getInstance().frame.getRootPane().getGraphics(); // DEBUG
        ParallelogramHorizontal pgmHoriz = null;
        ParallelogramVertical pgmVert = null;
        GridMatrixHorizontal pgmHorizOccupiedBlocks = null;
        GridMatrixVertical pgmVertOccupiedBlocks = null;

        Rectangle new_rect = go.getAbsBottomRect();
        new_rect.translate(dv.x(), dv.y()); // translocated rectangle

        if (dv.y() != 0) { // a)
            if (dv.y() < 0) {
                pgmHoriz = new ParallelogramHorizontal(
                    new Point2D_Integer(new_rect.x, new_rect.y),
                    new_rect.width,
                    go.loc.y() - new_rect.y + 1,
                    -dv.x()
                );
            }
            /*if (dv.y() > 0)*/ else {
                pgmHoriz = new ParallelogramHorizontal(
                    new Point2D_Integer(go.loc.x(), go.loc.y() + go.dim.y() - 1),
                    new_rect.width,
                    new_rect.y - go.loc.y() + 1,
                    dv.x()
                );
            }
            pgmHorizOccupiedBlocks = new GridMatrixHorizontal(pgmHoriz);
            pgmHoriz.render(currentGraphics); // DEBUG (draw parallelogram)
            pgmHorizOccupiedBlocks.render(currentGraphics); // DEBUG (draw occupied blocks)
        }
        if (dv.x() != 0) { // b)
            if (dv.x() < 0) {
                pgmVert = new ParallelogramVertical(
                    new Point2D_Integer(new_rect.x, new_rect.y),
                    go.loc.x() - new_rect.x + 1,
                    new_rect.height,
                    -dv.y()
                );
            }
            /* if (dv.x() > 0) */ else {
                pgmVert = new ParallelogramVertical(
                    new Point2D_Integer(go.loc.x() + go.dim.x() - 1, go.loc.y()),
                    new_rect.x - go.loc.x() + 1,
                    new_rect.height,
                    dv.y()
                );
            }
            pgmVertOccupiedBlocks = new GridMatrixVertical(pgmVert);
            pgmVert.render(currentGraphics); // DEBUG (draw parallelogram)
            pgmVertOccupiedBlocks.render(currentGraphics); // DEBUG (draw occupied blocks)
        }

        // Step 2. Iterate through all blocks obtained on the step 1 checking the map regarding which game objects
        // are located on each block which is being iterated. Store all such blocks to a list/hash with no repetition.
        // Exclude the current game object (unit) itself.
        HashSet<Rectangle> affectedObjects = new HashSet<>(); // HashSet<Rectangle>

        // Checking blocks occupied by the horizontal parallelogram:
        if (dv.y() != 0) { // a)
            for (int i = 0; i <= pgmHorizOccupiedBlocks.bottom - pgmHorizOccupiedBlocks.top; i++) {
                for (int j = pgmHorizOccupiedBlocks.left[i]; j <= pgmHorizOccupiedBlocks.right[i]; j++) {
                    HashSet<GameObject> objectsOnBlock = GameMap.getInstance().landscapeBlocks[j][i + pgmHorizOccupiedBlocks.top].objectsOnBlock;
                    for (GameObject gameObject : objectsOnBlock) {
                        if (gameObject != go) affectedObjects.add(gameObject.getAbsBottomRect());
                    }

                    // In case of INTERSECTION_STRATEGY_SEVERITY == 2 it is not allowed
                    // to go through blocks partly occupied by another objects
                    if (
                            (INTERSECTION_STRATEGY_SEVERITY > 1) // only one object is allowed on the block
                                    && (!objectsOnBlock.isEmpty())          // somebody is on the block
                                    && (!objectsOnBlock.contains(go))     // but not me
                    ) {
                        affectedObjects.add(GameMap.getInstance().landscapeBlocks[j][i + pgmHorizOccupiedBlocks.top].getAbsBottomRect());
                    }
                }
            }
        }
        // Checking blocks occupied by the vertical parallelogram:
        if (dv.x() != 0) { // b)
            for (int i = 0; i <= pgmVertOccupiedBlocks.right - pgmVertOccupiedBlocks.left; i++) {
                for (int j = pgmVertOccupiedBlocks.top[i]; j <= pgmVertOccupiedBlocks.bottom[i]; j++) {
                    HashSet<GameObject> objectsOnBlock = GameMap.getInstance().landscapeBlocks[i + pgmVertOccupiedBlocks.left][j].objectsOnBlock;
                    for (GameObject gameObject : objectsOnBlock) {
                        if (gameObject != go) affectedObjects.add(gameObject.getAbsBottomRect());
                    }

                    // In case of INTERSECTION_STRATEGY_SEVERITY == 2 it is not allowed
                    // to go through blocks partly occupied by another objects
                    if (
                            (INTERSECTION_STRATEGY_SEVERITY > 1) // only one object is allowed on the block
                                    && (!objectsOnBlock.isEmpty())          // somebody is on the block
                                    && (!objectsOnBlock.contains(go))     // but not me
                    ) {
                        affectedObjects.add(GameMap.getInstance().landscapeBlocks[i + pgmVertOccupiedBlocks.left][j].getAbsBottomRect());
                    }
                }
            }
        }

        // There are no impediments on the way
        if (affectedObjects.size() == 0) {
            return null;
        }

        /* Step 3. Iterate through all game objects (their rectangles) from the step 2 in order to get:
           a) All of them whose bottom (if dy > 0)/top(if dy < 0) edge has a common section with that parallelogram,
           whose edges are parallel Ox, that is pgmHoriz.
           b) All of them whose left(if dx > 0)/right(if dx < 0) edge has a common section with that parallelogram,
           whose edges are parallel Oy, that is pgmVert.
           c) All of them whose left-bottom(if dx > 0, dy > 0)/right-bottom(if dx < 0, dy > 0)/
           right-top(if dx < 0, dy < 0)/left-top(if dx > 0, dy < 0) vertex belongs to the terminating section between parallelograms.
         */
        HashSet<Rectangle> suspectedObjectsA = new HashSet<>(); // HashSet<Rectangle>
        HashSet<Rectangle> suspectedObjectsB = new HashSet<>(); // HashSet<Rectangle>
        HashSet<Rectangle> suspectedObjectsC = new HashSet<>(); // HashSet<Rectangle>

        /* frontPointStartPos corresponds to the direction where the current object is going to move
           Depending on the direction we choose the corresponding edge or vertex.
           For example:
           a) The object is moving to the right and down => frontPointStartPos is a right-bottom vertex.
           frontPointStartPos.x = x_right_bottom, frontPointStartPos.y = y_right_bottom
           b) If the object is moving just left (dy=0) => frontPointStartPos is an abscissa of the left edge.
           frontPointStartPos.x = x_left, frontPointStartPos.y = null (impossible to choose a point, the whole edge is moving if dy=0)
           c) If the object is moving just down (dx=0) => frontPointStartPos is an ordinate of the bottom edge.
           frontPointStartPos.y = y_bottom, frontPointStartPos.x = null (impossible to choose a point, the whole edge is moving if dx=0)
           d) dx=dx=0 => frontPointStartPos.x=frontPointStartPos.y=null (should be impossible, because we check above that dx!=0 or dy!=0)
         */

        Integer frontPointStartPos_x = null, frontPointStartPos_y = null;
        if (dv.x() < 0) frontPointStartPos_x = go.loc.x(); // left of the current object
        if (dv.x() > 0) frontPointStartPos_x = go.getAbsRight(); // right of the current object
        if (dv.y() < 0) frontPointStartPos_y = go.loc.y(); // top of the current object
        if (dv.y() > 0) frontPointStartPos_y = go.getAbsBottom(); // bottom of the current object
        Point2D_Integer frontPointStartPos = new Point2D_Integer(frontPointStartPos_x, frontPointStartPos_y);

        // frontPointEndPos is where frontPointStartPos should be moved to hypothetically
        Integer frontPointEndPos_x = null, frontPointEndPos_y = null;
        if (frontPointStartPos.x() != null) frontPointEndPos_x = frontPointStartPos.x() + dv.x();
        if (frontPointStartPos.y() != null) frontPointEndPos_y = frontPointStartPos.y() + dv.y();
        Point2D_Integer frontPointEndPos = new Point2D_Integer(frontPointEndPos_x, frontPointEndPos_y);

        for (Rectangle rect : affectedObjects) {

            GraphExtensions.fillRect(currentGraphics, rect, 1); // DEBUG
            Point2D_Integer go_top_left = new Point2D_Integer(rect.x, rect.y);
            Point2D_Integer go_top_right = new Point2D_Integer(rect.x + rect.width - 1, rect.y);
            Point2D_Integer go_bottom_left = new Point2D_Integer(rect.x, rect.y + rect.height - 1);
            Point2D_Integer go_bottom_right = new Point2D_Integer(rect.x + rect.width - 1, rect.y + rect.height - 1);

            // a
            if (dv.y() != 0) {
                // NOTE: Here "dy < 0" means movement UP since Y_ORIENT=-1 (we are not in Cartesian system in the game).
                if (dv.y() < 0) { // UP
                    // check intersection/touching of the object rectangle by the bottom edge of the impediment
                    if (pgmHoriz.intersects(go_bottom_left, go_bottom_right) > -1) {
                        suspectedObjectsA.add(rect);
                    }
                }
                if (dv.y() > 0) { // DOWN
                    // check intersection/touching of the object rectangle by the top edge of the impediment
                    if (pgmHoriz.intersects(go_top_left, go_top_right) > -1) {
                        suspectedObjectsA.add(rect);
                    }
                }
            }

            // b
            if (dv.x() != 0) {
                if (dv.x() > 0) { // RIGHT
                    // check intersection/touching of the object rectangle by the left edge of the impediment
                    if (pgmVert.intersects(go_top_left, go_bottom_left) > -1) {
                        suspectedObjectsB.add(rect);
                    }
                }
                if (dv.x() < 0) { // LEFT
                    // check intersection/touching of the object rectangle by the right edge of the impediment
                    if (pgmVert.intersects(go_top_right, go_bottom_right) > -1) {
                        suspectedObjectsB.add(rect);
                    }
                }
            }

            // c
            if (!dv.isZeroCortege()) {
                if ((dv.x() > 0) && (dv.y() < 0)) { // UP-RIGHT
                    // check if the bottom-left vertex of the impediment belongs to the common section [frontPointStartPos; frontPointEndPos]
                    // connecting pgmHoriz and pgmVert
                    if (sectionContains(frontPointStartPos, go_bottom_left, frontPointEndPos) > -1) {
                        suspectedObjectsC.add(rect);
                    }
                }
                if ((dv.x() < 0) && (dv.y() < 0)) { // UP-LEFT
                    // check if the bottom-left vertex of the impediment belongs to the common section [frontPointStartPos; frontPointEndPos]
                    // connecting pgmHoriz and pgmVert
                    if (sectionContains(frontPointStartPos, go_bottom_right, frontPointEndPos) > -1) {
                        suspectedObjectsC.add(rect);
                    }
                }
                if ((dv.x() > 0) && (dv.y() > 0)) { // DOWN-RIGHT
                    // check if the top-left vertex of the impediment belongs to the common section [frontPointStartPos; frontPointEndPos]
                    // connecting pgmHoriz and pgmVert
                    if (sectionContains(frontPointStartPos, go_top_left, frontPointEndPos) > -1) {
                        suspectedObjectsC.add(rect);
                    }
                }
                if ((dv.x() < 0) && (dv.y() > 0)) { // DOWN-LEFT
                    // check if the top-right vertex of the impediment belongs to the common section [frontPointStartPos; frontPointEndPos]
                    // connecting pgmHoriz and pgmVert
                    if (sectionContains(frontPointStartPos, go_top_right, frontPointEndPos) > -1) {
                        suspectedObjectsC.add(rect);
                    }
                }
            }

        }

        /* Step 4.
           a) From the game objects obtained on the step 3a choose only one whose bottom(if dy > 0)/top(if dy < 0) edge
           is lowest(if dy > 0)/highest(if dy < 0) and remember its ordinate Ya.
           b) From the game objects obtained on the step 3b choose only one whose left(if dx > 0)/right(if dx < 0) edge
           is leftest(if dx > 0)/rightest(if dx < 0) and remember its abscissa Xb.
           c) From the game objects obtained on the step 3c choose only one whose left-bottom(if dx 0, dy > 0)/
           right-bottom(if dx < 0, dy > 0)/right-top(if dx < 0, dy < 0)/left-top(if dx > 0, dy > 0) vertex
           is closest to frontPointStartPos and remember its coordinates Xc, Yc.
         */

        int opt_line_A_found = 0;
        int opt_line_B_found = 0;
        int opt_point_C_found = 0;
        // a
        Integer Ya = null;
        if (suspectedObjectsA.size() > 0) opt_line_A_found = 1;
        for (Rectangle rect : suspectedObjectsA) {

            GraphExtensions.fillRect(currentGraphics, rect, 2); // DEBUG

            if (dv.y() < 0) { // UP
                int Y_bottom = GraphBugfixes.getMaxY(rect);
                if (Ya == null) {
                    Ya = Y_bottom; // first time only
                    continue;
                }
                // the lowest bottom
                if (Y_bottom > Ya) Ya = Y_bottom;
            }
            if (dv.y() > 0) { // DOWN
                int Y_top = rect.y;
                if (Ya == null) {
                    Ya = Y_top; // first time only
                    continue;
                }
                // the highest top
                if (Y_top < Ya) Ya = Y_top;
            }
        }

        // b
        Integer Xb = null;
        if (suspectedObjectsB.size() > 0) opt_line_B_found = 1;
        for (Rectangle rect : suspectedObjectsB) {

            GraphExtensions.fillRect(currentGraphics, rect, 2); // DEBUG

            if (dv.x() > 0) { // RIGHT
                int X_left = rect.x;
                if (Xb == null) {
                    Xb = X_left; // first time only
                    continue;
                }
                // the leftest left
                if (X_left < Xb) Xb = X_left;
            }
            if (dv.x() < 0) { // LEFT
                int X_right = GraphBugfixes.getMaxX(rect);
                if (Xb == null) {
                    Xb = X_right; // first time only
                    continue;
                }
                // the rightest right
                if (X_right > Xb) Xb = X_right;
            }
        }

        // c
        Point2D_Integer frontPointMiddlePos = null;
        Point2D_Integer frontPointMiddlePosOpt = null;
        Long distSqrValMin = null;
        Long distSqrValCurr; // = -1L;
        if (suspectedObjectsC.size() > 0) opt_point_C_found = 1;

        // TODO: "new" on each loop iteration - wasting of memory
        for (Rectangle rect : suspectedObjectsC) {

            GraphExtensions.fillRect(currentGraphics, rect, 2); // DEBUG
            Point2D_Integer go_top_left = new Point2D_Integer(rect.x, rect.y);
            Point2D_Integer go_top_right = new Point2D_Integer(rect.x + rect.width - 1, rect.y);
            Point2D_Integer go_bottom_left = new Point2D_Integer(rect.x, rect.y + rect.height - 1);
            Point2D_Integer go_bottom_right = new Point2D_Integer(rect.x + rect.width - 1, rect.y + rect.height - 1);

            // TODO: it is known outside of the loop about signs of dx, dy
            // It is possible to avoid these 4 if-else here?
            // UP-RIGHT
            if ((dv.x() > 0) && (dv.y() < 0)) frontPointMiddlePosOpt = go_bottom_left;
            // UP-LEFT
            if ((dv.x() < 0) && (dv.y() < 0)) frontPointMiddlePosOpt = go_bottom_right;
            // DOWN-RIGHT
            if ((dv.x() > 0) && (dv.y() > 0)) frontPointMiddlePosOpt = go_top_left;
            // DOWN-LEFT
            if ((dv.x() < 0) && (dv.y() > 0)) frontPointMiddlePosOpt = go_top_right;

            // validation
            if ((dv.x() == 0) || (dv.y() == 0)) {
                terminateNoGiveUp(null,1000, "Impossible: dx or dy = 0 on step 4c.");
            }

            if (frontPointMiddlePos == null) { // first time only
                frontPointMiddlePos = frontPointMiddlePosOpt.clone();
                distSqrValMin = Cortege2D_Integer.distSqrVal(frontPointStartPos, frontPointMiddlePos).longValue();
                continue;
            }

            distSqrValCurr = Cortege2D_Integer.distSqrVal(frontPointStartPos, frontPointMiddlePos).longValue();
            if (distSqrValCurr < distSqrValMin) {
                distSqrValMin = distSqrValCurr;
                frontPointMiddlePos.assign(frontPointMiddlePosOpt);
            }
        }

        /*  Step 5.
            Having 3 "restricting" points/lines from 4a, 4b, 4c calculate 3 position candidates
            where the object center should be moved to at the end. Choose that one out of 3
            which is closest to the center (we choose the minimal, because the FIRST impediment
            which the object meets on the way stops it).
         */

        int num_opt_points = 0;
        LOG.debug("a_num=" + suspectedObjectsA.size() + ", b_num=" + suspectedObjectsB.size() + ", c_num=" + suspectedObjectsC.size());
        LOG.debug("a=" + opt_line_A_found + ", b=" + opt_line_B_found + ", c=" + opt_point_C_found);

        HashMap<Integer, Vector2D_Integer> opt_points = new HashMap<>(); // HashMap<Integer, Vector2D_Integer>

        // a (dy != 0)
        if (opt_line_A_found != 0) {
            LOG.debug("Ya=" + Ya);
            Ya -= (int)Math.signum(dv.y()); // we should not overlap even 1 pixel of the border of the impediment
            opt_points.put(num_opt_points, new Vector2D_Integer(
                    (Ya - frontPointStartPos.y()) * dv.x() / dv.y(),
                    Ya - frontPointStartPos.y()
            ));
            num_opt_points++;
        }
        // b (dx != 0)
        if (opt_line_B_found != 0) {
            LOG.debug("Xb=" + Xb);
            Xb -= (int)Math.signum(dv.x()); // we should not overlap even 1 pixel of the border of the impediment
            opt_points.put(num_opt_points, new Vector2D_Integer(
                    Xb - frontPointStartPos.x(),
                    (Xb - frontPointStartPos.x()) * dv.y() / dv.x()
            ));
            num_opt_points++;
        }
        // c ( dx != 0 and dy != 0)
        if (opt_point_C_found != 0) {
            LOG.debug("frontPointMiddlePos=" + frontPointMiddlePosOpt);

            frontPointMiddlePos.minus(new Point2D_Integer(Math.signum(dv.x()), Math.signum(dv.y()))); // we should not overlap even 1 pixel of the border of the impediment
            opt_points.put(num_opt_points, frontPointMiddlePos.minusClone(frontPointStartPos));
            num_opt_points++;
        }

        int i_opt = 0;
        if (num_opt_points > 0) {
            // distSqrValCurr = -1L;
            // TODO: Strange...if I don't use type cast then it complains that class Cortege is not public
            // It seems that it tries to call sumSqr on class Cortege, because it does not see that
            // opt_points must contain only elements of type Point2D_Integer.
            // I have a slight assumption that without explicit cast of the map element to Point2D_Integer Java
            // considers HashMap<Integer, Cortege> as HashMap<Integer, ? extends Cortege> and that might be the problem.
            // Probably Java is not "sure" that get() returns indeed the subclass element,
            // but one of its upper classes, because child is always castable to any upper.
            // https://stackoverflow.com/questions/56308571/cannot-call-static-method-of-the-upper-class-located-in-another-package-without
            //distSqrValMin = Point2D_Integer.sumSqr((Vector2D_Integer)opt_points.get(0)).longValue();
            distSqrValMin = opt_points.get(0).sumSqr().longValue();
            for (int i = 0; i < num_opt_points; i++) {
                //distSqrValCurr = Point2D_Integer.sumSqr((Vector2D_Integer)opt_points.get(i)).longValue();
                distSqrValCurr = opt_points.get(i).sumSqr().longValue();
                if (distSqrValCurr < distSqrValMin) {
                    distSqrValMin = distSqrValCurr;
                    i_opt = i;
                }
            }

            dv_opt = opt_points.get(i_opt).to3D();
            LOG.debug("dv_opt=" + dv_opt);

            if (dv_opt.isZeroCortege()) {
                LOG.trace("Cannot move even to 1 pixel, everything occupied!");
            }
            if (dv_opt.eq(dv)) {
                LOG.warn("The performance-consuming calculation started in vain: dx=dx_opt, dy_dy_opt.");
            }

        }

        return dv_opt;
    }

    /*
     EXPLANATION:
     Why do we need impediments check algorithm instead of just checking GameMap.getInstance().occupied()?
     Even if GameMap.getInstance().occupied() for the destination position is TRUE, we move till the impediment.
     This is why it is not correct just to stop movement if occupied() is true.
     Instead we move the unit, but till the impediment "back to back".
     */
    public static void recalcWayTillFirstImpediment(GameObject go, Vector3D_Integer dv) {
        if (INTERSECTION_STRATEGY_SEVERITY <= 0) {
            return;
        }

        // Check if there are impediments on the way to the destination point.
        // If they are, move along the vector (dv) towards destination point until the first impediment.
        Vector3D_Integer dv_opt = calcWayTillFirstImpediment(go, dv);
        if (dv_opt != null) {
            dv.assign(dv_opt);
        }
    }

    public static Vector3D_Integer calcNextMovementPositionVector(GameObject go, Point3D_Integer currentDestPoint) {

        // new_center: "planned" new position of the unit center
        Point3D_Integer new_center = calcNextPointOnRay(go.getAbsCenterInteger(), currentDestPoint, go.speed);
        LOG.trace("old_center=" + go.getAbsCenterInteger() + ", new_center=" + new_center);

        // dv: translation vector of the unit center from old to new position
        Vector3D_Integer dv = new_center.minusClone(go.getAbsCenterInteger());
        LOG.trace("dv=" + dv);

        // new_loc: "planned" new position of the left-top unit corner
        Point3D_Integer new_loc = go.loc.plusClone(dv);
        LOG.debug("move?: " + go.loc + " -> " + new_loc + ", speed=" + go.speed);

        // prevent movement outside map borders
        // TODO: include map borders into recalcWayTillFirstImpediment and remove this block?
        // (map is also a kind of impediment, just big)
        if (! GameMap.getInstance().contains(
                // new_x, new_y, new_z - absolute coordinates, not aliquot to the grid vertices
                new ParallelepipedOfBlocks(new_loc, go.dimInBlocks))
        ) {
            LOG.debug("prevent movement outside the map");
            return new Vector3D_Integer(0, 0, 0);
        }

        // Correct "dv" if impediments on the planned way were found
        recalcWayTillFirstImpediment(go, dv);

        // Paranoid check
        Rectangle new_rect = go.getAbsBottomRect(); // current rectangle position
        new_rect.translate(dv.x(), dv.y()); // next rectangle position
        if (GameMap.getInstance().occupied(new_rect, go)) {
            terminateNoGiveUp(null,1000, "Objects overlapping has been detected! The algorithm has a bug!");
            return new Vector3D_Integer(0, 0, 0);
        }

        return dv;
    }
}
