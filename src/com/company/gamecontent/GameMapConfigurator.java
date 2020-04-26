package com.company.gamecontent;

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

public abstract class GameMapConfigurator {

    private synchronized String [][] readMapFromConfig() {
        return null; // Will be implemented soon.
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
