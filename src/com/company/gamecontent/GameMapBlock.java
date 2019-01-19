package com.gamecontent;

import com.gamegraphics.Sprite;
import com.gamethread.Main;

import javax.imageio.IIOException;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.HashMap;

enum Nature {
    SAND,
    FOREST,
    WATER,
    HILL,
    MARSH,
    HOLE
}

public class GameMapBlock {

    private int          loc_x;
    private int          loc_y;

    private Nature       nature;
    private final Sprite sprite = new Sprite(null);

    private boolean      throughWalkable;
    private boolean      throughShootable;
    private boolean      onBuildable;

    private static HashMap<Nature,String> natSprite = new HashMap<Nature,String>();

    // Block Static natSprite initing with static content
    static {
        natSprite.put(Nature.SAND, "sand.png");
        natSprite.put(Nature.FOREST, "forest.png");
        natSprite.put(Nature.WATER, "water.png");
        natSprite.put(Nature.HILL, "hill.png");
        natSprite.put(Nature.MARSH, "marsh.png");
        natSprite.put(Nature.HOLE, "hole.png");
    }

    public GameMapBlock(int x, int y, int natType) {
        loc_x = x;
        loc_y = y;

        Nature nat;
        try {
            nat = Nature.values()[natType];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new EnumConstantNotPresentException(
                    Nature.class, "Wrong nature type: " + natType
            );
        }

        switch(nat) {
            case SAND:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = true;
                nature = Nature.SAND;
                break;
            case FOREST:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.FOREST;
                break;
            case WATER:
                throughWalkable = false;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.WATER;
                break;
            case HILL:
                throughWalkable = false;
                throughShootable = false;
                onBuildable = false;
                nature = Nature.HILL;
                break;
            case HOLE:
                throughWalkable = false;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.HOLE;
                break;
            case MARSH:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.MARSH;
                break;
            default:
                throw new EnumConstantNotPresentException(
                        Nature.class, "Wrong nature type: " + natType
                );
        }

        sprite.setImage(natSprite.get(nature));
    }

    // TODO Remove "occupied"
    public void render(Graphics g, boolean occupied) {
        if (occupied) {
            return;
        }

        this.sprite.render(
                g,
                loc_x * Restrictions.BLOCK_SIZE,
                loc_y * Restrictions.BLOCK_SIZE,
                Restrictions.BLOCK_SIZE,
                Restrictions.BLOCK_SIZE
        );
    }

    // TODO Remove getters. Use Class.attr
    public Nature getNature() {
        return nature;
    }

    // TODO Remove getters. Use Class.attr
    public Sprite getSprite() {
        return sprite;
    }

    // TODO Remove getters. Use Class.attr
    public boolean isThroughWalkable() {
        return throughWalkable;
    }

    // TODO Remove getters. Use Class.attr
    public boolean isThroughShootable() {
        return throughShootable;
    }

    // TODO Remove getters. Use Class.attr
    public boolean isOnBuildable() {
        return onBuildable;
    }

    /* TEST-01 */
    // Randomising landscapeBlocks
    public void changeNature() {
        nature = Nature.values()[(nature.ordinal() + 1) % Nature.values().length];
        try {
            sprite.setImage(natSprite.get(nature));
        } catch (Exception e) {
            Main.printMsg("Warning: could not change sprite image.");
        }
    }
}
