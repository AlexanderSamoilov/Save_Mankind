/* ***************** *
 * S I N G L E T O N *
 * ***************** */
package com.company.gamethread;

import java.util.concurrent.Semaphore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// We must derive from a generic type to use a full power of getGenericSuperclass()
// (avoiding type erasure)
public class ParameterizedMutexManager extends MutexManager<String, Semaphore> {
    private static Logger LOG = LogManager.getLogger(MutexManager.class.getName());

    public static final ParameterizedMutexManager instance = new ParameterizedMutexManager();
    public static synchronized ParameterizedMutexManager getInstance() {
        return instance;
    }
    private ParameterizedMutexManager() {
        LOG.debug(getClass() + " singleton created.");
    }
}
