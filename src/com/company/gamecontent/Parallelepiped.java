package com.company.gamecontent;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class Parallelepiped implements Renderable, Centerable {

    public int loc[]; // Coordinates of the left-back-bottom point
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
    public double[] getAbsCenter() {
        return new double[] {
                loc[0] + size[0] * BLOCK_SIZE / 2.0,
                loc[1] + size[1] * BLOCK_SIZE / 2.0,
                loc[2] + size[2] * BLOCK_SIZE / 2.0
        };
    }

    // TODO: is this new not memory leak prone?
    public Rectangle getBottomRect() {
        return new Rectangle(getAbsLoc()[0], getAbsLoc()[1], getAbsSize()[0], getAbsSize()[1]);
    }

    public Parallelepiped(int x, int y, int z, int sX, int sY, int sZ) {
        this.loc = new int[] {
                x * BLOCK_SIZE, y * BLOCK_SIZE, z * BLOCK_SIZE
        };

        this.size = new int[] {sX, sY, sZ};
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
