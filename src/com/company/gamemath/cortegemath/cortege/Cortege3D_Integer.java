package com.company.gamemath.cortegemath.cortege;

import com.company.gametools.GenericTools;

public class Cortege3D_Integer extends Cortege3D<Cortege3D_Integer, Integer> {

    public Cortege3D_Integer clone() { return new Cortege3D_Integer(data.clone()); }
    public Cortege3D_Integer(Integer [] arr) { super(Integer.class, arr); }
    public Cortege3D_Integer(Number x, Number y, Number z) {
        super(
                Integer.class,
                x == null ? null : x.intValue(),
                y == null ? null : y.intValue(),
                z == null ? null : z.intValue()
        );
    }

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */
    public Cortege3D_Integer(Cortege3D_Integer c) { super(c); }

    /* Problem 2: Java does not automatically downcast the methods implemented only in the base class.
       That is such a construction as:
           DerivedClass var = staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       does not work until we do explicit type cast:
           DerivedClass var = (DerivedClass)staticMethodOfBaseClassWhichReturnsObjectofBaseClass(...);
       To avoid writing "(DerivedClass)" everywhere the only (and also ugly!) workaround is to reimplement
       the method doing the same way as for Problem 1.
     */

    // Use <R extends Clazz> to avoid downcasting when using of these methods in all children of Clazz.

    /* Problem 3: We want to forbid some operations with child classes, like (point + point) or (vector +- point).
       It will be not enough just not implementing the corresponding methods in the child classes Point*, Vector*,
       because it this case the method from the corresponding Cortege?D_XXX class will be called,
       because the argument of the method is automatically castable to the parent class Cortege?D_XXX.

       It will be not enough as well to write the corresponding method Point?D_XXX plus(Point?D_XXX) method
       in the child class Point?D_XXX that throws an exception, because in this case it will be still possible
       to bypass it by converting of the type to the parent class:     ((Cortege?D_XXX)p1).plus(p2);

       The latter can be prevented by @Override-ing of a generic method of the parent class Cortege?D_XXX
       with a non-generic method with same name in the child class Point?D_XXX. In this case Java
       would have always called the method from the child class which can control type and throw an exception.

       But the idiotic "type erasure" makes us uncomfortable surprise in this case as well and forbids
       such a possibility: https://stackoverflow.com/questions/2357824/overriding-generic-methods-with-non-generic-implementations.

       We can also declare all parent class such methods PROTECTED to forbid to call them from main program.
       This way we are sure that both code expressions p1.plus(p2) and ((Cortege?D_XXX)p1).plus(p2) result to a compile error.
       However this puts a limitation on us, that is we cannot use directly operators on Cortege?D_XXX classes.

       SOLUTION:

       a) For non-static methods:
        - It is still possible to @Override generic with a non-generic if we use for the method argument
       the type of the parent class Cortege?D_XXX in the @Override-en method.

       b) For static methods:
       It is more complicated, because Java does not offer @Override mechanism for static methods.
         - In the parent implementation of the method we use a special call "checkWhoCalled" which looks if
           at least one method argument is an instance of a child class and if yes then it makes sure
           that the call comes from the child class code and not from anywhere else.
           This way it becomes impossible to bypass the code of the child class method.
         - The child has two kinds of implementation of the method. The first one ("black-list" implementation)
           uses the more general parent class type of the argument(s) and throws an exception.
           The second one ("white-list" implementation) uses a concrete child class type of the argument(s) and
           calls the corresponding static method of the parent class.

     */

    /* Unary operators modifying current object and returning it */
    public <R extends Cortege3D_Integer> R plus(Cortege3D_Integer c) {  return super.plus(c); }
    public <R extends Cortege3D_Integer> R plus(Cortege3D_Double c) {  return super.plus(c); }
    public <R extends Cortege3D_Integer> R minus(Cortege3D_Integer c) {  return super.minus(c); }
    public <R extends Cortege3D_Integer> R minus(Cortege3D_Double c) {  return super.minus(c); }
    public <R extends Cortege3D_Integer> R mult(Number num) {  return super.mult(num); }
    public <R extends Cortege3D_Integer> R divInt(Number num) {  return super.div(num); }
    public <R extends Cortege3D_Integer> R div(Number num) {  return super.div(num); }

    /* Unary operators returning new object */
    public <R extends Cortege3D_Integer> R plusClone(Cortege3D_Integer c) {  return _plusClone(c); }
    public <R extends Cortege3D_Double> R plusClone(Cortege3D_Double c) {  return this.toDouble()._plusClone(c); }
    public <R extends Cortege3D_Integer> R minusClone(Cortege3D_Integer c) {  return _minusClone(c); }
    public <R extends Cortege3D_Double> R minusClone(Cortege3D_Double c) {  return this.toDouble()._minusClone(c); }
    public <R extends Cortege3D_Integer> R multClone(Integer num) {  return _multClone(num); }
    public <R extends Cortege3D_Double> R multClone(Double num) {  return this.toDouble()._multToDoubleClone(num); }
    public <R extends Cortege3D_Integer> R divIntClone(Number num) {  return _divIntClone(num); }
    public <R extends Cortege3D_Double> R divClone(Number num) {  return this.toDouble()._divClone(num); }

    /* Binary operators */

    // Forbid to call static methods of Cortege?D_XXX(x, y) directly from the main code,
    // if x or y is child instance of Cortege?D_XXX, because in this case we bypass the type check.
    private static void checkWhoCalled(Cortege3D c1, Cortege3D c2) {
        if (
                (c1.getClass() != Cortege3D_Integer.class) && (c1.getClass() != Cortege3D_Double.class) ||
                (c2.getClass() != Cortege3D_Integer.class) && (c2.getClass() != Cortege3D_Double.class)
        ) {
            GenericTools.checkCallerClass(Cortege3D_Integer.class, 1);
        }
    }

    public static <R extends Cortege3D_Integer> R plus2(Cortege3D_Integer c1, Cortege3D_Integer c2) { checkWhoCalled(c1, c2); return (R)_plus2(c1, c2); }
    public static <R extends Cortege3D_Double> R plus2(Cortege3D_Integer c1, Cortege3D_Double c2) { checkWhoCalled(c1, c2); return (R)_plus2(c1.toDouble(), c2); }
    public static <R extends Cortege3D_Integer> R minus2(Cortege3D_Integer c1, Cortege3D_Integer c2) { checkWhoCalled(c1, c2); return (R)_minus2(c1, c2); }
    public static <R extends Cortege3D_Double> R minus2(Cortege3D_Integer c1, Cortege3D_Double c2) { checkWhoCalled(c1, c2); return (R)_minus2(c1.toDouble(), c2); }
    public static <R extends Cortege3D_Integer> R mult2(Cortege3D_Integer c, Integer num) { return (R)_mult2(c, num); }
    public static <R extends Cortege3D_Double> R mult2(Cortege3D_Integer c, Double num) { return (R)_multToDouble2(c.toDouble(), num); }
    public static <R extends Cortege3D_Double> R div2(Cortege3D_Integer c, Number num) { return (R)_div2(c.toDouble(), num.doubleValue()); }
    public static <R extends Cortege3D_Integer> R divInt2(Cortege3D_Integer c, Number num) { return (R)_divInt2(c, num); }

    /* Conversion operators */
    public Cortege2D_Integer to2D() { return new Cortege2D_Integer(x(), y()); }
    public Cortege3D_Double toDouble() { return new Cortege3D_Double(this); }
}
