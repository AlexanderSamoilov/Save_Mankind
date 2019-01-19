package com.gamethread;

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

//        Main.printMsg(super.getName() + " is managing the dashboard.");
        //Main.printMsg("x=" + Main.getMouseController().getX());
        //Main.printMsg("Main thread id for me: " + Main.getThreadId());
    }

}