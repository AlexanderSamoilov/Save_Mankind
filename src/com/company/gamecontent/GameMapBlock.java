package com.company.gamecontent;

import java.awt.*;
//import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

import com.company.gamegeom._3d.ParallelepipedOfBlocks;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;
import com.company.gamethread.ParameterizedMutexManager;

import static com.company.gamecontent.Constants.BLOCK_SIZE;
import static com.company.gamecontent.Constants.MAX_PLAYERS;

public class GameMapBlock extends ParallelepipedOfBlocks implements Renderable {
    // private static Logger LOG = LogManager.getLogger(GameMapBlock.class.getName());

    // A key-string which means type of landscape block, see class LandscapeBlockTemplate
    // The types are initialized in GameMap.initDefaultLandscapeBlockTemplates().
    private String landscape;

    HashMap<Integer,Boolean>   visible;
    HashSet<GameObject>        objectsOnBlock;

    // This constructor takes arbitrary block coordinates (without respect to the block grid)
    private GameMapBlock(Point2D_Integer loc, String landscape) {
        super(loc.to3D(), new Vector3D_Integer(1, 1, 1));
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("M"))); // Arrays.asList("M")

        this.landscape = landscape;
        this.objectsOnBlock = new HashSet<>(); // HashSet<GameObject>

        // TODO: currently everything is visible for everybody - the logic is to be designed
        this.visible = new HashMap<>(); // HashMap<Integer, Boolean>
        for (int k = 0; k <= MAX_PLAYERS - 1; k++) {
            this.visible.put(k, true);
        }
    }

    // This constructor takes block coordinates and block size (creates the block aliquot to the grid vertices)
    GameMapBlock(int grid_x, int grid_y, String landscape) {
        this((new Point2D_Integer(grid_x, grid_y)).mult(BLOCK_SIZE), landscape);
    }

    // Method of the "Renderable" interface
    public void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("V"))); // Arrays.asList("V")

        LandscapeBlockTemplate.get(landscape).sprite.draw(g, this, 0);
    }
}
