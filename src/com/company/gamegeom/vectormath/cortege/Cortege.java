package com.company.gamegeom.vectormath.cortege;

import com.company.gamethread.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;

import static com.company.gametools.GenericHelpers.castToGeneric;
import static com.company.gametools.MathTools.sqrVal;

/* TODO:
   1) Support Long.class as well. Make distSqrVal(), sumSqr() and other functions return Long instead of Integer.
      Remove then .longValue() from all places where it is temporarily used.
   2) Introduce check of exceeding of MAX_INTEGER, MIN_INTEGER, MAX_DOUBLE, MIN_DOUBLE on all operations.
   3) Add some counter of created objects (into cloneGeneric and/or constructors) and display it in debug mode
      in order to minimize number of objects and avoid creation of unnecessary ones to have max performance
      and min memory usage.
 */
//abstract class Cortege<T extends Number> implements Cloneable {
abstract class Cortege <E extends Cortege<E,T>, T extends Number> implements Cloneable {

    // Using logger
    private static Logger LOG = LogManager.getLogger(Cortege.class.getName());

    /* Defence from type erasure: introduce a class field which remembers the type */
    protected final Class<T> type;
    public Class<T> getMyType() {
        return this.type;
    }

    // Stores a vector(cortege) of generic type T
    // Why not using List<T>?
    protected T[] data = null;


    /************************************************
     * CONSTRUCTORS                                 *
     ************************************************/

    // Parameterized constructor
    public Cortege(Class<T> type, T[] data) {
        if (type == null) throw new NullPointerException("Cortege<null> is not allowed - type parameter required.");
        this.type = type; // avoid "type erasure"
        if ((data != null) && (data.length > 0)) {
            // data = new T[data.length]; - is not allowed due to type erasure, but ...
            ArrayList<T> data_0 = new ArrayList<T>(data.length);
            for (int i = 0; i <= data.length - 1; i++) data_0.add(data[i]);
            this.data = data_0.toArray(data); // go to the hell, type erasure !!
        }
    }

    // Copying constructor
    // NOTE: maybe we don't need it, because we use "cloneGeneric()" instead everywhere.
    // However this is worth to read: https://dzone.com/articles/java-cloning-copy-constructor-vs-cloning.
    public Cortege(Cortege<?,T> v) {
        if (v.type == null) throw new NullPointerException("Cortege<null> is not allowed - type parameter required.");
        this.type = v.type; // avoid "type erasure"
        if ((v != null) && (v.size() > 0)) {
            // data = new T[v.length]; - is not allowed due to type erasure, but ...
            ArrayList<T> data_0 = new ArrayList<T>(v.size());
            for (int i = 0; i <= v.size() - 1; i++) data_0.add(v.data[i]);
            this.data = data_0.toArray(v.data); // go to the hell, type erasure !!
        }
    }


    /************************************************
     * ARIPHMETICAL METHODS                         *
     ************************************************/


    /*** UNARY OPERATORS AND METHODS ***/

    /* Group 1 - Self unary operators (modify itself and return itself) */

    // =
    protected void _assignMe(Cortege<?,T> v) {
        if (v == null) { LOG.warn("_assignMe: v=null"); return; } // no effect
        if (v.size() == 0) { LOG.warn("_assignMe: v has zero size"); return; } // no effect
        if (this.size() != v.size()) throw new IllegalArgumentException(
                "Vector[" + this.size() + "] + Vector[" + v.size() + "]: different size!"
        );

        typeCheck(this, v);
        LOG.trace("_assignMe: v=" + v);

        for (int i = 0; i <= v.size() - 1; i++) {
            this.data[i] = v.data[i];
        }
    }

