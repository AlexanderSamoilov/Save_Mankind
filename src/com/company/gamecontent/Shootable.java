package com.gamecontent;

public interface Shootable {

    public void setAttackObject(GameObject go);

    public void setAttackPoint(Integer [] vect);

    public GameObject getAttackObject();

    public Integer[] getAttackPoint();

    public void unsetAttackObject();

    public void unsetAttackPoint();

}
