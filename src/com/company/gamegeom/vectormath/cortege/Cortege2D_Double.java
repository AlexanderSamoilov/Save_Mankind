package com.company.gamegeom.vectormath.cortege;

import static com.company.gametools.GenericHelpers.castToGeneric;

public class Cortege2D_Double extends Cortege2D<Cortege2D_Double,Double> {

    public Cortege2D_Double clone() { return new Cortege2D_Double(data.clone()); }
    public Cortege2D_Double(Double [] v) { super(Double.class, v); }
    public Cortege2D_Double(Number x, Number y) {
        super(
                Double.class,
                x == null ? null : x.doubleValue(),
                y == null ? null : y.doubleValue()
        );
    }

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */
    public Cortege2D_Double(Cortege2D_Double p) { super(p); }
    public Cortege2D_Double(Cortege2D_Integer p) {
        super(Double.class, null);
        if ((p == null) || (p.data == null)) return;
        this.data = new Double[] {p.data[0].doubleValue(), p.data[1].doubleValue()};
    }

    /* Problem 2: Java does not automatically downcast the methods implemented only in the base class.
       That is such a construction as:
           DerivedClass var = staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       does not work until we do explicit type cast:
           DerivedClass var = (DerivedClass)staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       To avoid writing "(DerivedClass)" everywhere the only (and also ugly!) workaround is to reimplement
       the method doing the same way as for Problem 1.
     */

    // Use <R extends Clazz> to avoid downcasting when using of these methods in all children of Clazz.

    // Unary operators modifying current object and returning it
    public <R extends Cortege2D_Double> R plus(Cortege2D_Integer v) { return super.plus(v); }
    public <R extends Cortege2D_Double> R plus(Cortege2D_Double v) { return super.plus(v); }
    public <R extends Cortege2D_Double> R minus(Cortege2D_Integer v) { return super.minus(v); }
    public <R extends Cortege2D_Double> R minus(Cortege2D_Double v) { return super.minus(v); }
    public <R extends Cortege2D_Double> R mult(Number K) { return super.mult(K); }
    public <R extends Cortege2D_Double> R div(Number K) { return super.div(K); }

    // Unary operators returning new object
    public <R extends Cortege2D_Double> R plus1(Cortege2D_Integer v) { return _plus1(v); }
    public <R extends Cortege2D_Double> R plus1(Cortege2D_Double v) { return _plus1(v); }
    public <R extends Cortege2D_Double> R minus1(Cortege2D_Integer v) { return _plus1(v); }
    public <R extends Cortege2D_Double> R minus1(Cortege2D_Double v) { return _plus1(v); }
    public <R extends Cortege2D_Double> R mult1(Integer K) { return _mult1(K); }
    public <R extends Cortege2D_Double> R mult1(Double K) { return _multToDouble1(K); }
    public <R extends Cortege2D_Integer> R divInt1(Number K) { return this.toInteger()._divInt1(K); }
    public <R extends Cortege2D_Double> R div1(Number K) { return _div1(K); }

    // Binary operators
    public static <R extends Cortege2D_Double> R plus2(Cortege2D_Double v1, Cortege2D_Integer v2) { return (R)_plus2(v1, v2); }
    public static <R extends Cortege2D_Double> R plus2(Cortege2D_Double v1, Cortege2D_Double v2) { return (R)_plus2(v1, v2); }
    public static <R extends Cortege2D_Double> R minus2(Cortege2D_Double v1, Cortege2D_Integer v2) { return (R)_minus2(v1, v2); }
    public static <R extends Cortege2D_Double> R minus2(Cortege2D_Double v1, Cortege2D_Double v2) { return (R)_minus2(v1, v2); }
    public static <R extends Cortege2D_Double> R mult2(Cortege2D_Double p, Integer K) { return (R)_mult2(p, K); }
    public static <R extends Cortege2D_Double> R mult2(Cortege2D_Double p, Double K) { return (R)_multToDouble2(p, K); }
    public static <R extends Cortege2D_Double> R div2(Cortege2D_Double p, Integer K) { return (R)_div2(p, K.doubleValue()); }
    public static <R extends Cortege2D_Double> R div2(Cortege2D_Double p, Double K) { return (R)_div2(p, K.doubleValue()); }
    public static <R extends Cortege2D_Integer> R divInt2(Cortege2D_Double p, Number K) { return (R)_divInt2(p.toInteger(), K); }

    // Conversion operators
    public Cortege3D_Double to3D() { return new Cortege3D_Double(x(), y(), castToGeneric(0, type)); }
    public Cortege2D_Integer toInteger() { return new Cortege2D_Integer(this.x().intValue(), this.y().intValue()); }
}
