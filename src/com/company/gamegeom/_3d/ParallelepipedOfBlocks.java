package com.company.gamegeom._3d;

import com.company.gamegeom._2d.GridRectangle;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;

import static com.company.gamecontent.Restrictions.BLOCK_SIZE;

public class ParallelepipedOfBlocks extends Parallelepiped {

    public final Vector3D_Integer dimInBlocks; // Object dimensions in GameMap cells (sX, sY, sZ)

    public Point3D_Integer getLoc() {
        return loc.divIntClone(BLOCK_SIZE).to2D().to3D();
    }

    public Vector3D_Integer getDim() {
        return dimInBlocks.clone();
    }

    public ParallelepipedOfBlocks(Point3D_Integer loc, Vector3D_Integer dim) {
        super(loc, dim.multClone(BLOCK_SIZE));
        this.dimInBlocks = dim.clone();
    }

    // TODO: is this "new" not memory leak prone?
    public GridRectangle getBottomRect() {
        return new GridRectangle(getAbsBottomRect());
    }
}
