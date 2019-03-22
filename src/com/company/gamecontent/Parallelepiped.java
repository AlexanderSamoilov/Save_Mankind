package com.company.gamecontent;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class Parallelepiped implements Renderable, Centerable {

    public int loc[]; // Coordinates of the left-top-back point
    private int size[]; // Object dimensions in GameMap cells (sX, sY, sZ)

    private final Color color = Color.GREEN;

    public int[] getAbsLoc() { return loc; }

    public int[] getLoc() {
        return new int[] {
                loc[0] / BLOCK_SIZE,
                loc[1] / BLOCK_SIZE,
                loc[2] / BLOCK_SIZE
        };
    }

    public int[] getAbsSize() {
        return new int[] {
                size[0] * BLOCK_SIZE,
                size[1] * BLOCK_SIZE,
                size[2] * BLOCK_SIZE
        };
    }

    public int[] getSize() { return size; }

    // ATTENTION: If the object width or length has uneven size in pixels then this function returns not integer!
    // We support rotation of such objects around floating coordinate which does not exist on the screen
    public double[] getAbsCenterDouble() {
        return new double[] {
                loc[0] + size[0] * BLOCK_SIZE / 2.0,
                loc[1] + size[1] * BLOCK_SIZE / 2.0,
                loc[2] + size[2] * BLOCK_SIZE / 2.0
        };
    }

    public Integer[] getAbsCenterInteger() {
        return new Integer[] {
                loc[0] + size[0] * BLOCK_SIZE / 2,
                loc[1] + size[1] * BLOCK_SIZE / 2,
                loc[2] + size[2] * BLOCK_SIZE / 2
        };
    }

    static class GridRectangle {

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
    }

    // TODO: is this "new" not memory leak prone?
    public GridRectangle getBottomRect() {
        return new GridRectangle(getAbsBottomRect());
    }

    // TODO: is this "new" not memory leak prone?
    public Rectangle getAbsBottomRect() {
        return new Rectangle(getAbsLoc()[0], getAbsLoc()[1], getAbsSize()[0], getAbsSize()[1]);
    }

    public Parallelepiped(int x, int y, int z, int sX, int sY, int sZ) {
        this.loc = new int[] { x, y, z };
        this.size = new int[] {sX, sY, sZ};
    }

    boolean contains(Parallelepiped otherPpd) {
        if (
           (loc[0] <= otherPpd.loc[0]) && (otherPpd.loc[0] + otherPpd.getAbsSize()[0] <= loc[0] + getAbsSize()[0]) &&
           (loc[1] <= otherPpd.loc[1]) && (otherPpd.loc[1] + otherPpd.getAbsSize()[1] <= loc[1] + getAbsSize()[1]) &&
           (loc[2] <= otherPpd.loc[2]) && (otherPpd.loc[2] + otherPpd.getAbsSize()[2] <= loc[2] + getAbsSize()[2])
        ) return true;
        return false;
    }

    boolean contains(Integer[] point) {
        if (
           (loc[0] <= point[0]) && (point[0] <= loc[0] + getAbsSize()[0] - 1) &&
           (loc[1] <= point[1]) && (point[1] <= loc[1] + getAbsSize()[1] - 1) &&
           (loc[2] <= point[2]) && (point[2] <= loc[2] + getAbsSize()[2] - 1)
        ) return true;
        return false;
    }

    // wrapper method
    public void render(Graphics g) {
        render(g, this, 0);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g, Parallelepiped parallelepiped, double rotation_angle) {
        Color origColor = g.getColor();
        g.setColor(color);

        g.drawRect(
                parallelepiped.getAbsLoc()[0], parallelepiped.getAbsLoc()[1],
                parallelepiped.getAbsSize()[0], parallelepiped.getAbsSize()[1]
        );

        g.setColor(origColor);
    }
}
