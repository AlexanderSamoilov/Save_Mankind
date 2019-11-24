package com.company.gamecontent;

import com.company.gamemath.cortegemath.point.Point3D_Integer;

public interface Moveable {

    void setDestinationPoint(Point3D_Integer point);

    void unsetDestinationPoint();

    boolean moveTo(Point3D_Integer point);

}
