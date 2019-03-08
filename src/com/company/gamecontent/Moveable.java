package com.company.gamecontent;

public interface Moveable {

    void setDestinationPoint(Integer[] dest);

    void unsetDestinationPoint();

    boolean moveTo(Integer [] point);

}
