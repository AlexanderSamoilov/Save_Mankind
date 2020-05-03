package com.company.gamecontent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import com.company.gamegraphics.Sprite;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;

import static com.company.gamethread.M_Thread.terminateNoGiveUp;

enum Nature {
    SAND,
    DIRT,
    FOREST,
    BUSH,
    WATER,
    HILL,
    MARSH,
    HOLE,
    PLATE,
}

public abstract class GameMapGenerator {

    private synchronized String [][] readMapFromConfig() {
        return null; // Will be implemented soon.
    }

    public static synchronized Vector3D_Integer readMapDimensionsFromConfig() {
        // TODO: read it from the game config. If not available - use default values.
        int width = Constants.MAX_X;
        int height = Constants.MAX_Y;
        int depth = Constants.MAX_Z; // MAX_Z because we don't support 3D so far

        // Validation of the map sizes.
        boolean width_ok = (width <= 0) || (width > Constants.MAX_X);
        boolean height_ok = (height <= 0) || (height > Constants.MAX_Y);
        boolean depth_ok = (depth <= 0) || (depth > Constants.MAX_Z);
        if (width_ok || height_ok || depth_ok) {
            terminateNoGiveUp(null,
                    1000,
                    GameMap.class +
                            " init error. width=" + width + ", height=" + height + ", depth=" + depth +
                            " - beyond the restricted boundaries."
            );
        }

        return new Vector3D_Integer(width, height, depth);
    }

    public static synchronized String [][] generateRandomMap(int x_size, int y_size) {
        String [][] terrain_map = new String[x_size][y_size];

        // Fill map with random numbers of textures (get it from the game config in the future)
        for (int x = 0; x < x_size; x++) {
            for (int y = 0; y < y_size; y++) {
                int natType = ((x * x + y * y) / 7) % 8;
                Nature nat = null;
                // 1 - Get enum key corresponding to the int number
                try {
                    nat = Nature.values()[natType];
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new EnumConstantNotPresentException(Nature.class, "Wrong nature type: " + natType);
                }
                // 2 - Get the LandscapeBlockTemplate corresponding to the enum key value string
                terrain_map[x][y] = nat.toString();
            }
        }

        return terrain_map;
    }

    // NOTE: So far store it here in the GameMapGenerator.
    // Probably we find better place in future.
    public static synchronized void generateRandomUnits() {
        // Initialising Player tank resources
        HashMap<Resource,Integer> testPlayerTankResources = new HashMap<Resource,Integer>();
        testPlayerTankResources.put(Resource.MASS, 500);
        testPlayerTankResources.put(Resource.ENERGY, 1000);

        // Initialising Player test tank Sprite
        Sprite testPlayerTankSprite = new Sprite("light_tank_1.png");

        // This is a test. Initialising tanks for Player.
        // We try calculate how much tanks we can place on map by width
//        int testNumTanks = GameMap.getInstance().getMaxX() / 2 - 1;
        int testNumTanks = 2;

        ArrayList<Unit> testPlayerUnits = new ArrayList<Unit>();
        for (int i=0; i <= testNumTanks - 1; i++) {
            testPlayerUnits.add(
                    new Unit(
                            new Weapon(150, 7,
                                    new BulletTemplate(10, 15, 7, Color.YELLOW, "Kurva_Smert-90")
                            ),
                            2,
                            testPlayerTankSprite,
                            new Point3D_Integer(4*i + 1, 1, 0),
                            new Vector3D_Integer(3, 3, 3),
                            testPlayerTankResources, 5000, 600, 10, 45,25, 15, 5, 5, 5
                    )
            );
        }

        // Initialising Enemy tank resources
        HashMap<Resource,Integer> testEnemyTankResources = new HashMap<Resource,Integer>();
        testEnemyTankResources.put(Resource.MASS, 500);
        testEnemyTankResources.put(Resource.ENERGY, 1000);

        // Initialising Enemy test tank Sprite
        Sprite testEnemyTankSprite = new Sprite("light_tank_2.png");



        // This is a test. Initialising tanks for Player.
        // We try calculate how much tanks we can place on map by width
//        int testEnemyTanks = GameMap.getInstance().getMaxX() / 2 - 1;
        int testEnemyTanks = 2;
        ArrayList<Unit> testEnemyUnits = new ArrayList<Unit>();
        for (int i=0; i <= testEnemyTanks - 1; i++) {
            testEnemyUnits.add(
                    new Unit(
                            new Weapon(150, 5,
                                    new BulletTemplate(30, 10, 8, Color.BLUE, "Dolbo-Banka-5M")
                            ),
                            3,
                            testEnemyTankSprite,
                            new Point3D_Integer(Constants.MAX_X - 2 - 2*i, Constants.MAX_DIM.y() - 2, 0),
                            new Vector3D_Integer(1, 1, 1),
                            testEnemyTankResources, 500, 7, 15, 45, 25, 15, 5, 5, 5
                    )
            );
        }

        // Initialising all Player resources
        HashMap<Resource,Integer> testPlayerResources = new HashMap<Resource,Integer>();
        testPlayerResources.put(Resource.MASS, 5000);
        testPlayerResources.put(Resource.ENERGY, 10000);

        // Initialising all Enemy resources
        HashMap<Resource,Integer> testEnemyResources = new HashMap<Resource,Integer>();
        testEnemyResources.put(Resource.MASS, 5000);
        testEnemyResources.put(Resource.ENERGY, 10000);

        // TODO Static creation
        // Initialising Player
        new Player(
                Race.RaceType.HUMANS,"Toughie", testPlayerResources, null, testPlayerUnits
        );

//        Player testPlayer = new Player(
//                Race.RaceType.HUMANS,"Toughie", testPlayerResources, null, testPlayerUnits
//        );

        // Initialising Enemy
        new Player(
                Race.RaceType.ROBOTS,"JavaBot", testEnemyResources, null, testEnemyUnits
        );

//        Player testEnemy = new Player(
//                Race.RaceType.ROBOTS,"JavaBot", testEnemyResources, null, testEnemyUnits
//        );
    }

    // Randomising landscapeBlocks
    /*
    public void changeNature() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));

        nature = Nature.values()[(nature.ordinal() + 1) % Nature.values().length];
        try {
            sprite.setImage(natSprite.get(nature));
        } catch (Exception e) {
            LOG.warn("Could not change sprite image.");
        }
    }*/
}
