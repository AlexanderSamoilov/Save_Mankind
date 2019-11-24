package com.company.gamegeom._2d;

import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamethread.Main;
import com.company.gametools.MathTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static java.lang.Thread.sleep;

public class ParallelogramVertical extends Figure_2D /*implements Centerable*/ {
    private static Logger LOG = LogManager.getLogger(ParallelogramVertical.class.getName());

    public final Point2D_Integer loc; // Coordinates of the left-top-back point
    public final int width, height;
    public final int shift; // shift of the second horizontal edge along Oy

    public final Color color = Color.BLUE;

    public ParallelogramVertical(Point2D_Integer loc, int width, int height, int shift) {
        this.loc = loc.clone();
        this.width = width;
        this.height = height;
        this.shift = shift;
    }

    /* Tests if the given point (x, y) belongs to the parallelogram interior or border.
       Return value:
       1 - belongs to the interior,
       0 - lays on the border,
      -1 - belongs to the exterior (does not belong, in human interpretation)
    */
    // TODO: To avoid check of discrepancy in "intersects()" better to use some common way for both functions
    // and not check if two different ways give the same result
    public int contains(Point2D_Integer p) {

        int x_left = this.loc.x();
        int x_right = this.loc.x() + this.width - 1;
        int y_left_top = this.loc.y();
        int y_left_bottom = this.loc.y() + this.height - 1;
        int y_right_top = y_left_top + this.shift;
        int y_right_bottom = y_left_bottom + this.shift;

        // 4 vertices on a line
        if (x_left == x_right) {
            if (p.x() != x_left) return -1; // not on the line
            int y_toppest = Math.min(y_left_top, y_right_top);
            int y_bottomest = Math.max(y_left_bottom, y_right_bottom);
            if ((y_toppest <= p.y()) && (p.y() <= y_bottomest)) return 0; // within the section
            return -1; // outside the section borders
        }

        // better to check the 4 vertices separately, because "double" type can give some difference
        // for example, 320.000000000001 and 320
        if (
                (p.x() == x_left) && (p.y() == y_left_top) // top-left vertex
             || (p.x() == x_right) && (p.y() == y_right_top) // top-right vertex
             || (p.x() == x_left) && (p.y() == y_left_bottom) // bottom-left vertex
             || (p.x() == x_right) && (p.y() == y_right_bottom) // bottom-right vertex
        ) return 0;

        double y_top = y_left_top + (p.x() - x_left) * (y_right_top - y_left_top) / (x_right - x_left);
        double y_bottom = y_left_bottom + (p.x() - x_left) * (y_right_bottom - y_left_bottom) / (x_right - x_left);

        LOG.trace("x_left=" + x_left + ", x=" + p.x() + ", x_right=" + x_right);
        LOG.trace("y_top=" + y_top + ", y=" + p.y() + ", y_bottom=" + y_bottom);

        if ((y_top > p.y()) || (p.y() > y_bottom) || (x_left > p.x()) || (p.x() > x_right)) return -1; // outside
        if ((y_top < p.y()) && (p.y() < y_bottom) && (x_left < p.x()) && (p.x() < x_right)) return 1; // inside
        return 0; // otherwise only on the border

    }

