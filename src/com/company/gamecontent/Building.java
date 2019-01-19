package com.gamecontent;

import com.gamegraphics.Sprite;

import java.util.HashMap;

public class Building extends GameObject {
    private int energyConsumption;
    private int upgradeTime; // game tacts
    private int level;

    boolean isOutOfEnergy;

    public Building(int eC, int uT, Sprite spr, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource, Integer> ress, int hp, int spd, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        super(spr, x, y, z, sX, sY, sZ, ress, hp, 0, arm, hard, bch, ech, eco);

        // 2 - child class specific parameters validation
        if ((eC < 0) || (eC > Restrictions.getMaxEnergyConsumption()) ||
                (uT < 0) || (uT > Restrictions.getMaxInitialUpgradeTime())) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries.");
        }
        energyConsumption = eC;
        upgradeTime = uT;

        // 3 - default values
        level = 0;
        isOutOfEnergy = false;
    }

    public int upgrade() {
        if (level >= Restrictions.getMaxUpgradeLevel()) {
            return level; // return current level - impossible to upgrade more
        }
        level++;
        energyConsumption *= 1.25;
        upgradeTime *= 1.5;

        // TODO: sprite should be updated also
        super.res.put(Resource.MASS, (int)(super.res.get(Resource.MASS) * 1.25));
        super.res.put(Resource.ENERGY, (int)(super.res.get(Resource.ENERGY) * 1.25));
        super.hitPoints *= 1.25;
        super.speed *= 1.2;
        super.armor *= 1.15;
        super.hardness *= 1.15;
        super.burnChanceOnHit *= (1 - 0.15);
        super.explosionChanceOnBurn *= (1 - 0.15);
        super.explosionChanceOnHit *= (1 - 0.15);

        return level; // return new level - success
    }
}
