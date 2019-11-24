package com.company.gamegeom._2d;

import com.company.gamegraphics.GraphBugfixes;
import com.company.gamethread.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class GridMatrixHorizontal extends GridFigure_2D {
    private static Logger LOG = LogManager.getLogger(GridMatrixHorizontal.class.getName());

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
        if (pgmHoriz.height < 1) Main.terminateNoGiveUp(null,1000,
                "GridMatrix: horizontal parallelogram with height zero: (" +
                        pgmHoriz.loc.x() + ", " + pgmHoriz.loc.y() + ", " + pgmHoriz.width + ", " + pgmHoriz.height + ", " + pgmHoriz.shift);

        // top-left vertex of the parallelogram
        top = pgmHoriz.loc.y() / BLOCK_SIZE; // top blocks of the grid-parallelogram
        bottom = (pgmHoriz.loc.y() + pgmHoriz.height - 1) / BLOCK_SIZE; // bottom block of the grid-parallelogram
        left = new Integer[bottom - top + 1];
        right = new Integer[bottom - top + 1];

        // edge case: 4 parallelogram vertices are on the same line
        if (pgmHoriz.height == 1) {
            int left_x = Math.min(pgmHoriz.loc.x(), pgmHoriz.loc.x() + pgmHoriz.shift);
            int right_x = Math.max(pgmHoriz.loc.x() + pgmHoriz.width - 1, pgmHoriz.loc.x() + pgmHoriz.width - 1 + pgmHoriz.shift);

            // TODO: make the types convertable (inherit GridRectangle from GridMatrixHorizontal?)
                /*return GridRectangle(
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
                /*return GridRectangle(
                        new Rectangle(pgmHoriz.x, pgmHoriz.y, pgmHoriz.width, pgmHoriz.height));
                 */
            for (int i = top; i <= bottom; i++) {
                left[i - top] = pgmHoriz.loc.x() / BLOCK_SIZE;
                right[i - top] = (pgmHoriz.loc.x() + pgmHoriz.width - 1) / BLOCK_SIZE;
            }
            LOG.warn("Using of more general GridMatrixHorizontal instead of GridRectangle.");
            return;
        }

        for (int i = top; i <= bottom; i++) {

            // y_curr, y_next
            int y_curr = i * BLOCK_SIZE;
            int y_next = (i + 1) * BLOCK_SIZE;
            // special case: first and last cell
            if (i == top) y_curr = pgmHoriz.loc.y();
            if (i == bottom) y_next = pgmHoriz.loc.y() + pgmHoriz.height - 1;

            // x_curr: intersection of the grid line "i" and the left edge of the parallelogram
            int x_curr_left = pgmHoriz.loc.x() + pgmHoriz.shift * (y_curr - pgmHoriz.loc.y()) / (pgmHoriz.height - 1);
            // x_next: intersection of the grid line "i+1" and the left edge of the parallelogram
            int x_next_left = pgmHoriz.loc.x() + pgmHoriz.shift * (y_next - pgmHoriz.loc.y()) / (pgmHoriz.height - 1);

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