    /* Tests if the section [A; B] intersects the interior of the parallelogram
       Return value:
       1 - section [A; B] intersects the interior of the parallelogram,
       0 - section [A; B] just touches some of parallelogram edges or vertices,
      -1 - section [A; B] lays completely in the exterior (does not even touch)
    */
    public int intersects(Point2D_Integer A, Point2D_Integer B) {

        // Validation. We are not supposed that the section turns to a point.
        // We call this function to check intersection of a parallelogram interior with a section [A; B].
        // This section must never turn to a point. Thus we don't just return here smth, but exit the program with a fatal error.
        if ((A.x() == B.x()) && (A.y() == B.y())) {
            Main.terminateNoGiveUp(null,1000, "Wrong data: section [A; B] is a point!");
            return contains(A);
        }

        int containsA = contains(A);
        int containsB = contains(B);

        // If one A or B belongs to the interior of a shape then [A; B] intersects it.
        if ((containsA == 1) || (containsB == 1)) return 1;

        // else: the points lay on the border or outside, let's investigate ...

        // If the section [A; B] intersects at least one edge of the parallelogram then it intersects its interior.
        Point2D_Integer top_left = new Point2D_Integer (this.loc);
        Point2D_Integer top_right = new Point2D_Integer (this.loc.x() + this.width - 1, this.loc.y() + this.shift);
        Point2D_Integer bottom_left = new Point2D_Integer (this.loc.x(), this.loc.y() + this.height - 1);
        Point2D_Integer bottom_right = new Point2D_Integer (this.loc.x() + this.width - 1, this.loc.y() + this.height + this.shift - 1);

        if (
               (MathTools.twoSectionsIntersect(A, B, top_left, top_right) == 1) // intersects top edge
            || (MathTools.twoSectionsIntersect(A, B, bottom_left, bottom_right) == 1) // intersects bottom edge
            || (MathTools.twoSectionsIntersect(A, B, top_left, bottom_left) == 1) // intersects left edge
            || (MathTools.twoSectionsIntersect(A, B, top_right, bottom_right) == 1) // intersects right edge
        ) return 1;

        /* else: the last case left when the section [A; B] intersects the parallelogram interior,
           it is a case when A and B lay on different parallelogram edges:
         */
        if ((containsA == 0) && (containsB == 0)) {
            // We need a little bit deeper analysis: checking each edge.
            Boolean [] edgesContainingA = new Boolean[] {
                    MathTools.sectionContains(top_left, A, top_right) > -1, // lays on the top edge
                    MathTools.sectionContains(bottom_left, A, bottom_right) > -1, // lays on the bottom edge
                    MathTools.sectionContains(top_left, A, bottom_left) > -1, // lay on the left edge
                    MathTools.sectionContains(top_right, A, bottom_right) > -1 // lay on the right edge
            };

            if ( // bug defense (to avoid the case if one function found that some edge contains the point, but another one cannot find which one)
                   (edgesContainingA[0] == false) && (edgesContainingA[1] == false)
                && (edgesContainingA[2] == false) && (edgesContainingA[3] == false)
            ) {
                LOG.debug("L=" + top_left + ", A=" + A + ", R=" + top_right + ", sectionContains=" + MathTools.sectionContains(top_left, A, top_right));
                LOG.debug("L=" + bottom_left + ", A=" + A + ", R=" + bottom_right + ", sectionContains=" + MathTools.sectionContains(bottom_left, A, bottom_right));
                LOG.debug("L=" + top_left + ", A=" + A + ", R=" + bottom_left + ", sectionContains=" + MathTools.sectionContains(top_left, A, bottom_left));
                LOG.debug("L=" + top_right + ", A=" + A + ", R=" + bottom_right + ", sectionContains=" + MathTools.sectionContains(top_right, A, bottom_right));
                Main.terminateNoGiveUp(null,1000, "Discrepancy!! .contains" + A + " is 0, but .sectionContains says that no one edges contains A.");
            }

            Boolean [] edgesContainingB = new Boolean[] {
                    MathTools.sectionContains(top_left, B, top_right) > -1, // lays on the top edge
                    MathTools.sectionContains(bottom_left, B, bottom_right) > -1, // lays on the bottom edge
                    MathTools.sectionContains(top_left, B, bottom_left) > -1, // lay on the left edge
                    MathTools.sectionContains(top_right, B, bottom_right) > -1 // lay on the right edge
            };

            if ( // bug defense (to avoid the case if one function found that some edge contains the point, but another one cannot find which one)
                   (edgesContainingB[0] == false) && (edgesContainingB[1] == false)
                && (edgesContainingB[2] == false) && (edgesContainingB[3] == false)
            ) {
                LOG.debug("L=" + top_left + ", B=" + B + ", R=" + top_right + ", sectionContains=" + MathTools.sectionContains(top_left, B, top_right));
                LOG.debug("L=" + bottom_left + ", B=" + B + ", R=" + bottom_right + ", sectionContains=" + MathTools.sectionContains(bottom_left, B, bottom_right));
                LOG.debug("L=" + top_left+ ", B=" + B + ", R=" + bottom_left + ", sectionContains=" + MathTools.sectionContains(top_left, B, bottom_left));
                LOG.debug("L=" + top_right + ", B=" + B + ", R=" + bottom_right + ", sectionContains=" + MathTools.sectionContains(top_right, B, bottom_right));
                Main.terminateNoGiveUp(null,1000, "Discrepancy!! .contains" + B + " is 0, but .sectionContains says that no one edges contains B.");
            }

            if (edgesContainingA[0] && edgesContainingB[0]) return 0; // A and B lay on the top edge
            if (edgesContainingA[1] && edgesContainingB[1]) return 0; // A and B lay on the bottom edge
            if (edgesContainingA[2] && edgesContainingB[2]) return 0; // A and B lay on the left edge
            if (edgesContainingA[3] && edgesContainingB[3]) return 0; // A and B lay on the right edge

            // otherwise points A and B lay on different parallelogram edges, so we intersect the parallelogram interior
            return 1;
        }

        /* else: now only the following possibilities left:
           a) Only one point lays on the parallelogram border and the section [A; B] touches the parallelogram
           with this point or partly overlap the section [A; B] with one of its edges.
           b) Both A and B lay outside the parallelogram and the whole section [A; B] is outside the parallelogram
           c) Both A and B lay outside the parallelogram and the section [A; B] touches one vertex of the parallelogram
        */

        if ((containsA == 0) || (containsB == 0)) return 0; // Case "a"

        // else: just need to distinguish cases "b" and "c"

        if (
               (MathTools.sectionContains(A, top_left, B) == 0)
            || (MathTools.sectionContains(A, top_right, B) == 0)
            || (MathTools.sectionContains(A, bottom_left, B) == 0)
            || (MathTools.sectionContains(A, bottom_right, B) == 0)
        ) {
            LOG.warn("The condition (contains(A) == 0) || (contains(B) == 0) did not detect case _a_. Check the algorithm!");
            return 0; // case "a"
        }

        if (
               (MathTools.sectionContains(A, top_left, B) == 1)
            || (MathTools.sectionContains(A, top_right, B) == 1)
            || (MathTools.sectionContains(A, bottom_left, B) == 1)
            || (MathTools.sectionContains(A, bottom_right, B) == 1)
        ) return 0; // case "c"

        // else: only case "b" is possible
        return -1;
    }

    // DEBUG (rendering):

    // Method of the "Renderable" interface
    public void render(Graphics g) {
        // TODO: NullPointerException when I close the game window
        // because the Graphics object is destroyed in another thread in parallel
        Color origColor = g.getColor();
        g.setColor(color);

        g.drawLine(loc.x(), loc.y(), loc.x() + width - 1, loc.y() + shift);
        g.drawLine(loc.x() + width - 1, loc.y() + shift, loc.x() + width - 1, loc.y() + shift + height - 1);
        g.drawLine(loc.x() + width - 1, loc.y() + shift + height - 1, loc.x(), loc.y() + height - 1);
        g.drawLine(loc.x(), loc.y() + height - 1, loc.x(), loc.y());

        g.setColor(origColor);

        /*g.setColor(origColor);
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            Main.printStackTrace(e);
        }*/
    }
}
