package com.company;

import java.util.concurrent.Semaphore;

public class Main {

    public static class ParameterizedMutexManager extends MutexManager<String,Semaphore> {
        public static ParameterizedMutexManager instance = null;
        public static ParameterizedMutexManager getInstance() {
            if (instance == null) return new ParameterizedMutexManager();
            return instance;
        }
        public ParameterizedMutexManager() { // must be private, but otherwise I cannot inherit in Main.
            super();
        }
    } // we must derive from a generic type to use a full power of getGenericSuperclass() (avoiding type erasure)

    public static void printMsg(String msg) {
        System.out.println("[" + Thread.currentThread().getId() + "] " + msg);
    }

    public static void timeout(long timeoutMSec) {
        /* TODO: what happens if we pass negative timeout into sleep()? */
        try {
            Thread.sleep(timeoutMSec);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // This is not a big trouble that we were not able to do sleep, so it is not a reason to interrupt the method on this
        }
    }

    // "Pattern" class for creation of singletones
    public static class ThreadPattern extends Thread {
        protected final String name;
        protected boolean SIGNAL_TERM_FROM_PARENT;

        private static ThreadPattern instance = null;
        public static ThreadPattern getInstance() {
            return instance;
        }
        protected ThreadPattern(String threadName) {
            name = threadName;
            SIGNAL_TERM_FROM_PARENT = false;
        }

        @Override @Deprecated
        public void start() {
            throw new UnsupportedOperationException("void start() method with no arguments is forbidden. Use start(int, long) instead.");
            // interrupt(); - is it possible that the method start() of the base class anyway started the run() at this moment?
        }

        public boolean start(int attempts, long timeoutMSec)
        {
            boolean rc = true;
            StackTraceElement [] ste = null;
            while (rc) {
                try {
                    printMsg("Thread " + super.getId() + " is starting");
                    super.start();
                    printMsg("Thread " + super.getId() + " started.");
                    rc = false;
                } catch (Exception e) {
                    ste = e.getStackTrace();
                    timeout(timeoutMSec);
                }
            }
            if (rc) {
                printMsg("Thread " +super.getId() + " failed to start after " + attempts + " attempts.");
                printMsg(ste.toString());
            }
            return rc;
        }

        @Override
        @Deprecated // Derived classes should use repeat() instead.
        public void run()
        {
            try
            {
                printMsg("Thread " + super.getId() + " is running");
                while (!interrupted() && !SIGNAL_TERM_FROM_PARENT) {
                    repeat(); // All the functionality is incapsulated here.
                    timeout(1000);
                }
                printMsg("Thread " + super.getId() + " finished.");
            }
            catch (InterruptedException e)
            {
                printMsg("Thread " + super.getId() + " died!");
                e.printStackTrace();
            }
            //super.run(); // Thread.run() is empty, so we don't need it.
        }

        public void repeat() throws InterruptedException {
            // Pure functionality without error handling
            // Is to be used by derived classes
        }

        // TODO: Should we release all mutexes held by a thread before terminating?
        // TODO: What if we call this function when the thread is held by another thread?
        public boolean terminate(long timeoutMsec) throws InterruptedException {
            SIGNAL_TERM_FROM_PARENT = true;
            timeout(timeoutMsec); // wait and hope that the thread finish its work normally

            // still did not exit - it's a pity...
            if (isAlive()) {
                /* Hard termination (See https://stackoverflow.com/questions/671049/how-do-you-kill-a-thread-in-java). */
                interrupt();
                super.interrupt(); // TODO: check of we need this
            } else {
                return false;
            }

            if (isAlive()) { // failed to interrupt
                return true;
            }
            return true;
        }
    } // end of class ThreadPattern

    //public class ThreadPatternImpl extends ThreadPattern { }

    private static enum Choice {
        CHOICE_EXIT_IMMEDIATELY,
        CHOICE_EXIT,
        CHOICE_PAUSE,
        CHOICE_RESUME,
        CHOICE_CANCEL
    }

    // public Main();
    static boolean SIGNAL_TERM_GENERAL = false;
    private static boolean SIGNAL_SUSPEND = true;
    /*
    This should be in the main thread and not in D-Thread, otherwise we will have to synchronize D-Thread
    with the main thread on exiting moment that is complicated (what if before we suspend D-Thread it also does some exiting stuff?
    will not it lead to double exiting with unexpected consequences?)
    */

