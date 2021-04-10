/* ************************* *
 * U T I L I T Y   C L A S S *
 * ************************* */

/*
   We use "utility class" ("abstract final" class) simulation as "empty enum"
   described on https://stackoverflow.com/questions/9618583/java-final-abstract-class.
   Empty enum constants list (;) makes impossible to use its non-static methods:
   https://stackoverflow.com/questions/61972971/non-static-enum-methods-what-is-the-purpose-and-how-to-call-them.
 */

package com.company.gamecontent;

import java.awt.*;
//import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamethread.ParameterizedMutexManager;
import com.company.gamethread.V_Thread;

/*
 The only sense of this class is to move rendering routines from GameMap to a separate file.
 */

public enum GameMapRenderer {
    ;

    private static Logger LOG = LogManager.getLogger(GameMapRenderer.class.getName());

    public static synchronized void render(Graphics g) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("V"))); // Arrays.asList("V")

        renderBlocks(g, GameMap.getInstance());
        renderObjects(g, GameMap.getInstance());
        renderBullets(g, GameMap.getInstance());
    }

    private static synchronized void renderBlocks(Graphics g, GameMap map) {
        // Redraw map blocks and Objects on them
        for (int i = 0; i < map.dimInBlocks.x(); i++) {
            for (int j = 0; j < map.dimInBlocks.y(); j++) {

                /* For debug purpose: We draw only those blocks which are not occupied, otherwise
                there will be white space there, because the whole picture is "erased" on each step.
                To remove/add marking of the occupied blocks with white color please comment/uncomment "if".
                 */
                if (map.landscapeBlocks[i][j].objectsOnBlock.size() == 0) {
                    map.landscapeBlocks[i][j].render(g);
                }
            }
        }
    }

    // FIXME: Current implementation is not optimal
    // We traverse through the same GO if it belongs to several LB.
    private static synchronized void renderObjects(Graphics g, GameMap map) {
        for (int i = 0; i < map.dimInBlocks.x(); i++) {
            for (int j = 0; j < map.dimInBlocks.y(); j++) {
                renderObjectsOnBlock(g, map.landscapeBlocks[i][j].objectsOnBlock);
            }
        }
    }

    private static synchronized void renderBullets(Graphics g, GameMap map) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("V"))); // Arrays.asList("V")

        if (map.bullets == null) {
            return;
        }

        for (Bullet b : map.bullets) {
            b.render(g);
        }
    }

    private static synchronized void renderObjectsOnBlock(Graphics g, HashSet<GameObject> gameObjSet) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("V"))); // Arrays.asList("M")

        if (gameObjSet.size() == 0) {
            return;
        }

        try {
            LOG.trace("--->");
            for (GameObject gameObj : gameObjSet) {
                gameObj.render(g);
            }
            LOG.trace("<---");
        } catch (ConcurrentModificationException e) {
            LOG.error("ConcurrentModificationException has reproduced!");
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                LOG.error(stackTraceElement.toString());
            }
            V_Thread.terminate(1000);
        }
    }
}
