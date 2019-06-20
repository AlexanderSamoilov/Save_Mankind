package com.company.gamecontent;

import com.company.gamegeom.vectormath.point.Point3D_Integer;

public interface Shootable {

    boolean setTargetObject(GameObject go);

    void setTargetPoint(Point3D_Integer p);

    GameObject getTargetObject();

    Point3D_Integer getTargetPoint();

    void unsetTargetObject();

    void unsetTargetPoint();

}
