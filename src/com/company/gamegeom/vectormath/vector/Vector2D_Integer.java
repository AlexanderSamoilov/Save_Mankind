package com.company.gamegeom.vectormath.vector;

import com.company.gamegeom.vectormath.cortege.Cortege2D_Integer;

import static com.company.gametools.GenericHelpers.castToGeneric;

public class Vector2D_Integer extends Cortege2D_Integer {

    public Vector2D_Integer clone() { return new Vector2D_Integer(data.clone()); }
    public Vector2D_Integer(Integer [] v) { super(v); }
    public Vector2D_Integer(Number x, Number y) { super(x, y); }

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */
    public Vector2D_Integer(Cortege2D_Integer p) { super(p); }

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
    public Vector2D_Integer plus(Cortege2D_Integer v) { return super.plus(v); }
    public Vector2D_Integer minus(Cortege2D_Integer v) { return super.minus(v); }
    public Vector2D_Integer mult(Number K) { return super.mult(K); }
    public Vector2D_Integer divInt(Number K) { return super.div(K); }

    // Unary operators returning new object
    public Vector2D_Integer plus1(Vector2D_Integer v) { return super.plus1(v); }
    public Vector2D_Double plus1(Vector2D_Double v) { return super.plus1(v); }
    public Vector2D_Integer minus1(Vector2D_Integer v) { return super.minus1(v); }
    public Vector2D_Double minus1(Vector2D_Double v) { return super.minus1(v); }
    public Vector2D_Integer mult1(Integer K) { return super.mult1(K); }
    public Vector2D_Double mult1(Double K) { return super.mult1(K); }
    public Vector2D_Integer divInt1(Number K) { return super.divInt1(K); }
    public Vector2D_Double div1(Number K) { return super.div1(K); }

    // Binary operators
    public static Vector2D_Integer plus2(Vector2D_Integer v1, Vector2D_Integer v2) { return Cortege2D_Integer.plus2(v1, v2); }
    public static Vector2D_Double plus2(Vector2D_Integer v1, Vector2D_Double v2) { return Cortege2D_Integer.plus2(v1, v2); }
    public static Vector2D_Integer minus2(Vector2D_Integer v1, Vector2D_Integer v2) { return Cortege2D_Integer.minus2(v1, v2); }
    public static Vector2D_Double minus2(Vector2D_Integer v1, Vector2D_Double v2) { return Cortege2D_Integer.minus2(v1, v2); }
    public static Vector2D_Integer mult2(Vector2D_Integer v, Integer K) { return Cortege2D_Integer.mult2(v, K); }
    public static Vector2D_Double mult2(Vector2D_Integer v, Double K) { return Cortege2D_Integer.mult2(v, K); }
    public static Vector2D_Double div2(Vector2D_Integer p, Integer K) { return Cortege2D_Integer.div2(p, K); }
    public static Vector2D_Double div2(Vector2D_Integer p, Double K) { return Cortege2D_Integer.div2(p, K); }
    public static Vector2D_Integer divInt2(Vector2D_Integer p, Number K) { return Cortege2D_Integer.divInt2(p, K); }

    // Conversion operators
    public Vector3D_Integer to3D() { return new Vector3D_Integer(x(), y(), castToGeneric(0, type)); }
    public Vector2D_Double toDouble() { return new Vector2D_Double(this); }
}
