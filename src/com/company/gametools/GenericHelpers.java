package com.company.gametools;

import com.company.gamethread.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class GenericHelpers {

    private static Logger LOG = LogManager.getLogger(GenericHelpers.class.getName());

    // Java does not allow to say this.data[i] = (T) (new T(this.data[i].intValue() + v.data[i].intValue())):
    // https://stackoverflow.com/questions/299998/instantiating-object-of-type-parameter. But there is a way:
    // of a workaround in our case: https://stackoverflow.com/a/14524815/4807875.
    public static <T> T castToGeneric(Object o, Class<T> clazz) {
        try {
            return clazz.cast(o);
        } catch(ClassCastException e) {
            if (clazz == Double.class) {
                return (T) new Double(((Integer)o).doubleValue());
            } else if (clazz == Integer.class) {
                return (T) new Integer(((Double)o).intValue());
            } else {
                LOG.error("castToGeneric: ClassCastException " + o.getClass() + " -> " + clazz);
                throw(e);
            }
        }
    }

    public static <T> Object constructGeneric(Class superType, T[] data) {
        Constructor ctor;
        Object cloned;

        try {
            ctor = superType.getConstructor(data.getClass());
            // If the argument is an array we must wrap it to another array to make it as an atomic argument:
            // https://stackoverflow.com/a/5760630/4807875.
            Object[] args = {data};
            cloned = ctor.newInstance(args);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
            Main.terminateNoGiveUp(1000, "Generic constructor of type " + superType + " failed with " + e);
            return null;
        }

        return cloned;
    }

    // This is a trick described here https://stackoverflow.com/a/17769207/4807875.
    // We want to compute something BEFORE calling super(...), for that we need "static" computation.
    // The problem is Java does not allow to say super(type, new T[] {x, y, z}).

    /* - does not work in runtime
     public static <T extends Number> ArrayList<T> createGenericArray(T x, T y, T z) {
         ArrayList<T> arr = new ArrayList<T>(3);
         arr.add(x); arr.add(y); arr.add(z);
         return arr;
    }*/

    /* - currently not use this implementation
    public static <T> T[] createGenericArray(Class<T> type, ArrayList<T> arrList) {
        if ((arrList == null) || (arrList.size() == 0)) return null;
        Object arr = Array.newInstance(type, arrList.size());
        for (int i=0; i < arrList.size(); i++) Array.set(arr, i, arrList.get(i));
        return (T[])arr;
    }*/

    // TODO: look also https://stackoverflow.com/questions/529085/how-to-create-a-generic-array-in-java.
    // TODO: Theoretically it is possible to refuse at all of arrays and use "data" as ArrayList.
    public static <T extends Number> T[] createGenericArray2(Class<T> type, T x, T y) {
        //Number[] tmp = new Number[] {x, y};
        //return (T[]) Arrays.copyOf(tmp, 2, (Class)type);
        Object arr = Array.newInstance(type, 2);
        Array.set(arr, 0, x);
        Array.set(arr, 1, y);
        return (T[])arr;
    }

    public static <T extends Number> T[] createGenericArray3(Class<T> type, T x, T y, T z) {
        //Number[] tmp = new Number[] {x, y, z};
        //return (T[]) Arrays.copyOf(tmp, 3, (Class)type);
        Object arr = Array.newInstance(type, 3);
        Array.set(arr, 0, x);
        Array.set(arr, 1, y);
        Array.set(arr, 2, z);
        return (T[])arr;
    }
}
