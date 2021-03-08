package com.company.gamegeom._3d;

import java.awt.*;

import com.company.gamecontent.Centerable;
import com.company.gamemath.cortegemath.point.Point3D_Double;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Double;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamegraphics.GraphBugfixes;

public class Parallelepiped extends Figure_3D implements Centerable {

    public final Point3D_Integer loc; // Coordinates of the left-top-back point
    public final Vector3D_Integer dim; // Object dimensions in pixels

    public final Color color = Color.GREEN;

    public int getAbsRight() { return loc.x() + dim.x() - 1; }
    public int getAbsBottom() { return loc.y() + dim.y() - 1; }

    // ATTENTION: If the object width or length has uneven size in pixels then this function returns not integer!
    // We support rotation of such objects around floating coordinate which does not exist on the screen
    public Point3D_Double getAbsCenterDouble() {
        return loc.plusClone(
                new Vector3D_Double(-1,-1,-1).plus(dim).div(2)
        ).to2D().to3D();
    }

    public Point3D_Integer getAbsCenterInteger() {
        return loc.plusClone(
                new Vector3D_Integer(-1,-1,-1).plus(dim).divInt(2)
        ).to2D().to3D();
    }

    // TODO: is this "new" not memory leak prone?
    public Rectangle getAbsBottomRect() {
        return new Rectangle(loc.x(), loc.y(), dim.x(), dim.y());
    }

    public Parallelepiped(Point3D_Integer loc, Vector3D_Integer dim) {
        this.loc = loc.clone();
        this.dim = dim.clone();
    }

    public boolean contains(Parallelepiped otherPpd) {
        return (loc.x() <= otherPpd.loc.x()) && (otherPpd.loc.x() + otherPpd.dim.x() <= loc.x() + dim.x()) &&
               (loc.y() <= otherPpd.loc.y()) && (otherPpd.loc.y() + otherPpd.dim.y() <= loc.y() + dim.y()) &&
               (loc.z() <= otherPpd.loc.z()) && (otherPpd.loc.z() + otherPpd.dim.z() <= loc.z() + dim.z());
    }

    public boolean contains(Point3D_Integer point) {
        return (loc.x() <= point.x()) && (point.x() <= loc.x() + dim.x() - 1) &&
               (loc.y() <= point.y()) && (point.y() <= loc.y() + dim.y() - 1) &&
               (loc.z() <= point.z()) && (point.z() <= loc.z() + dim.z() - 1);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g) {
        Color origColor = g.getColor();
        g.setColor(color);
        GraphBugfixes.drawRect(g, getAbsBottomRect());
        g.setColor(origColor);
    }
}
