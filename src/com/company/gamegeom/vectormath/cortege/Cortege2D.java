package com.company.gamegeom.vectormath.cortege;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gametools.GenericHelpers;
import static com.company.gametools.GenericHelpers.castToGeneric;
import static com.company.gametools.GenericHelpers.createGenericArray2;
import static com.company.gametools.GenericHelpers.createGenericArray3;

//public class Cortege2D <T extends Number> extends Cortege<Cortege2D<T>,T> {
abstract class Cortege2D <E extends Cortege<E,T>, T extends Number> extends Cortege<E,T> {

    private static Logger LOG = LogManager.getLogger(Cortege2D.class.getName());

    public T x() { return super.data == null? null: super.data[0]; }
    public T y() { return super.data == null? null: super.data[1]; }

    /*public Cortege2D<E,T> clone() {
        // return new Cortege2D<T>(type, data.clone()); - for non-abstract implementation
        return cloneGeneric((Cortege2D)this);
    }*/

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */

    /*public Cortege3D(Class<T> type, ArrayList<T> arrList) {
        super(type, createGenericArray(type, arrList));
    }*/
    public Cortege2D(Class<T> type, T x, T y) { super(type, createGenericArray2(type, x, y)); }
    public Cortege2D(Class<T> type, T[] p) { super(type, p); }

    // NOTE: make the default constructor private to forbid its usage, but make the compiler to shut up about "final".
    private Cortege2D() { super(null, null); }

    // Copying constructor
    public Cortege2D(Cortege2D<E,T> p) {
        this(
                p == null ? null : p.type,
                p == null ? null : p.x(),
                p == null ? null : p.y()
        );
        if (p == null) LOG.warn("Initialized Cortege2D: " + toString());
    }

    // There was an error earlier (cannot reproduce anymore) when 0 could not be converted to Integer
    // The workaround was to replace it with new Integer(0). Now it somehow works without it.
    public Cortege3D<?,T> _to3D() {
        // return new Cortege3D<T>(type, this.x(), this.y(), castToGeneric(0, type)); - for non-abstract implementation
        return (Cortege3D<?,T>) GenericHelpers.constructGeneric(this.getClass(),
                createGenericArray3(type, this.x(), this.y(), castToGeneric(0, type)));
    }

    /*public Point toJPoint () {
        return new Point((int) this.x, (int) this.y);
    }*/
}