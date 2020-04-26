package com.company.gamecontent;

import com.company.gamegraphics.Sprite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

/*
  Singleton. Contains the list of landscape blocks templates.
  We can add/remove/get LB models using only "name" as a unique string key.
  The user will be able to add own LB models using different name.
 */
public class LandscapeBlockTemplate {
    private static Logger LOG = LogManager.getLogger(LandscapeBlockTemplate.class.getName());

    private static final LandscapeBlockTemplate instance = new LandscapeBlockTemplate();
    private LandscapeBlockTemplate() {}
    public static LandscapeBlockTemplate getInstance() {
        return instance;
    }

    private static HashMap<String, LandscapeBlockTemplate> globalListOfKeys = new HashMap<String, LandscapeBlockTemplate>();

    // sprite
    public final Sprite sprite = new Sprite(null);

    // properties
    public String       name;
    public boolean      throughWalkable;
    public boolean      throughShootable;
    public boolean      onBuildable;

    private LandscapeBlockTemplate(String name, String spriteFileName, boolean throughWalkable, boolean throughShootable, boolean onBuildable) {
        this.sprite.setImage(spriteFileName);
        this.name             = name;
        this.throughWalkable  = throughWalkable;
        this.throughShootable = throughShootable;
        this.throughShootable = throughShootable;
        this.onBuildable = onBuildable;
    }

    public static void add(String name, boolean throughWalkable, boolean throughShootable, boolean onBuildable, String spriteFileName) {
        if (globalListOfKeys.containsKey(name)) {
            LOG.warn(LandscapeBlockTemplate.class.getName() + ": landscape block model '" + name + "' already exists - creation ignored.");
            return;
        }
        LandscapeBlockTemplate newItem = new LandscapeBlockTemplate(name, spriteFileName, throughWalkable, throughShootable, onBuildable);
        globalListOfKeys.put(name, newItem);
    }

    public static LandscapeBlockTemplate get(String name) {
        LandscapeBlockTemplate value = globalListOfKeys.get(name);
        if (value == null) {
            throw new NullPointerException("The is no LandscapeBlockTemplate with the name: " + name + ".");
        }
        return value;
    }

    public static void remove(String name) {
        globalListOfKeys.remove(name);
    }
}
