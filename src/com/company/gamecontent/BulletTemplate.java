package com.company.gamecontent;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import com.company.gamethread.ParameterizedMutexManager;

// TODO: Introduce global list of bullet models and disallow two different models with same "description"
class BulletTemplate {
    //private static Logger LOG = LogManager.getLogger(BulletTemplate.class.getName());

    final int damage;
    final int speed;
    final int caliber;
    final Color color; /* IDE_BUG: no warning about "package-private" if we set it public */
    //private final String description;

    BulletTemplate(
            int damage,
            int speed,
            int caliber,
            Color color/*,
            String description*/
    ) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        this.damage    = damage;
        this.speed     = speed;
        this.caliber   = caliber;
        this.color     = color;
        //this.description = description;
    }

}
