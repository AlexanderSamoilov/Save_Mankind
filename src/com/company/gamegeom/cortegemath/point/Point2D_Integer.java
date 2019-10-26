package com.company.gamegeom.cortegemath.point;

import com.company.gamegeom.cortegemath.cortege.Cortege2D_Double;
import com.company.gamegeom.cortegemath.cortege.Cortege2D_Integer;
import com.company.gamegeom.cortegemath.vector.Vector2D_Double;
import com.company.gamegeom.cortegemath.vector.Vector2D_Integer;

import static com.company.gametools.GenericTools.castToGeneric;

public class Point2D_Integer extends Cortege2D_Integer {

    public Point2D_Integer clone() { return new Point2D_Integer(data.clone()); }
    public Point2D_Integer(Integer [] arr) { super(arr); }
    public Point2D_Integer(Number x, Number y) { super(x, y); }

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */
    public Point2D_Integer(Cortege2D_Integer c) { super(c); }

    /* Problem 2: Java does not automatically downcast the methods implemented only in the base class.
       That is such a construction as:
           DerivedClass var = staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       does not work until we do explicit type cast:
           DerivedClass var = (DerivedClass)staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       To avoid writing "(DerivedClass)" everywhere the only (and also ugly!) workaround is to reimplement
       the method doing the same way as for Problem 1.
     */

    /* Problem 3:
       Operators won't work TRANSITIVELY without downcasting due to idiotic "type erasure" in Java.
                 The problem is that every method which does not contain the child generic class as an ARGUMENT
                 cannot clone/return the child generic class. It is always dropped to the base generic class.
     */

    // Rules:
    // point -= point = NOT ALLOWED (because the subtraction result is vector)
    // point += point : NOT ALLOWED
    // point + point : NOT ALLOWED
    // point - point = vector
    // point + vector = point
    // point - vector = point

    /* Unary operators modifying current object and returning it */
    @Override
    public Point2D_Integer plus(Cortege2D_Integer c) {
        if (c instanceof Vector2D_Integer) {
            return super.plus(c);
        } else {
            throw new IllegalArgumentException(
                getClass().getSimpleName() + " += " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Point2D_Integer plus(Cortege2D_Double c) {
        if (c instanceof Vector2D_Double) {
            return super.plus(c);
        } else {
            throw new IllegalArgumentException(
                getClass().getSimpleName() + " += " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Point2D_Integer minus(Cortege2D_Integer c) {
        if (c instanceof Vector2D_Integer) {
            return super.minus(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " -= " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Point2D_Integer minus(Cortege2D_Double c) {
        if (c instanceof Vector2D_Double) {
            return super.minus(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " -= " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    public Point2D_Integer mult(Number num) { return super.mult(num); }
    public Point2D_Integer divInt(Number num) { return super.div(num); }
    public Point2D_Integer div(Number num) { return super.div(num); }

    /* Unary operators returning new object */
    @Override
    public Point2D_Integer plusClone(Cortege2D_Integer c) {
        if (c instanceof Vector2D_Integer) {
            return super.plusClone(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " + " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Point2D_Double plusClone(Cortege2D_Double c) {
        if (c instanceof Vector2D_Double) {
            return super.plusClone(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " + " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    public Point2D_Integer minusClone(Vector2D_Integer v) { return super.minusClone(v); }
    public Point2D_Double minusClone(Vector2D_Double v) { return super.minusClone(v); }
    public Vector2D_Integer minusClone(Point2D_Integer p) { return new Vector2D_Integer(super.minusClone(p)); }
    public Vector2D_Double minusClone(Point2D_Double p) { return new Vector2D_Double(super.minusClone(p)); }
    public Point2D_Integer multClone(Integer num) { return super.multClone(num); }
    public Point2D_Double multClone(Double num) { return super.multClone(num); }
    public Point2D_Integer divIntClone(Number num) { return super.divIntClone(num); }
    public Point2D_Double divClone(Number num) { return super.divClone(num); }

    /* Binary operators */
    // black-list methods
    public static Point2D_Integer plus2(Cortege2D_Integer c1, Cortege2D_Integer c2) {
        throw new IllegalArgumentException(
            c1.getClass().getSimpleName() + " + " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    public static Point2D_Double plus2(Cortege2D_Integer c1, Cortege2D_Double c2) {
        throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + " + " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    public static Point2D_Integer minus2(Cortege2D_Integer c1, Cortege2D_Integer c2) {
        throw new IllegalArgumentException(
            c1.getClass().getSimpleName() + " - " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    public static Point2D_Double minus2(Cortege2D_Integer c1, Cortege2D_Double c2) {
        throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + " - " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    // white-list methods
    public static Point2D_Integer plus2(Point2D_Integer p1, Vector2D_Integer p2) { return p1.plusClone(p2); }
    public static Point2D_Double plus2(Point2D_Integer p1, Vector2D_Double p2) { return p1.plusClone(p2); }
    public static Point2D_Integer minus2(Point2D_Integer p1, Vector2D_Integer p2) { return p1.minusClone(p2); }
    public static Point2D_Double minus2(Point2D_Integer p1, Vector2D_Double p2) { return p1.minusClone(p2); }
    public static Vector2D_Integer minus2(Point2D_Integer p1, Point2D_Integer p2) { return p1.minusClone(p2); }
    public static Vector2D_Double minus2(Point2D_Integer p1, Point2D_Double p2) { return p1.minusClone(p2); }

    public static Point2D_Integer mult2(Point2D_Integer p, Integer num) { return Cortege2D_Integer.mult2(p, num); }
    public static Point2D_Double mult2(Point2D_Integer p, Double num) { return Cortege2D_Integer.mult2(p, num); }
    public static Point2D_Double div2(Point2D_Integer p, Number num) { return Cortege2D_Integer.div2(p, num); }
    public static Point2D_Integer divInt2(Point2D_Integer p, Number num) { return Cortege2D_Integer.divInt2(p, num); }

    /* Conversion operators */
    public Point3D_Integer to3D() { return new Point3D_Integer(x(), y(), castToGeneric(0, type)); }
    public Point2D_Double toDouble() { return new Point2D_Double(this); }
}
