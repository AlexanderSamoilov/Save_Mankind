package com.gamecontent;

import com.gamegraphics.Sprite;
import com.gamethread.Main;

import java.util.HashMap;

public class Building extends GameObject {
    private int energyConsumption;
    private int upgradeTime;
    private int level;

    boolean isOutOfEnergy;

    public Building(int energyConsumption, int upgradeTime, Sprite sprite, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource, Integer> res, int hp, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        super(sprite, x, y, z, sX, sY, sZ, res, hp, 0, arm, hard, bch, ech, eco);

        // 2 - child class specific parameters validation
        boolean valid = true;
        valid = valid && Main.in_range(
                0, energyConsumption, Restrictions.getMaxEnergyConsumption(), false
        );

        valid = valid && Main.in_range(
                0, upgradeTime, Restrictions.getMaxInitialUpgradeTime(), false
        );

        if (!valid) {
            Main.terminateNoGiveUp(
                    1000,
                    "Failed to initialize " + getClass() +
                               ". Some of parameters are beyond the restricted boundaries."
            );
        }

        this.energyConsumption = energyConsumption;
        this.upgradeTime = upgradeTime;

        // 3 - default values
        this.level = 0;
        this.isOutOfEnergy = false;
    }

    public int upgrade() {
        if (level >= Restrictions.getMaxUpgradeLevel()) {
            return level; // return current level - impossible to upgrade more
        }

        this.level++;

        // FIXME 1.25 -> SomeClass.CONSUMPTION_MODIFIER
        this.energyConsumption *= 1.25;

        // FIXME 1.5 -> SomeClass.UPGRADE_QUANTUM
        this.upgradeTime *= 1.5;

        // TODO: sprite should be updated also
        // FIXME 1.25 -> SomeClass.MASS_HARVEST_MODIFIER
        super.res.put(Resource.MASS, (int)(super.res.get(Resource.MASS) * 1.25));

        // FIXME 1.25 -> SomeClass.ENERGY_HARVEST_MODIFIER
        super.res.put(Resource.ENERGY, (int)(super.res.get(Resource.ENERGY) * 1.25));

        // FIXME 1.25 -> SomeClass.HP_MODIFIER
        super.hitPoints *= 1.25;

        // FIXME 1.25 -> SomeClass.SPEED_MODIFIER
        super.speed *= 1.2;
//        super.armor *= 1.15;
//        super.hardness *= 1.15;
//        super.burnChanceOnHit *= (1 - 0.15);
//        super.explosionChanceOnBurn *= (1 - 0.15);
//        super.explosionChanceOnHit *= (1 - 0.15);

        // return new level - success
        return level;
    }
}
