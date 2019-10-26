package com.company.gamecontent;

import com.company.gamegeom.Parallelepiped;
import com.company.gamegeom.cortegemath.point.Point2D_Integer;
import com.company.gamegeom.cortegemath.vector.Vector3D_Integer;
import com.company.gamegraphics.Sprite;
import com.company.gamethread.ParameterizedMutexManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

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

    // This constructor takes arbitrary block coordinates
    // (without respect to the block grid)
    public GameMapBlock(Point2D_Integer loc, int natType) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));

        parallelepiped = new Parallelepiped(loc.to3D(), new Vector3D_Integer(1, 1, 1));

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

    // This constructor takes block coordinates and block size
    // (creates the block aliquot to the grid vertices)
    public GameMapBlock(int grid_x, int grid_y, int natType) {
        this((new Point2D_Integer(grid_x, grid_y)).mult(BLOCK_SIZE), natType);
    }

    public Rectangle getRect() {
        return parallelepiped.getAbsBottomRect();
    }

    // wrapper method
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

        render(g, parallelepiped, 0);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g, Parallelepiped parallelepiped, double rotation_angle) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V", "D")));

        this.sprite.render(g, parallelepiped, rotation_angle);
    }

    /* TEST-01 */
    // Randomising landscapeBlocks
    public void changeNature() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));

        nature = Nature.values()[(nature.ordinal() + 1) % Nature.values().length];
        try {
            sprite.setImage(natSprite.get(nature));
        } catch (Exception e) {
            LOG.warn("Could not change sprite image.");
        }
    }
}
