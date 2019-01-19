package com.gamecontent;

import com.gamethread.Main;

import java.util.HashMap;
import java.util.ArrayList;

public class Player {
    // It will affect restrictions on type of buildings/weapons owned by the player
    private Race.RaceType race;
    /*(namely:
            1. The default set of buildings/units given at the beginning of the game
            2. The list of buildings/units available for creation, or the set of skills to learn.
    )*/

    // The name which each player specifies before the game starts
    private String nickName;

    // We should distinguish all players by id. Live player should always have id=0.
    // FIXME Move to other Class
    // TODO remove dis and use dynamic array
    private static int maxId = -1;
    private int        id    = -1;

    // FIXME Move to other Class
    private static Player[] players = null;

    // If the player is defected, there should be some rules about how do its leftover objects behave
    // and what is it allowed to do with then for the rest players which are still in game.
    // Once the player was defeated, it should not be allowed to be reanimated, most probably.
    boolean defeated;

    // resources (we can add/remove some ones or call them money, oxygen, energy, mass etc...)
    private HashMap<Resource, Integer> resources;
    private ArrayList<Building>        buildings = null;
    private ArrayList<Unit>            units     = null;

    public Player(Race.RaceType race, String name, HashMap<Resource, Integer> res, ArrayList<Building> buildings, ArrayList<Unit> units) {
        // 1 - parent class specific parameters
        /* ...*/

        // 2 - child class specific parameters validation
        maxId ++;
        if (maxId + 1 > Restrictions.getMaxPlayers()) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Not allowed to create more than " + Restrictions.getMaxPlayers() + " players!");
        }

        this.id = maxId;

        if (res == null) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Resource hash must be not null!");
        }

        if (Player.players == null) {
            Player.players = new Player[Restrictions.getMaxPlayers()];
        }

        this.race = race;
        this.nickName = name;

        int bSize = (buildings == null) ? 0 : buildings.size();
        int uSize = (units == null) ? 0 : units.size();

        if ((bSize + uSize) > Restrictions.getMaxPlayerObjects()) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() +
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
            this.buildings = new ArrayList<>();
            this.buildings.addAll(buildings);
        }

        // Assign units
        if (uSize != 0) {
            this.units = new ArrayList<>();
            this.units.addAll(units);

            for (Unit u : units) {
                u.setOwner(id);
            }
        }

        // Assign resources
        this.resources = new HashMap<>();

        // TODO: check types of res elements
        this.resources.putAll(res);

        /* DEBUG */
        if (this.units != null){
            for (Unit printUnit : this.units) {
                Main.printMsg("INITIAL Player #(" + id + ")" + this +" unit: " + printUnit + "(" + printUnit.getWeapon().getOwner() + ").");
            }
        }

        // 3 - default values
        // TODO May be useless
        this.defeated = false;

        Player.players[id] = this;
    }

    // FIXME Getter() to Class.attr
    public ArrayList<Unit> getUnits() {
        return units;
    }

    // TODO: return unmodifiable
    // FIXME Move to other Class
    public static Player[] getPlayers() {
        // TODO remove this and use dynamic array
        Player [] subArray = new Player[maxId + 1];
        System.arraycopy(Player.players, 0, subArray, 0, maxId + 1);
        return subArray;
    }

    // Add exception handling etc.
    // TODO rename to Remove()?
    public void destroy(GameObject gameObj) {
        if (gameObj instanceof Building) {
            this.removeBuilding((Building) gameObj);
        }

        if (gameObj instanceof Unit) {
            this.removeUnit((Unit) gameObj);
        }

        // TODO: do we need it to make GC to delete it indeed?
        gameObj = null;
    }

    private void removeBuilding (Building building) {
        if (!buildings.contains(building)) {
            /* DEBUG */
            Main.terminateNoGiveUp(
                    1000,
                    "Critical error: The size of the buildings collection for the Player #" +
                            id + " after the removal of the Unit #" + building
            );
            System.exit(1);
        }

        this.buildings.remove(building);
    }

    private void removeUnit (Unit unit) {
        if (!units.contains(unit)) {
            /* DEBUG */
            Main.terminateNoGiveUp(
                    1000,
                    "Critical error: The size of the units collection for the Player #" +
                            id + " after the removal of the Unit #" + unit
            );
            System.exit(1);
        }

        this.units.remove(unit);
    }
}
