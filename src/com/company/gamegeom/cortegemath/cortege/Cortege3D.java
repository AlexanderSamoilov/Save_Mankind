package com.company.gamegeom.cortegemath.cortege;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gametools.GenericTools;
import static com.company.gametools.GenericTools.createGenericArray2D;
import static com.company.gametools.GenericTools.createGenericArray3D;

//public class Cortege3D <T extends Number> extends Cortege<Cortege3D<T>,T> {
public abstract class Cortege3D <E extends Cortege3D<E,T>, T extends Number> extends Cortege<E,T> {

    private static Logger LOG = LogManager.getLogger(Cortege3D.class.getName());

    public T x() { return super.data == null? null: super.data[0]; }
    public T y() { return super.data == null? null: super.data[1]; }
    public T z() { return super.data == null? null: super.data[2]; }

    /*public Cortege3D<E,T> clone() {
        // return new Cortege3D<T>(type, data.clone()); - for non-abstract implementation
        return cloneGeneric((Cortege3D)this);
    }*/

    /* Problem 1: Java does not automatically inherit parameterized constructors of base class:
       https://stackoverflow.com/questions/55891828/java-parameterized-base-constructor-is-not-called-if-derived-class-constructor?noredirect=1#comment98440567_55891828.
       Due this we are forced to duplicate them in the derived class calling "super":
     */

    /*public Cortege3D(Class<T> type, ArrayList<T> arrList) {
        super(type, createGenericArray(type, arrList));
    }*/
    public Cortege3D(Class<T> type, T x, T y, T z) { super(type, createGenericArray3D(type, x, y, z)); }
    public Cortege3D(Class<T> type, T[] arr) { super(type, arr); }

    // NOTE: make the default constructor private to forbid its usage, but make the compiler to shut up about "final".
    private Cortege3D() { super(null, null); }

    // Copying constructor
    public Cortege3D(Cortege3D<E,T> c) {
        this(
                c == null ? null : c.type,
                c == null ? null : c.x(),
                c == null ? null : c.y(),
                c == null ? null : c.z()
        );
        if (c == null) LOG.warn("Initialized Cortege3D: " + toString());
    }

    public Cortege2D<?,T> _to2D() {
        // return new Cortege2D<T>(type, this.data); - for non-abstract implementation
        return (Cortege2D<?,T>) GenericTools.constructGeneric(this.getClass(),
                createGenericArray2D(type, this.x(), this.y()));
    }

    /*public Point toJPoint () {
        return new Point((int) this.x, (int) this.y);
    }*/
}