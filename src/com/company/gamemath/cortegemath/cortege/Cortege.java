package com.company.gamemath.cortegemath.cortege;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gametools.MathTools;
import static com.company.gametools.GenericTools.castToGeneric;
import static com.company.gametools.MathTools.sqrVal;
import static com.company.gamethread.M_Thread.terminateNoGiveUp;

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
    public Class<T> type() {
        return this.type;
    }

    // Stores a cortege of generic type T
    // Why not using List<T>?
    protected T[] data = null;


    /************************************************
     * CONSTRUCTORS                                 *
     ************************************************/


    /*public Cortege<E,T> clone() {
        //return new Cortege<T>(type(), this.data); - for non-abstract implementation
        return cloneGeneric((E)this);
    }*/

    // https://stackoverflow.com/questions/803971/cloning-with-generics
    public static <E extends Cortege<E,?>> E cloneGeneric(E obj) {

        E v_res = null;
        try {
            v_res = (E) obj.getClass().getMethod("clone").invoke(obj);
        } catch (Exception e) {
            terminateNoGiveUp(e,1000, e.getMessage() + " during invoking clone() for " + obj.getClass());
        }
        return v_res;
    }

    // Parameterized constructor
    public Cortege(Class<T> type, T[] data) {
        if (type == null) throw new NullPointerException(this.getClass().getSimpleName() + "<null> is not allowed - type parameter required.");
        this.type = type; // avoid "type erasure"
        if ((data != null) && (data.length > 0)) {
            // data = new T[data.length]; - is not allowed due to type erasure, but ...
            ArrayList<T> tmpArrayList = new ArrayList<T>(data.length);
            for (int i = 0; i <= data.length - 1; i++) {
                tmpArrayList.add(data[i]);
            }
            this.data = tmpArrayList.toArray(data); // go to the hell, type erasure !!
        }
    }

    // Stupid Java rule that super() and this() must be first statement.
    // But I need to check "smth" before calling it!
    // I overcome it this way: the static method which returns self object, but does "smth" before.
    // Drawback: unlike the upper method we cannot easily determine the real caller class here,
    // because .getClass() can be called only after this(). It is possible to get it by parsing the call stack,
    // but for current case it is not so important, because the call stack will be displayed anyway,
    // because we do throw a NullPointerException
    public static Cortege checkNull(Cortege c) {
        if (c == null) throw new NullPointerException(Cortege.class.getSimpleName() + "(null) is not allowed.");
        return c;
    }

    // Copying constructor
    // NOTE: maybe we don't need it, because we use "cloneGeneric()" instead everywhere.
    // However this is worth to read: https://dzone.com/articles/java-cloning-copy-constructor-vs-cloning.
    public Cortege(Cortege<?,T> c) {
        this(checkNull(c).type, (T[]) checkNull(c).data);
    }


    /************************************************
     * ARIPHMETICAL METHODS                         *
     ************************************************/


    /*** UNARY OPERATORS AND METHODS ***/

    /* Group 1 - Self unary operators (modify itself and return itself) */

    // =
    protected void _assign(Cortege<?,T> c) {
        if (c == null) { LOG.warn("_assign: c=null"); return; } // no effect
        if (c.size() == 0) { LOG.warn("_assign: c has zero size"); return; } // no effect
        if (this.size() != c.size()) throw new IllegalArgumentException(
                this.getClass().getSimpleName() + "[" + this.size() + "] + " +
                c.getClass().getSimpleName() + "[" + c.size() +
                "]: different size!"
        );

        // Argument type check
        if ((c.type != Integer.class) && (c.type != Double.class)) {
            throw new IllegalArgumentException("Arithmetic operators for the type " + c.type().getSimpleName() + " are not supported.");
        }
        typeCheck(this, c); // type equality check

        LOG.trace("_assign: c=" + c);

        for (int i = 0; i <= c.size() - 1; i++) {
            this.data[i] = c.data[i];
        }
    }

    /* TODO: In 4 next operators (+ - * /) we round up to an integer value with "intValue()"
       Maybe it would better to write a centralized function round() with a possibility to choose
       between intValue(), floor(), ceil() etc. that is different rounding strategies using one place.
     */

    // +=
    protected void _plus(Cortege<?,?> c) {
        if (c == null) { LOG.warn("_plus: c=null"); return; } // no effect
        if (c.size() == 0) { LOG.warn("_plus: c has zero size"); return; } // no effect
        if (this.size() != c.size()) throw new IllegalArgumentException(
                this.getClass().getSimpleName() + "[" + this.size() + "] + " +
                 c.getClass().getSimpleName() + "[" + c.size() +
                "]: different size!"
        );

        // Argument type check
        if ((c.type != Integer.class) && (c.type != Double.class)) {
            throw new IllegalArgumentException("Arithmetic operators for the type " + c.type().getSimpleName() + " are not supported.");
        }

        LOG.trace("_plus: c=" + c);

        if (this.type() == Integer.class) {
            for (int i = 0; i <= c.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() + c.data[i].intValue()));
                this.data[i] = castToGeneric(MathTools.round(this.data[i].doubleValue() + c.data[i].doubleValue()), type);
            }
        } else if (this.type() == Double.class) {
            for (int i = 0; i <= c.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() + c.data[i].doubleValue()));
                this.data[i] = castToGeneric(this.data[i].doubleValue() + c.data[i].doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.type().getSimpleName() + " are not supported.");
        }
    }

    // -=
    //public void _minus(Cortege<?,?> c) { _plus(mult((E)c, -1)); }
    protected void _minus(Cortege<?,?> c) {
        if (c == null) { LOG.warn("_minus: c=null"); return; } // no effect
        if (c.size() == 0) { LOG.warn("_minus: c has zero size"); return; } // no effect
        if (this.size() != c.size()) throw new IllegalArgumentException(
                this.getClass().getSimpleName() + "[" + this.size() + "] + " +
                c.getClass().getSimpleName() + "[" + c.size() +
                "]: different size!"
        );

        // Argument type check
        if ((c.type != Integer.class) && (c.type != Double.class)) {
            throw new IllegalArgumentException("Arithmetic operators for the type " + c.type().getSimpleName() + " are not supported.");
        }
        LOG.trace("_minus: c=" + c);

        if (this.type() == Integer.class) {
            for (int i = 0; i <= c.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() - c.data[i].intValue()));
                this.data[i] = castToGeneric(MathTools.round(this.data[i].doubleValue() - c.data[i].doubleValue()), type);
            }
        } else if (this.type() == Double.class) {
            for (int i = 0; i <= c.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() - c.data[i].doubleValue()));
                this.data[i] = castToGeneric(this.data[i].doubleValue() - c.data[i].doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.type().getSimpleName() + " are not supported.");
        }
    }

    // *=
    // TODO: check what happened if the multiplication result more than MAX_INT
    // Idea: forbid at all +=, -=, *=, /= and all "self-methods"? Or check what happens with a normal Integer in such case.
    protected void _mult(Number num) {
        if (num == null) { LOG.warn("_mult: num=null"); return; } // no effect
        if (this.size() == 0) { LOG.warn("_mult: the " + this.getClass().getSimpleName() + " has zero size"); return; } // no effect
        if (num.doubleValue() == 1.0) return; // no effect

        // Argument type check
        if ((num.getClass() != Integer.class) && (num.getClass() != Double.class)) {
             throw new IllegalArgumentException("Arithmetic operators for the type " + num.getClass().getSimpleName() + " are not supported.");
        }

        LOG.trace("_mult: num=" + num.toString());

        if (this.type() == Integer.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                if (num.getClass() == Integer.class) {
                    this.data[i] = castToGeneric(this.data[i].intValue() * num.intValue(), type);
                } else if (num.getClass() == Double.class) {
                    this.data[i] = castToGeneric(MathTools.round(this.data[i].intValue() * num.doubleValue()), type);
                }
            }
        } else if (this.type() == Double.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() * num));
                this.data[i] = castToGeneric(this.data[i].doubleValue() * num.doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.type().getSimpleName() + " are not supported.");
        }
    }

    // /=
    protected void _div(Number num) {
        if (num == null) { LOG.warn("_div: num=null"); return; } // no effect
        if (this.size() == 0) { LOG.warn("_div: the " + this.getClass().getSimpleName() + " has zero size"); return; } // no effect
        if (num.doubleValue() == 1.0) return; // no effect
        if (num.doubleValue() == 0.0) throw new ArithmeticException("Division by zero.");

        // Argument type check
        if ((num.getClass() != Integer.class) && (num.getClass() != Double.class)) {
            throw new IllegalArgumentException("Arithmetic operators for the type " + num.getClass().getSimpleName() + " are not supported.");
        }

        LOG.trace("_div: num=" + num.toString());

        // In case of integers we round the division result using (int).
        // In case of fractionals we don't round and return float(double).
        if (this.type() == Integer.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() * num));
                this.data[i] = castToGeneric(MathTools.round(this.data[i].intValue() / num.doubleValue()), type);
            }
        } else if (this.type() == Double.class) {
            for (int i = 0; i <= this.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() * num));
                this.data[i] = castToGeneric(this.data[i].doubleValue() / num.doubleValue(), type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + this.type().getSimpleName() + " are not supported.");
        }
    }


    /* Change and return */
    public <R extends E> R assign(Cortege<?,T> c) {
        this._assign(c);
        return (R)this;
    }
    protected <R extends E> R plus(Cortege<?,?> c) {
        this._plus(c);
        return (R)this;
    }
    protected <R extends E> R minus(Cortege<?,?> c) {
        this._minus(c);
        return (R)this;
    }
    protected <R extends E> R mult(Number num) {
        this._mult(num);
        return (R)this;
    }
    protected <R extends E> R div(Number num) {
        this._div(num);
        return (R)this;
    }

    /* Group 2 - Non-self unary operators. Clone itself, modify the clone and return modified clone as a result */

    /* Due to idiotical "type erasure" I cannot implement in the base class such polymorphic operators which return
       the corresponding child class type. See the class-specific implementations in the corresponding derived classes.
     */

    // +
    //public <E extends Cortege<E,?>, R extends Cortege<?,?>> R plus1(Cortege<?,?> c) {
    protected <R extends Cortege<?,?>> R _plusClone(Cortege<?,?> c) {
        //return (E)plus2(this, c);
        R v_res = (R)cloneGeneric((E)this);
        v_res._plus(c);
        return v_res;
    }

    // -
    //public <E extends Cortege<E,?>, R extends Cortege<?,?>> R minus1(Cortege<?,?> c) {
    protected <R extends Cortege<?,?>> R _minusClone(Cortege<?,?> c) {
        //return (E)minus2(this, c);
        R v_res = (R)cloneGeneric((E)this);
        v_res._minus(c);
        return v_res;
    }

    // * (returns Number)
    //public <E extends Cortege<E,?>, R extends Cortege<?,?>> R mult1A(Number num) {
    protected <R extends Cortege<?,?>> R _multClone(Number num) {
        //return (E)mult2(this, num);
        R v_res = (R)cloneGeneric((E)this);
        v_res._mult(num);
        return v_res;
    }

    // * (returns Double)
    //public <E extends Cortege<E,Double>, R extends Cortege<?,Double>> R multDouble1(Double num) {
    protected <R extends Cortege<?,Double>> R _multToDoubleClone(Double num) {
        //return (E)mult2(this, num);
        R v_res = (R)cloneGeneric((E)this);
        v_res._mult(num);
        return v_res;
    }

    // / (returns Double)
    //public <E extends Cortege<E,?>, R extends Cortege<?,Double>> R div1A(Number num) {
    protected <R extends Cortege<?,Double>> R _divClone(Number num) {
        //return (E)div(this, num);
        R v_res = (R)cloneGeneric((E)this);
        v_res._div(num);
        return v_res;
    }

    // / (returns Integer)
    //public <E extends Cortege<E,?>, R extends Cortege<?,Integer>> R divInt1A(Number num) {
    protected <R extends Cortege<?,Integer>> R _divIntClone(Number num) {
        //return (E)div(this, num);
        R v_res = (R)cloneGeneric((E)this);
        v_res._div(num);
        return v_res;
    }

    // ==
    public boolean eq(Cortege<?,?> c) {
        if ((c == null) || (this.size() != c.size())) return false;
        typeCheck(this, c);

        LOG.trace("this=" + this.toDebugString() + ", that=" + c.toDebugString());
        for (int i = 0; i <= c.size() - 1; i++) {
            //if (this.data[i] != c.data[i]) { - for class objects it compares references, not values!
            if (!this.data[i].equals(c.data[i])) {
                return false;
            }
        }

        return true;
    }

    // ~
    public boolean eqDouble(Cortege<?,?> c, double eps) {
        if ((c == null) || (this.size() != c.size())) return false;
        typeCheck(this, c);

        LOG.trace("this=" + this.toDebugString() + ", that=" + c.toDebugString()); 
        for (int i = 0; i <= c.size() - 1; i++) {
            if (this.data[i].doubleValue() - c.data[i].doubleValue() >= eps) {
                return false;
            }
        }

        return true;
    }

    /* Other unary methods */
    // == (null, null, ..., null)
    public Boolean isNullCortege() {
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
            isZero = isZero && (this.data[i] != null) && (this.data[i].doubleValue() == 0);
        }
        return isZero;
    }


    /* Group 3 - Binary operators and methods. Always static, are applied on two objects and
       return another 3rd object or a variable (boolean or numeric) as a calculation result. */

    // ==

    // a + b
    protected static <R extends Cortege<?,?>> R _plus2(Cortege<?,?> c1, Cortege<?,?> c2) {
        if ((c1 == null) && (c2 == null)) return null;
        if ((c1 == null) || (c2 == null) || (c1.size() != c2.size())) throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + "[" + (c1 == null ? "null" : c1.size()) + "] + " +
                c2.getClass().getSimpleName() + "[" + (c2 == null ? "null" : c2.size()) +
                "]: different size!"
        );
        if ((c1.size() == 0) && (c2.size() == 0)) return null;
        //typeCheck(c1, c2);

        LOG.trace("_plus2: c1=" + c1 + ", c2=" + c2);
        return c1._plusClone(c2);
    }

    // a - b
    protected static <R extends Cortege<?,?>> R _minus2(Cortege<?,?> c1, Cortege<?,?> c2) {
        if ((c1 == null) && (c2 == null)) return null;
        if ((c1 == null) || (c2 == null) || (c1.size() != c2.size())) throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + "[" + (c1 == null ? "null" : c1.size()) + "] + " +
                c2.getClass().getSimpleName() + "[" + (c2 == null ? "null" : c2.size()) +
                "]: different size!"
        );
        if ((c1.size() == 0) && (c2.size() == 0)) return null;
        //typeCheck(c1, c2);

        LOG.trace("_minus2: c1=" + c1 + ", c2=" + c2);
        return c1._minusClone(c2);
    }

    // a * num (num - Integer)
    // TODO: Do not implement via .mult !! Better do the same as in the sqr() and return Cortege<?>.
    //public static <E extends Cortege> E mult(E c, Integer num) {
    protected static <R extends Cortege<?,?>> R _mult2(Cortege<?,?> c, Integer num) {
        if ((c == null) || (c.size() == 0)) return null;
        if (num == null) throw new NullPointerException("Multiplier num is NULL!");

        LOG.trace("_mult2: c=" + c + ", num=" + num);
        return (R)c._multClone(num);

    }

    // a * num (num - Double => returns Double)
    //public static <E extends Cortege<E, Double>> E multDouble(Cortege<?,?> c, Double num) {
    protected static <R extends Cortege<?, Double>> R _multToDouble2(Cortege<?,?> c, Double num) {
        if ((c == null) || (c.size() == 0)) return null;
        if (num == null) throw new NullPointerException("Multiplier num is NULL!");

        LOG.trace("_multToDouble2: c=" + c + ", num=" + num.toString());
        return c._multToDoubleClone(num);
    }

    // a / num (returns Double)
    //public static <E extends Cortege<E,Double>> E div(Cortege<?,?> c, Double num) {
    protected static <R extends Cortege<?,Double>> R _div2(Cortege<?,?> c, Double num) {
        if ((c == null) || (c.size() == 0)) return null;
        if (num == null) throw new NullPointerException("Divider num is NULL!");

        LOG.trace("_div2: c=" + c + ", num=" + num.toString());
        return c._divClone(num);
    }

    // a / num (returns Integer)
    //public static <E extends Cortege<E,Integer>> E divInt(Cortege<?,?> c, Number num) {
    protected static <R extends Cortege<?,Integer>> R _divInt2(Cortege<?,?> c, Number num) {
        if ((c == null) || (c.size() == 0)) return null;
        if (num == null) throw new NullPointerException("Divider num is NULL!");

        LOG.trace("_divInt2: c=" + c + ", num=" + num.toString());
        return c._divIntClone(num);
    }

    // SUM(i) (Ai * Ai)
    // TODO: check what happened if the multiplication result more than MAX_INT

    //public static <R extends Cortege<R,T>, T extends  Number> T sumSqr(R c) {
    public static Number sumSqr(Cortege <?,?> c) {
        if ((c == null) || (c.size() == 0)) return null;

        //T v_res;
        Number res;

        if (c.type() == Integer.class) {
            res = castToGeneric(new Long(0), Long.class);
            for (int i = 0; i <= c.size() - 1; i++) {
                //this.data[i] = (T) (new Integer(this.data[i].intValue() * num));
                res = castToGeneric(res.intValue() + c.data[i].intValue() * c.data[i].intValue(), c.type);
            }
        } else if (c.type() == Double.class) {
            res = castToGeneric(new Double(0), Double.class);
            for (int i = 0; i <= c.size() - 1; i++) {
                //this.data[i] = (T) (new Double(this.data[i].doubleValue() * num));
                res = castToGeneric(res.doubleValue() + c.data[i].doubleValue() * c.data[i].doubleValue(), c.type);
            }
        } else {
            throw new IllegalArgumentException("Arithmetic operators for the type " + c.type().getSimpleName() + " are not supported.");
        }

        return res;
    }

    public T sumSqr() {
        return (T)sumSqr(this);
    }

    // SUM(i) ((Ai - Bi) ^ 2)
    //public static <R extends Cortege<R,T>, T extends Number> T distSqrVal(Cortege<R,T> c1, Cortege<R,T> c2) {
    public static Number _distSqrVal(Cortege<?,?> c1, Cortege<?,?> c2) {
        if ((c1 == null) && (c2 == null)) return null;
        if ((c1 == null) || (c2 == null) || (c1.size() != c2.size())) throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + "[" + (c1 == null ? "null" : c1.size()) + "] + " +
                c2.getClass().getSimpleName() + "[" + (c2 == null ? "null" : c2.size()) +
                "]: different size!"
        );
        if ((c1.size() == 0) && (c2.size() == 0)) return null;

        LOG.trace("distSqrVal: c1=" + c1 + ", c2=" + c2);

        Number res;
        if (
                (c1.type() != Integer.class) && (c1.type() != Double.class) ||
                (c2.type() != Integer.class) && (c2.type() != Double.class)
        ) throw new IllegalArgumentException("Function _distSqrVal for the type combination " +
                c1.type().getSimpleName() + "," + c2.type().getSimpleName() + " is not supported.");

        // If at least one Cortege is Double then the computation result must be Double
        Cortege <?,?> first, second;
        if (c1.type.isInstance(Double.class)) {
            first = c1;
            second = c2;
        } else {
            first = c2;
            second = c1;
        }

        return _minus2(first, second).sumSqr();

    }

    public static <T extends Number> T distSqrVal(Cortege<?,?> c1, Cortege<?,?> c2) {
        return (T)_distSqrVal(c1, c2);
    }

    // SUM(i) ((Ai - Bi) ^ 2) <> radius
    //public static <R extends Cortege<R,T>, T extends Number> Boolean withinRadius(Cortege<R,T> c1, Cortege<R,T> c2, int radius) {
    public static Boolean withinRadius(Cortege<?,?> c1, Cortege<?,?> c2, Number radius) {
        if ((c1 == null) || (c2 == null) || (c1.size() == 0) || (c2.size() == 0) || (c1.size() != c2.size()))
            throw new IllegalArgumentException(
                    "Cortege[" + (c1 == null ? "null" : c1.size()) + "]," +
                    "Cortege[" + (c2 == null ? "null" : c2.size()) +
                    "]: required two non-null Corteges with equal and non-zero size!"
            );

        //typeCheck(c1, c2);

        LOG.trace("withinRadius: c1=" + c1 + ", c2=" + c2);
        if (
                (c1.type() != Integer.class) && (c1.type() != Double.class) ||
                (c2.type() != Integer.class) && (c2.type() != Double.class)
        ) throw new IllegalArgumentException("Function withinRadius for the type combination " +
                c1.type().getSimpleName() + "," + c2.type().getSimpleName() + " is not supported.");

        return distSqrVal(c1, c2).doubleValue() <= sqrVal(radius.doubleValue());
    }


    /************************************************
     * HELPER METHODS (NON-ARITHMETICAL)            *
     ************************************************/

    public Integer size() {
        return (this.data == null) ? 0 : this.data.length;
    }

    public static void typeCheck(Cortege<?,?> c1, Cortege<?,?> c2) {
        if (c1.type() != c2.type()) throw new IllegalArgumentException(
                c1.getClass().getSimpleName() + "<" + c1.type().getSimpleName() + "> + " +
                c2.getClass().getSimpleName() + "<" + c2.type().getSimpleName() +
                ">: different type!"
        );
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

    public String toDebugString() {
        if (this.size() == 0) return "(null)";

        String str = "";
        for (int i = 0; i <= this.size() - 1; i++) {
            if (i > 0) str += ",";
            str += this.data[i];
            str += "#";
            str += System.identityHashCode(this.data[i]);
        }
        return "(" + str + ")";
    }

    // Test methods. Put it here to make possible to keep class Cortege private
    // (otherwise it is not possible to call .eq() method without making class public)

    public void assertEq(Cortege c) {
        LOG.debug(
                this + " / " + this.getClass().getSimpleName() + " vs " +
                c + " / " + c.getClass().getSimpleName()
        );
        assert(this.getClass() == c.getClass());
        assert (this.eq(c) == true);
    }

    public void assertNotEq(Cortege c) {
        LOG.debug(
                this + " / " + this.getClass().getSimpleName() + " vs " +
                c + " / " + c.getClass().getSimpleName()
        );
        assert ((this.getClass() != c.getClass()) || (this.eq(c) == false));
    }

    // We have to use some "epsilon", because double numbers are stored with binary representation
    // See https://stackoverflow.com/questions/322749/retain-precision-with-double-in-java?rq=1.
    public void assertEqDouble(Cortege c, double epsilon) {
        LOG.debug(
                this + " / " + this.getClass().getSimpleName() + " vs " +
                c + " / " + c.getClass().getSimpleName()
        );
        assert(this.getClass() == c.getClass());
        assert (this.eqDouble(c, epsilon) == true);
    }
}
