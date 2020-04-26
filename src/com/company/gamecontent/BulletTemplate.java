package com.company.gamecontent;

import com.company.gamethread.ParameterizedMutexManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

// TODO: Introduce global list of bullet models and disallow two different models with same "description"
public class BulletTemplate {
    private static Logger LOG = LogManager.getLogger(BulletTemplate.class.getName());

    public final int damage;
    public final int speed;
    public final int caliber;
    public final Color color;
    public final String description;

    public BulletTemplate(int damage, int speed, int caliber, Color color, String description) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        this.damage    = damage;
        this.speed     = speed;
        this.caliber   = caliber;
        this.color     = color;
        this.description = description;
    }

}
