package com.gamecontent;

import com.gamethread.Main;

import java.awt.*;

public class Weapon {
    // in order to support "shooter" field of class Bullet (see the comment in the class Bullet)
    private Unit owner = null;

    private int damage        = 0;
    private int radius        = 0;
    private int speed         = 0;
    private int caliber       = 0;
    private int reload        = 0;
    private int reloadCounter = 0;

    public Weapon(int damage, int radius, int speed, int caliber, int reload) {
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
    }

    public void shoot(Integer[] location, Integer[] target) {
        if (reloadCounter == 0) {
            //Main.printMsg("Player #(" + owner.getPlayerId() + ")" + Player.getPlayers()[owner.getPlayerId()] + ", unit #" + owner + " is shooting -> " + target);
            Bullet b = new Bullet(owner, location, target, damage, speed, caliber);
            GameMap.getInstance().registerBullet(b);
        }

        this.reloadCounter++;
        this.reloadCounter = reloadCounter % reload;
    }
}
