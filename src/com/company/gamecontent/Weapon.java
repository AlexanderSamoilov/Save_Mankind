package com.gamecontent;

import com.gamethread.Main;

import java.awt.*;

public class Weapon {
    private Unit owner = null; // in order to support "shooter" field of class Bullet (see the comment in the class Bullet)
    private int damage = 0;
    private int radius = 0;
    private int speed = 0;
    private int caliber = 0;
    private int reload = 0;
    private int reloadCounter = 0;

    public Weapon(int dmg, int rad, int spd, int calib, int rel) {
        radius = rad;
        damage = dmg;
        speed = spd;
        caliber = calib;
        reload = rel;

        // default
        reloadCounter = 0;
    }

    public void setOwner(Unit u) {
        owner = u;
    }

    public Unit getOwner() {
        return owner;
    }

    public int getDamage() {
        return damage;
    }

    public int getShootRadius() {
        return radius;
    }

    public void render(Graphics g) {
        // TODO: not implemented yet
    }

    public void shoot(Integer[] location, Integer[] target) {
        if (reloadCounter == 0) {
            //Main.printMsg("Player #(" + owner.getPlayerId() + ")" + Player.getPlayers()[owner.getPlayerId()] + ", unit #" + owner + " is shooting -> " + target);
            Bullet b = new Bullet(owner, location, target, damage, speed, caliber);
            GameMap.getInstance().registerBullet(b);
        }
        reloadCounter++;
        reloadCounter = reloadCounter % reload;
    }
}
