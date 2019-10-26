package com.company.gamecontent;

import com.company.gamegeom.cortegemath.point.Point3D_Integer;
import com.company.gamethread.ParameterizedMutexManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

public class Weapon {
    private static Logger LOG = LogManager.getLogger(Weapon.class.getName());

    // in order to support "shooter" field of class Bullet (see the comment in the class Bullet)
    private Unit owner = null;

    private int damage        = 0;
    private int radius        = 0;
    private int speed         = 0;
    private int caliber       = 0;
    private int reload        = 0;
    private int reloadCounter = 0;

    public Weapon(int damage, int radius, int speed, int caliber, int reload) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        this.radius  = radius;
        this.damage  = damage;
        this.speed   = speed;
        this.caliber = caliber;
        this.reload  = reload;

        // default
        this.reloadCounter = 0;
    }

    // FIXME Setter() to Class.attr = val
    public void setOwner(Unit u) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        owner = u;
    }

    // FIXME Getter() to Class.attr
    public Unit getOwner() {
        return owner;
    }

    // FIXME Getter() to Class.attr
    public int getDamage() {
        return damage;
    }

    // FIXME Getter() to Class.attr
    public int getShootRadius() {
        return radius;
    }

    // TODO: not implemented yet
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

    }

    public void shoot(Point3D_Integer location, Point3D_Integer target) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        if (reloadCounter == 0) {
            Integer plId = owner.getPlayerId();
            LOG.debug("Player #(" + plId + ")" + Player.getPlayers()[plId] + ", unit #" + owner + " is shooting -> " + target);
            Bullet b = new Bullet(owner, location, target, damage, speed, caliber);
            GameMap.getInstance().registerBullet(b);
        }

        this.reloadCounter++;
        this.reloadCounter = reloadCounter % reload;
    }
}
