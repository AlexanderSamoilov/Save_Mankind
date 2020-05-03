package com.company.gamemath.cortegemath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.company.gamemath.cortegemath.cortege.*;
import com.company.gamemath.cortegemath.point.Point2D_Double;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Double;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamemath.cortegemath.vector.Vector2D_Double;
import com.company.gamemath.cortegemath.vector.Vector2D_Integer;
import com.company.gamemath.cortegemath.vector.Vector3D_Double;
import com.company.gamemath.cortegemath.vector.Vector3D_Integer;

public class CortegeTest {

    private static Logger LOG = LogManager.getLogger(CortegeTest.class.getName());
    private static double eps = 0.000001;

    private static void testCortege2D() {

        Cortege2D_Integer c2i = new Cortege2D_Integer(1, -3);
        Cortege2D_Double c2d = new Cortege2D_Double(-0.9, 1.5);
        Cortege2D_Integer c2iInitial = new Cortege2D_Integer(null, null);
        Cortege2D_Double c2dInitial = new Cortege2D_Double(null, null);

        c2iInitial.assign(c2i);
        c2dInitial.assign(c2d);

        // Special tests
        assert(new Cortege2D_Integer(0, 0).isZeroCortege());
        assert(new Cortege2D_Integer(0.0, 0.0).isZeroCortege());
        assert(new Cortege2D_Double(0, 0).isZeroCortege());
        assert(new Cortege2D_Double(0.0, 0.0).isZeroCortege());
        new Cortege2D_Integer(-0.4, 0.6).assertEq(new Cortege2D_Integer(0, 0));
        new Cortege2D_Integer(-0.5, 0.5).assertEq(new Cortege2D_Integer(0, 0));
        new Cortege2D_Integer(-0.6, 0.4).assertEq(new Cortege2D_Integer(0, 0));

        // Test self unary operators
        c2i.plus(c2i).assertEq(new Cortege2D_Integer(2, -6)); c2i.assign(c2iInitial);
        c2i.plus(c2d).assertEq(new Cortege2D_Integer(0, -1)); c2i.assign(c2iInitial);
        c2i.minus(c2i).assertEq(new Cortege2D_Integer(0, 0)); c2i.assign(c2iInitial);
        c2i.minus(c2d).assertEq(new Cortege2D_Integer(2, -4)); c2i.assign(c2iInitial);
        c2i.mult(2).assertEq(new Cortege2D_Integer(2, -6)); c2i.assign(c2iInitial);
        c2i.mult(2.0).assertEq(new Cortege2D_Integer(2, -6)); c2i.assign(c2iInitial);
        c2i.div(2).assertEq(new Cortege2D_Integer(1, -1)); c2i.assign(c2iInitial);
        c2i.div(2.0).assertEq(new Cortege2D_Integer(1, -1)); c2i.assign(c2iInitial);
        c2i.divInt(2).assertEq(new Cortege2D_Integer(1, -1)); c2i.assign(c2iInitial);
        c2i.divInt(2.0).assertEq(new Cortege2D_Integer(1, -1)); c2i.assign(c2iInitial);

        c2d.plus(c2i).assertEqDouble(new Cortege2D_Double(0.1, -1.5), eps); c2d.assign(c2dInitial);
        c2d.plus(c2d).assertEqDouble(new Cortege2D_Double(-1.8, 3), eps); c2d.assign(c2dInitial);
        c2d.minus(c2i).assertEqDouble(new Cortege2D_Double(-1.9, 4.5), eps); c2d.assign(c2dInitial);
        c2d.minus(c2d).assertEq(new Cortege2D_Double(0, 0)); c2d.assign(c2dInitial);
        c2d.mult(2).assertEqDouble(new Cortege2D_Double(-1.8, 3), eps); c2d.assign(c2dInitial);
        c2d.mult(2.0).assertEqDouble(new Cortege2D_Double(-1.8, 3), eps); c2d.assign(c2dInitial);
        c2d.div(2).assertEqDouble(new Cortege2D_Double(-0.45, 0.75), eps); c2d.assign(c2dInitial);
        c2d.div(2.0).assertEqDouble(new Cortege2D_Double(-0.45, 0.75), eps); c2d.assign(c2dInitial);

        // Test transitivity of self unary operators
        c2i.plus(c2i).plus(c2i).assertEq(new Cortege2D_Integer(4, -12)); c2i.assign(c2iInitial);
        c2i.plus(c2i).plus(c2d).assertEq(new Cortege2D_Integer(1, -4)); c2i.assign(c2iInitial);
        c2i.plus(c2d).plus(c2i).assertEq(new Cortege2D_Integer(0, -2)); c2i.assign(c2iInitial);
        c2i.plus(c2d).plus(c2d).assertEq(new Cortege2D_Integer(-1, 1)); c2i.assign(c2iInitial);
        c2i.minus(c2i).minus(c2i).assertEq(new Cortege2D_Integer(0, 0)); c2i.assign(c2iInitial);
        c2i.minus(c2i).minus(c2d).assertEq(new Cortege2D_Integer(1, -1)); c2i.assign(c2iInitial);
        c2i.minus(c2d).minus(c2i).assertEq(new Cortege2D_Integer(0, 0)); c2i.assign(c2iInitial);
        c2i.minus(c2d).minus(c2d).assertEq(new Cortege2D_Integer(3, -5)); c2i.assign(c2iInitial);
        c2i.mult(2).mult(2.0).mult(2).assertEq(new Cortege2D_Integer(8, -24)); c2i.assign(c2iInitial);
        c2i.div(2).div(2.0).div(2).assertEq(new Cortege2D_Integer(1, 0)); c2i.assign(c2iInitial);
        c2i.divInt(2).divInt(2.0).divInt(2).assertEq(new Cortege2D_Integer(1, 0)); c2i.assign(c2iInitial);

        c2d.plus(c2i).plus(c2i).assertEqDouble(new Cortege2D_Double(1.1, -4.5), eps); c2d.assign(c2dInitial);
        c2d.plus(c2i).plus(c2d).assertEqDouble(new Cortege2D_Double(0.2, -3), eps); c2d.assign(c2dInitial);
        c2d.plus(c2d).plus(c2i).assertEqDouble(new Cortege2D_Double(-0.8, 0), eps); c2d.assign(c2dInitial);
        c2d.plus(c2d).plus(c2d).assertEqDouble(new Cortege2D_Double(-3.6, 6), eps); c2d.assign(c2dInitial);
        c2d.minus(c2i).minus(c2i).assertEqDouble(new Cortege2D_Double(-2.9, 7.5), eps); c2d.assign(c2dInitial);
        c2d.minus(c2i).minus(c2d).assertEq(new Cortege2D_Double(0, 0)); c2d.assign(c2dInitial);
        c2d.minus(c2d).minus(c2i).assertEqDouble(new Cortege2D_Double(-1, 3), eps); c2d.assign(c2dInitial);
        c2d.minus(c2d).minus(c2d).assertEq(new Cortege2D_Double(0, 0)); c2d.assign(c2dInitial);
        c2d.mult(2).mult(2.0).mult(2).assertEqDouble(new Cortege2D_Double(-7.2, 12), eps); c2d.assign(c2dInitial);
        c2d.div(2).div(2.0).div(2).assertEqDouble(new Cortege2D_Double(-0.1125, 0.1875), eps); c2d.assign(c2dInitial);
        //

        // Test transitivity of non-self unary operators
        c2i.plusClone(c2i).plusClone(c2i).assertEq(new Cortege2D_Integer(3, -9));
        c2i.plusClone(c2i).plusClone(c2d).assertEqDouble(new Cortege2D_Double(1.1, -4.5), eps);
        c2i.plusClone(c2d).plusClone(c2i).assertEqDouble(new Cortege2D_Double(1.1, -4.5), eps);
        c2i.plusClone(c2d).plusClone(c2d).assertEqDouble(new Cortege2D_Double(-0.8, 0), eps);
        c2i.minusClone(c2i).assertEq(new Cortege2D_Integer(0, 0));
        c2i.minusClone(c2i).minusClone(c2i).assertEq(new Cortege2D_Integer(-1, 3));
        c2i.minusClone(c2i).minusClone(c2d).assertEqDouble(new Cortege2D_Double(0.9, -1.5), eps);
        c2i.minusClone(c2d).minusClone(c2i).assertEqDouble(new Cortege2D_Double(0.9, -1.5), eps);
        c2i.minusClone(c2d).minusClone(c2d).assertEqDouble(new Cortege2D_Double(2.8, -6), eps);
        c2i.multClone(2).multClone(2).multClone(2).assertEq(new Cortege2D_Integer(8, -24));
        c2i.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Cortege2D_Double(8, -24), eps);
        c2i.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Cortege2D_Double(0.125, -0.375), eps);
        c2i.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Cortege2D_Integer(1, 0));
        c2i.assertEq(c2iInitial);

        c2d.plusClone(c2i).plusClone(c2i).assertEqDouble(new Cortege2D_Double(1.1, -4.5), eps);
        c2d.plusClone(c2i).plusClone(c2d).assertEqDouble(new Cortege2D_Double(-0.8, 0), eps);
        c2d.plusClone(c2d).plusClone(c2i).assertEqDouble(new Cortege2D_Double(-0.8, 0), eps);
        c2d.plusClone(c2d).plusClone(c2d).assertEqDouble(new Cortege2D_Double(-2.7, 4.5), eps);
        c2d.minusClone(c2i).minusClone(c2i).assertEqDouble(new Cortege2D_Double(-2.9, 7.5), eps);
        c2d.minusClone(c2i).minusClone(c2d).assertEqDouble(new Cortege2D_Double(-1, 3), eps);
        c2d.minusClone(c2d).minusClone(c2i).assertEqDouble(new Cortege2D_Double(-1, 3), eps);
        c2d.minusClone(c2d).minusClone(c2d).assertEqDouble(new Cortege2D_Double(0.9, -1.5), eps);
        c2d.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Cortege2D_Double(-7.2, 12), eps);
        c2d.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Cortege2D_Double(-0.1125, 0.1875), eps);
        c2d.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Cortege2D_Integer(0, 1));
        c2d.assertEq(c2dInitial);

        // Test transitivity of binary operators
        Cortege2D_Integer.plus2(c2i, c2i).to3D().to2D().assertEq(new Cortege2D_Integer(2, -6));
        Cortege2D_Integer.plus2(c2i, c2d).to3D().to2D().assertEqDouble(new Cortege2D_Double(0.1, -1.5), eps);
        Cortege2D_Double.plus2(c2d, c2i).to3D().to2D().assertEqDouble(new Cortege2D_Double(0.1, -1.5), eps);
        Cortege2D_Double.plus2(c2d, c2d).to3D().to2D().assertEqDouble(new Cortege2D_Double(-1.8, 3), eps);
        Cortege2D_Integer.minus2(c2i, c2i).to3D().to2D().assertEq(new Cortege2D_Integer(0, 0));
        Cortege2D_Integer.minus2(c2i, c2d).to3D().to2D().assertEqDouble(new Cortege2D_Double(1.9, -4.5), eps);
        Cortege2D_Double.minus2(c2d, c2i).to3D().to2D().assertEqDouble(new Cortege2D_Double(-1.9, 4.5), eps);
        Cortege2D_Double.minus2(c2d, c2d).to3D().to2D().assertEq(new Cortege2D_Double(0, 0));
        Cortege2D_Integer.mult2(c2i, 2).to3D().to2D().assertEq(new Cortege2D_Integer(2, -6));
        Cortege2D_Integer.mult2(c2i, 2.0).to3D().to2D().assertEqDouble(new Cortege2D_Double(2, -6), eps);
        Cortege2D_Double.mult2(c2d, 2).to3D().to2D().assertEqDouble(new Cortege2D_Double(-1.8, 3), eps);
        Cortege2D_Double.mult2(c2d, 2.0).to3D().to2D().assertEqDouble(new Cortege2D_Double(-1.8, 3), eps);
        Cortege2D_Integer.div2(c2i, 2).to3D().to2D().assertEqDouble(new Cortege2D_Double(0.5, -1.5), eps);
        Cortege2D_Integer.div2(c2i, 2.0).to3D().to2D().assertEqDouble(new Cortege2D_Double(0.5, -1.5), eps);
        Cortege2D_Double.div2(c2d, 2).to3D().to2D().assertEqDouble(new Cortege2D_Double(-0.45, 0.75), eps);
        Cortege2D_Double.div2(c2d, 2.0).to3D().to2D().assertEqDouble(new Cortege2D_Double(-0.45, 0.75), eps);
        Cortege2D_Integer.divInt2(c2i, 2).to3D().to2D().assertEq(new Cortege2D_Integer(1, -1));
        Cortege2D_Integer.divInt2(c2i, 2.0).to3D().to2D().assertEq(new Cortege2D_Integer(1, -1));
        Cortege2D_Double.divInt2(c2d, 2).to3D().to2D().assertEq(new Cortege2D_Integer(0, 1));
        Cortege2D_Double.divInt2(c2d, 2.0).to3D().to2D().assertEq(new Cortege2D_Integer(0, 1));
        c2i.assertEq(c2iInitial);
        c2d.assertEq(c2dInitial);
    }

    public static void testCortege3D() {

        Cortege3D_Integer c3i = new Cortege3D_Integer(-1, 0, 1);
        Cortege3D_Double c3d = new Cortege3D_Double(0.5, 0.2, -0.8);
        Cortege3D_Integer c3iInitial = new Cortege3D_Integer(null, null, null);
        Cortege3D_Double c3dInitial = new Cortege3D_Double(null, null, null);

        c3iInitial.assign(c3i);
        c3dInitial.assign(c3d);

        // Special tests
        assert(new Cortege3D_Integer(0, 0, 0).isZeroCortege());
        assert(new Cortege3D_Integer(0.0, 0.0, 0.0).isZeroCortege());
        assert(new Cortege3D_Double(0, 0, 0).isZeroCortege());
        assert(new Cortege3D_Double(0.0, 0.0, 0.0).isZeroCortege());
        new Cortege3D_Integer(-0.4, 0.6, 0.99).assertEq(new Cortege3D_Integer(0, 0, 0));
        new Cortege3D_Integer(-0.5, 0.5, -0.99).assertEq(new Cortege3D_Integer(0, 0, 0));
        new Cortege3D_Integer(-0.6, 0.4, -0.0).assertEq(new Cortege3D_Integer(0, 0, 0));

        // Test self unary operators
        c3i.plus(c3i).assertEq(new Cortege3D_Integer(-2, 0, 2)); c3i.assign(c3iInitial);
        c3i.plus(c3d).assertEq(new Cortege3D_Integer(0, 0, 0)); c3i.assign(c3iInitial);
        c3i.minus(c3i).assertEq(new Cortege3D_Integer(0, 0, 0)); c3i.assign(c3iInitial);
        c3i.minus(c3d).assertEq(new Cortege3D_Integer(-1, 0, 2)); c3i.assign(c3iInitial);
        c3i.mult(2).assertEq(new Cortege3D_Integer(-2, 0, 2)); c3i.assign(c3iInitial);
        c3i.mult(2.0).assertEq(new Cortege3D_Integer(-2, 0, 2)); c3i.assign(c3iInitial);
        c3i.div(2).assertEq(new Cortege3D_Integer(0, 0, 1)); c3i.assign(c3iInitial);
        c3i.div(2.0).assertEq(new Cortege3D_Integer(0, 0, 1)); c3i.assign(c3iInitial);
        c3i.divInt(2).assertEq(new Cortege3D_Integer(0, 0, 1)); c3i.assign(c3iInitial);
        c3i.divInt(2.0).assertEq(new Cortege3D_Integer(0, 0, 1)); c3i.assign(c3iInitial);

        c3d.plus(c3i).assertEqDouble(new Cortege3D_Double(-0.5, 0.2, 0.2), eps); c3d.assign(c3dInitial);
        c3d.plus(c3d).assertEqDouble(new Cortege3D_Double(1, 0.4, -1.6), eps); c3d.assign(c3dInitial);
        c3d.minus(c3i).assertEqDouble(new Cortege3D_Double(1.5, 0.2, -1.8), eps); c3d.assign(c3dInitial);
        c3d.minus(c3d).assertEq(new Cortege3D_Double(0, 0, 0)); c3d.assign(c3dInitial);
        c3d.mult(2).assertEqDouble(new Cortege3D_Double(1, 0.4, -1.6), eps); c3d.assign(c3dInitial);
        c3d.mult(2.0).assertEqDouble(new Cortege3D_Double(1, 0.4, -1.6), eps); c3d.assign(c3dInitial);
        c3d.div(2).assertEqDouble(new Cortege3D_Double(0.25, 0.1, -0.4), eps); c3d.assign(c3dInitial);
        c3d.div(2.0).assertEqDouble(new Cortege3D_Double(0.25, 0.1, -0.4), eps); c3d.assign(c3dInitial);

        // Test transitivity of self unary operators
        c3i.plus(c3i).plus(c3i).assertEq(new Cortege3D_Integer(-4, 0, 4)); c3i.assign(c3iInitial);
        c3i.plus(c3i).plus(c3d).assertEq(new Cortege3D_Integer(-1, 0, 1)); c3i.assign(c3iInitial);
        c3i.plus(c3d).plus(c3i).assertEq(new Cortege3D_Integer(0, 0, 0)); c3i.assign(c3iInitial);
        c3i.plus(c3d).plus(c3d).assertEq(new Cortege3D_Integer(1, 0, -1)); c3i.assign(c3iInitial);
        c3i.minus(c3i).minus(c3i).assertEq(new Cortege3D_Integer(0, 0, 0)); c3i.assign(c3iInitial);
        c3i.minus(c3i).minus(c3d).assertEq(new Cortege3D_Integer(0, 0, 1)); c3i.assign(c3iInitial);
        c3i.minus(c3d).minus(c3i).assertEq(new Cortege3D_Integer(0, 0, 0)); c3i.assign(c3iInitial);
        c3i.minus(c3d).minus(c3d).assertEq(new Cortege3D_Integer(-1, 0, 3)); c3i.assign(c3iInitial);
        c3i.mult(2).mult(2.0).mult(2).assertEq(new Cortege3D_Integer(-8, 0, 8)); c3i.assign(c3iInitial);
        c3i.div(2).div(2.0).div(2).assertEq(new Cortege3D_Integer(0, 0, 1)); c3i.assign(c3iInitial);
        c3i.divInt(2).divInt(2.0).divInt(2).assertEq(new Cortege3D_Integer(0, 0, 1)); c3i.assign(c3iInitial);

        c3d.plus(c3i).plus(c3i).assertEqDouble(new Cortege3D_Double(-1.5, 0.2, 1.2), eps); c3d.assign(c3dInitial);
        c3d.plus(c3i).plus(c3d).assertEqDouble(new Cortege3D_Double(-1, 0.4, 0.4), eps); c3d.assign(c3dInitial);
        c3d.plus(c3d).plus(c3i).assertEqDouble(new Cortege3D_Double(0, 0.4, -0.6), eps); c3d.assign(c3dInitial);
        c3d.plus(c3d).plus(c3d).assertEqDouble(new Cortege3D_Double(2, 0.8, -3.2), eps); c3d.assign(c3dInitial);
        c3d.minus(c3i).minus(c3i).assertEqDouble(new Cortege3D_Double(2.5, 0.2, -2.8), eps); c3d.assign(c3dInitial);
        c3d.minus(c3i).minus(c3d).assertEq(new Cortege3D_Double(0, 0, 0)); c3d.assign(c3dInitial);
        c3d.minus(c3d).minus(c3i).assertEqDouble(new Cortege3D_Double(1, 0, -1), eps); c3d.assign(c3dInitial);
        c3d.minus(c3d).minus(c3d).assertEq(new Cortege3D_Double(0, 0, 0)); c3d.assign(c3dInitial);
        c3d.mult(2).mult(2.0).mult(2).assertEqDouble(new Cortege3D_Double(4, 1.6, -6.4), eps); c3d.assign(c3dInitial);
        c3d.div(2).div(2.0).div(2).assertEqDouble(new Cortege3D_Double(0.0625, 0.025, -0.1), eps); c3d.assign(c3dInitial);
        //

        // Test transitivity of non-self unary operators
        c3i.plusClone(c3i).plusClone(c3i).assertEq(new Cortege3D_Integer(-3, 0, 3));
        c3i.plusClone(c3i).plusClone(c3d).assertEqDouble(new Cortege3D_Double(-1.5, 0.2, 1.2), eps);
        c3i.plusClone(c3d).plusClone(c3i).assertEqDouble(new Cortege3D_Double(-1.5, 0.2, 1.2), eps);
        c3i.plusClone(c3d).plusClone(c3d).assertEqDouble(new Cortege3D_Double(0, 0.4, -0.6), eps);
        c3i.minusClone(c3i).minusClone(c3i).assertEq(new Cortege3D_Integer(1, 0, -1));
        c3i.minusClone(c3i).minusClone(c3d).assertEqDouble(new Cortege3D_Double(-0.5, -0.2, 0.8), eps);
        c3i.minusClone(c3d).minusClone(c3i).assertEqDouble(new Cortege3D_Double(-0.5, -0.2, 0.8), eps);
        c3i.minusClone(c3d).minusClone(c3d).assertEqDouble(new Cortege3D_Double(-2, -0.4, 2.6), eps);
        c3i.multClone(2).multClone(2).multClone(2).assertEq(new Cortege3D_Integer(-8, 0, 8));
        c3i.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Cortege3D_Double(-8, 0, 8), eps);
        c3i.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Cortege3D_Double(-0.125, 0, 0.125), eps);
        c3i.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Cortege3D_Integer(0, 0, 1));
        c3i.assertEq(c3iInitial);

        c3d.plusClone(c3i).plusClone(c3i).assertEqDouble(new Cortege3D_Double(-1.5, 0.2, 1.2), eps);
        c3d.plusClone(c3i).plusClone(c3d).assertEqDouble(new Cortege3D_Double(0, 0.4, -0.6), eps);
        c3d.plusClone(c3d).plusClone(c3i).assertEqDouble(new Cortege3D_Double(0, 0.4, -0.6), eps);
        c3d.plusClone(c3d).plusClone(c3d).assertEqDouble(new Cortege3D_Double(1.5, 0.6, -2.4), eps);
        c3d.minusClone(c3i).minusClone(c3i).assertEqDouble(new Cortege3D_Double(2.5, 0.2, -2.8), eps);
        c3d.minusClone(c3i).minusClone(c3d).assertEqDouble(new Cortege3D_Double(1, 0, -1), eps);
        c3d.minusClone(c3d).minusClone(c3i).assertEqDouble(new Cortege3D_Double(1, 0, -1), eps);
        c3d.minusClone(c3d).minusClone(c3d).assertEqDouble(new Cortege3D_Double(-0.5, -0.2, 0.8), eps);
        c3d.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Cortege3D_Double(4, 1.6, -6.4), eps);
        c3d.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Cortege3D_Double(0.0625, 0.025, -0.1), eps);
        c3d.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Cortege3D_Integer(0, 0, 0));
        c3d.assertEq(c3dInitial);

        // Test transitivity of binary operators
        Cortege3D_Integer.plus2(c3i, c3i).to2D().to3D().assertEq(new Cortege3D_Integer(-2, 0, 0));
        Cortege3D_Integer.plus2(c3i, c3d).to2D().to3D().assertEqDouble(new Cortege3D_Double(-0.5, 0.2, 0), eps);
        Cortege3D_Double.plus2(c3d, c3i).to2D().to3D().assertEqDouble(new Cortege3D_Double(-0.5, 0.2, 0), eps);
        Cortege3D_Double.plus2(c3d, c3d).to2D().to3D().assertEqDouble(new Cortege3D_Double(1, 0.4, 0), eps);
        Cortege3D_Integer.minus2(c3i, c3i).to2D().to3D().assertEq(new Cortege3D_Integer(0, 0, 0));
        Cortege3D_Integer.minus2(c3i, c3d).to2D().to3D().assertEqDouble(new Cortege3D_Double(-1.5, -0.2, 0), eps);
        Cortege3D_Double.minus2(c3d, c3i).to2D().to3D().assertEqDouble(new Cortege3D_Double(1.5, 0.2, 0), eps);
        Cortege3D_Double.minus2(c3d, c3d).to2D().to3D().assertEq(new Cortege3D_Double(0, 0, 0));
        Cortege3D_Integer.mult2(c3i, 2).to2D().to3D().assertEq(new Cortege3D_Integer(-2, 0, 0));
        Cortege3D_Integer.mult2(c3i, 2.0).to2D().to3D().assertEqDouble(new Cortege3D_Double(-2, 0, 0), eps);
        Cortege3D_Double.mult2(c3d, 2).to2D().to3D().assertEqDouble(new Cortege3D_Double(1, 0.4, 0), eps);
        Cortege3D_Double.mult2(c3d, 2.0).to2D().to3D().assertEqDouble(new Cortege3D_Double(1, 0.4, 0), eps);
        Cortege3D_Integer.div2(c3i, 2).to2D().to3D().assertEqDouble(new Cortege3D_Double(-0.5, 0, 0), eps);
        Cortege3D_Integer.div2(c3i, 2.0).to2D().to3D().assertEqDouble(new Cortege3D_Double(-0.5, 0, 0), eps);
        Cortege3D_Double.div2(c3d, 2).to2D().to3D().assertEqDouble(new Cortege3D_Double(0.25, 0.1, 0), eps);
        Cortege3D_Double.div2(c3d, 2.0).to2D().to3D().assertEqDouble(new Cortege3D_Double(0.25, 0.1, 0), eps);
        Cortege3D_Integer.divInt2(c3i, 2).to2D().to3D().assertEq(new Cortege3D_Integer(0, 0, 0));
        Cortege3D_Integer.divInt2(c3i, 2.0).to2D().to3D().assertEq(new Cortege3D_Integer(0, 0, 0));
        Cortege3D_Double.divInt2(c3d, 2).to2D().to3D().assertEq(new Cortege3D_Integer(0, 0, 0));
        Cortege3D_Double.divInt2(c3d, 2.0).to2D().to3D().assertEq(new Cortege3D_Integer(0, 0, 0));
        c3i.assertEq(c3iInitial);
        c3d.assertEq(c3dInitial);
    }

    public static void testCortege2DChildren() {

        Point2D_Integer p2i = new Point2D_Integer(1, -3);
        Point2D_Double p2d = new Point2D_Double(-0.9, 1.5);
        Vector2D_Integer v2i = new Vector2D_Integer(1, -3);
        Vector2D_Double v2d = new Vector2D_Double(-0.9, 1.5);

        Point2D_Integer p2iInitial = new Point2D_Integer(null, null);
        Point2D_Double p2dInitial = new Point2D_Double(null, null);
        Vector2D_Integer v2iInitial = new Vector2D_Integer(null, null);
        Vector2D_Double v2dInitial = new Vector2D_Double(null, null);

        p2iInitial.assign(p2i);
        p2dInitial.assign(p2d);
        v2iInitial.assign(v2i);
        v2dInitial.assign(v2d);

        // Special tests
        assert(new Point2D_Integer(0, 0).isZeroCortege());
        assert(new Point2D_Integer(0.0, 0.0).isZeroCortege());
        assert(new Point2D_Double(0, 0).isZeroCortege());
        assert(new Point2D_Double(0.0, 0.0).isZeroCortege());
        assert(new Vector2D_Integer(0, 0).isZeroCortege());
        assert(new Vector2D_Integer(0.0, 0.0).isZeroCortege());
        assert(new Vector2D_Double(0, 0).isZeroCortege());
        assert(new Vector2D_Double(0.0, 0.0).isZeroCortege());
        new Point2D_Integer(-0.4, 0.6).assertEq(new Point2D_Integer(0, 0));
        new Point2D_Integer(-0.5, 0.5).assertEq(new Point2D_Integer(0, 0));
        new Point2D_Integer(-0.6, 0.4).assertEq(new Point2D_Integer(0, 0));
        new Point2D_Integer(-0.4, 0.6).assertNotEq(new Vector2D_Integer(0, 0));
        new Point2D_Integer(-0.5, 0.5).assertNotEq(new Vector2D_Integer(0, 0));
        new Point2D_Integer(-0.6, 0.4).assertNotEq(new Vector2D_Integer(0, 0));
        new Point2D_Integer(-0.4, 0.6).assertNotEq(new Cortege2D_Integer(0, 0));
        new Point2D_Integer(-0.5, 0.5).assertNotEq(new Cortege2D_Integer(0, 0));
        new Point2D_Integer(-0.6, 0.4).assertNotEq(new Cortege2D_Integer(0, 0));
        new Vector2D_Integer(-0.4, 0.6).assertEq(new Vector2D_Integer(0, 0));
        new Vector2D_Integer(-0.5, 0.5).assertEq(new Vector2D_Integer(0, 0));
        new Vector2D_Integer(-0.6, 0.4).assertEq(new Vector2D_Integer(0, 0));
        new Vector2D_Integer(-0.4, 0.6).assertNotEq(new Point2D_Integer(0, 0));
        new Vector2D_Integer(-0.5, 0.5).assertNotEq(new Point2D_Integer(0, 0));
        new Vector2D_Integer(-0.6, 0.4).assertNotEq(new Point2D_Integer(0, 0));
        new Vector2D_Integer(-0.4, 0.6).assertNotEq(new Cortege2D_Integer(0, 0));
        new Vector2D_Integer(-0.5, 0.5).assertNotEq(new Cortege2D_Integer(0, 0));
        new Vector2D_Integer(-0.6, 0.4).assertNotEq(new Cortege2D_Integer(0, 0));

        // Test self unary operators

        p2i.plus(v2i).assertEq(new Point2D_Integer(2, -6)); p2i.assign(p2iInitial);
        p2i.plus(v2d).assertEq(new Point2D_Integer(0, -1)); p2i.assign(p2iInitial);
        p2i.minus(v2i).assertEq(new Point2D_Integer(0, 0)); p2i.assign(p2iInitial);
        p2i.minus(v2d).assertEq(new Point2D_Integer(2, -4)); p2i.assign(p2iInitial);
        p2i.mult(2).assertEq(new Point2D_Integer(2, -6)); p2i.assign(p2iInitial);
        p2i.mult(2.0).assertEq(new Point2D_Integer(2, -6)); p2i.assign(p2iInitial);
        p2i.div(2).assertEq(new Point2D_Integer(1, -1)); p2i.assign(p2iInitial);
        p2i.div(2.0).assertEq(new Point2D_Integer(1, -1)); p2i.assign(p2iInitial);

        p2d.plus(v2i).assertEqDouble(new Point2D_Double(0.1, -1.5), eps); p2d.assign(p2dInitial);
        p2d.plus(v2d).assertEqDouble(new Point2D_Double(-1.8, 3), eps); p2d.assign(p2dInitial);
        p2d.minus(v2i).assertEqDouble(new Point2D_Double(-1.9, 4.5), eps); p2d.assign(p2dInitial);
        p2d.minus(v2d).assertEq(new Point2D_Double(0, 0)); p2d.assign(p2dInitial);
        p2d.mult(2).assertEqDouble(new Point2D_Double(-1.8, 3), eps); p2d.assign(p2dInitial);
        p2d.mult(2.0).assertEqDouble(new Point2D_Double(-1.8, 3), eps); p2d.assign(p2dInitial);
        p2d.div(2).assertEqDouble(new Point2D_Double(-0.45, 0.75), eps); p2d.assign(p2dInitial);
        p2d.div(2.0).assertEqDouble(new Point2D_Double(-0.45, 0.75), eps); p2d.assign(p2dInitial);

        v2i.plus(v2i).assertEq(new Vector2D_Integer(2, -6)); v2i.assign(v2iInitial);
        v2i.plus(v2d).assertEq(new Vector2D_Integer(0, -1)); v2i.assign(v2iInitial);
        v2i.minus(v2i).assertEq(new Vector2D_Integer(0, 0)); v2i.assign(v2iInitial);
        v2i.minus(v2d).assertEq(new Vector2D_Integer(2, -4)); v2i.assign(v2iInitial);
        v2i.mult(2).assertEq(new Vector2D_Integer(2, -6)); v2i.assign(v2iInitial);
        v2i.mult(2.0).assertEq(new Vector2D_Integer(2, -6)); v2i.assign(v2iInitial);
        v2i.div(2).assertEq(new Vector2D_Integer(1, -1)); v2i.assign(v2iInitial);
        v2i.div(2.0).assertEq(new Vector2D_Integer(1, -1)); v2i.assign(v2iInitial);

        v2d.plus(v2i).assertEqDouble(new Vector2D_Double(0.1, -1.5), eps); v2d.assign(v2dInitial);
        v2d.plus(v2d).assertEqDouble(new Vector2D_Double(-1.8, 3), eps); v2d.assign(v2dInitial);
        v2d.minus(v2i).assertEqDouble(new Vector2D_Double(-1.9, 4.5), eps); v2d.assign(v2dInitial);
        v2d.minus(v2d).assertEqDouble(new Vector2D_Double(0, 0), eps); v2d.assign(v2dInitial);
        v2d.mult(2).assertEqDouble(new Vector2D_Double(-1.8, 3), eps); v2d.assign(v2dInitial);
        v2d.mult(2.0).assertEqDouble(new Vector2D_Double(-1.8, 3), eps); v2d.assign(v2dInitial);
        v2d.div(2).assertEqDouble(new Vector2D_Double(-0.45, 0.75), eps); v2d.assign(v2dInitial);
        v2d.div(2.0).assertEqDouble(new Vector2D_Double(-0.45, 0.75), eps); v2d.assign(v2dInitial);

        // Test transitivity of self unary operators
        p2i.plus(v2i).plus(v2i).assertEq(new Point2D_Integer(3, -9)); p2i.assign(p2iInitial);
        p2i.plus(v2i).plus(v2d).assertEq(new Point2D_Integer(1, -4)); p2i.assign(p2iInitial);
        p2i.plus(v2d).plus(v2i).assertEq(new Point2D_Integer(1, -4)); p2i.assign(p2iInitial);
        p2i.plus(v2d).plus(v2d).assertEq(new Point2D_Integer(-1, 1)); p2i.assign(p2iInitial);
        p2i.minus(v2i).minus(v2i).assertEq(new Point2D_Integer(-1, 3)); p2i.assign(p2iInitial);
        p2i.minus(v2i).minus(v2d).assertEq(new Point2D_Integer(1, -1)); p2i.assign(p2iInitial);
        p2i.minus(v2d).minus(v2i).assertEq(new Point2D_Integer(1, -1)); p2i.assign(p2iInitial);
        p2i.minus(v2d).minus(v2d).assertEq(new Point2D_Integer(3, -5)); p2i.assign(p2iInitial);
        p2i.mult(2).mult(2.0).mult(2).assertEq(new Point2D_Integer(8, -24)); p2i.assign(p2iInitial);
        p2i.div(2).div(2.0).div(2).assertEq(new Point2D_Integer(1, 0)); p2i.assign(p2iInitial);
        p2i.divInt(2).divInt(2.0).divInt(2).assertEq(new Point2D_Integer(1, 0)); p2i.assign(p2iInitial);

        p2d.plus(v2i).plus(v2i).assertEqDouble(new Point2D_Double(1.1, -4.5), eps); p2d.assign(p2dInitial);
        p2d.plus(v2i).plus(v2d).assertEqDouble(new Point2D_Double(-0.8, 0), eps); p2d.assign(p2dInitial);
        p2d.plus(v2d).plus(v2i).assertEqDouble(new Point2D_Double(-0.8, 0), eps); p2d.assign(p2dInitial);
        p2d.plus(v2d).plus(v2d).assertEqDouble(new Point2D_Double(-2.7, 4.5), eps); p2d.assign(p2dInitial);
        p2d.minus(v2i).minus(v2i).assertEqDouble(new Point2D_Double(-2.9, 7.5), eps); p2d.assign(p2dInitial);
        p2d.minus(v2i).minus(v2d).assertEqDouble(new Point2D_Double(-1, 3), eps); p2d.assign(p2dInitial);
        p2d.minus(v2d).minus(v2i).assertEqDouble(new Point2D_Double(-1, 3), eps); p2d.assign(p2dInitial);
        p2d.minus(v2d).minus(v2d).assertEqDouble(new Point2D_Double(0.9, -1.5), eps); p2d.assign(p2dInitial);
        p2d.mult(2).mult(2.0).mult(2).assertEqDouble(new Point2D_Double(-7.2, 12), eps); p2d.assign(p2dInitial);
        p2d.div(2).div(2.0).div(2).assertEqDouble(new Point2D_Double(-0.1125, 0.1875), eps); p2d.assign(p2dInitial);

        v2i.plus(v2i).plus(v2i).assertEq(new Vector2D_Integer(4, -12)); v2i.assign(v2iInitial);
        v2i.plus(v2i).plus(v2d).assertEq(new Vector2D_Integer(1, -4)); v2i.assign(v2iInitial);
        v2i.plus(v2d).plus(v2i).assertEq(new Vector2D_Integer(0, -2)); v2i.assign(v2iInitial);
        v2i.plus(v2d).plus(v2d).assertEq(new Vector2D_Integer(-1, 1)); v2i.assign(v2iInitial);
        v2i.minus(v2i).minus(v2i).assertEq(new Vector2D_Integer(0, 0)); v2i.assign(v2iInitial);
        v2i.minus(v2i).minus(v2d).assertEq(new Vector2D_Integer(1, -1)); v2i.assign(v2iInitial);
        v2i.minus(v2d).minus(v2i).assertEq(new Vector2D_Integer(0, 0)); v2i.assign(v2iInitial);
        v2i.minus(v2d).minus(v2d).assertEq(new Vector2D_Integer(3, -5)); v2i.assign(v2iInitial);
        v2i.mult(2).mult(2.0).mult(2).assertEq(new Vector2D_Integer(8, -24)); v2i.assign(v2iInitial);
        v2i.div(2).div(2.0).div(2).assertEq(new Vector2D_Integer(1, 0)); v2i.assign(v2iInitial);
        v2i.divInt(2).divInt(2.0).divInt(2).assertEq(new Vector2D_Integer(1, 0)); v2i.assign(v2iInitial);

        v2d.plus(v2i).plus(v2i).assertEqDouble(new Vector2D_Double(1.1, -4.5), eps); v2d.assign(v2dInitial);
        v2d.plus(v2i).plus(v2d).assertEqDouble(new Vector2D_Double(0.2, -3), eps); v2d.assign(v2dInitial);
        v2d.plus(v2d).plus(v2i).assertEqDouble(new Vector2D_Double(-0.8, 0), eps); v2d.assign(v2dInitial);
        v2d.plus(v2d).plus(v2d).assertEqDouble(new Vector2D_Double(-3.6, 6), eps); v2d.assign(v2dInitial);
        v2d.minus(v2i).minus(v2i).assertEqDouble(new Vector2D_Double(-2.9, 7.5), eps); v2d.assign(v2dInitial);
        v2d.minus(v2i).minus(v2d).assertEq(new Vector2D_Double(0, 0)); v2d.assign(v2dInitial);
        v2d.minus(v2d).minus(v2i).assertEqDouble(new Vector2D_Double(-1, 3), eps); v2d.assign(v2dInitial);
        v2d.minus(v2d).minus(v2d).assertEq(new Vector2D_Double(0, 0)); v2d.assign(v2dInitial);
        v2d.mult(2).mult(2.0).mult(2).assertEqDouble(new Vector2D_Double(-7.2, 12), eps); v2d.assign(v2dInitial);
        v2d.div(2).div(2.0).div(2).assertEqDouble(new Vector2D_Double(-0.1125, 0.1875), eps); v2d.assign(v2dInitial);
        //

        // Test transitivity of non-self unary operators
        p2i.plusClone(v2i).plusClone(v2i).assertEq(new Point2D_Integer(3, -9));
        p2i.plusClone(v2i).plusClone(v2d).assertEqDouble(new Point2D_Double(1.1, -4.5), eps);
        p2i.plusClone(v2d).plusClone(v2i).assertEqDouble(new Point2D_Double(1.1, -4.5), eps);
        p2i.plusClone(v2d).plusClone(v2d).assertEqDouble(new Point2D_Double(-0.8, 0), eps);
        p2i.minusClone(p2i).minusClone(v2i).assertEq(new Vector2D_Integer(-1, 3));
        p2i.minusClone(p2i).minusClone(v2d).assertEqDouble(new Vector2D_Double(0.9, -1.5), eps);
        p2i.minusClone(p2d).minusClone(v2i).assertEqDouble(new Vector2D_Double(0.9, -1.5), eps);
        p2i.minusClone(p2d).minusClone(v2d).assertEqDouble(new Vector2D_Double(2.8, -6), eps);
        p2i.minusClone(v2i).minusClone(v2i).assertEq(new Point2D_Integer(-1, 3));
        p2i.minusClone(v2i).minusClone(v2d).assertEqDouble(new Point2D_Double(0.9, -1.5), eps);
        p2i.minusClone(v2d).minusClone(v2i).assertEqDouble(new Point2D_Double(0.9, -1.5), eps);
        p2i.minusClone(v2d).minusClone(v2d).assertEqDouble(new Point2D_Double(2.8, -6), eps);
        p2i.multClone(2).multClone(2.0).multClone(2).assertEq(new Point2D_Double(8, -24));
        p2i.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Point2D_Double(0.125, -0.375), eps);
        p2i.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Point2D_Integer(1, 0));
        p2i.assertEq(p2iInitial);

        p2d.plusClone(v2i).plusClone(v2i).assertEqDouble(new Point2D_Double(1.1, -4.5), eps);
        p2d.plusClone(v2i).plusClone(v2d).assertEqDouble(new Point2D_Double(-0.8, 0), eps);
        p2d.plusClone(v2d).plusClone(v2i).assertEqDouble(new Point2D_Double(-0.8, 0), eps);
        p2d.plusClone(v2d).plusClone(v2d).assertEqDouble(new Point2D_Double(-2.7, 4.5), eps);
        p2d.minusClone(p2i).minusClone(v2i).assertEqDouble(new Vector2D_Double(-2.9, 7.5), eps);
        p2d.minusClone(p2i).minusClone(v2d).assertEqDouble(new Vector2D_Double(-1, 3), eps);
        p2d.minusClone(p2d).minusClone(v2i).assertEqDouble(new Vector2D_Double(-1, 3), eps);
        p2d.minusClone(p2d).minusClone(v2d).assertEqDouble(new Vector2D_Double(0.9, -1.5), eps);
        p2d.minusClone(v2i).minusClone(v2i).assertEqDouble(new Point2D_Double(-2.9, 7.5), eps);
        p2d.minusClone(v2i).minusClone(v2d).assertEqDouble(new Point2D_Double(-1, 3), eps);
        p2d.minusClone(v2d).minusClone(v2i).assertEqDouble(new Point2D_Double(-1, 3), eps);
        p2d.minusClone(v2d).minusClone(v2d).assertEqDouble(new Point2D_Double(0.9, -1.5), eps);
        p2d.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Point2D_Double(-7.2, 12), eps);
        p2d.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Point2D_Double(-0.1125, 0.1875), eps);
        p2d.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Point2D_Integer(0, 1));
        p2d.assertEq(p2dInitial);

        v2i.plusClone(v2i).plusClone(v2i).assertEq(new Vector2D_Integer(3, -9));
        v2i.plusClone(v2i).plusClone(v2d).assertEqDouble(new Vector2D_Double(1.1, -4.5), eps);
        v2i.plusClone(v2d).plusClone(v2i).assertEqDouble(new Vector2D_Double(1.1, -4.5), eps);
        v2i.plusClone(v2d).plusClone(v2d).assertEqDouble(new Vector2D_Double(-0.8, 0), eps);
        v2i.minusClone(v2i).minusClone(v2i).assertEq(new Vector2D_Integer(-1, 3));
        v2i.minusClone(v2i).minusClone(v2d).assertEqDouble(new Vector2D_Double(0.9, -1.5), eps);
        v2i.minusClone(v2d).minusClone(v2i).assertEqDouble(new Vector2D_Double(0.9, -1.5), eps);
        v2i.minusClone(v2d).minusClone(v2d).assertEqDouble(new Vector2D_Double(2.8, -6), eps);
        v2i.multClone(2).multClone(2.0).multClone(2).assertEq(new Vector2D_Double(8, -24));
        v2i.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Vector2D_Double(0.125, -0.375), eps);
        v2i.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Vector2D_Integer(1, 0));
        v2i.assertEq(v2iInitial);

        v2d.plusClone(v2i).plusClone(v2i).assertEqDouble(new Vector2D_Double(1.1, -4.5), eps);
        v2d.plusClone(v2i).plusClone(v2d).assertEqDouble(new Vector2D_Double(-0.8, 0), eps);
        v2d.plusClone(v2d).plusClone(v2i).assertEqDouble(new Vector2D_Double(-0.8, 0), eps);
        v2d.plusClone(v2d).plusClone(v2d).assertEqDouble(new Vector2D_Double(-2.7, 4.5), eps);
        v2d.minusClone(v2i).minusClone(v2i).assertEqDouble(new Vector2D_Double(-2.9, 7.5), eps);
        v2d.minusClone(v2i).minusClone(v2d).assertEqDouble(new Vector2D_Double(-1, 3), eps);
        v2d.minusClone(v2d).minusClone(v2i).assertEqDouble(new Vector2D_Double(-1, 3), eps);
        v2d.minusClone(v2d).minusClone(v2d).assertEqDouble(new Vector2D_Double(0.9, -1.5), eps);
        v2d.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Vector2D_Double(-7.2, 12), eps);
        v2d.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Vector2D_Double(-0.1125, 0.1875), eps);
        v2d.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Vector2D_Integer(0, 1));
        v2d.assertEq(v2dInitial);

        // Test transitivity of binary operators

        Point2D_Integer.plus2(p2i, v2i).to3D().to2D().assertEq(new Point2D_Integer(2, -6));
        Point2D_Integer.plus2(p2i, v2d).to3D().to2D().assertEqDouble(new Point2D_Double(0.1, -1.5), eps);
        p2i.assertEq(p2iInitial);
        Point2D_Double.plus2(p2d, v2i).to3D().to2D().assertEqDouble(new Point2D_Double(0.1, -1.5), eps);
        Point2D_Double.plus2(p2d, v2d).to3D().to2D().assertEqDouble(new Point2D_Double(-1.8, 3), eps);
        p2d.assertEq(p2dInitial);
        Vector2D_Integer.plus2(v2i, v2i).to3D().to2D().assertEq(new Vector2D_Integer(2, -6));
        Vector2D_Integer.plus2(v2i, v2d).to3D().to2D().assertEqDouble(new Vector2D_Double(0.1, -1.5), eps);
        v2i.assertEq(v2iInitial);
        Vector2D_Double.plus2(v2d, v2i).to3D().to2D().assertEqDouble(new Vector2D_Double(0.1, -1.5), eps);
        Vector2D_Double.plus2(v2d, v2d).to3D().to2D().assertEqDouble(new Vector2D_Double(-1.8, 3), eps);
        v2d.assertEq(v2dInitial);

        Point2D_Integer.minus2(p2i, v2i).to3D().to2D().assertEq(new Point2D_Integer(0, 0));
        Point2D_Integer.minus2(p2i, v2d).to3D().to2D().assertEqDouble(new Point2D_Double(1.9, -4.5), eps);
        Point2D_Integer.minus2(p2i, p2i).to3D().to2D().assertEq(new Vector2D_Integer(0, 0));
        Point2D_Integer.minus2(p2i, p2d).to3D().to2D().assertEqDouble(new Vector2D_Double(1.9, -4.5), eps);
        p2i.assertEq(p2iInitial);
        Point2D_Double.minus2(p2d, v2i).to3D().to2D().assertEqDouble(new Point2D_Double(-1.9, 4.5), eps);
        Point2D_Double.minus2(p2d, v2d).to3D().to2D().assertEq(new Point2D_Double(0, 0));
        Point2D_Double.minus2(p2d, p2i).to3D().to2D().assertEqDouble(new Vector2D_Double(-1.9, 4.5), eps);
        Point2D_Double.minus2(p2d, p2d).to3D().to2D().assertEq(new Vector2D_Double(0, 0));
        p2d.assertEq(p2dInitial);
        Vector2D_Integer.minus2(v2i, v2i).to3D().to2D().assertEq(new Vector2D_Integer(0, 0));
        Vector2D_Integer.minus2(v2i, v2d).to3D().to2D().assertEqDouble(new Vector2D_Double(1.9, -4.5), eps);
        v2i.assertEq(v2iInitial);
        Vector2D_Double.minus2(v2d, v2i).to3D().to2D().assertEqDouble(new Vector2D_Double(-1.9, 4.5), eps);
        Vector2D_Double.minus2(v2d, v2d).to3D().to2D().assertEq(new Vector2D_Double(0, 0));
        v2d.assertEq(v2dInitial);

        Point2D_Integer.mult2(p2i, 2).to3D().to2D().assertEq(new Point2D_Integer(2, -6));
        Point2D_Integer.mult2(p2i, 2.0).to3D().to2D().assertEqDouble(new Point2D_Double(2, -6), eps);
        Point2D_Double.mult2(p2d, 2).to3D().to2D().assertEqDouble(new Point2D_Double(-1.8, 3), eps);
        Point2D_Double.mult2(p2d, 2.0).to3D().to2D().assertEqDouble(new Point2D_Double(-1.8, 3), eps);
        Vector2D_Integer.mult2(v2i, 2).to3D().to2D().assertEq(new Vector2D_Integer(2, -6));
        Vector2D_Integer.mult2(v2i, 2.0).to3D().to2D().assertEqDouble(new Vector2D_Double(2, -6), eps);
        Vector2D_Double.mult2(v2d, 2).to3D().to2D().assertEqDouble(new Vector2D_Double(-1.8, 3), eps);
        Vector2D_Double.mult2(v2d, 2.0).to3D().to2D().assertEqDouble(new Vector2D_Double(-1.8, 3), eps);
        p2i.assertEq(p2iInitial);
        p2d.assertEq(p2dInitial);
        v2i.assertEq(v2iInitial);
        v2d.assertEq(v2dInitial);

        Point2D_Integer.div2(p2i, 2).to3D().to2D().assertEqDouble(new Point2D_Double(0.5, -1.5), eps);
        Point2D_Integer.div2(p2i, 2.0).to3D().to2D().assertEqDouble(new Point2D_Double(0.5, -1.5), eps);
        Point2D_Double.div2(p2d, 2).to3D().to2D().assertEqDouble(new Point2D_Double(-0.45, 0.75), eps);
        Point2D_Double.div2(p2d, 2.0).to3D().to2D().assertEqDouble(new Point2D_Double(-0.45, 0.75), eps);
        Vector2D_Integer.div2(v2i, 2).to3D().to2D().assertEqDouble(new Vector2D_Double(0.5, -1.5), eps);
        Vector2D_Integer.div2(v2i, 2.0).to3D().to2D().assertEqDouble(new Vector2D_Double(0.5, -1.5), eps);
        Vector2D_Double.div2(v2d, 2).to3D().to2D().assertEqDouble(new Vector2D_Double(-0.45, 0.75), eps);
        Vector2D_Double.div2(v2d, 2.0).to3D().to2D().assertEqDouble(new Vector2D_Double(-0.45, 0.75), eps);
        p2i.assertEq(p2iInitial);
        p2d.assertEq(p2dInitial);
        v2i.assertEq(v2iInitial);
        v2d.assertEq(v2dInitial);

        Point2D_Integer.divInt2(p2i, 2).to3D().to2D().assertEq(new Point2D_Integer(1, -1));
        Point2D_Integer.divInt2(p2i, 2.0).to3D().to2D().assertEq(new Point2D_Integer(1, -1));
        Point2D_Double.divInt2(p2d, 2).to3D().to2D().assertEq(new Point2D_Integer(0, 1));
        Point2D_Double.divInt2(p2d, 2.0).to3D().to2D().assertEq(new Point2D_Integer(0, 1));
        Vector2D_Integer.divInt2(v2i, 2).to3D().to2D().assertEq(new Vector2D_Integer(1, -1));
        Vector2D_Integer.divInt2(v2i, 2.0).to3D().to2D().assertEq(new Vector2D_Integer(1, -1));
        Vector2D_Double.divInt2(v2d, 2).to3D().to2D().assertEq(new Vector2D_Integer(0, 1));
        Vector2D_Double.divInt2(v2d, 2.0).to3D().to2D().assertEq(new Vector2D_Integer(0, 1));
        p2i.assertEq(p2iInitial);
        p2d.assertEq(p2dInitial);
        v2i.assertEq(v2iInitial);
        v2d.assertEq(v2dInitial);
    }

    public static void testCortege3DChildren() {

        Point3D_Integer p3i = new Point3D_Integer(-1, 0, 1);
        Point3D_Double p3d = new Point3D_Double(-0.4, 0.6, 0.99);
        Vector3D_Integer v3i = new Vector3D_Integer(-1, 0, 1);
        Vector3D_Double v3d = new Vector3D_Double(0.5, -0.5, 0.01);

        Point3D_Integer p3iInitial = new Point3D_Integer(null, null, null);
        Point3D_Double p3dInitial = new Point3D_Double(null, null, null);
        Vector3D_Integer v3iInitial = new Vector3D_Integer(null, null, null);
        Vector3D_Double v3dInitial = new Vector3D_Double(null, null, null);

        p3iInitial.assign(p3i);
        p3dInitial.assign(p3d);
        v3iInitial.assign(v3i);
        v3dInitial.assign(v3d);

        assert(new Point3D_Integer(0, 0, 0).isZeroCortege());
        assert(new Point3D_Integer(0.0, 0.0, 0.0).isZeroCortege());
        assert(new Point3D_Double(0, 0, 0).isZeroCortege());
        assert(new Point3D_Double(0.0, 0.0, 0.0).isZeroCortege());
        assert(new Vector3D_Integer(0, 0, 0).isZeroCortege());
        assert(new Vector3D_Integer(0.0, 0.0, 0.0).isZeroCortege());
        assert(new Vector3D_Double(0, 0, 0).isZeroCortege());
        assert(new Vector3D_Double(0.0, 0.0, 0.0).isZeroCortege());
        new Point3D_Integer(-0.4, 0.6, 0.99).assertEq(new Point3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.5, 0.5, -0.99).assertEq(new Point3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.6, 0.4, -0.0).assertEq(new Point3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.4, 0.6, 0.99).assertNotEq(new Vector3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.5, 0.5, -0.99).assertNotEq(new Vector3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.6, 0.4, -0.0).assertNotEq(new Vector3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.4, 0.6, 0.99).assertNotEq(new Cortege3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.5, 0.5, -0.99).assertNotEq(new Cortege3D_Integer(0, 0, 0));
        new Point3D_Integer(-0.6, 0.4, -0.0).assertNotEq(new Cortege3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.4, 0.6, 0.99).assertEq(new Vector3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.5, 0.5, -0.99).assertEq(new Vector3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.6, 0.4, -0.0).assertEq(new Vector3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.4, 0.6, 0.99).assertNotEq(new Point3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.5, 0.5, -0.99).assertNotEq(new Point3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.6, 0.4, -0.0).assertNotEq(new Point3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.4, 0.6, 0.99).assertNotEq(new Cortege3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.5, 0.5, -0.99).assertNotEq(new Cortege3D_Integer(0, 0, 0));
        new Vector3D_Integer(-0.6, 0.4, -0.0).assertNotEq(new Cortege3D_Integer(0, 0, 0));

        // Test self unary operators

        p3i.plus(v3i).assertEq(new Point3D_Integer(-2, 0, 2)); p3i.assign(p3iInitial);
        p3i.plus(v3d).assertEq(new Point3D_Integer(0, 0, 1)); p3i.assign(p3iInitial);
        p3i.minus(v3i).assertEq(new Point3D_Integer(0, 0, 0)); p3i.assign(p3iInitial);
        p3i.minus(v3d).assertEq(new Point3D_Integer(-1, 1, 1)); p3i.assign(p3iInitial);
        p3i.mult(2).assertEq(new Point3D_Integer(-2, 0, 2)); p3i.assign(p3iInitial);
        p3i.mult(2.0).assertEq(new Point3D_Integer(-2, 0, 2)); p3i.assign(p3iInitial);
        p3i.div(2).assertEq(new Point3D_Integer(0, 0, 1)); p3i.assign(p3iInitial);
        p3i.div(2.0).assertEq(new Point3D_Integer(0, 0, 1)); p3i.assign(p3iInitial);

        p3d.plus(v3i).assertEqDouble(new Point3D_Double(-1.4, 0.6, 1.99), eps); p3d.assign(p3dInitial);
        p3d.plus(v3d).assertEqDouble(new Point3D_Double(0.1, 0.1, 1), eps); p3d.assign(p3dInitial);
        p3d.minus(v3i).assertEqDouble(new Point3D_Double(0.6, 0.6, -0.01), eps); p3d.assign(p3dInitial);
        p3d.minus(v3d).assertEq(new Point3D_Double(-0.9, 1.1, 0.98)); p3d.assign(p3dInitial);
        p3d.mult(2).assertEqDouble(new Point3D_Double(-0.8, 1.2, 1.98), eps); p3d.assign(p3dInitial);
        p3d.mult(2.0).assertEqDouble(new Point3D_Double(-0.8, 1.2, 1.98), eps); p3d.assign(p3dInitial);
        p3d.div(2).assertEqDouble(new Point3D_Double(-0.2, 0.3, 0.495), eps); p3d.assign(p3dInitial);
        p3d.div(2.0).assertEqDouble(new Point3D_Double(-0.2, 0.3, 0.495), eps); p3d.assign(p3dInitial);

        v3i.plus(v3i).assertEq(new Vector3D_Integer(-2, 0, 2)); v3i.assign(v3iInitial);
        v3i.plus(v3d).assertEq(new Vector3D_Integer(0, 0, 1)); v3i.assign(v3iInitial);
        v3i.minus(v3i).assertEq(new Vector3D_Integer(0, 0, 0)); v3i.assign(v3iInitial);
        v3i.minus(v3d).assertEq(new Vector3D_Integer(-1, 1, 1)); v3i.assign(v3iInitial);
        v3i.mult(2).assertEq(new Vector3D_Integer(-2, 0, 2)); v3i.assign(v3iInitial);
        v3i.mult(2.0).assertEq(new Vector3D_Integer(-2, 0, 2)); v3i.assign(v3iInitial);
        v3i.div(2).assertEq(new Vector3D_Integer(0, 0, 1)); v3i.assign(v3iInitial);
        v3i.div(2.0).assertEq(new Vector3D_Integer(0, 0, 1)); v3i.assign(v3iInitial);

        v3d.plus(v3i).assertEqDouble(new Vector3D_Double(-0.5, -0.5, 1.01), eps); v3d.assign(v3dInitial);
        v3d.plus(v3d).assertEqDouble(new Vector3D_Double(1, -1, 0.02), eps); v3d.assign(v3dInitial);
        v3d.minus(v3i).assertEqDouble(new Vector3D_Double(1.5, -0.5, -0.99), eps); v3d.assign(v3dInitial);
        v3d.minus(v3d).assertEqDouble(new Vector3D_Double(0, 0, 0), eps); v3d.assign(v3dInitial);
        v3d.mult(2).assertEqDouble(new Vector3D_Double(1, -1, 0.02), eps); v3d.assign(v3dInitial);
        v3d.mult(2.0).assertEqDouble(new Vector3D_Double(1, -1, 0.02), eps); v3d.assign(v3dInitial);
        v3d.div(2).assertEqDouble(new Vector3D_Double(0.25, -0.25, 0.005), eps); v3d.assign(v3dInitial);
        v3d.div(2.0).assertEqDouble(new Vector3D_Double(0.25, -0.25, 0.005), eps); v3d.assign(v3dInitial);

        // Test transitivity of self unary operators
        p3i.plus(v3i).plus(v3i).assertEq(new Point3D_Integer(-3, 0, 3)); p3i.assign(p3iInitial);
        p3i.plus(v3i).plus(v3d).assertEq(new Point3D_Integer(-1, 0, 2)); p3i.assign(p3iInitial);
        p3i.plus(v3d).plus(v3i).assertEq(new Point3D_Integer(-1, 0, 2)); p3i.assign(p3iInitial);
        p3i.plus(v3d).plus(v3d).assertEq(new Point3D_Integer(1, 0, 1)); p3i.assign(p3iInitial);
        p3i.minus(v3i).minus(v3i).assertEq(new Point3D_Integer(1, 0, -1)); p3i.assign(p3iInitial);
        p3i.minus(v3i).minus(v3d).assertEq(new Point3D_Integer(0, 1, 0)); p3i.assign(p3iInitial);
        p3i.minus(v3d).minus(v3i).assertEq(new Point3D_Integer(0, 1, 0)); p3i.assign(p3iInitial);
        p3i.minus(v3d).minus(v3d).assertEq(new Point3D_Integer(-1, 2, 1)); p3i.assign(p3iInitial);
        p3i.mult(2).mult(2.0).mult(2).assertEq(new Point3D_Integer(-8, 0, 8)); p3i.assign(p3iInitial);
        p3i.div(2).div(2.0).div(2).assertEq(new Point3D_Integer(0, 0, 1)); p3i.assign(p3iInitial);
        p3i.divInt(2).divInt(2.0).divInt(2).assertEq(new Point3D_Integer(0, 0, 1)); p3i.assign(p3iInitial);

        p3d.plus(v3i).plus(v3i).assertEqDouble(new Point3D_Double(-2.4, 0.6, 2.99), eps); p3d.assign(p3dInitial);
        p3d.plus(v3i).plus(v3d).assertEqDouble(new Point3D_Double(-0.9, 0.1, 2), eps); p3d.assign(p3dInitial);
        p3d.plus(v3d).plus(v3i).assertEqDouble(new Point3D_Double(-0.9, 0.1, 2), eps); p3d.assign(p3dInitial);
        p3d.plus(v3d).plus(v3d).assertEqDouble(new Point3D_Double(0.6, -0.4, 1.01), eps); p3d.assign(p3dInitial);
        p3d.minus(v3i).minus(v3i).assertEqDouble(new Point3D_Double(1.6, 0.6, -1.01), eps); p3d.assign(p3dInitial);
        p3d.minus(v3i).minus(v3d).assertEqDouble(new Point3D_Double(0.1, 1.1, -0.02), eps); p3d.assign(p3dInitial);
        p3d.minus(v3d).minus(v3i).assertEqDouble(new Point3D_Double(0.1, 1.1, -0.02), eps); p3d.assign(p3dInitial);
        p3d.minus(v3d).minus(v3d).assertEqDouble(new Point3D_Double(-1.4, 1.6, 0.97), eps); p3d.assign(p3dInitial);
        p3d.mult(2).mult(2.0).mult(2).assertEqDouble(new Point3D_Double(-3.2, 4.8, 7.92), eps); p3d.assign(p3dInitial);
        p3d.div(2).div(2.0).div(2).assertEqDouble(new Point3D_Double(-0.05, 0.075, 0.12375), eps); p3d.assign(p3dInitial);

        v3i.plus(v3i).plus(v3i).assertEq(new Vector3D_Integer(-4, 0, 4)); v3i.assign(v3iInitial);
        v3i.plus(v3i).plus(v3d).assertEq(new Vector3D_Integer(-1, 0, 2)); v3i.assign(v3iInitial);
        v3i.plus(v3d).plus(v3i).assertEq(new Vector3D_Integer(0, 0, 2)); v3i.assign(v3iInitial);
        v3i.plus(v3d).plus(v3d).assertEq(new Vector3D_Integer(1, 0, 1)); v3i.assign(v3iInitial);
        v3i.minus(v3i).minus(v3i).assertEq(new Vector3D_Integer(0, 0, 0)); v3i.assign(v3iInitial);
        v3i.minus(v3i).minus(v3d).assertEq(new Vector3D_Integer(0, 1, 0)); v3i.assign(v3iInitial);
        v3i.minus(v3d).minus(v3i).assertEq(new Vector3D_Integer(0, 0, 0)); v3i.assign(v3iInitial);
        v3i.minus(v3d).minus(v3d).assertEq(new Vector3D_Integer(-1, 2, 1)); v3i.assign(v3iInitial);
        v3i.mult(2).mult(2.0).mult(2).assertEq(new Vector3D_Integer(-8, 0, 8)); v3i.assign(v3iInitial);
        v3i.div(2).div(2.0).div(2).assertEq(new Vector3D_Integer(0, 0, 1)); v3i.assign(v3iInitial);
        v3i.divInt(2).divInt(2.0).divInt(2).assertEq(new Vector3D_Integer(0, 0, 1)); v3i.assign(v3iInitial);

        v3d.plus(v3i).plus(v3i).assertEqDouble(new Vector3D_Double(-1.5, -0.5, 2.01), eps); v3d.assign(v3dInitial);
        v3d.plus(v3i).plus(v3d).assertEqDouble(new Vector3D_Double(-1, -1, 2.02), eps); v3d.assign(v3dInitial);
        v3d.plus(v3d).plus(v3i).assertEqDouble(new Vector3D_Double(0, -1, 1.02), eps); v3d.assign(v3dInitial);
        v3d.plus(v3d).plus(v3d).assertEqDouble(new Vector3D_Double(2, -2, 0.04), eps); v3d.assign(v3dInitial);
        v3d.minus(v3i).minus(v3i).assertEqDouble(new Vector3D_Double(2.5, -0.5, -1.99), eps); v3d.assign(v3dInitial);
        v3d.minus(v3i).minus(v3d).assertEq(new Vector3D_Double(0, 0, 0)); v3d.assign(v3dInitial);
        v3d.minus(v3d).minus(v3i).assertEqDouble(new Vector3D_Double(1, 0, -1), eps); v3d.assign(v3dInitial);
        v3d.minus(v3d).minus(v3d).assertEq(new Vector3D_Double(0, 0, 0)); v3d.assign(v3dInitial);
        v3d.mult(2).mult(2.0).mult(2).assertEqDouble(new Vector3D_Double(4, -4, 0.08), eps); v3d.assign(v3dInitial);
        v3d.div(2).div(2.0).div(2).assertEqDouble(new Vector3D_Double(0.0625, -0.0625, 0.00125), eps); v3d.assign(v3dInitial);
        //

        // Test transitivity of non-self unary operators
        p3i.plusClone(v3i).plusClone(v3i).assertEq(new Point3D_Integer(-3, 0, 3));
        p3i.plusClone(v3i).plusClone(v3d).assertEqDouble(new Point3D_Double(-1.5, -0.5, 2.01), eps);
        p3i.plusClone(v3d).plusClone(v3i).assertEqDouble(new Point3D_Double(-1.5, -0.5, 2.01), eps);
        p3i.plusClone(v3d).plusClone(v3d).assertEqDouble(new Point3D_Double(0, -1, 1.02), eps);
        p3i.minusClone(p3i).minusClone(v3i).assertEq(new Vector3D_Integer(1, 0, -1));
        p3i.minusClone(p3i).minusClone(v3d).assertEqDouble(new Vector3D_Double(-0.5, 0.5, -0.01), eps);
        p3i.minusClone(p3d).minusClone(v3i).assertEqDouble(new Vector3D_Double(0.4, -0.6, -0.99), eps);
        p3i.minusClone(p3d).minusClone(v3d).assertEqDouble(new Vector3D_Double(-1.1, -0.1, 0), eps);
        p3i.minusClone(v3i).minusClone(v3i).assertEq(new Point3D_Integer(1, 0, -1));
        p3i.minusClone(v3i).minusClone(v3d).assertEqDouble(new Point3D_Double(-0.5, 0.5, -0.01), eps);
        p3i.minusClone(v3d).minusClone(v3i).assertEqDouble(new Point3D_Double(-0.5, 0.5, -0.01), eps);
        p3i.minusClone(v3d).minusClone(v3d).assertEqDouble(new Point3D_Double(-2, 1, 0.98), eps);
        p3i.multClone(2).multClone(2.0).multClone(2).assertEq(new Point3D_Double(-8, 0, 8));
        p3i.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Point3D_Double(-0.125, 0, 0.125), eps);
        p3i.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Point3D_Integer(0, 0, 1));
        p3i.assertEq(p3iInitial);

        p3d.plusClone(v3i).plusClone(v3i).assertEqDouble(new Point3D_Double(-2.4, 0.6, 2.99), eps);
        p3d.plusClone(v3i).plusClone(v3d).assertEqDouble(new Point3D_Double(-0.9, 0.1, 2), eps);
        p3d.plusClone(v3d).plusClone(v3i).assertEqDouble(new Point3D_Double(-0.9, 0.1, 2), eps);
        p3d.plusClone(v3d).plusClone(v3d).assertEqDouble(new Point3D_Double(0.6, -0.4, 1.01), eps);
        p3d.minusClone(p3i).minusClone(v3i).assertEqDouble(new Vector3D_Double(1.6, 0.6, -1.01), eps);
        p3d.minusClone(p3i).minusClone(v3d).assertEqDouble(new Vector3D_Double(0.1, 1.1, -0.02), eps);
        p3d.minusClone(p3d).minusClone(v3i).assertEqDouble(new Vector3D_Double(1, 0, -1), eps);
        p3d.minusClone(p3d).minusClone(v3d).assertEqDouble(new Vector3D_Double(-0.5, 0.5, -0.01), eps);
        p3d.minusClone(v3i).minusClone(v3i).assertEqDouble(new Point3D_Double(1.6, 0.6, -1.01), eps);
        p3d.minusClone(v3i).minusClone(v3d).assertEqDouble(new Point3D_Double(0.1, 1.1, -0.02), eps);
        p3d.minusClone(v3d).minusClone(v3i).assertEqDouble(new Point3D_Double(0.1, 1.1, -0.02), eps);
        p3d.minusClone(v3d).minusClone(v3d).assertEqDouble(new Point3D_Double(-1.4, 1.6, 0.97), eps);
        p3d.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Point3D_Double(-3.2, 4.8, 7.92), eps);
        p3d.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Point3D_Double(-0.05, 0.075, 0.12375), eps);
        p3d.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Point3D_Integer(0, 0, 0));
        p3d.assertEq(p3dInitial);

        v3i.plusClone(v3i).plusClone(v3i).assertEq(new Vector3D_Integer(-3, 0, 3));
        v3i.plusClone(v3i).plusClone(v3d).assertEqDouble(new Vector3D_Double(-1.5, -0.5, 2.01), eps);
        v3i.plusClone(v3d).plusClone(v3i).assertEqDouble(new Vector3D_Double(-1.5, -0.5, 2.01), eps);
        v3i.plusClone(v3d).plusClone(v3d).assertEqDouble(new Vector3D_Double(0, -1, 1.02), eps);
        v3i.minusClone(v3i).minusClone(v3i).assertEq(new Vector3D_Integer(1, 0, -1));
        v3i.minusClone(v3i).minusClone(v3d).assertEqDouble(new Vector3D_Double(-0.5, 0.5, -0.01), eps);
        v3i.minusClone(v3d).minusClone(v3i).assertEqDouble(new Vector3D_Double(-0.5, 0.5, -0.01), eps);
        v3i.minusClone(v3d).minusClone(v3d).assertEqDouble(new Vector3D_Double(-2, 1, 0.98), eps);
        v3i.multClone(2).multClone(2.0).multClone(2).assertEq(new Vector3D_Double(-8, 0, 8));
        v3i.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Vector3D_Double(-0.125, 0, 0.125), eps);
        v3i.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Vector3D_Integer(0, 0, 1));
        v3i.assertEq(v3iInitial);

        v3d.plusClone(v3i).plusClone(v3i).assertEqDouble(new Vector3D_Double(-1.5, -0.5, 2.01), eps);
        v3d.plusClone(v3i).plusClone(v3d).assertEqDouble(new Vector3D_Double(0, -1, 1.02), eps);
        v3d.plusClone(v3d).plusClone(v3i).assertEqDouble(new Vector3D_Double(0, -1, 1.02), eps);
        v3d.plusClone(v3d).plusClone(v3d).assertEqDouble(new Vector3D_Double(1.5, -1.5, 0.03), eps);
        v3d.minusClone(v3i).minusClone(v3i).assertEqDouble(new Vector3D_Double(2.5, -0.5, -1.99), eps);
        v3d.minusClone(v3i).minusClone(v3d).assertEqDouble(new Vector3D_Double(1, 0, -1), eps);
        v3d.minusClone(v3d).minusClone(v3i).assertEqDouble(new Vector3D_Double(1, 0, -1), eps);
        v3d.minusClone(v3d).minusClone(v3d).assertEqDouble(new Vector3D_Double(-0.5, 0.5, -0.01), eps);
        v3d.multClone(2).multClone(2.0).multClone(2).assertEqDouble(new Vector3D_Double(4, -4, 0.08), eps);
        v3d.divClone(2).divClone(2.0).divClone(2).assertEqDouble(new Vector3D_Double(0.0625, -0.0625, 0.00125), eps);
        v3d.divIntClone(2).divIntClone(2.0).divIntClone(2).assertEq(new Vector3D_Integer(0, 0, 0));
        v3d.assertEq(v3dInitial);

        // Test transitivity of binary operators

        Point3D_Integer.plus2(p3i, v3i).to2D().to3D().assertEq(new Point3D_Integer(-2, 0, 0));
        Point3D_Integer.plus2(p3i, v3d).to2D().to3D().assertEqDouble(new Point3D_Double(-0.5, -0.5, 0), eps);
        p3i.assertEq(p3iInitial);
        Point3D_Double.plus2(p3d, v3i).to2D().to3D().assertEqDouble(new Point3D_Double(-1.4, 0.6, 0), eps);
        Point3D_Double.plus2(p3d, v3d).to2D().to3D().assertEqDouble(new Point3D_Double(0.1, 0.1, 0), eps);
        p3d.assertEq(p3dInitial);
        Vector3D_Integer.plus2(v3i, v3i).to2D().to3D().assertEq(new Vector3D_Integer(-2, 0, 0));
        Vector3D_Integer.plus2(v3i, v3d).to2D().to3D().assertEqDouble(new Vector3D_Double(-0.5, -0.5, 0), eps);
        v3i.assertEq(v3iInitial);
        Vector3D_Double.plus2(v3d, v3i).to2D().to3D().assertEqDouble(new Vector3D_Double(-0.5, -0.5, 0), eps);
        Vector3D_Double.plus2(v3d, v3d).to2D().to3D().assertEqDouble(new Vector3D_Double(1, -1, 0), eps);
        v3d.assertEq(v3dInitial);

        Point3D_Integer.minus2(p3i, v3i).to2D().to3D().assertEq(new Point3D_Integer(0, 0, 0));
        Point3D_Integer.minus2(p3i, v3d).to2D().to3D().assertEqDouble(new Point3D_Double(-1.5, 0.5, 0), eps);
        Point3D_Integer.minus2(p3i, p3i).to2D().to3D().assertEq(new Vector3D_Integer(0, 0, 0));
        Point3D_Integer.minus2(p3i, p3d).to2D().to3D().assertEqDouble(new Vector3D_Double(-0.6, -0.6, 0), eps);
        p3i.assertEq(p3iInitial);
        Point3D_Double.minus2(p3d, v3i).to2D().to3D().assertEqDouble(new Point3D_Double(0.6, 0.6, 0), eps);
        Point3D_Double.minus2(p3d, v3d).to2D().to3D().assertEq(new Point3D_Double(-0.9, 1.1, 0));
        Point3D_Double.minus2(p3d, p3i).to2D().to3D().assertEqDouble(new Vector3D_Double(0.6, 0.6, 0), eps);
        Point3D_Double.minus2(p3d, p3d).to2D().to3D().assertEq(new Vector3D_Double(0, 0, 0));
        p3d.assertEq(p3dInitial);
        Vector3D_Integer.minus2(v3i, v3i).to2D().to3D().assertEq(new Vector3D_Integer(0, 0, 0));
        Vector3D_Integer.minus2(v3i, v3d).to2D().to3D().assertEqDouble(new Vector3D_Double(-1.5, 0.5, 0), eps);
        v3i.assertEq(v3iInitial);
        Vector3D_Double.minus2(v3d, v3i).to2D().to3D().assertEqDouble(new Vector3D_Double(1.5, -0.5, 0), eps);
        Vector3D_Double.minus2(v3d, v3d).to2D().to3D().assertEq(new Vector3D_Double(0, 0, 0));
        v3d.assertEq(v3dInitial);

        Point3D_Integer.mult2(p3i, 2).to2D().to3D().assertEq(new Point3D_Integer(-2, 0, 0));
        Point3D_Integer.mult2(p3i, 2.0).to2D().to3D().assertEqDouble(new Point3D_Double(-2, 0, 0), eps);
        Point3D_Double.mult2(p3d, 2).to2D().to3D().assertEqDouble(new Point3D_Double(-0.8, 1.2, 0), eps);
        Point3D_Double.mult2(p3d, 2.0).to2D().to3D().assertEqDouble(new Point3D_Double(-0.8, 1.2, 0), eps);
        Vector3D_Integer.mult2(v3i, 2).to2D().to3D().assertEq(new Vector3D_Integer(-2, 0, 0));
        Vector3D_Integer.mult2(v3i, 2.0).to2D().to3D().assertEqDouble(new Vector3D_Double(-2, 0, 0), eps);
        Vector3D_Double.mult2(v3d, 2).to2D().to3D().assertEqDouble(new Vector3D_Double(1, -1, 0), eps);
        Vector3D_Double.mult2(v3d, 2.0).to2D().to3D().assertEqDouble(new Vector3D_Double(1, -1, 0), eps);
        p3i.assertEq(p3iInitial);
        p3d.assertEq(p3dInitial);
        v3i.assertEq(v3iInitial);
        v3d.assertEq(v3dInitial);

        Point3D_Integer.div2(p3i, 2).to2D().to3D().assertEqDouble(new Point3D_Double(-0.5, 0, 0), eps);
        Point3D_Integer.div2(p3i, 2.0).to2D().to3D().assertEqDouble(new Point3D_Double(-0.5, 0, 0), eps);
        Point3D_Double.div2(p3d, 2).to2D().to3D().assertEqDouble(new Point3D_Double(-0.2, 0.3, 0), eps);
        Point3D_Double.div2(p3d, 2.0).to2D().to3D().assertEqDouble(new Point3D_Double(-0.2, 0.3, 0), eps);
        Vector3D_Integer.div2(v3i, 2).to2D().to3D().assertEqDouble(new Vector3D_Double(-0.5, 0, 0), eps);
        Vector3D_Integer.div2(v3i, 2.0).to2D().to3D().assertEqDouble(new Vector3D_Double(-0.5, 0, 0), eps);
        Vector3D_Double.div2(v3d, 2).to2D().to3D().assertEqDouble(new Vector3D_Double(0.25, -0.25, 0.005), eps);
        Vector3D_Double.div2(v3d, 2.0).to2D().to3D().assertEqDouble(new Vector3D_Double(0.25, -0.25, 0.005), eps);
        p3i.assertEq(p3iInitial);
        p3d.assertEq(p3dInitial);
        v3i.assertEq(v3iInitial);
        v3d.assertEq(v3dInitial);

        Point3D_Integer.divInt2(p3i, 2).to2D().to3D().assertEq(new Point3D_Integer(0, 0, 0));
        Point3D_Integer.divInt2(p3i, 2.0).to2D().to3D().assertEq(new Point3D_Integer(0, 0, 0));
        Point3D_Double.divInt2(p3d, 2).to2D().to3D().assertEq(new Point3D_Integer(0, 0, 0));
        Point3D_Double.divInt2(p3d, 2.0).to2D().to3D().assertEq(new Point3D_Integer(0, 0, 0));
        Vector3D_Integer.divInt2(v3i, 2).to2D().to3D().assertEq(new Vector3D_Integer(0, 0, 0));
        Vector3D_Integer.divInt2(v3i, 2.0).to2D().to3D().assertEq(new Vector3D_Integer(0, 0, 0));
        Vector3D_Double.divInt2(v3d, 2).to2D().to3D().assertEq(new Vector3D_Integer(0, 0, 0));
        Vector3D_Double.divInt2(v3d, 2.0).to2D().to3D().assertEq(new Vector3D_Integer(0, 0, 0));
        p3i.assertEq(p3iInitial);
        p3d.assertEq(p3dInitial);
        v3i.assertEq(v3iInitial);
        v3d.assertEq(v3dInitial);
    }

    public static void testForbidden2D_plus(Cortege2D_Integer c2i_l, Cortege2D_Double c2d_l, Cortege2D_Integer c2i_r, Cortege2D_Double c2d_r) {

        // (Integer) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c2i_l.plus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).plus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Integer) , (Double)
        try { // This is a trick to test that the exception was thrown
            c2i_l.plus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).plus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c2d_l.plus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).plus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Double)
        try { // This is a trick to test that the exception was thrown
            c2d_l.plus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).plus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden2D_plusClone(Cortege2D_Integer c2i_l, Cortege2D_Double c2d_l, Cortege2D_Integer c2i_r, Cortege2D_Double c2d_r) {
        try { // This is a trick to test that the exception was thrown
            c2i_l.plusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).plusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c2i_l.plusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).plusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c2d_l.plusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).plusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c2d_l.plusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).plusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden2D_plus2(Cortege2D_Integer c2i_l, Cortege2D_Double c2d_l, Cortege2D_Integer c2i_r, Cortege2D_Double c2d_r) {

        try { // This is a trick to test that the exception was thrown
            Cortege2D_Integer.plus2(c2i_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }


        try { // This is a trick to test that the exception was thrown
            Cortege2D_Integer.plus2(c2i_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege2D_Double.plus2(c2d_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege2D_Double.plus2(c2d_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }

    public static void testForbidden2D_minus(Cortege2D_Integer c2i_l, Cortege2D_Double c2d_l, Cortege2D_Integer c2i_r, Cortege2D_Double c2d_r) {

        // (Integer) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c2i_l.minus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).minus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Integer) , (Double)
        try { // This is a trick to test that the exception was thrown
            c2i_l.minus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).minus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c2d_l.minus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).minus(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Double)
        try { // This is a trick to test that the exception was thrown
            c2d_l.minus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).minus(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden2D_minusClone(Cortege2D_Integer c2i_l, Cortege2D_Double c2d_l, Cortege2D_Integer c2i_r, Cortege2D_Double c2d_r) {
        try { // This is a trick to test that the exception was thrown
            c2i_l.minusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).minusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c2i_l.minusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Integer)c2i_l).minusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c2d_l.minusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).minusClone(c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c2d_l.minusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege2D_Double)c2d_l).minusClone(c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden2D_minus2(Cortege2D_Integer c2i_l, Cortege2D_Double c2d_l, Cortege2D_Integer c2i_r, Cortege2D_Double c2d_r) {

        try { // This is a trick to test that the exception was thrown
            Cortege2D_Integer.minus2(c2i_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }


        try { // This is a trick to test that the exception was thrown
            Cortege2D_Integer.minus2(c2i_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege2D_Double.minus2(c2d_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d_l, c2i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege2D_Double.minus2(c2d_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d_l, c2d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }

    public static void testForbidden3D_plus(Cortege3D_Integer c3i_l, Cortege3D_Double c3d_l, Cortege3D_Integer c3i_r, Cortege3D_Double c3d_r) {

        // (Integer) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c3i_l.plus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).plus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Integer) , (Double)
        try { // This is a trick to test that the exception was thrown
            c3i_l.plus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).plus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c3d_l.plus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).plus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Double)
        try { // This is a trick to test that the exception was thrown
            c3d_l.plus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).plus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden3D_plusClone(Cortege3D_Integer c3i_l, Cortege3D_Double c3d_l, Cortege3D_Integer c3i_r, Cortege3D_Double c3d_r) {
        try { // This is a trick to test that the exception was thrown
            c3i_l.plusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).plusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c3i_l.plusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).plusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c3d_l.plusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).plusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c3d_l.plusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).plusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden3D_plus2(Cortege3D_Integer c3i_l, Cortege3D_Double c3d_l, Cortege3D_Integer c3i_r, Cortege3D_Double c3d_r) {

        try { // This is a trick to test that the exception was thrown
            Cortege3D_Integer.plus2(c3i_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }


        try { // This is a trick to test that the exception was thrown
            Cortege3D_Integer.plus2(c3i_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege3D_Double.plus2(c3d_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege3D_Double.plus2(c3d_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }

    public static void testForbidden3D_minus(Cortege3D_Integer c3i_l, Cortege3D_Double c3d_l, Cortege3D_Integer c3i_r, Cortege3D_Double c3d_r) {

        // (Integer) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c3i_l.minus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).minus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Integer) , (Double)
        try { // This is a trick to test that the exception was thrown
            c3i_l.minus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).minus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Integer)
        try { // This is a trick to test that the exception was thrown
            c3d_l.minus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).minus(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // (Double) , (Double)
        try { // This is a trick to test that the exception was thrown
            c3d_l.minus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).minus(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden3D_minusClone(Cortege3D_Integer c3i_l, Cortege3D_Double c3d_l, Cortege3D_Integer c3i_r, Cortege3D_Double c3d_r) {
        try { // This is a trick to test that the exception was thrown
            c3i_l.minusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).minusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c3i_l.minusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Integer)c3i_l).minusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c3d_l.minusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).minusClone(c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            c3d_l.minusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            ((Cortege3D_Double)c3d_l).minusClone(c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

    }

    public static void testForbidden3D_minus2(Cortege3D_Integer c3i_l, Cortege3D_Double c3d_l, Cortege3D_Integer c3i_r, Cortege3D_Double c3d_r) {

        try { // This is a trick to test that the exception was thrown
            Cortege3D_Integer.minus2(c3i_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }


        try { // This is a trick to test that the exception was thrown
            Cortege3D_Integer.minus2(c3i_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege3D_Double.minus2(c3d_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d_l, c3i_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Cortege3D_Double.minus2(c3d_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d_l, c3d_r); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }

    // TODO: it would be very very nice to pass function and dimension as parameters ...
    public static void testNotSupportedCombinationsForbidden2D_plus() {

        Cortege2D_Integer c2i = new Cortege2D_Integer(0, 0);
        Cortege2D_Double c2d = new Cortege2D_Double(0, 0);
        Point2D_Integer p2i = new Point2D_Integer(0, 0);
        Point2D_Double p2d = new Point2D_Double(0, 0);
        Vector2D_Integer v2i = new Vector2D_Integer(0, 0);
        Vector2D_Double v2d = new Vector2D_Double(0, 0);

        // pass Cortege to Vector
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(c2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(c2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Point to Vector
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(p2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(p2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(p2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(p2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(p2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(p2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(p2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(p2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(p2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.plus2(p2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(p2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.plus2(p2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Cortege to Point
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(c2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(c2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Vector to Point
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(v2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(v2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(v2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(v2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(v2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(v2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(v2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(v2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(v2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.plus2(v2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(v2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.plus2(v2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }
    public static void testNotSupportedCombinationsForbidden2D_minus() {

        Cortege2D_Integer c2i = new Cortege2D_Integer(0, 0);
        Cortege2D_Double c2d = new Cortege2D_Double(0, 0);
        Point2D_Integer p2i = new Point2D_Integer(0, 0);
        Point2D_Double p2d = new Point2D_Double(0, 0);
        Vector2D_Integer v2i = new Vector2D_Integer(0, 0);
        Vector2D_Double v2d = new Vector2D_Double(0, 0);

        // pass Cortege to Vector
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(c2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(c2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Point to Vector
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(p2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(p2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(p2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(p2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(p2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(p2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(p2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(p2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(p2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Integer.minus2(p2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(p2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector2D_Double.minus2(p2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Cortege to Point
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(c2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(c2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Vector to Point
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(v2i, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(v2i, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(v2d, c2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(v2d, c2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(v2i, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(v2i, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(v2d, p2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(v2d, p2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(v2i, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Integer.minus2(v2i, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(v2d, v2i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point2D_Double.minus2(v2d, v2d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }
    public static void testNotSupportedCombinationsForbidden3D_plus() {

        Cortege3D_Integer c3i = new Cortege3D_Integer(0, 0, 0);
        Cortege3D_Double c3d = new Cortege3D_Double(0, 0, 0);
        Point3D_Integer p3i = new Point3D_Integer(0, 0, 0);
        Point3D_Double p3d = new Point3D_Double(0, 0, 0);
        Vector3D_Integer v3i = new Vector3D_Integer(0, 0, 0);
        Vector3D_Double v3d = new Vector3D_Double(0, 0, 0);

        // pass Cortege to Vector
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(c3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(c3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Point to Vector
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(p3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(p3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(p3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(p3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(p3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(p3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(p3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(p3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(p3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.plus2(p3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(p3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.plus2(p3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Cortege to Point
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(c3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(c3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Vector to Point
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(v3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(v3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(v3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(v3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(v3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(v3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(v3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(v3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(v3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.plus2(v3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(v3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.plus2(v3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }
    public static void testNotSupportedCombinationsForbidden3D_minus() {

        Cortege3D_Integer c3i = new Cortege3D_Integer(0, 0, 0);
        Cortege3D_Double c3d = new Cortege3D_Double(0, 0, 0);
        Point3D_Integer p3i = new Point3D_Integer(0, 0, 0);
        Point3D_Double p3d = new Point3D_Double(0, 0, 0);
        Vector3D_Integer v3i = new Vector3D_Integer(0, 0, 0);
        Vector3D_Double v3d = new Vector3D_Double(0, 0, 0);

        // pass Cortege to Vector
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(c3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(c3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Point to Vector
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(p3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(p3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(p3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(p3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(p3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(p3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(p3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(p3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(p3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Integer.minus2(p3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(p3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Vector3D_Double.minus2(p3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Cortege to Point
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(c3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(c3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        // pass Vector to Point
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(v3i, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(v3i, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(v3d, c3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(v3d, c3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(v3i, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(v3i, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(v3d, p3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(v3d, p3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }

        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(v3i, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Integer.minus2(v3i, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(v3d, v3i); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
        try { // This is a trick to test that the exception was thrown
            Point3D_Double.minus2(v3d, v3d); // exception must be thrown from here
            assert (false); // if this line is reached it means the test is failed
        } catch (IllegalArgumentException e) { /* the test is passed */ }
    }

    public static void testOperatorRestrictions2D() {

        Point2D_Integer p2i = new Point2D_Integer(0, 0);
        Point2D_Double p2d = new Point2D_Double(0, 0);
        Vector2D_Integer v2i = new Vector2D_Integer(0, 0);
        Vector2D_Double v2d = new Vector2D_Double(0, 0);

        testForbidden2D_plus(p2i, p2d, p2i, p2d); // point += point = NOT ALLOWED
        testForbidden2D_plus(v2i, v2d, p2i, p2d); // vector += point = NOT ALLOWED
        testForbidden2D_plusClone(p2i, p2d, p2i, p2d); // point + point = NOT ALLOWED
        testForbidden2D_plusClone(v2i, v2d, p2i, p2d); // vector + point = NOT ALLOWED
        testForbidden2D_plus2(p2i, p2d, p2i, p2d); // z = point + point = NOT ALLOWED
        testForbidden2D_plus2(v2i, v2d, p2i, p2d); // z = vector + point = NOT ALLOWED

        testForbidden2D_minus(p2i, p2d, p2i, p2d); // point -= point = NOT ALLOWED
        testForbidden2D_minus(v2i, v2d, p2i, p2d); // vector -= point = NOT ALLOWED
        testForbidden2D_minusClone(v2i, v2d, p2i, p2d); // vector - point = NOT ALLOWED
        testForbidden2D_minus2(v2i, v2d, p2i, p2d); // z = vector - point = NOT ALLOWED

        testNotSupportedCombinationsForbidden2D_plus();
        testNotSupportedCombinationsForbidden2D_minus();
    }

    public static void testOperatorRestrictions3D() {

        Point3D_Integer p3i = new Point3D_Integer(0, 0, 0);
        Point3D_Double p3d = new Point3D_Double(0, 0, 0);
        Vector3D_Integer v3i = new Vector3D_Integer(0, 0, 0);
        Vector3D_Double v3d = new Vector3D_Double(0, 0, 0);

        testForbidden3D_plus(p3i, p3d, p3i, p3d); // point += point = NOT ALLOWED
        testForbidden3D_plus(v3i, v3d, p3i, p3d); // vector += point = NOT ALLOWED
        testForbidden3D_plusClone(p3i, p3d, p3i, p3d); // point + point = NOT ALLOWED
        testForbidden3D_plusClone(v3i, v3d, p3i, p3d); // vector + point = NOT ALLOWED
        testForbidden3D_plus2(p3i, p3d, p3i, p3d); // z = point + point = NOT ALLOWED
        testForbidden3D_plus2(v3i, v3d, p3i, p3d); // z = vector + point = NOT ALLOWED

        testForbidden3D_minus(p3i, p3d, p3i, p3d); // point -= point = NOT ALLOWED
        testForbidden3D_minus(v3i, v3d, p3i, p3d); // vector -= point = NOT ALLOWED
        testForbidden3D_minusClone(v3i, v3d, p3i, p3d); // vector - point = NOT ALLOWED
        testForbidden3D_minus2(v3i, v3d, p3i, p3d); // z = vector - point = NOT ALLOWED

        testNotSupportedCombinationsForbidden3D_plus();
        testNotSupportedCombinationsForbidden3D_minus();
    }

    public static void testOperators() {

        assert(Cortege2D_Integer.class.isAssignableFrom(Point2D_Integer.class));
        assert(Cortege2D_Double.class.isAssignableFrom(Point2D_Double.class));
        assert(Cortege2D_Integer.class.isAssignableFrom(Vector2D_Integer.class));
        assert(Cortege2D_Double.class.isAssignableFrom(Vector2D_Double.class));

        assert(Cortege3D_Integer.class.isAssignableFrom(Point3D_Integer.class));
        assert(Cortege3D_Double.class.isAssignableFrom(Point3D_Double.class));
        assert(Cortege3D_Integer.class.isAssignableFrom(Vector3D_Integer.class));
        assert(Cortege3D_Double.class.isAssignableFrom(Vector3D_Double.class));

        testCortege2D();
        testCortege3D();

        testCortege2DChildren();
        testCortege3DChildren();

        testOperatorRestrictions2D();
        testOperatorRestrictions3D();
    }
}
