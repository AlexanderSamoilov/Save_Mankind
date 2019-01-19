package com.gamecontent;

import com.gamegraphics.Sprite;



public abstract class Race {
    public enum RaceType {
        HUMANS,
        ROBOTS,
    }

    public String getSpritePic(RaceType rt) {
        switch(rt) {
            case HUMANS:
                return "humans.png";
            case ROBOTS:
                return "robots.png";
            default:
                throw new EnumConstantNotPresentException(RaceType.class, "No such race:" + rt);
        }
    }
}