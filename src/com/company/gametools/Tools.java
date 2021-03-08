/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gametools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Type;

/*
 This class contains miscellaneous helper functions and wrappers for standard Java API.
 */
public enum Tools {
    ; // utility class

    private static Logger LOG = LogManager.getLogger(Tools.class.getName());

    public static void timeout(long timeoutMSec) {
        /* TODO: what happens if we pass negative timeout into sleep()? */
        try {
            Thread.sleep(timeoutMSec);
        } catch (InterruptedException e) {
            // This is not a big trouble that we were not able to do sleep, so it is not a reason to interrupt the method on this
        }
    }

    public static void printStackTrace(Exception e) {
        StackTraceElement [] stackTrace;
        if (e != null) {
            stackTrace = e.getStackTrace();
        } else { // if the Exception parameter is null then we print the current stack trace
            stackTrace = Thread.currentThread().getStackTrace();
        }

        if (LOG != null) {
            for (StackTraceElement stackTraceElement : stackTrace) {
                LOG.info(stackTraceElement.toString());
            }
        } else { // If the logger is not working for some reason, at least print to console
            for (StackTraceElement stackTraceElement : stackTrace) {
                System.out.println(stackTraceElement.toString());
            }
        }
    }

    public static int getArraySize(Object[] arr) {
        return (arr == null) ? 0 : arr.length;
    }

    public static void arrayCopyFull(Object[] src, Object[] dest) {
        System.arraycopy(src, 0, dest, 0, getArraySize(src));
    }

    // General comment about clone().
    // I read https://stackoverflow.com/questions/9252803/how-to-avoid-unchecked-cast-warning-when-cloning-a-hashset.
    // I tried 3 following implementations (see below).
    // It always leads to exception handling when we use .clone() and cast the type.
    // This is why my decision currently is to use workaround approaches, such as:
    //  - "new Clazz()" (should be carefully used for derived objects, child class name must be used in "new")
    //  - Concrete clone() implementation for our program classes
    //  - https://stackoverflow.com/questions/10993403/how-to-replace-hashmap-values-while-iterating-over-them-in-java

    // stupid Java stubbornly acknowledges o as Object, not clazz.getClass() !!!
    /*
    public Object safeClone(Object o, Class clazz) {
        Object newObj = null;
        try {
            newObj = ((clazz.getClass())o).clone();
            newObj = o.clone();
            newObj = clazz.cast(o).clone();
        } catch (CloneNotSupportedException e) {

        }
    }*/

    // https://stackoverflow.com/a/25338780/4807875
    /*
    private static Object safeClone(Object obj){
        try{
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }
     */

    // https://stackoverflow.com/questions/803971/cloning-with-generics
    /*
    public Object safeClone(Object obj) {
        try {
            return obj.getClass().getMethod("clone").invoke(obj);
        //} catch (NoSuchMethodException|IllegalAccessException| InvocationTargetException e) {
        } catch(Exception e) { // а чорт його знає!
            terminateNoGiveUp(e,1000, e.getMessage() + e.getMessage() + " during invoking clone() for " + obj.getClass());
        }
        return null;
    }*/

}
