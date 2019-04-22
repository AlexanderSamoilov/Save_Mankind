package com.company.gamegeom;

import java.awt.*;

// TODO operation +, -, ==, >= ...
public class Point3D <T> {
    /**
     * 3D Point in Game
     */

    public final T x;
    public final T y;
    public final T z;

    public Point3D (T x, T y, T z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D () {
        this(null, null, null);
    }

    public Point3D (Point3D<T> p) {
        this(p.x, p.y, p.z);
    }

    public Point2D<T> to2D() {
        return new Point2D<T>(this.x, this.y);
    }

    public Point toJPoint () {
        return new Point((int) this.x, (int) this.y);
    }

    public String show() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
