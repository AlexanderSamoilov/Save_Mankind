package com.company.gamegeom;

import com.company.gamecontent.Centerable;
import com.company.gamecontent.Renderable;
import com.company.gamegeom.cortegemath.point.Point3D_Double;
import com.company.gamegeom.cortegemath.point.Point3D_Integer;
import com.company.gamegeom.cortegemath.vector.Vector3D_Double;
import com.company.gamegeom.cortegemath.vector.Vector3D_Integer;
import com.company.gamegraphics.GraphBugfixes;

import java.awt.*;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class Parallelepiped implements Renderable, Centerable {

    public Point3D_Integer loc; // Coordinates of the left-top-back point
    private Vector3D_Integer size; // Object dimensions in GameMap cells (sX, sY, sZ)

    private final Color color = Color.GREEN;

    public Point3D_Integer getAbsLoc() { return loc.clone(); }

    public Point3D_Integer getLoc() {
        return loc.divIntClone(BLOCK_SIZE).to2D().to3D();
    }

    public Vector3D_Integer getAbsSize() {
        return size.multClone(BLOCK_SIZE);
        //return Vector3D_Integer.mult2(size, BLOCK_SIZE);
    }

    public Vector3D_Integer getSize() { return size.clone(); }

    public int getAbsRight() { return loc.x() + size.x() * BLOCK_SIZE - 1; }
    public int getAbsBottom() { return loc.y() + size.y() * BLOCK_SIZE - 1; }

    // ATTENTION: If the object width or length has uneven size in pixels then this function returns not integer!
    // We support rotation of such objects around floating coordinate which does not exist on the screen
    public Point3D_Double getAbsCenterDouble() {
        //return loc.plusClone(size.multClone(BLOCK_SIZE).minus(new Vector3D_Integer(1,1,1)).divClone(2).to2D().to3D());
        return loc.plusClone(
                new Vector3D_Double(-1,-1,-1).plus(size.multClone(BLOCK_SIZE)).div(2)
        ).to2D().to3D();

        /*
        return new Point3D_Double (
                loc.x() + (size.x() * BLOCK_SIZE - 1) / 2.0,
                loc.y() + (size.y() * BLOCK_SIZE - 1) / 2.0,
                0.0
        );*/
    }

    public Point3D_Integer getAbsCenterInteger() {
        //return loc.plusClone(size.multClone(BLOCK_SIZE).minus(new Vector3D_Integer(1,1,1)).divInt(2)).to2D().to3D();
        return loc.plusClone(
                new Vector3D_Integer(-1,-1,-1).plus(size.multClone(BLOCK_SIZE)).divInt(2)
        ).to2D().to3D();

        /*
        return new Point3D_Integer (
                loc.x() + (size.x() * BLOCK_SIZE - 1) / 2,
                loc.y() + (size.y() * BLOCK_SIZE - 1) / 2,
                0
        );
        */
    }

    public static class GridRectangle {
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
    }

    // TODO: is this "new" not memory leak prone?
    public GridRectangle getBottomRect() {
        return new GridRectangle(getAbsBottomRect());
    }

    // TODO: is this "new" not memory leak prone?
    public Rectangle getAbsBottomRect() {
        return new Rectangle(getAbsLoc().x(), getAbsLoc().y(), getAbsSize().x(), getAbsSize().y());
    }

    public Parallelepiped(Point3D_Integer loc, Vector3D_Integer dim) {
        this.loc = loc.clone();
        this.size = dim.clone();
    }

    public boolean contains(Parallelepiped otherPpd) {
        if (
           (loc.x() <= otherPpd.loc.x()) && (otherPpd.loc.x() + otherPpd.getAbsSize().x() <= loc.x() + getAbsSize().x()) &&
           (loc.y() <= otherPpd.loc.y()) && (otherPpd.loc.y() + otherPpd.getAbsSize().y() <= loc.y() + getAbsSize().y()) &&
           (loc.z() <= otherPpd.loc.z()) && (otherPpd.loc.z() + otherPpd.getAbsSize().z() <= loc.z() + getAbsSize().z())
        ) return true;
        return false;
    }

    public boolean contains(Point3D_Integer point) {
        if (
           (loc.x() <= point.x()) && (point.x() <= loc.x() + getAbsSize().x() - 1) &&
           (loc.y() <= point.y()) && (point.y() <= loc.y() + getAbsSize().y() - 1) &&
           (loc.z() <= point.z()) && (point.z() <= loc.z() + getAbsSize().z() - 1)
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
        GraphBugfixes.drawRect(g, parallelepiped.getAbsBottomRect());
        g.setColor(origColor);
    }
}
