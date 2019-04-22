package com.company.gamegeom;

// TODO operation +, -, ==, >= ...
public class Point2D <T> {
    /**
     * 2D Point in Game
     */

    public final T x;
    public final T y;

    public Point2D (T x, T y) {
        this.x = x;
        this.y = y;
    }

    public Point2D () {
        this(null, null);
    }

    public Point2D (Point2D<T> p) {
        this(p.x, p.y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
