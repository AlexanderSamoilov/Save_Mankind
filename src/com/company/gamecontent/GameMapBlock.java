package com.gamecontent;

import com.gamegraphics.Sprite;
import com.gamethread.Main;

import javax.imageio.IIOException;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.HashMap;

enum Nature {
    NATURE_SAND,
    NATURE_FOREST,
    NATURE_WATER,
    NATURE_HILL,
    NATURE_MARSH,
    NATURE_HOLE
}

public class GameMapBlock {

    private static HashMap<Nature,String> natSprite = new HashMap<Nature,String>();
    static {
        natSprite.put(Nature.NATURE_SAND, "sand.png");
        natSprite.put(Nature.NATURE_FOREST, "forest.png");
        natSprite.put(Nature.NATURE_WATER, "water.png");
        natSprite.put(Nature.NATURE_HILL, "hill.png");
        natSprite.put(Nature.NATURE_MARSH, "marsh.png");
        natSprite.put(Nature.NATURE_HOLE, "hole.png");
    }
    private int loc[];
    private Nature nature;
    private final Sprite sprite = new Sprite(null);
    private boolean throughWalkable;
    private boolean throughShootable;
    private boolean onBuildable;

    public Nature getNature() {return nature;}
    public Sprite getSprite() {return sprite;}
    public boolean isThroughWalkable() {return throughWalkable;}
    public boolean isThroughShootable() {return throughShootable;}
    public boolean isOnBuildable() {return onBuildable;}

    public GameMapBlock(int x, int y, int natType) throws IIOException, IOException {

        loc = new int[]{x,y};

        Nature nat = null;
        try {
            nat = Nature.values()[natType];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new EnumConstantNotPresentException(Nature.class, "Wrong nature type: " + natType);
        }

        switch(nat) {
            case NATURE_SAND:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = true;
                nature = Nature.NATURE_SAND;
                sprite.setImage(natSprite.get(nature));
                break;
            case NATURE_FOREST:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.NATURE_FOREST;
                sprite.setImage(natSprite.get(nature));
                break;
            case NATURE_WATER:
                throughWalkable = false;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.NATURE_WATER;
                sprite.setImage(natSprite.get(nature));
                break;
            case NATURE_HILL:
                throughWalkable = false;
                throughShootable = false;
                onBuildable = false;
                nature = Nature.NATURE_HILL;
                sprite.setImage(natSprite.get(nature));
                break;
            case NATURE_HOLE:
                throughWalkable = false;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.NATURE_HOLE;
                sprite.setImage(natSprite.get(nature));
                break;
            case NATURE_MARSH:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.NATURE_MARSH;
                sprite.setImage(natSprite.get(nature));
                break;
            default:
                throw new EnumConstantNotPresentException(Nature.class, "Wrong nature type: " + natType);
        }
    }

    public void render(Graphics g, boolean occupied) {
        /* -------------------------- Picture drawing ------------------------------------------- */
        sprite.render(g, loc[0] * Restrictions.getBlockSize(), loc[1] * Restrictions.getBlockSize(), Restrictions.getBlockSize(), Restrictions.getBlockSize());
        // for debugging
        if (occupied) {
            g.setColor(Color.BLACK);
            g.fillRect(loc[0] * Restrictions.getBlockSize(), loc[1] * Restrictions.getBlockSize(), Restrictions.getBlockSize(), Restrictions.getBlockSize());
        }
    }

    // test
    public void changeNature() {
        nature = Nature.values()[(nature.ordinal() + 1) % Nature.values().length];
        try {
            sprite.setImage(natSprite.get(nature));
        } catch (Exception e) {
            Main.printMsg("Warning: could not change sprite image.");
        }
    }
}
