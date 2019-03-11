package com.company.gamethread;

import java.util.ConcurrentModificationException;
import java.util.concurrent.Semaphore;

// Singleton
public class MutexManager <TypeKey, TypeValue> extends AbstractMutexManager <TypeKey, TypeValue> {
    //private static MutexManager instance = new MutexManager(); // sun.reflect.generics.reflectiveObjects.TypeVariableImpl cannot be cast to java.lang.Class
    //public static MutexManager getInstance() { return instance; } // sun.reflect.generics.reflectiveObjects.TypeVariableImpl cannot be cast to java.lang.Class


    public TypeValue getMutex(String threadType, TypeKey mutexPurpose) {
        Long threadIdA = Thread.currentThread().getId(); // calculate threadId of the calling thread
        Long threadIdB = null;
        switch (threadType) {
            case "M":
                threadIdB = Main.getThreadId();
                break;
            case "D":
                threadIdB = D_Thread.getInstance().getId();
                break;
            case "V":
                threadIdB = V_Thread.getInstance().getId();
                break;
            case "C":
                threadIdB = C_Thread.getInstance().getId();
                break;
            default:
                Main.terminateNoGiveUp(
                        1000,
                        "Error: threads of type " + threadType + " are not supported."
                );
        }

        if (threadIdA == threadIdB) {
            Main.terminateNoGiveUp(1000, "An attempt to block the own thread was detected!");
        }

        Long threadIdLess = Math.min(threadIdA, threadIdB);
        Long threadIdMore = Math.max(threadIdA, threadIdB);
        TypeValue value = null;
        String key = threadIdLess.toString() + ":" + threadIdMore.toString() + ":" + mutexPurpose.toString();
        value = obtain((TypeKey) (key));

        if (value != null) { return value; }
        else {
            // ATTENTION: After the last fix for concurrent call of "insert" is supposed to be safe
            // However I am not 100% sure that this implementation is perfect.
            //return super.insert((TypeKey)key, (TypeValue)(super.TypeV.getConstructor(TypeV).));

            Semaphore s = new Semaphore(1);
            //Main.printMsg(insert((TypeKey)key, (TypeValue)(s)).toString());
            //Main.printMsg(obtain((TypeKey)key).toString());
            TypeValue insertionRes = null;
            boolean concurrentAccessOK = false;
            // I suspect that it is possible that one thread executes half of "insert" operation
            // that is creates a new hash key but still does not catch to assign a value
            // If at the same time the second thread calls also "insert" then it will try
            // to return still not completely created value which is null, so we should wait a little bit
            // using the condition insertionRes == null until the first thread completes its "insert"
            while (!concurrentAccessOK && insertionRes == null) {
                try {
                    insertionRes = insert((TypeKey) key, (TypeValue) (s));
                    Main.printMsg("-> New Semaphore: [" + key.toString() + "]=" + s.toString());
                    concurrentAccessOK = true;
                } catch (ConcurrentModificationException e) {
                    // just keep trying
                }
            }

            // Another thread was faster to create a hash value
            // So we did "new" in vain and must delete our value.
            // There is no "delete" in Java, but for other coding languages I leave this commented.
            /*if (insertionRes != s) {
                delete s;
            }*/

            if (insertionRes instanceof  Semaphore) {
                if (((Semaphore) insertionRes).availablePermits() > 1) {
                    Main.terminateNoGiveUp(1000, "Semaphores permits number more than 1!");
                }
            }
            return insertionRes;
        }
    }

    // for debugging
    public void printHash() {
        Main.printMsg("__________");
        for (Object key : keySet()) {
            Semaphore value = (Semaphore)get(key);
            Main.printMsg(key + " => " + value);
        }
        Main.printMsg("__________");
    }
}
