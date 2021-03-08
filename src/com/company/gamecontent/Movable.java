package com.company.gamecontent;

import com.company.gamemath.cortegemath.point.Point3D_Integer;

public interface Movable {

    void setDestinationPoint(Point3D_Integer point);

    void unsetDestinationPoint();

    boolean moveTo(Point3D_Integer point);

}
