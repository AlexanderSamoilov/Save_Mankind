package com.company;

// Singleton
public class C_Thread extends Main.ThreadPattern {

    private static C_Thread instance = new C_Thread("C-Thread");
    public static C_Thread getInstance() {
        return instance;
    }
    private C_Thread(String threadName) {
        super(threadName);
    }

    @Override
    public void repeat() throws InterruptedException {
        Main.printMsg(super.getName() + " is calculating.");
    }

}