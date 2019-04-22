package com.company.gamecontent;

import com.company.gamegeom.Parallelepiped;
import com.company.gamegraphics.Sprite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.HashMap;

enum Nature {
    SAND,
    DIRT,
//    FOREST,
    BUSH,
    WATER,
    HILL,
    MARSH,
    HOLE,
    PLATE,
}

public class GameMapBlock implements Renderable {
    private static Logger LOG = LogManager.getLogger(GameMapBlock.class.getName());

    private Parallelepiped parallelepiped;

    private Nature       nature;
    private final Sprite sprite = new Sprite(null);

    private boolean      throughWalkable;
    private boolean      throughShootable;
    private boolean      onBuildable;

    private static HashMap<Nature,String> natSprite = new HashMap<Nature,String>();

    // Block Static natSprite initing with static content
    static {
        natSprite.put(Nature.SAND,   "sand_dark_stackable.png");
        natSprite.put(Nature.DIRT,   "dirt.png");
//        natSprite.put(Nature.FOREST, "forest.png");
        natSprite.put(Nature.BUSH,   "bush.png");
        natSprite.put(Nature.WATER,  "water_dirt.png");
        natSprite.put(Nature.HILL,   "hill_dirt.png");
        natSprite.put(Nature.MARSH,  "marsh_dirt_stackable.png");
        natSprite.put(Nature.HOLE,   "hole_dirt.png");
        natSprite.put(Nature.PLATE,  "plate.png");
    }

    public GameMapBlock(int x, int y, int natType) {
        parallelepiped = new Parallelepiped(x, y, 0, 1, 1, 1);

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
                onBuildable = false;
                nature = Nature.SAND;
                break;
            case DIRT:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.DIRT;
                break;
            case PLATE:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = true;
                nature = Nature.PLATE;
                break;

//            case FOREST:
//                throughWalkable = true;
//                throughShootable = true;
//                onBuildable = false;
//                nature = Nature.FOREST;
//                break;
            case BUSH:
                throughWalkable = true;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.BUSH;
                break;

            case WATER:
                throughWalkable = false;
                throughShootable = true;
                onBuildable = false;
                nature = Nature.WATER;
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

            case HILL:
                throughWalkable = false;
                throughShootable = false;
                onBuildable = false;
                nature = Nature.HILL;
                break;
            default:
                throw new EnumConstantNotPresentException(
                        Nature.class, "Wrong nature type: " + natType
                );
        }

        sprite.setImage(natSprite.get(nature));
    }

    public Rectangle getRect() {
        return parallelepiped.getAbsBottomRect();
    }

    // wrapper method
    public void render(Graphics g) {
        render(g, parallelepiped, 0);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g, Parallelepiped parallelepiped, double rotation_angle) {
        this.sprite.render(g, parallelepiped, rotation_angle);
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
            LOG.warn("Could not change sprite image.");
        }
    }
}
