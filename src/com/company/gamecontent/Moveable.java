package com.company.gamecontent;

public interface Moveable {

    void setDestinationPoint(Integer[] point);

    void unsetDestinationPoint();

    boolean moveTo(Integer [] point);

}
