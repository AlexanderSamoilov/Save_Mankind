package com.company.gamecontent;

public interface Moveable {

    public void setDestinationPoint(Integer[] dest);

    public void unsetDestinationPoint();

    public boolean moveTo(Integer [] point);

}
