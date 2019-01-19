package com.company;

// Singleton
public class V_Thread extends Main.ThreadPattern {

    private static V_Thread instance = new V_Thread("V-Thread");
    public static V_Thread getInstance() {
        return instance;
    }
    private V_Thread(String threadName) { super(threadName); }

    @Override
    public void repeat() throws InterruptedException {
        Main.printMsg(super.getName() + " is drawing.");
    }

}