    private static void destroy() {
        // Here we implement releasing of allocated memory for all objects.
    }

    private static boolean terminate(long timeoutMsec) {
        boolean rc = true;
        try {
            C_Thread.getInstance().terminate(timeoutMsec);
            V_Thread.getInstance().terminate(timeoutMsec);
            D_Thread.getInstance().terminate(timeoutMsec);
            destroy(); // delete all objects
            SIGNAL_TERM_GENERAL = true;
            rc = false;
        } catch (Exception e) {
            Main.printMsg(e.getStackTrace().toString());
        }
        return rc;
    }

    public static void terminateNoGiveUp(long timeoutMsec) {
        ////ErrWindow ew = displayErrorWindow("The game was interrupted due to exception. Exiting...(if this window does not disappear for a long time, kill the game process manually from OS.)");
        while (terminate(timeoutMsec)) {
            timeout(10); // just for safety we set here some small timeout unconfigurable to avoid brutal rush of terminate() requests
        }
        ////ew.close();
    }

    private boolean suspend() {
        if (!SIGNAL_SUSPEND) { // to avoid multiple suspend (or mutex relock)
            // TODO: Exception handling, return code
            try {
                ((Semaphore) ParameterizedMutexManager.getInstance().getMutex("C", "Esc")).acquire(); // does not require try/catch rather than Thread.suspend()
            } catch (InterruptedException e) {
                printMsg("Caught InterruptedException when was trying to suspend C-thread!");
                return true;
            }
            try {
                ((Semaphore) ParameterizedMutexManager.getInstance().getMutex("V", "Esc")).acquire(); // does not require try/catch rather than Thread.suspend()
            } catch (InterruptedException e) {
                printMsg("Caught InterruptedException when was trying to suspend V-thread!");
                return true;
            }
            try {
                ((Semaphore) ParameterizedMutexManager.getInstance().getMutex("D", "Esc")).acquire(); // does not require try/catch rather than Thread.suspend()
            } catch (InterruptedException e) {
                printMsg("Caught InterruptedException when was trying to suspend D-thread!");
                return true;
            }

            SIGNAL_SUSPEND = true;
            return false;
        }
        return true;
    }

    private boolean resume() {
        if (SIGNAL_SUSPEND) { // to avoid multiple resume
            // TODO: Exception handling, return code
            try {
                ((Semaphore)ParameterizedMutexManager.getInstance().getMutex("C", "Esc")).release(); // does not require try/catch rather than Thread.resume()
            } catch (InterruptedException e) {
                printMsg("Caught InterruptedException when was trying to resume C-Thread.");
                return true;
            }
            try {
                ((Semaphore)ParameterizedMutexManager.getInstance().getMutex("V", "Esc")).release(); // does not require try/catch rather than Thread.resume()
            } catch (InterruptedException e) {
                printMsg("Caught InterruptedException when was trying to resume V-Thread.");
                return true;
            }
            try {
                ((Semaphore)ParameterizedMutexManager.getInstance().getMutex("D", "Esc")).release(); // does not require try/catch rather than Thread.resume()
            } catch (InterruptedException e) {
                printMsg("Caught InterruptedException when was trying to resume D-Thread.");
                return true;
            }
            SIGNAL_SUSPEND = false;
            return false;
        }
        return true;
    }

    // This function handles ESC key press (it runs in a special unnamed thread automatically by Java mechanisms)
    // I think that this must be handled in the main thread, because it is the highest priority action - game state managing.
    public void escKeyHandling() {
        suspend();
        Main.Choice choice = Choice.CHOICE_EXIT; //// = handlePauseWindow(); // The window with 4 buttons: Exit Immediately, Exit, Pause, Cancel

        if (choice == Choice.CHOICE_EXIT_IMMEDIATELY) {
            terminate(0);
        } else if (choice == Choice.CHOICE_EXIT) {
            terminate(1000);
        } else if (choice == Choice.CHOICE_PAUSE) {
            // Just keep being suspended...
        } else if (choice == Choice.CHOICE_RESUME) {
            resume();
        } else if (choice == Choice.CHOICE_CANCEL) {
            // Do nothing
        } else {
            // TODO: Display a new window
            //ErrWindow ew = displayErrorWindow("No such choice: " + choice.toString() + ". Exiting...(if this window does not disappear for a long time, kill the game process manually from OS.)");
            terminateNoGiveUp(1000);
            //ew.close();
        }
    }

