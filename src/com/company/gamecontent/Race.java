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

// import com.company.gamegraphics.Sprite;

/*
public enum Race {
    ;

    public enum RaceType {
        HUMANS,
        ROBOTS,
    }

    public static String getSpritePic(RaceType rt) {
        switch(rt) {
            case HUMANS:
                return "humans.png";
            case ROBOTS:
                return "robots.png";
            default:
                throw new EnumConstantNotPresentException(RaceType.class, "No such race:" + rt);
        }
    }
}*/