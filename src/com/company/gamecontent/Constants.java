/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gamecontent;

import java.lang.Math;

import com.company.gamemath.cortegemath.vector.Vector3D_Integer;

public enum Constants {
    ;

    public static final int ROTATE_MOD = 1; // 0 - angle mode, not 0 - ray mode

    // Sprites of all object are oriented to north by default at the beginning of the game.
    // But since we perform all calculations in Cartesian coordinate system
    // we have to take into account that north is 90° (PI / 2) in Cartesian coordinates
    // and use this angle in the sprite rotation formulae.
    public static final double INIT_ANGLE = Math.toRadians(90);

    // The coordinate system in computer is not always Cartesian (because of different graphics libraries)
    // But we use all formulae for Cartesian coordinate system. In order to make these "classic" math formulae
    // working for non-Cartesian coordinate systems we introduce extra multiplier which transform classic formulae.
    // At the moment we use only one such multiplier (for Y axe orientation)
    // +1 for Cartesian coordinate system (orthonormal basis) - default case
    // -1 for typical game map (Y axe direction to south)
    public static final int Y_ORIENT = -1; // Y axe is oriented to south

    public static final int TIME_QUANTUM = 80;
    public static final int BLOCK_SIZE = 64;

    // 0 - multiple objects on the same block are allowed (even if they overlap)
    // 1 - multiple objects on the same block are allowed when they don't overlap
    // 2 - multiple objects on the same block are not allowed (even if they actually don't overlap)
    public static final int INTERSECTION_STRATEGY_SEVERITY = 1;

    public static final int MONITOR_MAX_X = BLOCK_SIZE * 10; // TODO: calculate it!
    public static final int MONITOR_MAX_Y = BLOCK_SIZE * 10; // TODO: calculate it!

    private static final int MAX_X = Math.min(MONITOR_MAX_X, MONITOR_MAX_Y) / BLOCK_SIZE;
    private static final int MAX_Y = Math.min(MONITOR_MAX_X, MONITOR_MAX_Y) / BLOCK_SIZE;
    private static final int MAX_Z = 16;
    public static final Vector3D_Integer MAX_DIM = new Vector3D_Integer(MAX_X, MAX_Y, MAX_Z);
    public static final Vector3D_Integer MAX_DIM_ABS = MAX_DIM.multClone(BLOCK_SIZE);

    public static final int MAX_MASS = 1000;
    public static final int MAX_ENERGY = 5000;
    public static final int MAX_ENERGY_CONSUMPTION = 100;

    public static final int MAX_HP = 1000000;
    // public static final int MAX_ARMOR = 90;
    // public static final int MAX_HARDNESS = 50;
    public static final int MAX_SPEED = 1000;
    public static final int MAX_PRE_MOVE_ANGLE = 90;

    // public static final int MAX_BURN_CHANCE_ON_HIT = 50;
    // public static final int MAX_EXPLOSION_CHANCE_ON_HIT = 10;
    // public static final int MAX_EXPLOSION_CHANCE_ON_BURN = 10;

    public static final int MAX_PLAYER_OBJECTS = 100;
    public static final int MAX_PLAYERS = 4;
    //public static final int MAX_UPGRADE_LEVEL = 3;
    public static final int MAX_INITIAL_UPGRADE_TIME = 100; // (in game time units)

    public static final int MAX_OBJECT_SIZE_BLOCKS = 3;
    // public static final int MAX_OBJECT_SIZE_PIXELS = Math.min(MAX_OBJECT_SIZE_BLOCKS, Math.min(MAX_X, MAX_Y));
    public static final int MAX_DETECT_RADIUS_ABS = Math.max(MAX_DIM_ABS.x(), MAX_DIM_ABS.y()) / 2;

    // Unit/Building upgrade modifiers
    /*
    public static final double UPGRADE_MODIFIER_CONSUMPTION    = 0.25;
    public static final double UPGRADE_MODIFIER_QUANTUM        = 0.5;
    public static final double UPGRADE_MODIFIER_MASS_HARVEST   = 0.25;
    public static final double UPGRADE_MODIFIER_ENERGY_HARVEST = 0.25;
    public static final double UPGRADE_MODIFIER_HP             = 0.25;
    public static final double UPGRADE_MODIFIER_SPEED          = 0.2;
    public static final double UPGRADE_MODIFIER_ARMOR          = 0.15;
    public static final double UPGRADE_MODIFIER_HARDNESS       = 0.15;
    public static final double UPGRADE_MODIFIER_BURN_CHANCE_ON_HIT       = 0.15;
    public static final double UPGRADE_MODIFIER_EXPLOSION_CHANCE_ON_HIT  = 0.15;
    public static final double UPGRADE_MODIFIER_EXPLOSION_CHANCE_ON_BURN = 0.15;
    */
}