    // Main thread of the game. Starts other game thread in the correct order. Exist if and only if all other threads exited.
    public static void main(String[] args) throws  InterruptedException {
        printMsg("The game starts.");
        // 1. Initialize units of all players.
        // 2. Initialize and start D-Thread.
        if (D_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000);
        }
        // 3. Initialize and start V-Thread.
        if (V_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000);
        }

        // wait for D-Thread to get ready (at the same time D-Thread is waiting for V-Thread to get ready)
        ((Semaphore) ParameterizedMutexManager.getInstance().getMutex("D", "getReady")).acquire();
        // wait for V-Thread to get ready
        ((Semaphore) ParameterizedMutexManager.getInstance().getMutex("V", "getReady")).acquire();

        // 4. Initialize and start C-Thread.

        if (C_Thread.getInstance().start(100, 10)) {
            terminateNoGiveUp(1000);
        }

        int deadStatus = 0;
        while(!SIGNAL_TERM_GENERAL) { // Endless loop of the main thread. Here we just catch signals and do nothing more.
            timeout(1000);
            // NOTE: I have an idea of some intelligent implementation - to respawn the thread if it dies due to really unexpected events, like
            // Java internal bug or an external influence (for example, if OS killed the thread)
            // I did not implement it, because at the moment I don't know how is it possible to notify the parent thread
            // immediately when the child thread dies. Probably, it is even not possible.
            // Tracking of the threads state in a loop with isAlive() is NOT safe, because between two loop iterations
            // some small time passes, so there is very low chance that during this time another threads try to get access
            // to mutexes associated with the dead thread.
            // I left the investigation of this question for the future, but oin case I find the solution
            // I leave here the list of actions which must be done if some thread is not alive and we want to respawn it:
            //  - block all threads
            //  - clear cache for dead thread AND think about what to do with still helded locks associated with dead thread
            //  - respawn dead thread
            //  - update cache for it taking into account new threadID?
            // Here is something to think about: https://stackoverflow.com/questions/12521776/what-happens-to-the-lock-when-thread-crashes-inside-a-synchronized-block.
            // I suppose it is possible to implement it, but to be very sure it is better to add also one more check in the loop
            // which checks if some of thread is in the waiting state very long (more than a given timeout seconds)
            // and exit the game or kill/respawn the thread in this case, BUT with the obligatory logging message about which lock
            // and from which other thread the given thread waited and could not acquire.
            //}

            if (!D_Thread.getInstance().isAlive()) {
                deadStatus++;
            }
            if (!V_Thread.getInstance().isAlive()) {
                deadStatus++;
            }
            if (!C_Thread.getInstance().isAlive()) {
                deadStatus++;
            }

            if (deadStatus > 0) {
                printMsg(deadStatus + " of game threads were unexpectedly terminated. To ensure the correct game flow we must exit. Please, restart the game.");
                break;
            }
        }

        if (deadStatus == 0) { // If no one thread died then we wait for all threads to exit normally {
            // End of the game (wrap up actions)
            // We expect not just InterruptedException, but general Exception, because MutexManager can throw many times of exception from inside *_Thread
            try {
                C_Thread.getInstance().join(); // wait the C-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                try {
                    C_Thread.getInstance().terminate(1000);
                } catch (Exception eInner) {
                    eInner.printStackTrace();
                }
            }

            try {
                V_Thread.getInstance().join(); // wait the V-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                try {
                    V_Thread.getInstance().terminate(1000);
                } catch (Exception eInner) {
                    eInner.printStackTrace();
                }
            }

            try {
                D_Thread.getInstance().join(); // wait the D-thread to finish
            } catch (Exception eOuter) {
                eOuter.printStackTrace();
                try {
                    D_Thread.getInstance().terminate(1000);
                } catch (Exception eInner) {
                    eInner.printStackTrace();
                }
            }
        }

        // For sure
        terminateNoGiveUp(1000);
        printMsg("The game exited.");
        System.exit(deadStatus);
    }
}
