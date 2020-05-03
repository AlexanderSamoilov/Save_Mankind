package com.company.gamecontent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamegraphics.Sprite;
import com.company.gamethread.ParameterizedMutexManager;

import static com.company.gametools.MathTools.in_range;
import static com.company.gamethread.M_Thread.terminateNoGiveUp;

public class Building extends GameObject {
    private static Logger LOG = LogManager.getLogger(Building.class.getName());

    private int energyConsumption;
    private int upgradeTime;
    private int level;

    boolean isOutOfEnergy;

    public Building(int energyConsumption, int upgradeTime, Sprite sprite, Point3D_Integer loc, Vector3D_Integer dim, HashMap<Resource, Integer> res, int hp, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        super(sprite, loc, dim, res, hp, 0, 0, 0, arm, hard, bch, ech, eco);
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        // 2 - child class specific parameters validation
        boolean valid = true;
        valid = valid && in_range(
                0, energyConsumption, Constants.MAX_ENERGY_CONSUMPTION, false
        );

        valid = valid && in_range(
                0, upgradeTime, Constants.MAX_INITIAL_UPGRADE_TIME, false
        );

        if (!valid) {
            terminateNoGiveUp(null,
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
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        if (level >= Constants.MAX_UPGRADE_LEVEL) {
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
