package com.company.gamecontent;

import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Double;

public interface Centerable {
    Point3D_Integer getAbsCenterInteger();
    Point3D_Double getAbsCenterDouble();
}
