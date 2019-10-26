package com.company.gamegeom.cortegemath.vector;

import com.company.gamegeom.cortegemath.cortege.Cortege3D_Double;
import com.company.gamegeom.cortegemath.cortege.Cortege3D_Integer;

public class Vector3D_Double extends Cortege3D_Double {

    public Vector3D_Double clone() { return new Vector3D_Double(data.clone()); }
    public Vector3D_Double(Double [] arr) { super(arr); }
    public Vector3D_Double(Number x, Number y, Number z) { super(x, y, z); }

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */
    public Vector3D_Double(Cortege3D_Double c) { super(c); }
    public Vector3D_Double(Cortege3D_Integer c) { super(c); }

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
    // vector += point = NOT ALLOWED (because the subtraction result is point)
    // vector -= point = NOT ALLOWED
    // vector + point : NOT ALLOWED
    // vector - point = NOT ALLOWED
    // vector + vector = vector
    // vector - vector = vector

    /* Unary operators modifying current object and returning it */
    @Override
    public Vector3D_Double plus(Cortege3D_Integer c) {
        if (c instanceof Vector3D_Integer) {
            return super.plus(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " += " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Vector3D_Double plus(Cortege3D_Double c) {
        if (c instanceof Vector3D_Double) {
            return super.plus(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " += " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Vector3D_Double minus(Cortege3D_Integer c) {
        if (c instanceof Vector3D_Integer) {
            return super.minus(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " -= " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Vector3D_Double minus(Cortege3D_Double c) {
        if (c instanceof Vector3D_Double) {
            return super.minus(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " -= " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    public Vector3D_Double mult(Number num) { return super.mult(num); }
    public Vector3D_Double div(Number num) { return super.div(num); }

    /* Unary operators returning new object */
    @Override
    public Vector3D_Double plusClone(Cortege3D_Integer c) {
        if (c instanceof Vector3D_Integer) {
            return super.plusClone(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " + " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Vector3D_Double plusClone(Cortege3D_Double c) {
        if (c instanceof Vector3D_Double) {
            return super.plusClone(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " + " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Vector3D_Double minusClone(Cortege3D_Integer c) {
        if (c instanceof Vector3D_Integer) {
            return super.minusClone(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " - " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    @Override
    public Vector3D_Double minusClone(Cortege3D_Double c) {
        if (c instanceof Vector3D_Double) {
            return super.minusClone(c);
        } else {
            throw new IllegalArgumentException(
                    getClass().getSimpleName() + " - " + c.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
            );
        }
    }

    public Vector3D_Double multClone(Integer num) { return super.multClone(num); }
    public Vector3D_Double multClone(Double num) { return super.multClone(num); }
    public Vector3D_Integer divIntClone(Number num) { return super.divIntClone(num); }
    public Vector3D_Double divClone(Number num) { return super.divClone(num); }

    /* Binary operators */
    // black-list methods
    public static Vector3D_Double plus2(Cortege3D_Double c1, Cortege3D_Integer c2) {
        throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + " + " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    public static Vector3D_Double plus2(Cortege3D_Double c1, Cortege3D_Double c2) {
        throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + " + " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    public static Vector3D_Double minus2(Cortege3D_Double c1, Cortege3D_Integer c2) {
        throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + " - " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    public static Vector3D_Double minus2(Cortege3D_Double c1, Cortege3D_Double c2) {
        throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + " - " + c2.getClass().getSimpleName() + " : FORBIDDEN OPERATION."
        );
    }
    // white-list methods
    public static Vector3D_Double plus2(Vector3D_Double v1, Vector3D_Integer v2) { return Cortege3D_Double.plus2(v1, v2); }
    public static Vector3D_Double plus2(Vector3D_Double v1, Vector3D_Double v2) { return Cortege3D_Double.plus2(v1, v2); }
    public static Vector3D_Double minus2(Vector3D_Double v1, Vector3D_Integer v2) { return Cortege3D_Double.minus2(v1, v2); }
    public static Vector3D_Double minus2(Vector3D_Double v1, Vector3D_Double v2) { return Cortege3D_Double.minus2(v1, v2); }

    public static Vector3D_Double mult2(Vector3D_Double v, Integer num) { return Cortege3D_Double.mult2(v, num); }
    public static Vector3D_Double mult2(Vector3D_Double v, Double num) { return Cortege3D_Double.mult2(v, num); }
    public static Vector3D_Double div2(Vector3D_Double p, Number num) { return Cortege3D_Double.div2(p, num); }
    public static Vector3D_Integer divInt2(Vector3D_Double p, Number num) { return Cortege3D_Double.divInt2(p, num); }

    /* Conversion operators */
    public Vector2D_Double to2D() { return new Vector2D_Double(x(), y()); }
    public Vector3D_Integer toInteger() { return new Vector3D_Integer(this.x().intValue(), this.y().intValue(), this.z().intValue()); }
}
