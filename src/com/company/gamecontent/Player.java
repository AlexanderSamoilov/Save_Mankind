package com.company.gamecontent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamethread.ParameterizedMutexManager;

import static com.company.gamethread.M_Thread.terminateNoGiveUp;
import static com.company.gamecontent.Constants.MAX_PLAYERS;
import static com.company.gamecontent.Constants.MAX_PLAYER_OBJECTS;

public class Player {
    private static Logger LOG = LogManager.getLogger(Player.class.getName());

    // It will affect restrictions on type of buildings/weapons owned by the player
    //private Race.RaceType race;
    /*(namely:
            1. The default set of buildings/units given at the beginning of the game
            2. The list of buildings/units available for creation, or the set of skills to learn.
    )*/

    // The name which each player specifies before the game starts
    //private String nickName;

    // We should distinguish all players by id. Live player should always have id=0.
    final int        id;

    // static: stores collection of the class instances
    public static ConcurrentLinkedQueue<Player> players = null; // WRITABLE

    // If the player is defeated, there should be some rules about how do its leftover objects behave
    // and what is it allowed to do with them for the rest players which are still in game.
    // Once the player was defeated, it should not be allowed to be reanimated, most probably.
    //private boolean defeated;

    // resources (we can add/remove some ones or call them money, oxygen, energy, mass etc...)
    //private HashMap<Resource, Integer> resources;

    // ConcurrentLinkedQueue is concurrent ArrayList, see https://stackoverflow.com/a/25630263/4807875.
    // and https://stackoverflow.com/questions/37117470/how-to-loop-arraylist-from-one-thread-while-adding-to-it-from-other-thread.
    // NOTE: If we want random processing of Units in C-Thread then it is better to use ConcurrentHashSet.
    private ConcurrentLinkedQueue<Building> buildings = null;
    public ConcurrentLinkedQueue<Unit>     units     = null; // WRITABLE

    Player(
            /*Race.RaceType race,
            String name,*/
            HashMap<Resource, Integer> res,
            ArrayList<Building> buildings,
            ArrayList<Unit> units
    ) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("M"))); // Arrays.asList("M")

        // 1 - parent class specific parameters
        /* ...*/

        // 2 - child class specific parameters validation
        if (players == null) {
            players = new ConcurrentLinkedQueue<>(); // ConcurrentLinkedQueue<Player>()
        }
        if (players.size() + 1 > MAX_PLAYERS) {
            throw new IllegalArgumentException(
                "Failed to initialize " + getClass() +
                ". Not allowed to create more than " + MAX_PLAYERS + " players!"
            );
        }

        if (res == null) {
            throw new IllegalArgumentException(
                "Failed to initialize " + getClass() +
                ". Resource hash must be not null!"
            );
        }

        this.id = players.size();
        //this.race = race;
        //this.nickName = name;

        int bSize = (buildings == null) ? 0 : buildings.size();
        int uSize = (units == null) ? 0 : units.size();

        if ((bSize + uSize) > MAX_PLAYER_OBJECTS) {
            throw new IllegalArgumentException(
                "Failed to initialize " + getClass() +
                ". Some of parameters are beyond the restricted boundaries."
            );
        }

        if (res.size() > Resource.values().length) {
            throw new IllegalArgumentException(
                "Failed to initialize " + getClass() + ". Resource enum has only " +
                Resource.values().length + " elements, but " + res.size() + " were passed."
            );
        }

        // Assign buildings
        if (bSize != 0) {
            this.buildings = new ConcurrentLinkedQueue<>();
            this.buildings.addAll(buildings);
        }

        // Assign units
        if (uSize != 0) {
            this.units = new ConcurrentLinkedQueue<>();
            this.units.addAll(units);

            for (Unit u : units) {
                u.setOwner(this);
            }
        }

        // Assign resources
        //this.resources = new HashMap<>();

        // TODO: check types of res elements
        //this.resources.putAll(res);

        /* DEBUG */
        if (this.units != null){
            for (Unit printUnit : this.units) {
                LOG.debug(
                    "INITIAL Player #(" + id + ")" + this + " unit: " +
                    printUnit +"(" + printUnit.weapon.owner + ")."
                );
            }
        }

        // 3 - default values
        //this.defeated = false;

        players.add(this);
    }

    void remove(GameObject gameObj) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        if (gameObj instanceof Building) {
            this.removeBuilding((Building) gameObj);
        }

        if (gameObj instanceof Unit) {
            this.removeUnit((Unit) gameObj);
        }
    }

    private void removeBuilding (Building building) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        if (!buildings.contains(building)) {
            /* DEBUG */
            terminateNoGiveUp(null,
                1000,
                "Player #" + id + ": " +
                " the object #" + building + " does not exist."
            );
        }
        this.buildings.remove(building);
    }

    private void removeUnit (Unit unit) {
        ParameterizedMutexManager.getInstance().checkThreadPermission(new HashSet<>(Collections.singletonList("C"))); // Arrays.asList("C")

        if (!units.contains(unit)) {
            /* DEBUG */
            terminateNoGiveUp(null,
                1000,
                "Player #" + id + ": " +
                " the object #" + unit + " does not exist."
            );
        }
        this.units.remove(unit);
    }
}
