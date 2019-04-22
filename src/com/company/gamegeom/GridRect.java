package com.company.gamegeom;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class GridRect {
        public final int left;
        public final int right;
        public final int top;
        public final int bottom;

        public GridRect(Rectangle rect) {
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
}
