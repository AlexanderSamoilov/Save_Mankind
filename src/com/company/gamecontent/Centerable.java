package com.company.gamecontent;

import com.company.gamegeom.vectormath.point.Point3D_Integer;
import com.company.gamegeom.vectormath.point.Point3D_Double;

public interface Centerable {
    Point3D_Integer getAbsCenterInteger();
    Point3D_Double getAbsCenterDouble();
}
