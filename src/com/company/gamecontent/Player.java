package com.gamecontent;

import com.gamethread.Main;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;

public class Player {
    private Race.RaceType race; // it will affect restrictions on type of buildings/weapons owned by the player
    /*(namely:
            1. The default set of buildings/units given at the beginning of the game
            2. The list of buildings/units available for creation, or the set of skills to learn.
    )*/
    private static int maxId = -1; // we should distinguish all players by id. Live player should always have id=0.
    private int id = -1;
    private static Player[] players = null;
    private String nickName; // the name which each player specifies before the game starts
    boolean defeated; // if the player is defected, there should be some rules about how do its leftover objects behave and what is it allowed to do with then for the rest players which are still in game. Once the player was defeated, it should not be allowed to be reanimated, most probably.

    // resources (we can add/remove some ones or call them money, oxygen, energy, mass etc...)
    private HashMap<Resource, Integer> resources;
    private ArrayList<Building> buildings;
    private ArrayList<Unit> units;

    public Player(Race.RaceType race, String name, HashMap<Resource, Integer> ress, ArrayList<Building> blds, ArrayList<Unit> unts) {
        // 1 - parent class specific parameters
        // 2 - child class specific parameters validation
        maxId ++;
        if (maxId + 1 > Restrictions.getMaxPlayers()) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Not allowed to create more than " + Restrictions.getMaxPlayers() + " players!");
        }
        id = maxId;

        if (ress == null) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Resource hash must be not null!");
        }
        if (Player.players == null) {
            Player.players = new Player[Restrictions.getMaxPlayers()];
        }

        this.race = race;
        this.nickName = name;

        int bSize = 0, uSize = 0;
        if (blds != null) {
            bSize = blds.size();
        }
        if (unts != null) {
            uSize = unts.size();
        }

        // assign resourcess
        if ((ress.size() > Restrictions.getMaxResourcesTypes()) ||
            (bSize + uSize > Restrictions.getMaxPlayerObjects())) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Some of parameters are beyond the restricted boundaries.");
        }
        if (ress.size() > Resource.values().length) {
            throw new IllegalArgumentException("Failed to initialize " + getClass() + ". Resource enum has only " + Resource.values().length + " elements, but " + ress.size() + " were passed.");
        }
        this.resources = new HashMap<Resource, Integer>();
        resources.putAll(ress); // TODO: check types of ress elements

        // assign buildings
        if (bSize == 0) {
            this.buildings = null;
        } else {
            this.buildings = new ArrayList<Building>();
            for (int i = 0; i < blds.size(); i++) this.buildings.add(blds.get(i));
        }
        // assign units
        if (uSize == 0) {
            units = null;
        } else {
            units = new ArrayList<Unit>();
            for (int i = 0; i < unts.size(); i++) {
                Unit u = unts.get(i);
                u.setOwner(id);
                units.add(u);
            }
            for (Unit uu : units) {
                Main.printMsg("INITIAL Player #(" + id + ")" + this +" unit: " + uu + "(" + uu.getWeapon().getOwner() + ").");
            }
        }

        // 3 - default values
        defeated = false;
        Player.players[id] = this;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }


    public static Player[] getPlayers() { // TODO: return unmodifiable
        Player [] subarray = new Player[maxId + 1];
        System.arraycopy(Player.players, 0, subarray, 0, maxId + 1);
        return subarray;
    }

    // Add exception handling etc.
    public void destroy(GameObject go) {
        if (go instanceof Building) {
            int sizeBefore = buildings.size();
            buildings.remove(go);
            if (buildings.size() == sizeBefore) {
                Main.printMsg("Critical error: The size of the buildings collection for the Player #" + id + " after the removal of the Unit #" + go + " is still " + sizeBefore);
                Main.terminateNoGiveUp(1000);
                System.exit(1);
            }
        } else if (go instanceof Unit) {
            int sizeBefore = units.size();
            units.remove(go);
            if (units.size() == sizeBefore) {
                Main.printMsg("Critical error: The size of the units collection for the Player #" + id + " after the removal of the Unit #" + go + " is still " + sizeBefore);
                Main.terminateNoGiveUp(1000);
                System.exit(1);
            }
        } else {
        }
        go = null; // TODO: do we need it to make GC to delete it indeed?
    }

    public static int getMaxPlayerId() {
        return maxId;
    }

    public int getId() {
        return id;
    }
}
