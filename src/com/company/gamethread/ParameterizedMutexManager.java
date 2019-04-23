package com.company.gamethread;

import java.util.concurrent.Semaphore;

// We must derive from a generic type to use a full power of getGenericSuperclass()
// (avoiding type erasure)
public class ParameterizedMutexManager extends MutexManager<String, Semaphore> {
    public static ParameterizedMutexManager instance = null;
    public static ParameterizedMutexManager getInstance() {
        if (instance == null) {
            instance = new ParameterizedMutexManager();
        }
        return instance;
    }
    private ParameterizedMutexManager() { // must be private, but otherwise I cannot inherit in Main.
        super();
    }
}
