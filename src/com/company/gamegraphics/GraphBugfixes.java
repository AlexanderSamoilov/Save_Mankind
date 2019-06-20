package com.company.gamegraphics;

import java.awt.*;

public abstract class GraphBugfixes {

    // drawRect() adds +1 pixel to width and height!
    // See https://stackoverflow.com/questions/55445079/java-awt-drawrect-wrongly-interprets-width-and-height-produces-1-pixel-r.
    // This is why we use wrappers for these methods to workaround this issue:
    public static void drawRect(Graphics g, Rectangle rect) {
        g.drawRect(rect.x, rect.y, rect.width - 1, rect.height - 1);
    }

    // getMaxX() and getMaxY() also return the coordinates of the "next" pixel,
    // but not the coordinates of the "right bottom pixel" of the rectangle
    public static int getMaxX(Rectangle rect) {
        return (int)rect.getMaxX() - 1;
    }
    public static int getMaxY(Rectangle rect) {
        return (int)rect.getMaxY() - 1;
    }
}
