package com.company.gamegeom.vectormath.point;

import com.company.gamegeom.vectormath.cortege.Cortege2D_Double;
import com.company.gamegeom.vectormath.cortege.Cortege2D_Integer;
import com.company.gamegeom.vectormath.vector.Vector2D_Double;
import com.company.gamegeom.vectormath.vector.Vector2D_Integer;

import static com.company.gametools.GenericHelpers.castToGeneric;

public class Point2D_Double extends Cortege2D_Double {

    public Point2D_Double clone() { return new Point2D_Double(data.clone()); }
    public Point2D_Double(Double [] v) { super(v); }
    public Point2D_Double(Number x, Number y) { super(x, y); }

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */
    public Point2D_Double(Cortege2D_Double p) { super(p); }
    public Point2D_Double(Cortege2D_Integer p) { super(p); }

    /* Problem 2: Java does not automatically downcast the methods implemented only in the base class.
       That is such a construction as:
           DerivedClass var = staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       does not work until we do explicit type cast:
           DerivedClass var = (DerivedClass)staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       To avoid writing "(DerivedClass)" everywhere the only (and also ugly!) workaround is to reimplement
       the method doing the same way as for Problem 1.
     */

    /* Problem 3:
       Operators won't work TRANSITIVELY without downcasting due to idiotical "type erasure" in Java.
                 The problem is that every method which does not contain the child generic class as an ARGUMENT
                 cannot clone/return the child generic class. It is always dropped to the base generic class.
     */

    // Unary operators modifying current object and returning it
    public Point2D_Double plus(Cortege2D_Integer v) { return super.plus(v); }
    public Point2D_Double plus(Cortege2D_Double v) { return super.plus(v); }
    public Point2D_Double minus(Cortege2D_Integer v) { return super.minus(v); }
    public Point2D_Double minus(Cortege2D_Double v) { return super.minus(v); }
    public Point2D_Double mult(Number K) { return super.mult(K); }
    public Point2D_Double div(Number K) { return super.div(K); }

    // Unary operators returning new object
    public Point2D_Double plus1(Vector2D_Integer v) { return super.plus1(v); }
    public Point2D_Double plus1(Vector2D_Double v) { return super.plus1(v); }
    public Point2D_Double minus1(Vector2D_Integer v) { return super.minus1(v); }
    public Point2D_Double minus1(Vector2D_Double v) { return super.minus1(v); }
    public Vector2D_Double minus1(Point2D_Integer v) { return new Vector2D_Double(this).minus(v); }
    public Vector2D_Double minus1(Point2D_Double v) { return new Vector2D_Double(this).minus(v); }
    public Point2D_Double mult1(Integer K) { return super.mult1(K); }
    public Point2D_Double mult1(Double K) { return super.mult1(K); }
    public Point2D_Integer divInt1(Number K) { return super.divInt1(K); }
    public Point2D_Double div1(Number K) { return super.div1(K); }

    // Binary operators
    public static Point2D_Double plus2(Point2D_Double p, Vector2D_Integer v) { return Cortege2D_Double.plus2(p, v); }
    public static Point2D_Double plus2(Point2D_Double p, Vector2D_Double v) { return Cortege2D_Double.plus2(p, v); }
    public static Point2D_Double minus2(Point2D_Double p, Vector2D_Integer v) { return Cortege2D_Double.minus2(p, v); }
    public static Point2D_Double minus2(Point2D_Double p, Vector2D_Double v) { return Cortege2D_Double.minus2(p, v); }
    public static Vector2D_Double minus2(Point2D_Double p1, Point2D_Integer p2) {
        return new Vector2D_Double(p1).minus(p2);
    }
    public static Vector2D_Double minus2(Point2D_Double p1, Point2D_Double p2) {
        return new Vector2D_Double(p1).minus(p2);
    }
    public static Point2D_Double mult2(Point2D_Double p, Integer K) { return Cortege2D_Double.mult2(p, K); }
    public static Point2D_Double mult2(Point2D_Double p, Double K) { return Cortege2D_Double.mult2(p, K); }
    public static Point2D_Double div2(Point2D_Double p, Integer K) { return Cortege2D_Double.div2(p, K); }
    public static Point2D_Double div2(Point2D_Double p, Double K) { return Cortege2D_Double.div2(p, K); }
    public static Point2D_Integer divInt2(Point2D_Double p, Number K) { return Cortege2D_Double.divInt2(p, K); }

    // Conversion operators
    public Point3D_Double to3D() { return new Point3D_Double(x(), y(), castToGeneric(0, type)); }
    public Point2D_Integer toInteger() { return new Point2D_Integer(this.x().intValue(), this.y().intValue()); }
}
