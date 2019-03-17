package com.company.gamecontent;

import java.lang.Math;

public abstract class Restrictions {

    static final int ROTATE_MOD = 1; // 0 - angle mode, not 0 - ray mode

    // Sprites of all object are oriented to north by default at the beginning of the game.
    // But since we perform all calculations in Cartesian coordinate system
    // we have to take into account that north is 90° (PI / 2) in Cartesian coordinates
    // and use this angle in the sprite rotation formulae.
    static final double INIT_ANGLE = Math.toRadians(90);

    // The coordinate system in computer is not always Cartesian (because of different graphics libraries)
    // But we use all formulae for Cartesian coordinate system. In order to make these "classic" math formulae
    // working for non-Cartesian coordinate systems we introduce extra multiplier which transform classic formulae.
    // At the moment we use only one such multiplier (for Y axe orientation)
    // +1 for Cartesian coordinate system (orthonormal basis) - default case
    // -1 for typical game map (Y axe direction to south)
    static final int Y_ORIENT = -1; // Y axe is oriented to south

    static final int BLOCK_SIZE = 64;

    // 0 - multiple objects on the same block are allowed (even if they overlap)
    // 1 - multiple objects on the same block are allowed when they don't overlap
    // 2 - multiple objects on the same block are not allowed (even if they actually don't overlap)
    static final int INTERSECTION_STRATEGY_SEVERITY = 1;

    private static final int MONITOR_MAX_X = BLOCK_SIZE * 10; // TODO: calculate it!
    private static final int MONITOR_MAX_Y = BLOCK_SIZE * 10; // TODO: calculate it!

    public static final int MAX_X = Math.min(MONITOR_MAX_X, MONITOR_MAX_Y) / BLOCK_SIZE;
    public static final int MAX_Y = Math.min(MONITOR_MAX_X, MONITOR_MAX_Y) / BLOCK_SIZE;
    static final int MAX_Z = 16;

    private static final int MAX_OBJECT_SIZE_BLOCKS = 3;

    static final int MAX_MASS = 1000;
    static final int MAX_ENERGY = 5000;
    private static final int MAX_ENERGY_CONSUMPTION = 100;

    static final int MAX_HP = 1000000;
    private static final int MAX_ARMOR = 90;
    private static final int MAX_HARDNESS = 50;
    static final int MAX_SPEED = 10;
    static final int MAX_PRE_MOVE_ANGLE = 90;

    private static final int MAX_BURN_CHANCE_ON_HIT = 50;
    private static final int MAX_EXPLOSION_CHANCE_ON_HIT = 10;
    private static final int MAX_EXPLOSION_CHANCE_ON_BURN = 10;

    private static final int MAX_PLAYER_OBJECTS = 100;
    private static final int MAX_PLAYERS = 4;
    private static final int MAX_UPGRADE_LEVEL = 3;
    private static final int MAX_INITIAL_UPGRADE_TIME = 100; // game tacts

    // FIXME Killall getters, use constants
    public static int getBlockSize() { return BLOCK_SIZE; }
    public static int getMaxX() { return MAX_X; }
    public static int getMaxY() { return MAX_Y; }
    public static int getMaxZ() { return MAX_Z; }
    public static int getMaxXAbs() { return MAX_X*BLOCK_SIZE; }
    public static int getMaxYAbs() { return MAX_Y*BLOCK_SIZE; }
    public static int getMaxZAbs() { return MAX_Z*BLOCK_SIZE; }
    public static int getMaxObjectSizeBlocks() {
        return Math.min(MAX_OBJECT_SIZE_BLOCKS, Math.min(MAX_X, MAX_Y));
    }
    public static int getMaxMass() { return MAX_MASS; }
    public static int getMaxEnergy() { return MAX_ENERGY; }
    public static int getMaxHp() { return MAX_HP; }
    public static int getMaxArmor() { return MAX_ARMOR; }
    public static int getMaxHardness() { return MAX_HARDNESS; }
    public static int getMaxSpeed() { return MAX_SPEED; }
    public static int getMaxPlayerObjects() { return MAX_PLAYER_OBJECTS; }
    public static int getMaxPlayers() { return MAX_PLAYERS; }
    public static int getMaxUpgradeLevel() { return MAX_UPGRADE_LEVEL; }
    public static int getMaxBCH() { return MAX_BURN_CHANCE_ON_HIT; }
    public static int getMaxECH() { return MAX_EXPLOSION_CHANCE_ON_HIT; }
    public static int getMaxECO() { return MAX_EXPLOSION_CHANCE_ON_BURN; }
    public static int getMaxDetectRadiusAbs() { return Math.max(getMaxXAbs(), getMaxYAbs()) / 2; }
    public static int getMaxEnergyConsumption() { return MAX_ENERGY_CONSUMPTION; }
    public static int getMaxInitialUpgradeTime() { return MAX_INITIAL_UPGRADE_TIME;}
    public static int getIntersectionStrategySeverity() { return INTERSECTION_STRATEGY_SEVERITY; }
    /*
    // Singleton stuff
    public static Restrictions instance = null;
    public static Restrictions getInstance() {
        if (instance == null) return new Restrictions();
        return instance;
    }
    private Restrictions() { // must be private, but otherwise I cannot inherit in Main.
        super();
    }*/
}
