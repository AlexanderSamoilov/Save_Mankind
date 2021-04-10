package com.company.gamecontent;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamethread.ParameterizedMutexManager;

public class Weapon implements Renderable {
    private static Logger LOG = LogManager.getLogger(Weapon.class.getName());

    // properties
    // in order to support "shooter" field of class Bullet (see the comment in the class Bullet)
    Unit owner = null; // WRITABLE

    final int radius;
    private final int reload;
    private final BulletTemplate bulletTemplate;
    private int reloadCounter;

    Weapon(int radius, int reload, BulletTemplate bulletTemplate) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        this.radius  = radius;
        this.reload  = reload;
        this.bulletTemplate = bulletTemplate;

        // default
        this.reloadCounter = 0;
    }

    // FIXME Setter() to Class.attr = val
    void setOwner(Unit u) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        owner = u;
    }

    // TODO: not implemented yet
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("V"))); // Arrays.asList("V")

    }

    void orderShoot(Point3D_Integer location, Point3D_Integer target) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        // Loading ...
        this.reloadCounter++;
        this.reloadCounter = this.reloadCounter % reload;

        // Not yet loaded ...
        if (this.reloadCounter != 0) {
            return;
        }

        // Bang!
        shoot(location, target);
    }

    void shoot(Point3D_Integer location, Point3D_Integer target) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        Player pl = this.owner.owner;
        LOG.debug("Player #" + pl.id + ", unit #" + owner + " is shooting -> " + target);
        Bullet b = new Bullet(this.owner, location, target, bulletTemplate);
        GameMap.getInstance().registerBullet(b);
    }
}
