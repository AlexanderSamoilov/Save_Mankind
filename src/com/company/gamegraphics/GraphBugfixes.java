/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gamegraphics;

import java.awt.*;

// TODO: Add test to the beginning of the game ot test if all standard functions
// which are not "fixed" here behave as expected and display an error if not
// something like "ERROR: 3rd party API <func_name> was changed, the program will work wrongly!"

public enum GraphBugfixes {
    ; // utility class

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
