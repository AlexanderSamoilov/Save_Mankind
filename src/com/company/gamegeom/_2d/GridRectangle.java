package com.company.gamegeom._2d;

import com.company.gamecontent.Centerable;
import com.company.gamegraphics.GraphBugfixes;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class GridRectangle extends GridFigure_2D /*implements Centerable*/ {
    public final int left;
    public final int right;
    public final int top;
    public final int bottom;

    public GridRectangle(Rectangle rect) {
        left = rect.x / BLOCK_SIZE;
        right = (rect.x + rect.width - 1) / BLOCK_SIZE;
        top = rect.y / BLOCK_SIZE;
        bottom = (rect.y + rect.height - 1) / BLOCK_SIZE;
    }

    // The block is in the middle (not a side-block)
    public boolean isMiddleBlock(int grid_x, int grid_y) {
        return (grid_x > left) && (grid_x < right) &&
                (grid_y > top) && (grid_y < bottom);
    }

    public void render(Graphics g) {
        GraphBugfixes.drawRect(g, new Rectangle(
                left * BLOCK_SIZE,
                top * BLOCK_SIZE,
                (right - left + 1) * BLOCK_SIZE,
                (bottom - top + 1) * BLOCK_SIZE));
    }
}