    // +=
    protected void _plusMe(Cortege<?,?> v) {
        if (v == null) { LOG.warn("_plusMe: v=null"); return; } // no effect
        if (v.size() == 0) { LOG.warn("_plusMe: v has zero size"); return; } // no effect
        if (this.size() != v.size()) throw new IllegalArgumentException(
                "Vector[" + this.size() + "] + Vector[" + v.size() + "]: different size!"
        );

        LOG.trace("_plusMe: v=" + v);

        if (this.getMyType() == Integer.class) {
            for (int i = 0; i <= v.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() + v.data[i].intValue()));
                this.data[i] = castToGeneric(this.data[i].intValue() + v.data[i].intValue(), type);
            }
        } else if (this.getMyType() == Double.class) {
            for (int i = 0; i <= v.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() + v.data[i].doubleValue()));
                this.data[i] = castToGeneric(this.data[i].doubleValue() + v.data[i].doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.getMyType() + " are not supported.");
        }
    }

    // -=
    //public void _minusMe(Cortege<?,?> v) { _plusMe(mult((E)v, -1)); }
    protected void _minusMe(Cortege<?,?> v) {
        if (v == null) { LOG.warn("_minusMe: v=null"); return; } // no effect
        if (v.size() == 0) { LOG.warn("_minusMe: v has zero size"); return; } // no effect
        if (this.size() != v.size()) throw new IllegalArgumentException(
                "Vector[" + this.size() + "] + Vector[" + v.size() + "]: different size!"
        );

        LOG.trace("_minusMe: v=" + v);

        if (this.getMyType() == Integer.class) {
            for (int i = 0; i <= v.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() - v.data[i].intValue()));
                this.data[i] = castToGeneric(this.data[i].intValue() - v.data[i].intValue(), type);
            }
        } else if (this.getMyType() == Double.class) {
            for (int i = 0; i <= v.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() - v.data[i].doubleValue()));
                this.data[i] = castToGeneric(this.data[i].doubleValue() - v.data[i].doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.getMyType() + " are not supported.");
        }
    }

    // *=
    // TODO: check what happened if the multiplication result more than MAX_INT
    // Idea: forbid at all +=, -=, *=, /= and all "self-methods"? Or check what happens with a normal Integer in such case.
    protected void _multMe(Number K) {
        if (K == null) { LOG.warn("_plusMe: K=null"); return; } // no effect
        if (this.size() == 0) { LOG.warn("_multMe: the cortege has zero size"); return; } // no effect
        if (K.doubleValue() == 1.0) return; // no effect

        LOG.trace("_multMe: K=" + K.toString());

        if (this.getMyType() == Integer.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() * K));
                this.data[i] = castToGeneric(this.data[i].intValue() * K.intValue(), type);
            }
        } else if (this.getMyType() == Double.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() * K));
                this.data[i] = castToGeneric(this.data[i].doubleValue() * K.doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.getMyType() + " are not supported.");
        }
    }

    // /=
    protected void _divMe(Number K) {
        if (K == null) { LOG.warn("_divMe: K=null"); return; } // no effect
        if (this.size() == 0) { LOG.warn("_divMe: the cortege has zero size"); return; } // no effect
        if (K.doubleValue() == 1.0) return; // no effect
        if (K.doubleValue() == 0.0) throw new ArithmeticException("Division by zero.");

        LOG.trace("_divMe: K=" + K.toString());

        // In case of integers we round the division result using (int).
        // In case of floats we don't round and return float(double).
        if (this.getMyType() == Integer.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() * K));
                this.data[i] = castToGeneric((int)(this.data[i].intValue() / K.intValue()), type);
            }
        } else if (this.getMyType() == Double.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() * K));
                this.data[i] = castToGeneric(this.data[i].doubleValue() / K.doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.getMyType() + " are not supported.");
        }
    }


    /* Change and return */
    public <R extends E> R assign(Cortege<?,T> v) {
        this._assignMe(v);
        return (R)this;
    }
    public <R extends E> R plus(Cortege<?,?> v) {
        this._plusMe(v);
        return (R)this;
    }
    public <R extends E> R minus(Cortege<?,?> v) {
        this._minusMe(v);
        return (R)this;
    }
    public <R extends E> R mult(Number K) {
        this._multMe(K);
        return (R)this;
    }
    public <R extends E> R div(Number K) {
        this._divMe(K);
        return (R)this;
    }

    /* Group 2 - Non-self unary operators. Clone itself, modify the clone and return modified clone as a result */

    /* Due to idiotical "type erasure" I cannot implement in the base class such polymorphic operators which return
       the corresponding child class type. See the class-specific implementations in the corresponding derived classes.
     */
    // +
    //public <E extends Cortege<E,?>, R extends Cortege<?,?>> R plus1(Cortege<?,?> v) {
    protected <R extends Cortege<?,?>> R _plus1(Cortege<?,?> v) {
        //return (E)plus2(this, v);
        R v_res = (R)cloneGeneric((E)this);
        v_res._plusMe(v);
        return v_res;
    }

    // -
    //public <E extends Cortege<E,?>, R extends Cortege<?,?>> R minus1(Cortege<?,?> v) {
    protected <R extends Cortege<?,?>> R _minus1(Cortege<?,?> v) {
        //return (E)minus2(this, v);
        R v_res = (R)cloneGeneric((E)this);
        v_res._minusMe(v);
        return v_res;
    }

    // * (returns Number)
    //public <E extends Cortege<E,?>, R extends Cortege<?,?>> R mult1A(Number K) {
    protected <R extends Cortege<?,?>> R _mult1(Number K) {
        //return (E)mult2(this, K);
        R v_res = (R)cloneGeneric((E)this);
        v_res._multMe(K);
        return v_res;
    }

    // * (returns Double)
    //public <E extends Cortege<E,Double>, R extends Cortege<?,Double>> R multDouble1(Double K) {
    protected <R extends Cortege<?,Double>> R _multToDouble1(Double K) {
        //return (E)mult2(this, K);
        R v_res = (R)cloneGeneric((E)this);
        v_res._multMe(K);
        return v_res;
    }

    // / (returns Double)
    //public <E extends Cortege<E,?>, R extends Cortege<?,Double>> R div1A(Number K) {
    protected <R extends Cortege<?,Double>> R _div1(Number K) {
        //return (E)div(this, K);
        R v_res = (R)cloneGeneric((E)this);
        v_res._divMe(K);
        return v_res;
    }

    // / (returns Integer)
    //public <E extends Cortege<E,?>, R extends Cortege<?,Integer>> R divInt1A(Number K) {
    protected <R extends Cortege<?,Integer>> R _divInt1(Number K) {
        //return (E)div(this, K);
        R v_res = (R)cloneGeneric((E)this);
        v_res._divMe(K);
        return v_res;
    }

    /* Other unary methods */
    // == (null, null, ..., null)
    public Boolean isNullVector() {
        boolean isNulled = true;
        for (int i = 0; (i <= this.size() - 1) && (isNulled); i++) {
            isNulled = isNulled && (this.data[i] == null);
        }
        return isNulled;
    }

    // == (0, 0, ..., 0)
    public Boolean isZeroCortege() {
        boolean isZero = true;
        for (int i = 0; (i <= this.size() - 1) && (isZero); i++) {
            isZero = isZero && (this.data[i] != null) && (this.data[i].intValue() == 0);
        }
        return isZero;
    }


    /* Group 3 - Binary operators and methods. Always static, are applied on two objects and
       return another 3rd object or a variable (boolean or numeric) as a calculation result. */

    // ==
    public static boolean eq(Cortege<?,?> v1, Cortege<?,?> v2) {
        if ((v1 == null) && (v2 == null)) return true;
        if ((v1 == null) || (v2 == null) || (v1.size() != v2.size())) return false;
        typeCheck(v1, v2);

        for (int i = 0; i <= v1.size() - 1; i++) {
            if (v1.data[i] != v2.data[i]) return false;
        }

        return true;
    }

    // a + b
    protected static <R extends Cortege<?,?>> R _plus2(Cortege<?,?> v1, Cortege<?,?> v2) {
        if ((v1 == null) && (v2 == null)) return null;
        if ((v1 == null) || (v2 == null) || (v1.size() != v2.size())) throw new IllegalArgumentException(
                "Cortege[" + (v1 == null ? "null" : v1.size()) + "] + Cortege[" + (v2 == null ? "null" : v2.size()) + "]: different size!"
        );
        if ((v1.size() == 0) && (v2.size() == 0)) return null;
        //typeCheck(v1, v2);

        LOG.trace("_plus2: v1=" + v1 + ", v2=" + v2);
        return v1._plus1(v2);
    }

    // a - b
    protected static <R extends Cortege<?,?>> R _minus2(Cortege<?,?> v1, Cortege<?,?> v2) {
        if ((v1 == null) && (v2 == null)) return null;
        if ((v1 == null) || (v2 == null) || (v1.size() != v2.size())) throw new IllegalArgumentException(
                "Cortege[" + (v1 == null ? "null" : v1.size()) + "] + Cortege[" + (v2 == null ? "null" : v2.size()) + "]: different size!"
        );
        if ((v1.size() == 0) && (v2.size() == 0)) return null;
        //typeCheck(v1, v2);

        LOG.trace("_minus2: v1=" + v1 + ", v2=" + v2);
        return v1._minus1(v2);
    }

    // a * K (K - Integer)
    // TODO: Do not implement via .mult !! Better do the same as in the sqr() and return Cortege<?>.
    //public static <E extends Cortege> E mult(E v, Integer K) {
    protected static <R extends Cortege<?,?>> R _mult2(Cortege<?,?> v, Integer K) {
        if ((v == null) || (v.size() == 0)) return null;
        if (K == null) throw new NullPointerException("Vector multiplier K is NULL!");

        LOG.trace("_mult2: v=" + v + ", K=" + K);
        return (R)v._mult1(K);

    }

    // a * K (K - Double => returns Double)
    //public static <E extends Cortege<E, Double>> E multDouble(Cortege<?,?> v, Double K) {
    protected static <R extends Cortege<?, Double>> R _multToDouble2(Cortege<?,?> v, Double K) {
        if ((v == null) || (v.size() == 0)) return null;
        if (K == null) throw new NullPointerException("Vector multiplier K is NULL!");

        LOG.trace("_multToDouble2: v=" + v + ", K=" + K.toString());
        return v._multToDouble1(K);
    }

    // a / K (returns Double)
    //public static <E extends Cortege<E,Double>> E div(Cortege<?,?> v, Double K) {
    protected static <R extends Cortege<?,Double>> R _div2(Cortege<?,?> v, Double K) {
        if ((v == null) || (v.size() == 0)) return null;
        if (K == null) throw new NullPointerException("Vector divider K is NULL!");

        LOG.trace("_div2: v=" + v + ", K=" + K.toString());
        return v._div1(K);
    }

    // a / K (returns Integer)
    //public static <E extends Cortege<E,Integer>> E divInt(Cortege<?,?> v, Number K) {
    protected static <R extends Cortege<?,Integer>> R _divInt2(Cortege<?,?> v, Number K) {
        if ((v == null) || (v.size() == 0)) return null;
        if (K == null) throw new NullPointerException("Vector divider K is NULL!");

        LOG.trace("_divInt2: v=" + v + ", K=" + K.toString());
        return v._divInt1(K);
    }

    // SUM(i) (Ai * Ai)
    // TODO: check what happened if the multiplication result more than MAX_INT

    //public static <R extends Cortege<R,T>, T extends  Number> T sumSqr(R v) {
    public static Number sumSqr(Cortege <?,?> v) {
        if ((v == null) || (v.size() == 0)) return null;

        //T v_res;
        Number res;

        if (v.getMyType() == Integer.class) {
            res = castToGeneric(new Long(0), Long.class);
            for (int i = 0; i <= v.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() * K));
                res = castToGeneric(res.intValue() + v.data[i].intValue() * v.data[i].intValue(), v.type);
            }
        } else if (v.getMyType() == Double.class) {
            res = castToGeneric(new Double(0), Double.class);
            for (int i = 0; i <= v.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() * K));
                res = castToGeneric(res.doubleValue() + v.data[i].doubleValue() * v.data[i].doubleValue(), v.type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + v.getMyType() + " are not supported.");
        }

        return res;
    }

    public T sumSqr() {
        return (T)sumSqr(this);
    }

    // SUM(i) ((Ai - Bi) ^ 2)
    //public static <R extends Cortege<R,T>, T extends Number> T distSqrVal(Cortege<R,T> v1, Cortege<R,T> v2) {
    public static Number _distSqrVal(Cortege<?,?> v1, Cortege<?,?> v2) {
        if ((v1 == null) && (v2 == null)) return null;
        if ((v1 == null) || (v2 == null) || (v1.size() != v2.size())) throw new IllegalArgumentException(
                "Cortege[" + (v1 == null ? "null" : v1.size()) + "] + Cortege[" + (v2 == null ? "null" : v2.size()) + "]: different size!"
        );
        if ((v1.size() == 0) && (v2.size() == 0)) return null;

        LOG.trace("distSqrVal: v1=" + v1 + ", v2=" + v2);

        Number res;
        if (
                (v1.getMyType() != Integer.class) && (v1.getMyType() != Double.class) ||
                (v2.getMyType() != Integer.class) && (v2.getMyType() != Double.class)
        ) throw new IllegalArgumentException("Function _distSqrVal for the type combination " +
                v1.getMyType() + "," + v2.getMyType() + " is not supported.");

        // If at least one Cortege is Double then the computation result must be Double
        Cortege <?,?> first, second;
        if (v1.type.isInstance(Double.class)) {
            first = v1;
            second = v2;
        } else {
            first = v2;
            second = v1;
        }

        return _minus2(first, second).sumSqr();

    }

    public static <T extends Number> T distSqrVal(Cortege<?,?> v1, Cortege<?,?> v2) {
        return (T)_distSqrVal(v1, v2);
    }

    // SUM(i) ((Ai - Bi) ^ 2) <> radius
    //public static <R extends Cortege<R,T>, T extends Number> Boolean withinRadius(Cortege<R,T> v1, Cortege<R,T> v2, int radius) {
    public static Boolean withinRadius(Cortege<?,?> v1, Cortege<?,?> v2, Number radius) {
        if ((v1 == null) || (v2 == null) || (v1.size() == 0) || (v2.size() == 0) || (v1.size() != v2.size()))
            throw new IllegalArgumentException(
                    "Cortege[" + (v1 == null ? "null" : v1.size()) + "]," +
                    "Cortege[" + (v2 == null ? "null" : v2.size()) + "]: required two non-null Corteges with equal and non-zero size!"
            );

        //typeCheck(v1, v2);

        LOG.trace("withinRadius: v1=" + v1 + ", v2=" + v2);
        if (
                (v1.getMyType() != Integer.class) && (v1.getMyType() != Double.class) ||
                (v2.getMyType() != Integer.class) && (v2.getMyType() != Double.class)
        ) throw new IllegalArgumentException("Function withinRadius for the type combination " +
                v1.getMyType() + "," + v2.getMyType() + " is not supported.");

        return distSqrVal(v1, v2).doubleValue() <= sqrVal(radius.doubleValue());
    }


    /************************************************
     * HELPER METHODS (NON-ARITHMETICAL)            *
     ************************************************/

    public Integer size() {
        return (this.data == null) ? 0 : this.data.length;
    }

    public static void typeCheck(Cortege<?,?> v1, Cortege<?,?> v2) {
        if (v1.getMyType() != v2.getMyType()) throw new IllegalArgumentException(
                "Vector<" + v1.getMyType() +  "] + Vector[" + v2.getMyType() + "]: different type!"
        );
    }

    /*public Cortege<E,T> clone() {
        //return new Cortege<T>(getMyType(), this.data); - for non-abstract implementation
        return cloneGeneric((E)this);
    }*/

    // https://stackoverflow.com/questions/803971/cloning-with-generics
    public static <E extends Cortege<E,?>> E cloneGeneric(E obj) {

        E v_res = null;
        try {
            v_res = (E) obj.getClass().getMethod("clone").invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
            Main.terminateNoGiveUp(1000, e.getMessage() + " during invoking clone() for " + obj.getClass());
        }
        return v_res;
    }

    public String toString() {
        if (this.size() == 0) return "(null)";

        String str = "";
        for (int i = 0; i <= this.size() - 1; i++) {
            if (i > 0) str += ",";
            str += this.data[i];
        }
        return "(" + str + ")";
    }
}
