package com.company.gamecontent;

import com.company.gamegeom._3d.Parallelepiped;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamegraphics.GraphExtensions;
import com.company.gamethread.ParameterizedMutexManager;
import com.company.gametools.MathBugfixes;
import com.company.gametools.MathTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class Bullet implements Moveable, Renderable {
    private static Logger LOG = LogManager.getLogger(Bullet.class.getName());

    // NOTE: now this field is used to detect which Unit made a shoot in order to set its "targetObject" to null when the target dies
    // Yes, it is possible to do the same even without this extra field if we just check the "units" list of Player class
    // to test, whether a given Unit exists or does not. However, it look for me as a big overhead if many units check the
    // existence of an object in the list many times (proportional to the number of units on the map)
    // Moreover, this  "shooter" field may be used for another purpose - to know whom to grant the kill frag (experience)
    // when its bullet kills something.
    private final Unit shooter; // who shoot?
    private final Parallelepiped parallelepiped;
    private final BulletTemplate bulletTemplate;

    // TODO dest_x, dest_y
    private Point3D_Integer destPoint = null;

    public Bullet(Unit shooter, Point3D_Integer center_location, Point3D_Integer target, BulletTemplate bulletTemplate) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        this.shooter = shooter;
        // TODO: check max caliber and whether the location is valid
        // TODO: so far we don't consider Z-coordinate

        // NOTE: yes, we modify the existing "center_location" here, but it is not used anywhere else afterwards
        this.parallelepiped   = new Parallelepiped(
                center_location.minus(new Vector3D_Integer(1,1,1).mult(bulletTemplate.caliber - 1).divInt(2)),
                new Vector3D_Integer(1,1,1).mult(bulletTemplate.caliber)
        );

        this.destPoint = target; // use reference safely, because it was cloned in the calling function
        this.bulletTemplate = bulletTemplate;
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

    public boolean move() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        // The bullet can fly only where it was shot to. Its destination is not possible to change
        return moveTo(destPoint);
    }

    public boolean moveTo(Point3D_Integer next) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        // Calculate future coordinates where we want to move hypothetically (if nothing prevents this)
        LOG.trace("bullet_center: " + parallelepiped.getAbsCenterInteger() + ", next: " + next);
        Point3D_Integer new_center = MathTools.getNextPointOnRay(parallelepiped.getAbsCenterInteger(), next, bulletTemplate.speed);

        // translation vector
        Vector3D_Integer dv = new_center.minusClone(parallelepiped.getAbsCenterInteger());

        // move left-top object angle to the same vector which the center was moved to
        parallelepiped.loc.plus(dv);
        LOG.trace("move?: new_loc=" + parallelepiped.loc + ", dist=" + MathBugfixes.sqrt(dv.sumSqr()) + ", obj=" + this);

        if (! GameMap.getInstance().contains(new_center)) {
            // the bullet left the map - forget it!
            // TODO: check it it is safe to make null the object which method is being called at the moment
            GameMap.getInstance().destroyBullet(this);
            return false;
        }

        // Destination point reached, bullet do damage
        if (new_center.eq(next)) {
            this.causeDamage();
        }

        return true;
    }

    public void causeDamage() {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("C")));

        Point2D_Integer block = parallelepiped.loc.to2D().divInt(BLOCK_SIZE);

        HashSet<GameObject> objectsOnBlock = (HashSet<GameObject>)GameMap.getInstance().landscapeBlocks[block.x()][block.y()].objectsOnBlock.clone();

        for (GameObject objOnBlock : objectsOnBlock) {
            LOG.debug("--- hit [" + bulletTemplate.damage + " dmg] -> " + block + " -> " + objOnBlock);
            if (objOnBlock.hitPoints > bulletTemplate.damage) {
                objOnBlock.hitPoints -= bulletTemplate.damage;
                continue;
            }

            // Get experience - not implemented yet
            // shooter.giveExp(go.getExpFromMe());
            GameMap.getInstance().eraseObject(objOnBlock);

            // TODO: it is a hard question what is more optimal:
            // - to check the list of all Units who had a given Unit as a target and unset their targetObject
            // - to check if the target exist each time for each Unit inside Unit.processTargets()
            // It is hard to say what is bigger - the number of Units or the number of killed per game tact
            // I propose to implement the second option now, but implement also the first one in the future and test  which one is faster.
            // TODO: Move global iteration of player and units to function
            int unset = 0; // DEBUG
            for (Player p : Player.getPlayers()) {
                for (Unit u : p.getUnits()) {
                    if (u.getTargetObject() != objOnBlock) {
                        continue;
                    }

                    // The bullet killed exactly the target of that Unit "u"
                    u.unsetTargetObject();
                    LOG.debug(objOnBlock + " died, unset is as a target for: " + u);
                    unset ++;
                }
            }

            // DEBUG
            if (unset == 0) {
                LOG.warn(objOnBlock + " died from a stray bullet!");
            }

            // NOTE: this part must be the last one if we want to support such fun as self-killing
            // Because otherwise it will no more be returned in the getUnits() list
            // TODO: It looks very dirty
            // FIXME Unit.destroy()
            Player.getPlayers()[objOnBlock.getPlayerId()].destroy(objOnBlock);
        }

        // TODO: check it it is safe to make null the object which method is being called at the moment
        GameMap.getInstance().destroyBullet(this);
    }

    // TODO Use Sprite rendering
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

        g.setColor(bulletTemplate.color);
        GraphExtensions.fillRect(g, new Rectangle(parallelepiped.loc.x(), parallelepiped.loc.y(), parallelepiped.getAbsDim().x(), parallelepiped.getAbsDim().y()), 0);
        parallelepiped.render(g);
    }
}
