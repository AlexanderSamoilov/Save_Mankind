package com.company.gamecontent;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamegeom._3d.Parallelepiped;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamegraphics.GraphExtensions;
import com.company.gamethread.ParameterizedMutexManager;
import com.company.gametools.MathBugfixes;
import com.company.gametools.MathTools;

import static com.company.gamecontent.Constants.BLOCK_SIZE;

public class Bullet extends Parallelepiped implements Movable, Renderable {
    private static Logger LOG = LogManager.getLogger(Bullet.class.getName());

    // NOTE: now this field is used to detect which Unit made a shoot in order to set its "targetObject" to null when the target dies
    // Yes, it is possible to do the same even without this extra field if we just check the "units" list of Player class
    // to test, whether a given Unit exists or does not. However, it look for me as a big overhead if many units check the
    // existence of an object in the list many times (proportional to the number of units on the map)
    // Moreover, this  "shooter" field may be used for another purpose - to know whom to grant the kill frag (experience)
    // when its bullet kills something.
    final Unit shooter;
    private final BulletTemplate bulletTemplate;

    private Point3D_Integer destPoint;

    Bullet(Unit shooter, Point3D_Integer center_location, Point3D_Integer target, BulletTemplate bulletTemplate) {
        // NOTE: yes, we modify the existing "center_location" here, but it is not used anywhere else afterwards
        super(
                center_location.minus(new Vector3D_Integer(1,1,1).mult(bulletTemplate.caliber - 1).divInt(2)),
                new Vector3D_Integer(1,1,1).mult(bulletTemplate.caliber)
        );

        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        // TODO: check max caliber and whether the location is valid
        // TODO: so far we don't consider Z-coordinate
        this.destPoint = target; // use reference safely, because it was cloned in the calling function
        this.bulletTemplate = bulletTemplate;
        this.shooter = shooter;
    }

    public void setDestinationPoint(Point3D_Integer dest) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        // The destination point of the bullet is defined one time at the shooting moment
        // It is unmodifiable
    }

    public void unsetDestinationPoint() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C", "D")));

        // It is maybe not needed, because the Bullet must be deleted after the bullet hit
        destPoint = null;
    }

    public /*boolean*/ void move() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        boolean destPointReached = moveTo(destPoint);
        // The bullet can fly only where it was shot to. Its destination is not possible to change
        if (destPointReached) {
            GameMap.getInstance().eraseBullet(this);
            this.unsetDestinationPoint();
            // TODO: Check if it is safe to destroy the object which method is being called at the moment
            // this.delete() - in C/C++ object destruction will be called here
        }
        /*return destPointReached;*/
    }

    public boolean moveTo(Point3D_Integer next) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        // Calculate future coordinates where we want to move hypothetically (if nothing prevents this)
        LOG.trace("bullet_center: " + getAbsCenterInteger() + ", next: " + next);
        Point3D_Integer new_center = MathTools.calcNextPointOnRay(getAbsCenterInteger(), next, bulletTemplate.speed);

        // translation vector
        Vector3D_Integer dv = new_center.minusClone(getAbsCenterInteger());

        // move left-top object angle to the same vector which the center was moved to
        loc.plus(dv);
        LOG.trace("move?: new_loc=" + loc + ", dist=" + MathBugfixes.sqrt(dv.sumSqr()) + ", obj=" + this);

        if (! GameMap.getInstance().contains(new_center)) {
            // map border reached
            return true;
        }

        // Destination point reached, bullet do damage
        if (new_center.eq(next)) {
            this.causeDamage();
            return true;
        }

        return false;
    }

    private void causeDamage() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        Point2D_Integer block = loc.to2D().divInt(BLOCK_SIZE);
        HashSet<GameObject> objectsOnBlock = GameMap.getInstance().landscapeBlocks[block.x()][block.y()].objectsOnBlock;
        HashSet<GameObject> killedObjectsOnBlock = new HashSet<>(); // HashSet<GameObject>

        LOG.debug(
            "Player #" + this.shooter.owner.id + ", unit #" + this.shooter + ", bullet " + this.toString() +
            "--- hit [" + bulletTemplate.damage + " dmg] -> " + block + '=' + loc.to2D() + " -> " + objectsOnBlock
        );

        // Decrease HP of every object on the block in which the bullet hit.
        // Put killed objects to a separate collection.
        // We will erase them from the map in the second loop to avoid ConcurrentModificationException.
        for (GameObject obj : objectsOnBlock) {
            if (obj.hitPoints > bulletTemplate.damage) {
                obj.hitPoints -= bulletTemplate.damage;
                continue;
            }
            killedObjectsOnBlock.add(obj);
        }

        // Iterate through killed objects, do necessary actions for them (erase etc.)
        for (GameObject obj : killedObjectsOnBlock) {
            // Get experience - not implemented yet
            // shooter.giveExp(obj.getExpFromMe());
            GameMap.getInstance().eraseObject(obj);

            // TODO: it is a hard question what is more optimal:
            // - to check the list of all Units who had a given Unit as a target and unset their targetObject
            // - to check if the target exist each time for each Unit inside Unit.processTargets()
            // It is hard to say what is bigger - the number of Units or the number of killed per game tact
            // I propose to implement the second option now, but implement also the first one in the future and test  which one is faster.
            // TODO: Move global iteration of player and units to function
            int unset = 0; // DEBUG
            for (Player p : Player.players) {
                for (Unit u : p.units) {
                    if (u.getTargetObject() != obj) {
                        continue;
                    }

                    // The bullet killed exactly the target of that Unit "u"
                    u.unsetTargetObject();
                    LOG.debug(obj + " died, unset is as a target for: " + u);
                    unset ++;
                }
            }

            // DEBUG
            if (unset == 0) {
                LOG.warn(obj + " died from a stray bullet!");
            }

            // NOTE: this part must be the last one if we want to support such fun as self-killing
            // Because otherwise it will no more be returned in the getUnits() list
            obj.remove();
        }
    }

    // TODO Use Sprite rendering
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("V"))); // Arrays.asList("V")

        g.setColor(bulletTemplate.color);
        GraphExtensions.fillRect(g, getAbsBottomRect(), 0);
        super.render(g);
    }
}
