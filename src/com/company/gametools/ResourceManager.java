package com.gametools;

/**
 * Resource Manager will rule of resources in the game
 * */

public abstract class ResourceManager {
    protected int ref_count = 1;

    public void addReference() {
        ref_count++;
    }

    public boolean removeReference() {
        ref_count--;
        return ref_count == 0;
    }
}
