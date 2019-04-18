package com.company.gamethread;

// Singleton
public class D_Thread extends Main.ThreadPattern {

    private static final D_Thread instance = new D_Thread("D-Thread");

    public static D_Thread getInstance() {
        return instance;
    }

    private D_Thread(String threadName) {
        super(threadName);
    }

    @Override
    public void repeat() throws InterruptedException {

//        LOG.debug(super.getName() + " is managing the dashboard.");
//        LOG.debug("x=" + Main.getMouseController().getx);
//        LOG.debug("Main thread id for me: " + Main.getThreadId());
    }

}