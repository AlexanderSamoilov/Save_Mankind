package com.company.gamecontent;

import com.company.gamegeom._3d.Parallelepiped;
import com.company.gamegeom._3d.ParallelepipedOfBlocks;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamethread.ParameterizedMutexManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class GameMapBlock extends ParallelepipedOfBlocks implements Renderable {
    private static Logger LOG = LogManager.getLogger(GameMapBlock.class.getName());

    public String landscape;
    public HashMap<Integer,Boolean>   visible;
    public HashSet<GameObject>        objectsOnBlock;

    // This constructor takes arbitrary block coordinates (without respect to the block grid)
    public GameMapBlock(Point2D_Integer loc, String landscape) {
        super(loc.to3D(), new Vector3D_Integer(1, 1, 1));
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("M")));

        this.landscape = landscape;
        this.objectsOnBlock = new HashSet<GameObject>();

        // TODO: currently everything is visible for everybody - the logic is to be designed
        this.visible = new HashMap<Integer, Boolean>();
        for (int k = 0; k <= Restrictions.MAX_PLAYERS - 1; k++) {
            this.visible.put(k, true);
        }
    }

    // This constructor takes block coordinates and block size (creates the block aliquot to the grid vertices)
    public GameMapBlock(int grid_x, int grid_y, String landscape) {
        this((new Point2D_Integer(grid_x, grid_y)).mult(BLOCK_SIZE), landscape);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Arrays.asList("V")));

        LandscapeBlockTemplate.get(landscape).sprite.draw(g, this, 0);
    }
}
