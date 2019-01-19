package com.gamecontent;

import com.gamegraphics.Sprite;
import com.gamethread.Main;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

// For details read the DOC "Data Structure"
public class GameObject implements Moveable {
    // TODO loc_x, loc_y, loc_z
    protected Integer[] loc;         // Location of object (x, y, and z for airplanes)

    // TODO dest_x, dest_y
    protected Integer[] destPoint;   // The map point to move to (has x, y)

    protected Sprite sprite;

    // TODO Check body block usability
    // We should decide if we implement object of complex form.
    // That is, the object which contains of several block items
    // which are not a solid rectangular parallelepiped. */
//    bodyBlocks[][] bodyBlocks;

    protected int playerId;

    // TODO size_x, size_y, size_z
    protected int[] size;            // Object dimensions in GameMap cells (sX, sY, sZ)

    protected int hitPoints;
    protected int maxHitPoints;
    protected int speed;
//    protected int armor;             // 0..100% - percentage damage decrement

    // absolute damage decrement - minimal HP amount that makes some
    // damage to the object
    // (lower damages are just congested and make no damage) */
//    protected int hardness;

    protected HashMap<Resource,Integer> res;   // res[0] is mass, res[1] is energy, res[2} is money etc.

//    protected int burnChanceOnHit;           // 0..100% - gives the chance of ignition at the bullet hitting.
//    protected int explosionChanceOnHit;      // 0..100% - similar with the previous one

    // 0..100% - this is another one. On each calculation step
    // the object can explode if it is burning.
//    protected int explosionChanceOnBurn;

//    protected boolean isBroken;
//    protected boolean isBurning;
//    protected boolean isDying;
//    protected boolean isMoving;

    protected boolean isSelected;


    // Here x,y,z - coordinates on grid (not absolute)
    public GameObject(Sprite sprite, int x, int y, int z, int sX, int sY, int sZ, HashMap<Resource,Integer> res, int hp, int speed, int arm, int hard, int bch, int ech, int eco) {
        // 1 - parent class specific parameters
        // 2 - validation
        if (sprite == null) {
            throw new IllegalArgumentException("Failed to initialize GameObject with spr=null.");
        }

        // TODO: check if the object borders are within map area!
        boolean valid;

        // Check object coordinates
        valid = Main.in_range(0, x, Restrictions.MAX_X, false);
        valid = valid && Main.in_range(0, y, Restrictions.MAX_Y, false);
        valid = valid && Main.in_range(0, z, Restrictions.MAX_Z, false);

        // Check object stats
        valid = valid && Main.in_range(0, hp, Restrictions.MAX_HP, false);
        valid = valid && Main.in_range(-Restrictions.MAX_SPEED, speed, Restrictions.MAX_SPEED, false);

        // Check object dimensions
        valid = valid && Main.in_range(0, sX, Restrictions.getMaxObjectSizeBlocks() + 1, true);
        valid = valid && Main.in_range(0, sY, Restrictions.getMaxObjectSizeBlocks() + 1, true);
        valid = valid && Main.in_range(0, sZ, Restrictions.getMaxObjectSizeBlocks() + 1, true);

        // Check object resources limits
        valid = valid && Main.in_range(
                0, res.get(Resource.MASS), Restrictions.MAX_MASS + 1, true
        );

        valid = valid && Main.in_range(
                0, res.get(Resource.ENERGY), Restrictions.MAX_ENERGY, false
        );

        if (!valid) {
            Main.terminateNoGiveUp(
                    1000,
                    "Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries."
            );
        }

        this.sprite = sprite;
        this.loc = new Integer[]{
                x * Restrictions.BLOCK_SIZE, y * Restrictions.BLOCK_SIZE, z * Restrictions.BLOCK_SIZE
        };

        this.size = new int[]{sX, sY, sZ};

        this.res = new HashMap<Resource,Integer>();
        this.res.put(Resource.MASS, res.get(Resource.MASS));
        this.res.put(Resource.ENERGY, res.get(Resource.ENERGY));

        this.maxHitPoints = hp;
        this.speed = speed;
//        this.armor = arm;
//        this.hardness = hard;
//        this.burnChanceOnHit = bch;
//        this.explosionChanceOnHit = ech;
//        this.explosionChanceOnBurn = eco;

        // 3 - default values
        this.hitPoints = hp;
//        isBroken = false;
//        isBurning = false;
//        isDying = false;
//        isMoving = false;
        this.isSelected = false;
        this.destPoint = null;

        // FIXME this.playerId = Faction.NEUTRAL
        this.playerId = -1;

        // Mark the object on the map
        GameMap.getInstance().registerObject(this);
    }

