package com.company.gamegeom;

import com.company.gamegraphics.GraphBugfixes;
import com.company.gamethread.Main;
import com.company.gametools.MathTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;
import static java.lang.Thread.sleep;

public class ParallelogramHorizontal {
    private static Logger LOG = LogManager.getLogger(ParallelogramHorizontal.class.getName());

    private int x, y; // Coordinates of the left-top-back point
    private int width, height;
    private int shift; // shift of the second horizontal edge along Ox

    private final Color color = Color.BLUE;

    public ParallelogramHorizontal(int x, int y, int width, int height, int shift) {
        this.x = x;
        this.y = y;
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
    public int contains(Integer [] p) {

        int y_top = this.y;
        int y_bottom = this.y + this.height - 1;
        int x_left_top = this.x;
        int x_right_top = this.x + this.width - 1;
        int x_left_bottom = x_left_top + this.shift;
        int x_right_bottom = x_right_top + this.shift;

        // 4 vertices on a line
        if (y_top == y_bottom) {
            if (p[1] != y_top) return -1; // not on the line
            int x_leftest = Math.min(x_left_top, x_left_bottom);
            int x_rightest = Math.max(x_right_top, x_right_bottom);
            if ((x_leftest <= p[0]) && (p[0] <= x_rightest)) return 0; // within the section
            return -1; // outside the section borders
        }

        // better to check the 4 vertices separately, because "double" type can give some difference
        // for example, 320.000000000001 and 320
        if (
               (p[0] == x_left_top) && (p[1] == y_top) // top-left vertice
            || (p[0] == x_right_top) && (p[1] == y_top) // top-right vertice
            || (p[0] == x_left_bottom) && (p[1] == y_bottom) // bottom-left vertice
            || (p[0] == x_right_bottom) && (p[1] == y_bottom) // bottom-right vertice
        ) return 0;

        double x_left = x_left_top + (p[1] - y_top) * (x_left_bottom - x_left_top) / (y_bottom - y_top);
        double x_right = x_right_top + (p[1] - y_top) * (x_right_bottom - x_right_top) / (y_bottom - y_top);

        LOG.debug("x_left=" + x_left + ", x=" + p[0] + ", x_right=" + x_right);
        LOG.debug("y_top=" + y_top + ", y=" + p[1] + ", y_bottom=" + y_bottom);

        if ((y_top > p[1]) || (p[1] > y_bottom) || (x_left > p[0]) || (p[0] > x_right)) return -1; // outside
        if ((y_top < p[1]) && (p[1] < y_bottom) && (x_left < p[0]) && (p[0] < x_right)) return 1; // inside
        return 0; // otherwise only on the border

    }

    /* Tests if the section [A; B] intersects the interior of the parallelogram
       Return value:
       1 - section [A; B] intersects the interior of the parallelogram,
       0 - section [A; B] just touches some of parallelogram edges or vertices,
      -1 - section [A; B] lays completely in the exterior (does not even touch)
     */
    public int intersects(Integer [] A, Integer [] B) {

        // Validation. We are not supposed that the section turns to a point.
        // We call this function to check intersection of a parallelogram interior with a section [A; B].
        // This section must never turn to a point. Thus we don't just return here smth, but exit the program with a fatal error.
        if ((A[0] == B[0]) && (A[1] == B[1])) {
            Main.terminateNoGiveUp(1000, "Wrong data: section [A; B] is a point!");
            return contains(A);
        }

        int containsA = contains(A);
        int containsB = contains(B);

        // If one A or B belongs to the interior of a shape then [A; B] intersects it.
        if ((containsA == 1) || (containsB == 1)) return 1;

        // else: the points lay on the border or outside, let's investigate ...

        // If the section [A; B] intersects at least one edge of the parallelogram then it intersects its interior.
        Integer [] top_left = new Integer[] {this.x, this.y};
        Integer [] top_right = new Integer[] {this.x + this.width - 1, this.y};
        Integer [] bottom_left = new Integer[] {this.x + this.shift, this.y + this.height - 1};
        Integer [] bottom_right = new Integer[] {this.x + this.shift + this.width - 1, this.y + this.height - 1};

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
                LOG.debug("L=(" + top_left[0] + ", " + top_left[1] + ") , A=(" + A[0] + ", " + A[1] + "), R=(" + top_right[0] + ", " + top_right[1] + "), sectionContains=" + MathTools.sectionContains(top_left, A, top_right));
                LOG.debug("L=(" + bottom_left[0] + ", " + bottom_left[1] + ") , A=(" + A[0] + ", " + A[1] + "), R=(" + bottom_right[0] + ", " + bottom_right[1] + "), sectionContains=" + MathTools.sectionContains(bottom_left, A, bottom_right));
                LOG.debug("L=(" + top_left[0] + ", " + top_left[1] + ") , A=(" + A[0] + ", " + A[1] + "), R=(" + bottom_left[0] + ", " + bottom_left[1] + "), sectionContains=" + MathTools.sectionContains(top_left, A, bottom_left));
                LOG.debug("L=(" + top_right[0] + ", " + top_right[1] + ") , A=(" + A[0] + ", " + A[1] + "), R=(" + bottom_right[0] + ", " + bottom_right[1] + "), sectionContains=" + MathTools.sectionContains(top_right, A, bottom_right));
                Main.terminateNoGiveUp(1000, "Discrepancy! .contains(" + A[0] + "," + A[1] + ") is 0, but .sectionContains says that no one edges contains A.");
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
                LOG.debug("L=(" + top_left[0] + ", " + top_left[1] + ") , B=(" + B[0] + ", " + B[1] + "), R=(" + top_right[0] + ", " + top_right[1] + "), sectionContains=" + MathTools.sectionContains(top_left, B, top_right));
                LOG.debug("L=(" + bottom_left[0] + ", " + bottom_left[1] + ") , B=(" + B[0] + ", " + B[1] + "), R=(" + bottom_right[0] + ", " + bottom_right[1] + "), sectionContains=" + MathTools.sectionContains(bottom_left, B, bottom_right));
                LOG.debug("L=(" + top_left[0] + ", " + top_left[1] + ") , B=(" + B[0] + ", " + B[1] + "), R=(" + bottom_left[0] + ", " + bottom_left[1] + "), sectionContains=" + MathTools.sectionContains(top_left, B, bottom_left));
                LOG.debug("L=(" + top_right[0] + ", " + top_right[1] + ") , B=(" + B[0] + ", " + B[1] + "), R=(" + bottom_right[0] + ", " + bottom_right[1] + "), sectionContains=" + MathTools.sectionContains(top_right, B, bottom_right));
                Main.terminateNoGiveUp(1000, "Discrepancy! .contains(" + B[0] + "," + B[1] + ") is 0, but .sectionContains says that no one edges contains B.");
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
           c) Both A and B lay outside the parallelogram and the section [A; B] touches one vertice of the parallelogram
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

    public static class GridMatrixHorizontal {

        public final Integer[] left;
        public final Integer[] right;
        public final Integer top;
        public final Integer bottom;

        // NOTE: This implementation works only for Y_ORIENT = -1
        // This function returns the matrix of grid blocks occupied by the given parallelogram
        // GridMatrix is a more general case for GridRectangle
        // It distinct to GridRectangle GridMatrixHorizontal does not have the same number of blocks on each row.
        // It may sometimes be bigger to 1 extra block. It is mathematically possible to calculate
        // when there will be this extra block, but the algorithm will be so much complicated
        // that I am not sure that it will properly work in the integer world of PC.
        // For the time being I'd prefer more simple, but more reliable algorithm that is
        // to determine the occupied blocks by intersection of the grid lines by the skew sides of the parallelogram.
        public GridMatrixHorizontal(ParallelogramHorizontal pgmHoriz) {

            // edge case: height 0 or negative
            if (pgmHoriz.height < 1) Main.terminateNoGiveUp(1000,
                    "GridMatrix: horizontal parallelogram with height zero: (" +
                            pgmHoriz.x + ", " + pgmHoriz.y + ", " + pgmHoriz.width + ", " + pgmHoriz.height + ", " + pgmHoriz.shift);

            // top-left vertice of the parallelogram
            top = pgmHoriz.y / BLOCK_SIZE; // top blocks of the grid-parallelogram
            bottom = (pgmHoriz.y + pgmHoriz.height - 1) / BLOCK_SIZE; // bottom block of the grid-parallelogram
            left = new Integer[bottom - top + 1];
            right = new Integer[bottom - top + 1];

            // edge case: 4 parallelogram vertices are on the same line
            if (pgmHoriz.height == 1) {
                int left_x = Math.min(pgmHoriz.x, pgmHoriz.x + pgmHoriz.shift);
                int right_x = Math.max(pgmHoriz.x + pgmHoriz.width - 1, pgmHoriz.x + pgmHoriz.width - 1 + pgmHoriz.shift);

                // TODO: make the types convertable (inherit GridRectangle from GridMatrixHorizontal?)
                /*return Parallelepiped.GridRectangle(
                        new Rectangle(left_x, pgmHoriz.y, right_x - left_x + 1, 1));
                 */
                left[0] = left_x / BLOCK_SIZE;
                right[0] = (right_x - left_x + 1) / BLOCK_SIZE;
                LOG.warn("Using of more general GridMatrixHorizontal instead of GridRectangle width h=1.");
                return;
            }

            // edge case: parallelogram turns to a rectangle
            if (pgmHoriz.shift == 0) {
                // TODO: make the types convertable (inherit GridRectangle from GridMatrixHorizontal?)
                /*return Parallelepiped.GridRectangle(
                        new Rectangle(pgmHoriz.x, pgmHoriz.y, pgmHoriz.width, pgmHoriz.height));
                 */
                for (int i = top; i <= bottom; i++) {
                    left[i - top] = pgmHoriz.x / BLOCK_SIZE;
                    right[i - top] = (pgmHoriz.x + pgmHoriz.width - 1) / BLOCK_SIZE;
                }
                LOG.warn("Using of more general GridMatrixHorizontal instead of GridRectangle.");
                return;
            }

            for (int i = top; i <= bottom; i++) {

                // y_curr, y_next
                int y_curr = i * BLOCK_SIZE;
                int y_next = (i + 1) * BLOCK_SIZE;
                // special case: first and last cell
                if (i == top) y_curr = pgmHoriz.y;
                if (i == bottom) y_next = pgmHoriz.y + pgmHoriz.height - 1;

                // x_curr: intersection of the grid line "i" and the left edge of the parallelogram
                int x_curr_left = pgmHoriz.x + pgmHoriz.shift * (y_curr - pgmHoriz.y) / (pgmHoriz.height - 1);
                // x_next: intersection of the grid line "i+1" and the left edge of the parallelogram
                int x_next_left = pgmHoriz.x + pgmHoriz.shift * (y_next - pgmHoriz.y) / (pgmHoriz.height - 1);

                // x_curr_right: intersection of the grid line "i" and the right edge of the parallelogram
                int x_curr_right = x_curr_left + pgmHoriz.width - 1;
                // x_next_right: intersection of the grid line "i+1" and the right edge of the parallelogram
                int x_next_right = x_next_left + pgmHoriz.width - 1;

                // Determine to which grid blocks got points (x_curr, y_curr) and (x_next, y_next)
                left[i - top] = Math.min(x_curr_left, x_next_left) / BLOCK_SIZE;
                right[i - top] = Math.max(x_curr_right, x_next_right) / BLOCK_SIZE;
            }

        }

        public void render(Graphics g) {
            for (int i = 0; i <= bottom - top; i++) {
                for (int j = left[i]; j <= right[i]; j++) {
                    GraphBugfixes.drawRect(g, new Rectangle(j * BLOCK_SIZE, (i + top) * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE));
                }
            }
        }

    }

    // DEBUG (rendering):

    // wrapper method
    public void render(Graphics g) {
        render(g, this, 0);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g, ParallelogramHorizontal pgmHoriz, double rotation_angle) {
        // TODO: NullPointerException when I close the game window
        // because the Graphics object is destroyed in another thread in parallel
        Color origColor = g.getColor();
        g.setColor(color);

        g.drawLine(pgmHoriz.x, pgmHoriz.y, pgmHoriz.x + pgmHoriz.width - 1, pgmHoriz.y);
        g.drawLine(pgmHoriz.x + pgmHoriz.width - 1, pgmHoriz.y, pgmHoriz.x + pgmHoriz.shift + pgmHoriz.width - 1, pgmHoriz.y + pgmHoriz.height - 1);
        g.drawLine(pgmHoriz.x + pgmHoriz.shift + pgmHoriz.width - 1, pgmHoriz.y + pgmHoriz.height - 1, pgmHoriz.x + pgmHoriz.shift, pgmHoriz.y + pgmHoriz.height - 1);
        g.drawLine(pgmHoriz.x + pgmHoriz.shift, pgmHoriz.y + pgmHoriz.height - 1, pgmHoriz.x, pgmHoriz.y);

        g.setColor(origColor);

        /*try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

}
