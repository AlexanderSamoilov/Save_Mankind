package com.company.gamecontent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamegraphics.Sprite;
import com.company.gamethread.ParameterizedMutexManager;

import static com.company.gametools.MathTools.in_range;
import static com.company.gamethread.M_Thread.terminateNoGiveUp;
import static com.company.gamecontent.Constants.MAX_ENERGY_CONSUMPTION;
import static com.company.gamecontent.Constants.MAX_INITIAL_UPGRADE_TIME;
//import static com.company.gamecontent.Constants.MAX_UPGRADE_LEVEL;

public class Building extends GameObject {
    //private static Logger LOG = LogManager.getLogger(Building.class.getName());

    //private int energyConsumption;
    //private int upgradeTime;
    //private int level;
    //private boolean isOutOfEnergy;

    public Building(
            int energyConsumption,
            int upgradeTime,
            Sprite sprite,
            Point3D_Integer loc,
            Vector3D_Integer dim,
            HashMap<Resource, Integer> res,
            int hp
            //int arm,
            //int hard,
            //int bch,
            //int ech,
            //int eco
    ) {
        // 1 - parent class specific parameters
        super(sprite, loc, dim, res, hp, 0, 0, 0/*, arm, hard, bch, ech, eco*/);
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        // 2 - child class specific parameters validation
        boolean valid = in_range(
                0, energyConsumption, MAX_ENERGY_CONSUMPTION, false
        );

        valid = valid && in_range(
                0, upgradeTime, MAX_INITIAL_UPGRADE_TIME, false
        );

        if (!valid) {
            terminateNoGiveUp(null,
                    1000,
                    "Failed to initialize " + getClass() +
                               ". Some of parameters are beyond the restricted boundaries."
            );
        }

        //this.energyConsumption = energyConsumption;
        //this.upgradeTime = upgradeTime;

        // 3 - default values
        //this.level = 0;
        //this.isOutOfEnergy = false;
    }

    /*
    public int upgrade() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        if (level >= MAX_UPGRADE_LEVEL) {
            return level; // return current level - impossible to upgrade more
        }

        this.level++;

        // TODO: sprite should be updated also
        this.energyConsumption *= 1 + Constants.UPGRADE_MODIFIER_CONSUMPTION;
        this.upgradeTime *= 1 + Constants.UPGRADE_MODIFIER_QUANTUM;
        super.res.put(Resource.MASS, (int)(super.res.get(Resource.MASS) * (1 + Constants.UPGRADE_MODIFIER_MASS_HARVEST)));
        super.res.put(Resource.ENERGY, (int)(super.res.get(Resource.ENERGY) * (1 + Constants.UPGRADE_MODIFIER_ENERGY_HARVEST)));
        super.hitPoints *= 1 + Constants.UPGRADE_MODIFIER_HP;
        super.speed *= 1 + Constants.UPGRADE_MODIFIER_SPEED;
        super.armor *= 1 + Constants.UPGRADE_MODIFIER_ARMOR;
        super.hardness *= 1 + Constants.UPGRADE_MODIFIER_HARDNESS;
        super.burnChanceOnHit *= (1 - Constants.UPGRADE_MODIFIER_BURN_CHANCE_ON_HIT);
        super.explosionChanceOnBurn *= (1 - Constants.UPGRADE_MODIFIER_EXPLOSION_CHANCE_ON_HIT);
        super.explosionChanceOnHit *= (1 - Constants.UPGRADE_MODIFIER_EXPLOSION_CHANCE_ON_BURN);

        return level;
    }*/
}