    public void render(Graphics g) {
        int rect_x    = loc[0];
        int rect_y    = loc[1];
        int rect_w    = size[0] * Restrictions.BLOCK_SIZE;
        int rect_h    = size[1] * Restrictions.BLOCK_SIZE;

        this.sprite.render(g, rect_x, rect_y, rect_w, rect_h);

        if (isSelected) {
            g.drawRect(rect_x, rect_y, rect_w, rect_h);
        }

        /* TODO Move it in HUD Class.render() */
        Color hpColor = null;

        int percentageHP   = 100 * hitPoints / maxHitPoints;
        int actualPartOfHP = (100 * hitPoints / maxHitPoints - 1) / 25;

        if (actualPartOfHP == 3){
            hpColor = Color.GREEN;
        }

        if (actualPartOfHP == 0){
            hpColor = Color.RED;
        }

        if (0 < actualPartOfHP && actualPartOfHP < 3) {
            hpColor = Color.YELLOW;
        }

        // "healthy" HP
        g.setColor(hpColor);
        g.fillRect(rect_x, rect_y + rect_h, rect_w * percentageHP / 100, 5);

        // "loosed" HP
        g.setColor(Color.BLACK);
        g.fillRect(
                rect_x + rect_w * percentageHP / 100,
                rect_y + rect_h,
                rect_w * (100 - percentageHP) / 100,
                5
        );

        g.drawRect(rect_x, rect_y + rect_h, rect_w, 5);
    }

    public void setDestinationPoint(Integer [] dest) {
        // TODO: check if coordinates are within restrictions
        if (destPoint == null) {
            this.destPoint = new Integer[2];
        }

        this.destPoint[0] = dest[0];
        this.destPoint[1] = dest[1];

        if (this instanceof Shootable) {
            ((Shootable)this).unsetTargetObject();
            ((Shootable)this).unsetTargetPoint();
        }

        Main.printMsg("Destination: OBJ: " + this.playerId + " x=" + dest[0] + ", y=" + dest[1]);
    }

    // TODO next_x, next_y
    // FIXME boolean ?
    public boolean moveTo(Integer [] next) {
        // FIXME replace later
        int size_x = size[0];
        int size_y = size[1];

        // Store current coordinates (we roll back changes if the calculation reveals that we cannot move)
        int new_x, new_y;
        int new_z = loc[2];

        double norm = Math.sqrt(sqrVal(next[0] - loc[0]) + sqrVal(next[1] - loc[1]));
        //Main.printMsg("norm=" + norm + ", speed=" + speed);

        // TODO Move it to Tools.Class checkNorm()
        // Avoid division by zero and endless wandering around the destination point
        if (norm <= speed) {
            // One step to target
            new_x = next[0];
            new_y = next[1];

        } else {
            // Many steps to target
            new_x = loc[0] + (int)((next[0] - loc[0]) * speed / norm);
            new_y = loc[1] + (int)((next[1] - loc[1]) * speed / norm);
        }

        //Main.printMsg("move?: x=" + newX + ", y=" + newY + ", norm=" + norm);

        // TODO Rename later!
        int cube_w = size[0] * Restrictions.BLOCK_SIZE;
        int cube_h = size[1] * Restrictions.BLOCK_SIZE;
        int cube_d = size[2] * Restrictions.BLOCK_SIZE;

        // TODO Is this must be here or lower? (about validation data after this)
        if (isIntersect(new_x, new_y, size_x, size_y)) {
            return false;
        }

        // TODO: check if the object borders are within map area!
        boolean not_valid;
        not_valid = new_x < 0 || new_y < 0 || new_z < 0;

        not_valid = not_valid || new_x + cube_w >= Restrictions.getMaxXAbs();
        not_valid = not_valid || new_y + cube_h >= Restrictions.getMaxYAbs();
        not_valid = not_valid || new_z + cube_d >= Restrictions.getMaxZAbs();

        if (not_valid) {
            return false;
        }

        // All checks passed - do movement finally:
        if (new_x == next[0] && new_y == next[1]) {
            // Destination point reached
            unsetDestinationPoint();
        }

        GameMap.getInstance().eraseObject(this);

        this.loc[0] = new_x;
        this.loc[1] = new_y;
        GameMap.getInstance().registerObject(this);

        //Main.printMsg("move: x=" + loc[0] + ", y=" + loc[1] + ", obj=" + this);

        return true;
    }

