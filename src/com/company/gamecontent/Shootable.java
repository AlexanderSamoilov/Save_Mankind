package com.company.gamecontent;

public interface Shootable {

    boolean setTargetObject(GameObject go);

    void setTargetPoint(Integer [] vect);

    GameObject getTargetObject();

    Integer[] getTargetPoint();

    void unsetTargetObject();

    void unsetTargetPoint();

}
