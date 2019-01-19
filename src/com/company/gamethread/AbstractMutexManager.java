package com.gamethread;

import java.lang.reflect.ParameterizedType;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

// TODO:
// 1. Remember threadId of each D, V, C and check if it matches and if there is only one when a new element is added
// 2. concurrent access (+ What happens if two threads try to CREATE the same mutex at the same time)
// 3. check that we cannot do something bad with returned Semaphore object (for example, override its max counter to make non-binary)
// if yes then replace Semaphore with an own class derived from Semaphore, but with restrictions
public abstract class AbstractMutexManager <TypeKey, TypeValue> extends HashMap {

    //Class clazz = HashMap<T2,HashMap<T3, V>>; - does not work! (http://javanotepad.blogspot.com/2007/09/instanceof-doesnt-work-with-generics.html)
    // Thanks! https://stackoverflow.com/questions/5734720/test-if-object-is-instanceof-a-parameter-type
    protected final Class <TypeValue> TypeV = (Class<TypeValue>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];

        /* For the safety we deprecate the most base class methods */
        @Override @Deprecated public Object put(Object k, Object v) {
            throw new UnsupportedOperationException("This method is deprecated, use insert() instead.");
        }

        /* Only since Java >= 8 */
        @Override @Deprecated public Object putIfAbsent(Object k, Object v) {
            throw new UnsupportedOperationException("This method is deprecated, use insert() instead.");
        }

        @Override @Deprecated public void putAll(Map m) {
            throw new UnsupportedOperationException("This method is deprecated, use clearRecordsForThread() instead.");
        }

        @Override @Deprecated public Object remove(Object k) {
            throw new UnsupportedOperationException("This method is deprecated, use clearRecordsForThread() instead.");
        }

        @Override @Deprecated public Object replace(Object k, Object v) {
            throw new UnsupportedOperationException("This method is deprecated.");
        }

        @Override @Deprecated public boolean replace(Object k, Object oldValue, Object newValue) {
            throw new UnsupportedOperationException("This method is deprecated.");
        }

        @Override @Deprecated public void replaceAll(BiFunction biFunction) {
            throw new UnsupportedOperationException("This method is deprecated.");
        }

        @Override @Deprecated public void clear(){
            throw new UnsupportedOperationException("This method is deprecated, use clearRecordsForThread() instead.");
        }

        @Override @Deprecated public Object merge(Object k, Object v, BiFunction biFunction){
            throw new UnsupportedOperationException("This method is deprecated.");
        }
        /* -- */


        // Careful about types: https://stackoverflow.com/questions/33765010/put-an-object-of-the-wrong-type-in-a-map.
        protected TypeValue insert(TypeKey key, TypeValue value) {

            if (super.containsKey(key)) {
                // NOTE: may be it has been just created in a sibling thread, but the value still was not assigned?
                // No, because we organized key names so that neither two threads can build the same hash key
                // Even more. When thread A asks thread B it uses not the same key when thread B asks thread A.
                TypeValue val = (TypeValue) super.get(key); // class cast exception?
                if (val != value) {
                    Main.terminateNoGiveUp(
                            1000,
                            "Error: insert(" + key + "," + value + "): conflict, value already = " + val
                    );
                } else {
                    return val;
                }
            } else {
                try {
                    super.put(key, value);
                } catch (ConcurrentModificationException e) {
                    Main.terminateNoGiveUp(
                            1000,
                            "Error: ConcurrentModificationException on writing to hash[" + key + "]"
                    );
                }
            }

            return value;
        }

        /* Implement this function if we allow to respawn threads */
        public boolean clearRecordsForThread(int threadId) {
            return false;
        }

        /*public void test() {
            class myClass extends HashMap<Long, HashMap<String, Semaphore>> {}
            Type type = myClass.class.getGenericSuperclass();
            Object o = null;
            boolean res = type.getClass().isInstance(o);
        }*/

        // Careful about types: https://stackoverflow.com/questions/33765010/put-an-object-of-the-wrong-type-in-a-map.
        protected TypeValue obtain(TypeKey key) throws NullPointerException, NoSuchElementException, ClassCastException {
            // TODO: check what happens if it contains, but another type (String "123" instead of integer 123).
            // Will in this case (or even in case we just call containsKey() with another type and variables are not equal literally as int and String) some exception be thrown?
            if (super.containsKey(key)) {
                Object value = super.get(key);
                if (TypeV.isInstance(value)) {
                    return (TypeValue)value;
                } else {
                    Main.terminateNoGiveUp(
                            1000,
                            "Error: Wrong map element type: expected " + TypeV.getClass().toString() + ", got " + value.getClass() + ".");
                    return null;
                }
            } else {
                //throw new NoSuchElementException("No such key: Hash[" + key1 + "].");
                return null;
            }
            //return null;
        }


}
