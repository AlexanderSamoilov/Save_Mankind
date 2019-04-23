package com.company.gamethread;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

// Singleton
public class MutexManager <TypeKey, TypeValue> extends AbstractMutexManager <TypeKey, TypeValue> {
    private static Logger LOG = LogManager.getLogger(MutexManager.class.getName());

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
            LOG.trace(insert((TypeKey)key, (TypeValue)(s)).toString());
            LOG.trace(obtain((TypeKey)key).toString());
            TypeValue insertionRes = null;
            boolean concurrentAccessOK = false;
            // I suspect that it is possible that one thread executes half of "insert" operation
            // that is creates a new hash key but still does not catch to assign a value
            // If at the same time the second thread calls also "insert" then it will try
            // to return still not completely created value which is null, so we should wait a little bit
            // using the condition insertionRes == null until the first thread completes its "insert"
            int errCounter = 0;
            while (!concurrentAccessOK && insertionRes == null) {
                try {
                    insertionRes = insert((TypeKey) key, (TypeValue) (s));
                } catch (ConcurrentModificationException e) {
                    // just keep trying
                    continue;
                } catch (Exception e) {
                    errCounter ++;
                    if (errCounter > 100) {
                        Main.terminateNoGiveUp(1000, "Got exception too much times!");
                        // TODO: move to a func
                        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                            LOG.error(stackTraceElement.toString());
                        }
                    }
                    continue;
                }
                concurrentAccessOK = true;
            }

            // Another thread was faster to create a hash value
            // So we did "new" in vain and must delete our value.
            // There is no "delete" in Java, but for other coding languages I leave this commented.
            /*if (insertionRes != s) {
                delete s;
            }*/

            LOG.debug("-> New Semaphore: [" + key.toString() + "]=" + insertionRes.toString());

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
        LOG.debug("__________");
        for (Object key : keySet()) {
            Semaphore value = (Semaphore)get(key);
            LOG.debug(key + " => " + value);
        }
        LOG.debug("__________");
    }

    // quasi-unique thread string name (unique for all except "U" that means "other" or "unknown")
    public String getThreadType() {
        long EDT_Thread_ID = Main.getEDTId();
        // TODO: reactive this "if" after introduction of drawing routines which run before initMap() and initObjects()
        // (for example, drawing of the main game menu)
        // if (EDT_Thread_ID == -1) Main.terminateNoGiveUp(1000, "EDT thread ID must be defined first!");

        Long threadId = Thread.currentThread().getId(); // calculate threadId of the calling thread

        if      (threadId == Main.getThreadId())             { return "M"; }
        else if (threadId == D_Thread.getInstance().getId()) { return "D"; }
        else if (threadId == C_Thread.getInstance().getId()) { return "C"; }
        else if (threadId == V_Thread.getInstance().getId()) { return "V"; }
        else if (threadId == EDT_Thread_ID)                { return "EDT"; }
        else { return "U"; } // unknown
    }

    /* Check if the function is called in the legal thread */
    public void checkThreadPermission(HashSet<String> allowedThreadTypes) {
        /* We are using Java Swing which draws automatically in EDT and handles mouse buttons in EDT
           This is why we have a deviation from our math model which implies drawing in V-Thread and
           handling controllers in D-Thread. Due to this we make here some adaptations. If a function
           is supposed to be called in V-Thread/D-Thread we also allow EDT to call it.
        */
        if (allowedThreadTypes.contains("D") || allowedThreadTypes.contains("V")) {
            allowedThreadTypes.add("EDT");
        }

        String currentThreadType = getThreadType();
        boolean illegal = true;
        for (String allowedThreadType : allowedThreadTypes) {
            if (currentThreadType.equals(allowedThreadType)) {
                illegal = false;
                break;
            }
        }

        if (illegal) Main.terminateNoGiveUp(1000, "Illegal thread: " + currentThreadType + " (id: " + Thread.currentThread().getId() + ")");
    }

}
