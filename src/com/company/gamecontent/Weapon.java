package com.company.gamecontent;

import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamethread.ParameterizedMutexManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

public class Weapon implements Renderable {
    private static Logger LOG = LogManager.getLogger(Weapon.class.getName());

    // in order to support "shooter" field of class Bullet (see the comment in the class Bullet)
    private Unit owner = null;

    private final int radius;
    private final int reload;
    private BulletModel bulletModel = null;
    private int reloadCounter = 0;

    public Weapon(int radius, int reload, BulletModel bulletModel) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M", "C")));

        this.radius  = radius;
        this.reload  = reload;
        this.bulletModel = bulletModel;

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
            Bullet b = new Bullet(owner, location, target, bulletModel);
            GameMap.getInstance().registerBullet(b);
        }

        this.reloadCounter++;
        this.reloadCounter = reloadCounter % reload;
    }
}
