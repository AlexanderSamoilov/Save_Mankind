package com.company.gamegeom._2d;

import com.company.gamegraphics.GraphBugfixes;
import com.company.gamethread.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class GridMatrixVertical extends GridFigure_2D {
    private static Logger LOG = LogManager.getLogger(GridMatrixVertical.class.getName());

    public final Integer left;
    public final Integer right;
    public final Integer[] top;
    public final Integer[] bottom;

    // NOTE: This implementation works only for Y_ORIENT = -1
    // This function returns the matrix of grid blocks occupied by the given parallelogram
    // GridMatrix is a more general case for GridRectangle
    // It distinct to GridRectangle GridMatrixVertical does not have the same number of blocks on each column.
    // It may sometimes be bigger to 1 extra block. It is mathematically possible to calculate
    // when there will be this extra block, but the algorithm will be so much complicated
    // that I am not sure that it will properly work in the integer world of PC.
    // For the time being I'd prefer more simple, but more reliable algorithm that is
    // to determine the occupied blocks by intersection of the grid lines by the skew sides of the parallelogram.
    public GridMatrixVertical(ParallelogramVertical pgmVect) {

        // edge case: width 0 or negative
        if (pgmVect.width < 1) Main.terminateNoGiveUp(null,1000,
                "GridMatrix: horizontal parallelogram with width zero: (" +
                        pgmVect.loc.x() + ", " + pgmVect.loc.y() + ", " + pgmVect.width + ", " + pgmVect.height + ", " + pgmVect.shift);

        // top-left vertex of the parallelogram
        left = pgmVect.loc.x() / BLOCK_SIZE; // left blocks of the grid-parallelogram
        right = (pgmVect.loc.x() + pgmVect.width - 1) / BLOCK_SIZE; // right blocks of the grid-parallelogram
        top = new Integer[right - left + 1];
        bottom = new Integer[right - left + 1];

        // edge case: 4 parallelogram vertices are on the same line
        if (pgmVect.width == 1) {
            int top_y = Math.min(pgmVect.loc.y(), pgmVect.loc.y() + pgmVect.shift);
            int bottom_y = Math.max(pgmVect.loc.y() + pgmVect.height - 1, pgmVect.loc.y() + pgmVect.height - 1 + pgmVect.shift);

            // TODO: make the types convertable (inherit GridRectangle from GridMatrixHorizontal?)
                /*return GridRectangle(
                        new Rectangle(pgmVect.x, top_y, 1, bottom_y - top_y + 1));
                 */
            top[0] = top_y / BLOCK_SIZE;
            bottom[0] = (bottom_y - top_y + 1) / BLOCK_SIZE;
            LOG.warn("Using of more general GridMatrixHorizontal instead of GridRectangle width w=1.");
            return;
        }

        // edge case: parallelogram turns to a rectangle
        if (pgmVect.shift == 0) {
            // TODO: make the types convertable (inherit GridRectangle from GridMatrixHorizontal?)
                /*return GridRectangle(
                        new Rectangle(pgmVect.x, pgmVect.y, pgmVect.width, pgmVect.height));
                 */
            for (int i = left; i <= right; i++) {
                top[i - left] = pgmVect.loc.y() / BLOCK_SIZE;
                bottom[i - left] = (pgmVect.loc.y() + pgmVect.height - 1) / BLOCK_SIZE;
            }
            LOG.warn("Using of more general GridMatrixHorizontal instead of GridRectangle.");
            return;
        }

        for (int i = left; i <= right; i++) {

            // x_curr, x_next
            int x_curr = i * BLOCK_SIZE;
            int x_next = (i + 1) * BLOCK_SIZE;
            // special case: first and last cell
            if (i == left) x_curr = pgmVect.loc.x();
            if (i == right) x_next = pgmVect.loc.x() + pgmVect.width - 1;

            // y_curr_top: intersection of the grid line "i" and the top edge of the parallelogram
            int y_curr_top = pgmVect.loc.y() + pgmVect.shift * (x_curr - pgmVect.loc.x()) / (pgmVect.width - 1);
            // y_next_top: intersection of the grid line "i+1" and the top edge of the parallelogram
            int y_next_top = pgmVect.loc.y() + pgmVect.shift * (x_next - pgmVect.loc.x()) / (pgmVect.width - 1);

            // y_curr_bottom: intersection of the grid line "i" and the bottom edge of the parallelogram
            int y_curr_bottom = y_curr_top + pgmVect.height - 1;
            // y_next_bottom: intersection of the grid line "i+1" and the bottom edge of the parallelogram
            int y_next_bottom = y_next_top + pgmVect.height - 1;

            // Determine to which grid blocks got points (x_curr, y_curr) and (x_next, y_next)
            top[i - left] = Math.min(y_curr_top, y_next_top) / BLOCK_SIZE;
            bottom[i - left] = Math.max(y_curr_bottom, y_next_bottom) / BLOCK_SIZE;
        }

    }

    public void render(Graphics g) {
        for (int i = 0; i <= right - left; i++) {
            for (int j = top[i]; j <= bottom[i]; j++) {
                GraphBugfixes.drawRect(g, new Rectangle((i + left) * BLOCK_SIZE, j * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE));
            }
        }
    }

}