    public boolean isIntersect(int new_x, int new_y, int sX, int sY) {
        if (Restrictions.INTERSECTION_STRATEGY_SEVERITY == 0) {
            return false;
        }

        int rect_w = size[0] * Restrictions.BLOCK_SIZE;
        int rect_h = size[1] * Restrictions.BLOCK_SIZE;

        // FIXME What is it?
        int left_block_x = new_x / Restrictions.BLOCK_SIZE;
        int right_block_x = left_block_x + sX;
        int top_block_y = new_y / Restrictions.BLOCK_SIZE;
        int bottom_block_y = top_block_y + sY;

        // Check if we intersect another object
        // 1 - obtain the list of the map blocks which are intersected by the line of the object
        // FIXME What is i or j?
        for (int i = left_block_x; i <= right_block_x; i++) {
            for (int j = top_block_y; j <= bottom_block_y; j++) {
                // FIXME What is this?
                if (
                    (i != left_block_x) && (i != right_block_x) && 
                    (j != top_block_y) && (j != bottom_block_y)
                ) {
                    // Skip all blocks which are in the middle
                    continue;
                }

                // TODO: remove these temporary defense after implement safe check of map bounds:
                int i_fixed = (i == GameMap.getInstance().getWidth()) ? i-1 : i;
                int j_fixed = (j == GameMap.getInstance().getWidth()) ? j-1 : j;

                HashSet<GameObject> objectsOnBlock = GameMap.getInstance().objectsOnMap[i_fixed][j_fixed];
                if (objectsOnBlock.size() == 0) {
                    continue;
                }

                for (GameObject objOnBlock : objectsOnBlock) {
                    // Is me?
                    if (objOnBlock == this) {
                        continue;
                    }

                    // Multiple objects on the same block are allowed when they don't intersect
                    if (Restrictions.INTERSECTION_STRATEGY_SEVERITY > 1) {
                        //Main.printMsg("INTERSECTS: i=" + i_fixed + ", j=" + j_fixed + ", thisObject=" + this + ", objOnBlock=" + objOnBlock);
                        return true;
                    }

                    // Multiple objects on the same block are forbidden even
                    // if they actually don't intersect
                    Rectangle thisObjRect = new Rectangle(new_x, new_y, rect_w, rect_h);
                    Rectangle objOnBlockRect = new Rectangle(
                            objOnBlock.loc[0],
                            objOnBlock.loc[1],
                            objOnBlock.size[0] * Restrictions.BLOCK_SIZE,
                            objOnBlock.size[1] * Restrictions.BLOCK_SIZE
                    );

                    if (thisObjRect.intersects(objOnBlockRect)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // TODO Move it in gametools Class
    public int sqrVal(int value) {
        return value * value;
    }

    public void deselect() {
        this.isSelected = false;
    }

    // TODO How about delete objects?
    public Rectangle getRect () {
        return new Rectangle(
                this.loc[0],
                this.loc[1],
                this.size[0] * Restrictions.BLOCK_SIZE,
                this.size[1] * Restrictions.BLOCK_SIZE
        );
    }

    // TODO Use Patterns here for: Point, point_x, point_y, GameObject, Building
    public boolean contains (Integer[] point) {
        return this.getRect().contains(point[0], point[1]);
    }

    // TODO Remove setters. Use Class.attr = newVal
    public void setOwner(int plId) {
        this.playerId = plId;
    }

    public void unsetDestinationPoint() {
        this.destPoint = null;
    }

    public void select() {
        this.isSelected = true;
    }

    // TODO Remove getters. Use Class.attr
    public int getPlayerId() {
        return playerId;
    }
}
