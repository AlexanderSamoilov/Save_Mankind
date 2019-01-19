package com.gamecontent;

public interface Shootable {

    public boolean setTargetObject(GameObject go);

    public void setTargetPoint(Integer [] vect);

    public GameObject getTargetObject();

    public Integer[] getTargetPoint();

    public void unsetTargetObject();

    public void unsetTargetPoint();

}
