package com.company.gamegeom.vectormath;

import com.company.gamegeom.vectormath.cortege.Cortege2D_Double;
import com.company.gamegeom.vectormath.cortege.Cortege2D_Integer;
import com.company.gamegeom.vectormath.cortege.Cortege3D_Double;
import com.company.gamegeom.vectormath.cortege.Cortege3D_Integer;
import com.company.gamegeom.vectormath.point.Point2D_Double;
import com.company.gamegeom.vectormath.point.Point2D_Integer;
import com.company.gamegeom.vectormath.point.Point3D_Double;
import com.company.gamegeom.vectormath.point.Point3D_Integer;
import com.company.gamegeom.vectormath.vector.Vector2D_Double;
import com.company.gamegeom.vectormath.vector.Vector2D_Integer;
import com.company.gamegeom.vectormath.vector.Vector3D_Double;
import com.company.gamegeom.vectormath.vector.Vector3D_Integer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CortegeTest {

    private static Logger LOG = LogManager.getLogger(CortegeTest.class.getName());

    public static void testCortege2D() {

        Cortege2D_Integer c2i = new Cortege2D_Integer(0, 0);
        Cortege2D_Double c2d = new Cortege2D_Double(0.0, 0.0);

        // Test self unary operators
        c2i.plus(c2i);
        c2i.plus(c2d);
        c2i.minus(c2i);
        c2i.minus(c2d);
        c2i.mult(2);
        c2i.mult(2.0);
        c2i.div(2);
        c2i.div(2.0);
        c2d.plus(c2i);
        c2d.plus(c2d);
        c2d.minus(c2i);
        c2d.minus(c2d);
        c2d.mult(2);
        c2d.mult(2.0);
        c2d.div(2);
        c2d.div(2.0);

        // Test transitivity of self unary operators
        LOG.debug(c2i.plus(c2i).plus(c2i).toString());
        LOG.debug(c2i.plus(c2i).plus(c2d).toString());
        LOG.debug(c2i.plus(c2d).plus(c2i).toString());
        LOG.debug(c2i.plus(c2d).plus(c2d).toString());
        LOG.debug(c2i.minus(c2i).minus(c2i).toString());
        LOG.debug(c2i.minus(c2i).minus(c2d).toString());
        LOG.debug(c2i.minus(c2d).minus(c2i).toString());
        LOG.debug(c2i.minus(c2d).minus(c2d).toString());
        LOG.debug(c2i.mult(2).mult(2.0).mult(2).toString());
        LOG.debug(c2i.div(2).div(2.0).div(2).toString());
        LOG.debug(c2i.divInt(2).divInt(2.0).divInt(2).toString());

        LOG.debug(c2d.plus(c2i).plus(c2i).toString());
        LOG.debug(c2d.plus(c2i).plus(c2d).toString());
        LOG.debug(c2d.plus(c2d).plus(c2i).toString());
        LOG.debug(c2d.plus(c2d).plus(c2d).toString());
        LOG.debug(c2d.minus(c2i).minus(c2i).toString());
        LOG.debug(c2d.minus(c2i).minus(c2d).toString());
        LOG.debug(c2d.minus(c2d).minus(c2i).toString());
        LOG.debug(c2d.minus(c2d).minus(c2d).toString());
        LOG.debug(c2d.mult(2).mult(2.0).mult(2).toString());
        LOG.debug(c2d.div(2).div(2.0).div(2).toString());
        //

        // Test transitivity of non-self unary operators
        LOG.debug(c2i.plus1(c2i).plus1(c2i).toString());
        LOG.debug(c2i.plus1(c2i).plus1(c2d).toString());
        LOG.debug(c2i.plus1(c2d).plus1(c2i).toString());
        LOG.debug(c2i.plus1(c2d).plus1(c2d).toString());
        LOG.debug(c2i.minus1(c2i).minus1(c2i).toString());
        LOG.debug(c2i.minus1(c2i).minus1(c2d).toString());
        LOG.debug(c2i.minus1(c2d).minus1(c2i).toString());
        LOG.debug(c2i.minus1(c2d).minus1(c2d).toString());
        LOG.debug(c2i.mult1(2).mult1(2.0).mult1(2).toString());
        LOG.debug(c2i.div1(2).div1(2.0).div1(2).toString());
        LOG.debug(c2i.divInt1(2).divInt1(2.0).divInt1(2).toString());

        LOG.debug(c2d.plus1(c2i).plus1(c2i).toString());
        LOG.debug(c2d.plus1(c2i).plus1(c2d).toString());
        LOG.debug(c2d.plus1(c2d).plus1(c2i).toString());
        LOG.debug(c2d.plus1(c2d).plus1(c2d).toString());
        LOG.debug(c2d.minus1(c2i).minus1(c2i).toString());
        LOG.debug(c2d.minus1(c2i).minus1(c2d).toString());
        LOG.debug(c2d.minus1(c2d).minus1(c2i).toString());
        LOG.debug(c2d.minus1(c2d).minus1(c2d).toString());
        LOG.debug(c2d.mult1(2).mult1(2.0).mult1(2).toString());
        LOG.debug(c2d.div1(2).div1(2.0).div1(2).toString());
        LOG.debug(c2d.divInt1(2).divInt1(2.0).divInt1(2).toString());

        // Test transitivity of binary operators
        LOG.debug(Cortege2D_Integer.plus2(c2i, c2i).to3D().to2D());
        LOG.debug(Cortege2D_Integer.plus2(c2i, c2d).to3D().to2D());
        LOG.debug(Cortege2D_Double.plus2(c2d, c2i).to3D().to2D());
        LOG.debug(Cortege2D_Double.plus2(c2d, c2d).to3D().to2D());
        LOG.debug(Cortege2D_Integer.minus2(c2i, c2i).to3D().to2D());
        LOG.debug(Cortege2D_Integer.minus2(c2i, c2d).to3D().to2D());
        LOG.debug(Cortege2D_Double.minus2(c2d, c2i).to3D().to2D());
        LOG.debug(Cortege2D_Double.minus2(c2d, c2d).to3D().to2D());
        LOG.debug(Cortege2D_Integer.mult2(c2i, 2).to3D().to2D());
        LOG.debug(Cortege2D_Integer.mult2(c2i, 2.0).to3D().to2D());
        LOG.debug(Cortege2D_Double.mult2(c2d, 2).to3D().to2D());
        LOG.debug(Cortege2D_Double.mult2(c2d, 2.0).to3D().to2D());
        LOG.debug(Cortege2D_Integer.div2(c2i, 2).to3D().to2D());
        LOG.debug(Cortege2D_Integer.div2(c2i, 2.0).to3D().to2D());
        LOG.debug(Cortege2D_Double.div2(c2d, 2).to3D().to2D());
        LOG.debug(Cortege2D_Double.div2(c2d, 2.0).to3D().to2D());
        LOG.debug(Cortege2D_Integer.divInt2(c2i, 2).to3D().to2D());
        LOG.debug(Cortege2D_Integer.divInt2(c2i, 2.0).to3D().to2D());
        LOG.debug(Cortege2D_Double.divInt2(c2d, 2).to3D().to2D());
        LOG.debug(Cortege2D_Double.divInt2(c2d, 2.0).to3D().to2D());

    }

    public static void testCortege3D() {

        Cortege3D_Integer c3i = new Cortege3D_Integer(0, 0, 0);
        Cortege3D_Double c3d = new Cortege3D_Double(0.0, 0.0, 0.0);

        // Test self unary operators
        c3i.plus(c3i);
        c3i.plus(c3d);
        c3i.minus(c3i);
        c3i.minus(c3d);
        c3i.mult(2);
        c3i.mult(2.0);
        c3i.div(2);
        c3i.div(2.0);
        c3d.plus(c3i);
        c3d.plus(c3d);
        c3d.minus(c3i);
        c3d.minus(c3d);
        c3d.mult(2);
        c3d.mult(2.0);
        c3d.div(2);
        c3d.div(2.0);

        // Test transitivity of self unary operators
        LOG.debug(c3i.plus(c3i).plus(c3i).toString());
        LOG.debug(c3i.plus(c3i).plus(c3d).toString());
        LOG.debug(c3i.plus(c3d).plus(c3i).toString());
        LOG.debug(c3i.plus(c3d).plus(c3d).toString());
        LOG.debug(c3i.minus(c3i).minus(c3i).toString());
        LOG.debug(c3i.minus(c3i).minus(c3d).toString());
        LOG.debug(c3i.minus(c3d).minus(c3i).toString());
        LOG.debug(c3i.minus(c3d).minus(c3d).toString());
        LOG.debug(c3i.mult(2).mult(2.0).mult(2).toString());
        LOG.debug(c3i.div(2).div(2.0).div(2).toString());
        LOG.debug(c3i.divInt(2).divInt(2.0).divInt(2).toString());

        LOG.debug(c3d.plus(c3i).plus(c3i).toString());
        LOG.debug(c3d.plus(c3i).plus(c3d).toString());
        LOG.debug(c3d.plus(c3d).plus(c3i).toString());
        LOG.debug(c3d.plus(c3d).plus(c3d).toString());
        LOG.debug(c3d.minus(c3i).minus(c3i).toString());
        LOG.debug(c3d.minus(c3i).minus(c3d).toString());
        LOG.debug(c3d.minus(c3d).minus(c3i).toString());
        LOG.debug(c3d.minus(c3d).minus(c3d).toString());
        LOG.debug(c3d.mult(2).mult(2.0).mult(2).toString());
        LOG.debug(c3d.div(2).div(2.0).div(2).toString());
        //

        // Test transitivity of non-self unary operators
        LOG.debug(c3i.plus1(c3i).plus1(c3i).toString());
        LOG.debug(c3i.plus1(c3i).plus1(c3d).toString());
        LOG.debug(c3i.plus1(c3d).plus1(c3i).toString());
        LOG.debug(c3i.plus1(c3d).plus1(c3d).toString());
        LOG.debug(c3i.minus1(c3i).minus1(c3i).toString());
        LOG.debug(c3i.minus1(c3i).minus1(c3d).toString());
        LOG.debug(c3i.minus1(c3d).minus1(c3i).toString());
        LOG.debug(c3i.minus1(c3d).minus1(c3d).toString());
        LOG.debug(c3i.mult1(2).mult1(2.0).mult1(2).toString());
        LOG.debug(c3i.div1(2).div1(2.0).div1(2).toString());
        LOG.debug(c3i.divInt1(2).divInt1(2.0).divInt1(2).toString());

        LOG.debug(c3d.plus1(c3i).plus1(c3i).toString());
        LOG.debug(c3d.plus1(c3i).plus1(c3d).toString());
        LOG.debug(c3d.plus1(c3d).plus1(c3i).toString());
        LOG.debug(c3d.plus1(c3d).plus1(c3d).toString());
        LOG.debug(c3d.minus1(c3i).minus1(c3i).toString());
        LOG.debug(c3d.minus1(c3i).minus1(c3d).toString());
        LOG.debug(c3d.minus1(c3d).minus1(c3i).toString());
        LOG.debug(c3d.minus1(c3d).minus1(c3d).toString());
        LOG.debug(c3d.mult1(2).mult1(2.0).mult1(2).toString());
        LOG.debug(c3d.div1(2).div1(2.0).div1(2).toString());
        LOG.debug(c3d.divInt1(2).divInt1(2.0).divInt1(2).toString());

        // Test transitivity of binary operators
        LOG.debug(Cortege3D_Integer.plus2(c3i, c3i).to2D().to3D());
        LOG.debug(Cortege3D_Integer.plus2(c3i, c3d).to2D().to3D());
        LOG.debug(Cortege3D_Double.plus2(c3d, c3i).to2D().to3D());
        LOG.debug(Cortege3D_Double.plus2(c3d, c3d).to2D().to3D());
        LOG.debug(Cortege3D_Integer.minus2(c3i, c3i).to2D().to3D());
        LOG.debug(Cortege3D_Integer.minus2(c3i, c3d).to2D().to3D());
        LOG.debug(Cortege3D_Double.minus2(c3d, c3i).to2D().to3D());
        LOG.debug(Cortege3D_Double.minus2(c3d, c3d).to2D().to3D());
        LOG.debug(Cortege3D_Integer.mult2(c3i, 2).to2D().to3D());
        LOG.debug(Cortege3D_Integer.mult2(c3i, 2.0).to2D().to3D());
        LOG.debug(Cortege3D_Double.mult2(c3d, 2).to2D().to3D());
        LOG.debug(Cortege3D_Double.mult2(c3d, 2.0).to2D().to3D());
        LOG.debug(Cortege3D_Integer.div2(c3i, 2).to2D().to3D());
        LOG.debug(Cortege3D_Integer.div2(c3i, 2.0).to2D().to3D());
        LOG.debug(Cortege3D_Double.div2(c3d, 2).to2D().to3D());
        LOG.debug(Cortege3D_Double.div2(c3d, 2.0).to2D().to3D());
        LOG.debug(Cortege3D_Integer.divInt2(c3i, 2).to2D().to3D());
        LOG.debug(Cortege3D_Integer.divInt2(c3i, 2.0).to2D().to3D());
        LOG.debug(Cortege3D_Double.divInt2(c3d, 2).to2D().to3D());
        LOG.debug(Cortege3D_Double.divInt2(c3d, 2.0).to2D().to3D());

    }

    public static void testCortege2DChildren() {

        Point2D_Integer p2i = new Point2D_Integer(0, 0);
        Point2D_Double p2d = new Point2D_Double(0.0, 0.0);
        Vector2D_Integer v2i = new Vector2D_Integer(1, 1);
        Vector2D_Double v2d = new Vector2D_Double(1.0, 1.0);

        // Test self unary operators
        p2i.plus(p2i);
        p2i.plus(p2d);
        p2i.plus(v2i);
        p2i.plus(v2d);
        p2i.minus(p2i);
        p2i.minus(p2d);
        p2i.minus(v2i);
        p2i.minus(v2d);
        p2i.mult(2);
        p2i.mult(2.0);
        p2i.div(2);
        p2i.div(2.0);

        p2d.plus(p2i);
        p2d.plus(p2d);
        p2d.plus(v2i);
        p2d.plus(v2d);
        p2d.minus(p2i);
        p2d.minus(p2d);
        p2d.minus(v2i);
        p2d.minus(v2d);
        p2d.mult(2);
        p2d.mult(2.0);
        p2d.div(2);
        p2d.div(2.0);

        // Test transitivity of self unary operators
        LOG.debug(p2i.plus(v2i).plus(v2i));
        LOG.debug(p2i.plus(v2i).plus(v2d).toString());
        LOG.debug(p2i.plus(v2d).plus(v2i).toString());
        LOG.debug(p2i.plus(v2d).plus(v2d).toString());
        LOG.debug(p2i.minus(v2i).minus(v2i));
        LOG.debug(p2i.minus(v2i).minus(v2d).toString());
        LOG.debug(p2i.minus(v2d).minus(v2i).toString());
        LOG.debug(p2i.minus(v2d).minus(v2d).toString());
        LOG.debug(p2i.mult(2).mult(2.0).mult(2));
        LOG.debug(p2i.div(2).div(2.0).div(2).toString());
        LOG.debug(p2i.divInt(2).divInt(2.0).divInt(2));

        LOG.debug(p2d.plus(v2i).plus(v2i));
        LOG.debug(p2d.plus(v2i).plus(v2d));
        LOG.debug(p2d.plus(v2d).plus(v2i));
        LOG.debug(p2d.plus(v2d).plus(v2d));
        LOG.debug(p2d.minus(v2i).minus(v2i));
        LOG.debug(p2d.minus(v2i).minus(v2d));
        LOG.debug(p2d.minus(v2d).minus(v2i));
        LOG.debug(p2d.minus(v2d).minus(v2d));
        LOG.debug(p2d.mult(2).mult(2.0).mult(2));
        LOG.debug(p2d.div(2).div(2.0).div(2));

        LOG.debug(v2i.plus(v2i).plus(v2i));
        LOG.debug(v2i.plus(v2i).plus(v2d).toString());
        LOG.debug(v2i.plus(v2d).plus(v2i).toString());
        LOG.debug(v2i.plus(v2d).plus(v2d).toString());
        LOG.debug(v2i.minus(v2i).minus(v2i));
        LOG.debug(v2i.minus(v2i).minus(v2d).toString());
        LOG.debug(v2i.minus(v2d).minus(v2i).toString());
        LOG.debug(v2i.minus(v2d).minus(v2d).toString());
        LOG.debug(v2i.mult(2).mult(2.0).mult(2));
        LOG.debug(v2i.div(2).div(2.0).div(2).toString());
        LOG.debug(v2i.divInt(2).divInt(2.0).divInt(2));

        LOG.debug(v2d.plus(v2i).plus(v2i));
        LOG.debug(v2d.plus(v2i).plus(v2d));
        LOG.debug(v2d.plus(v2d).plus(v2i));
        LOG.debug(v2d.plus(v2d).plus(v2d));
        LOG.debug(v2d.minus(v2i).minus(v2i));
        LOG.debug(v2d.minus(v2i).minus(v2d));
        LOG.debug(v2d.minus(v2d).minus(v2i));
        LOG.debug(v2d.minus(v2d).minus(v2d));
        LOG.debug(v2d.mult(2).mult(2.0).mult(2));
        LOG.debug(v2d.div(2).div(2.0).div(2));
        //

        // Test transitivity of non-self unary operators
        LOG.debug(p2i.plus1(v2i).plus1(v2i));
        LOG.debug(p2i.plus1(v2i).plus1(v2d));
        LOG.debug(p2i.plus1(v2d).plus1(v2i));
        LOG.debug(p2i.plus1(v2d).plus1(v2d));
        LOG.debug(p2i.minus1(v2i).minus1(v2i));
        LOG.debug(p2i.minus1(v2i).minus1(v2d));
        LOG.debug(p2i.minus1(v2d).minus1(v2i));
        LOG.debug(p2i.minus1(v2d).minus1(v2d));
        LOG.debug(p2i.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(p2i.div1(2).div1(2.0).div1(2));
        LOG.debug(p2i.divInt1(2).divInt1(2.0).divInt1(2));

        LOG.debug(p2d.plus1(v2i).plus1(v2i));
        LOG.debug(p2d.plus1(v2i).plus1(v2d));
        LOG.debug(p2d.plus1(v2d).plus1(v2i));
        LOG.debug(p2d.plus1(v2d).plus1(v2d));
        LOG.debug(p2d.minus1(v2i).minus1(v2i));
        LOG.debug(p2d.minus1(v2i).minus1(v2d));
        LOG.debug(p2d.minus1(v2d).minus1(v2i));
        LOG.debug(p2d.minus1(v2d).minus1(v2d));
        LOG.debug(p2d.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(p2d.div1(2).div1(2.0).div1(2));
        LOG.debug(p2d.divInt1(2).divInt1(2.0).divInt1(2));

        LOG.debug(v2i.plus1(v2i).plus1(v2i));
        LOG.debug(v2i.plus1(v2i).plus1(v2d));
        LOG.debug(v2i.plus1(v2d).plus1(v2i));
        LOG.debug(v2i.plus1(v2d).plus1(v2d));
        LOG.debug(v2i.minus1(v2i).minus1(v2i));
        LOG.debug(v2i.minus1(v2i).minus1(v2d));
        LOG.debug(v2i.minus1(v2d).minus1(v2i));
        LOG.debug(v2i.minus1(v2d).minus1(v2d));
        LOG.debug(v2i.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(v2i.div1(2).div1(2.0).div1(2));
        LOG.debug(v2i.divInt1(2).divInt1(2.0).divInt1(2));

        LOG.debug(v2d.plus1(v2i).plus1(v2i));
        LOG.debug(v2d.plus1(v2i).plus1(v2d));
        LOG.debug(v2d.plus1(v2d).plus1(v2i));
        LOG.debug(v2d.plus1(v2d).plus1(v2d));
        LOG.debug(v2d.minus1(v2i).minus1(v2i));
        LOG.debug(v2d.minus1(v2i).minus1(v2d));
        LOG.debug(v2d.minus1(v2d).minus1(v2i));
        LOG.debug(v2d.minus1(v2d).minus1(v2d));
        LOG.debug(v2d.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(v2d.div1(2).div1(2.0).div1(2));
        LOG.debug(v2d.divInt1(2).divInt1(2.0).divInt1(2));

        // Test transitivity of binary operators
        LOG.debug(Point2D_Integer.plus2(p2i, v2i).to3D().to2D());
        LOG.debug(Point2D_Integer.plus2(p2i, v2d).to3D().to2D());
        LOG.debug(Vector2D_Integer.plus2(v2i, v2i).to3D().to2D());
        LOG.debug(Vector2D_Integer.plus2(v2i, v2d).to3D().to2D());
        LOG.debug(Vector2D_Double.plus2(v2d, v2i).to3D().to2D());
        LOG.debug(Vector2D_Double.plus2(v2d, v2d).to3D().to2D());

        LOG.debug(Point2D_Integer.minus2(p2i, v2i).to3D().to2D());
        LOG.debug(Point2D_Integer.minus2(p2i, v2d).to3D().to2D());
        LOG.debug(Vector2D_Integer.minus2(v2i, v2i).to3D().to2D());
        LOG.debug(Vector2D_Integer.minus2(v2i, v2d).to3D().to2D());
        LOG.debug(Vector2D_Double.minus2(v2d, v2i).to3D().to2D());
        LOG.debug(Vector2D_Double.minus2(v2d, v2d).to3D().to2D());

        LOG.debug(Point2D_Integer.mult2(p2i, 2).to3D().to2D());
        LOG.debug(Point2D_Integer.mult2(p2i, 2.0).to3D().to2D());
        LOG.debug(Point2D_Double.mult2(p2d, 2).to3D().to2D());
        LOG.debug(Point2D_Double.mult2(p2d, 2.0).to3D().to2D());
        LOG.debug(Vector2D_Integer.mult2(v2i, 2).to3D().to2D());
        LOG.debug(Vector2D_Integer.mult2(v2i, 2.0).to3D().to2D());
        LOG.debug(Vector2D_Double.mult2(v2d, 2).to3D().to2D());
        LOG.debug(Vector2D_Double.mult2(v2d, 2.0).to3D().to2D());

        LOG.debug(Point2D_Integer.div2(p2i, 2).to3D().to2D());
        LOG.debug(Point2D_Integer.div2(p2i, 2.0).to3D().to2D());
        LOG.debug(Point2D_Double.div2(p2d, 2).to3D().to2D());
        LOG.debug(Point2D_Double.div2(p2d, 2.0).to3D().to2D());
        LOG.debug(Vector2D_Integer.div2(v2i, 2).to3D().to2D());
        LOG.debug(Vector2D_Integer.div2(v2i, 2.0).to3D().to2D());
        LOG.debug(Vector2D_Double.div2(v2d, 2).to3D().to2D());
        LOG.debug(Vector2D_Double.div2(v2d, 2.0).to3D().to2D());

        LOG.debug(Point2D_Integer.divInt2(p2i, 2).to3D().to2D());
        LOG.debug(Point2D_Integer.divInt2(p2i, 2.0).to3D().to2D());
        LOG.debug(Point2D_Double.divInt2(p2d, 2).to3D().to2D());
        LOG.debug(Point2D_Double.divInt2(p2d, 2.0).to3D().to2D());
        LOG.debug(Vector2D_Integer.divInt2(v2i, 2).to3D().to2D());
        LOG.debug(Vector2D_Integer.divInt2(v2i, 2.0).to3D().to2D());
        LOG.debug(Vector2D_Double.divInt2(v2d, 2).to3D().to2D());
        LOG.debug(Vector2D_Double.divInt2(v2d, 2.0).to3D().to2D());
    }

    public static void testCortege3DChildren() {

        Point3D_Integer p3i = new Point3D_Integer(0, 0, 0);
        Point3D_Double p3d = new Point3D_Double(0.0, 0.0, 0.0);
        Vector3D_Integer v3i = new Vector3D_Integer(1, 1, 1);
        Vector3D_Double v3d = new Vector3D_Double(1.0, 1.0, 1.0);

        // Test self unary operators
        p3i.plus(p3i);
        p3i.plus(p3d);
        p3i.plus(v3i);
        p3i.plus(v3d);
        p3i.minus(p3i);
        p3i.minus(p3d);
        p3i.minus(v3i);
        p3i.minus(v3d);
        p3i.mult(2);
        p3i.mult(2.0);
        p3i.div(2);
        p3i.div(2.0);

        p3d.plus(p3i);
        p3d.plus(p3d);
        p3d.plus(v3i);
        p3d.plus(v3d);
        p3d.minus(p3i);
        p3d.minus(p3d);
        p3d.minus(v3i);
        p3d.minus(v3d);
        p3d.mult(2);
        p3d.mult(2.0);
        p3d.div(2);
        p3d.div(2.0);

        // Test transitivity of self unary operators
        LOG.debug(p3i.plus(v3i).plus(v3i));
        LOG.debug(p3i.plus(v3i).plus(v3d).toString());
        LOG.debug(p3i.plus(v3d).plus(v3i).toString());
        LOG.debug(p3i.plus(v3d).plus(v3d).toString());
        LOG.debug(p3i.minus(v3i).minus(v3i));
        LOG.debug(p3i.minus(v3i).minus(v3d).toString());
        LOG.debug(p3i.minus(v3d).minus(v3i).toString());
        LOG.debug(p3i.minus(v3d).minus(v3d).toString());
        LOG.debug(p3i.mult(2).mult(2.0).mult(2));
        LOG.debug(p3i.div(2).div(2.0).div(2).toString());
        LOG.debug(p3i.divInt(2).divInt(2.0).divInt(2));

        LOG.debug(p3d.plus(v3i).plus(v3i));
        LOG.debug(p3d.plus(v3i).plus(v3d));
        LOG.debug(p3d.plus(v3d).plus(v3i));
        LOG.debug(p3d.plus(v3d).plus(v3d));
        LOG.debug(p3d.minus(v3i).minus(v3i));
        LOG.debug(p3d.minus(v3i).minus(v3d));
        LOG.debug(p3d.minus(v3d).minus(v3i));
        LOG.debug(p3d.minus(v3d).minus(v3d));
        LOG.debug(p3d.mult(2).mult(2.0).mult(2));
        LOG.debug(p3d.div(2).div(2.0).div(2));

        LOG.debug(v3i.plus(v3i).plus(v3i));
        LOG.debug(v3i.plus(v3i).plus(v3d).toString());
        LOG.debug(v3i.plus(v3d).plus(v3i).toString());
        LOG.debug(v3i.plus(v3d).plus(v3d).toString());
        LOG.debug(v3i.minus(v3i).minus(v3i));
        LOG.debug(v3i.minus(v3i).minus(v3d).toString());
        LOG.debug(v3i.minus(v3d).minus(v3i).toString());
        LOG.debug(v3i.minus(v3d).minus(v3d).toString());
        LOG.debug(v3i.mult(2).mult(2.0).mult(2));
        LOG.debug(v3i.div(2).div(2.0).div(2).toString());
        LOG.debug(v3i.divInt(2).divInt(2.0).divInt(2));

        LOG.debug(v3d.plus(v3i).plus(v3i));
        LOG.debug(v3d.plus(v3i).plus(v3d));
        LOG.debug(v3d.plus(v3d).plus(v3i));
        LOG.debug(v3d.plus(v3d).plus(v3d));
        LOG.debug(v3d.minus(v3i).minus(v3i));
        LOG.debug(v3d.minus(v3i).minus(v3d));
        LOG.debug(v3d.minus(v3d).minus(v3i));
        LOG.debug(v3d.minus(v3d).minus(v3d));
        LOG.debug(v3d.mult(2).mult(2.0).mult(2));
        LOG.debug(v3d.div(2).div(2.0).div(2));
        //

        // Test transitivity of non-self unary operators
        LOG.debug(p3i.plus1(v3i).plus1(v3i));
        LOG.debug(p3i.plus1(v3i).plus1(v3d));
        LOG.debug(p3i.plus1(v3d).plus1(v3i));
        LOG.debug(p3i.plus1(v3d).plus1(v3d));
        LOG.debug(p3i.minus1(v3i).minus1(v3i));
        LOG.debug(p3i.minus1(v3i).minus1(v3d));
        LOG.debug(p3i.minus1(v3d).minus1(v3i));
        LOG.debug(p3i.minus1(v3d).minus1(v3d));
        LOG.debug(p3i.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(p3i.div1(2).div1(2.0).div1(2));
        LOG.debug(p3i.divInt1(2).divInt1(2.0).divInt1(2));

        LOG.debug(p3d.plus1(v3i).plus1(v3i));
        LOG.debug(p3d.plus1(v3i).plus1(v3d));
        LOG.debug(p3d.plus1(v3d).plus1(v3i));
        LOG.debug(p3d.plus1(v3d).plus1(v3d));
        LOG.debug(p3d.minus1(v3i).minus1(v3i));
        LOG.debug(p3d.minus1(v3i).minus1(v3d));
        LOG.debug(p3d.minus1(v3d).minus1(v3i));
        LOG.debug(p3d.minus1(v3d).minus1(v3d));
        LOG.debug(p3d.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(p3d.div1(2).div1(2.0).div1(2));
        LOG.debug(p3d.divInt1(2).divInt1(2.0).divInt1(2));

        LOG.debug(v3i.plus1(v3i).plus1(v3i));
        LOG.debug(v3i.plus1(v3i).plus1(v3d));
        LOG.debug(v3i.plus1(v3d).plus1(v3i));
        LOG.debug(v3i.plus1(v3d).plus1(v3d));
        LOG.debug(v3i.minus1(v3i).minus1(v3i));
        LOG.debug(v3i.minus1(v3i).minus1(v3d));
        LOG.debug(v3i.minus1(v3d).minus1(v3i));
        LOG.debug(v3i.minus1(v3d).minus1(v3d));
        LOG.debug(v3i.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(v3i.div1(2).div1(2.0).div1(2));
        LOG.debug(v3i.divInt1(2).divInt1(2.0).divInt1(2));

        LOG.debug(v3d.plus1(v3i).plus1(v3i));
        LOG.debug(v3d.plus1(v3i).plus1(v3d));
        LOG.debug(v3d.plus1(v3d).plus1(v3i));
        LOG.debug(v3d.plus1(v3d).plus1(v3d));
        LOG.debug(v3d.minus1(v3i).minus1(v3i));
        LOG.debug(v3d.minus1(v3i).minus1(v3d));
        LOG.debug(v3d.minus1(v3d).minus1(v3i));
        LOG.debug(v3d.minus1(v3d).minus1(v3d));
        LOG.debug(v3d.mult1(2).mult1(2.0).mult1(2));
        LOG.debug(v3d.div1(2).div1(2.0).div1(2));
        LOG.debug(v3d.divInt1(2).divInt1(2.0).divInt1(2));

        // Test transitivity of binary operators
        LOG.debug(Point3D_Integer.plus2(p3i, v3i).to2D().to3D());
        LOG.debug(Point3D_Integer.plus2(p3i, v3d).to2D().to3D());
        LOG.debug(Vector3D_Integer.plus2(v3i, v3i).to2D().to3D());
        LOG.debug(Vector3D_Integer.plus2(v3i, v3d).to2D().to3D());
        LOG.debug(Vector3D_Double.plus2(v3d, v3i).to2D().to3D());
        LOG.debug(Vector3D_Double.plus2(v3d, v3d).to2D().to3D());

        LOG.debug(Point3D_Integer.minus2(p3i, v3i).to2D().to3D());
        LOG.debug(Point3D_Integer.minus2(p3i, v3d).to2D().to3D());
        LOG.debug(Vector3D_Integer.minus2(v3i, v3i).to2D().to3D());
        LOG.debug(Vector3D_Integer.minus2(v3i, v3d).to2D().to3D());
        LOG.debug(Vector3D_Double.minus2(v3d, v3i).to2D().to3D());
        LOG.debug(Vector3D_Double.minus2(v3d, v3d).to2D().to3D());

        LOG.debug(Point3D_Integer.mult2(p3i, 2).to2D().to3D());
        LOG.debug(Point3D_Integer.mult2(p3i, 2.0).to2D().to3D());
        LOG.debug(Point3D_Double.mult2(p3d, 2).to2D().to3D());
        LOG.debug(Point3D_Double.mult2(p3d, 2.0).to2D().to3D());
        LOG.debug(Vector3D_Integer.mult2(v3i, 2).to2D().to3D());
        LOG.debug(Vector3D_Integer.mult2(v3i, 2.0).to2D().to3D());
        LOG.debug(Vector3D_Double.mult2(v3d, 2).to2D().to3D());
        LOG.debug(Vector3D_Double.mult2(v3d, 2.0).to2D().to3D());

        LOG.debug(Point3D_Integer.div2(p3i, 2).to2D().to3D());
        LOG.debug(Point3D_Integer.div2(p3i, 2.0).to2D().to3D());
        LOG.debug(Point3D_Double.div2(p3d, 2).to2D().to3D());
        LOG.debug(Point3D_Double.div2(p3d, 2.0).to2D().to3D());
        LOG.debug(Vector3D_Integer.div2(v3i, 2).to2D().to3D());
        LOG.debug(Vector3D_Integer.div2(v3i, 2.0).to2D().to3D());
        LOG.debug(Vector3D_Double.div2(v3d, 2).to2D().to3D());
        LOG.debug(Vector3D_Double.div2(v3d, 2.0).to2D().to3D());

        LOG.debug(Point3D_Integer.divInt2(p3i, 2).to2D().to3D());
        LOG.debug(Point3D_Integer.divInt2(p3i, 2.0).to2D().to3D());
        LOG.debug(Point3D_Double.divInt2(p3d, 2).to2D().to3D());
        LOG.debug(Point3D_Double.divInt2(p3d, 2.0).to2D().to3D());
        LOG.debug(Vector3D_Integer.divInt2(v3i, 2).to2D().to3D());
        LOG.debug(Vector3D_Integer.divInt2(v3i, 2.0).to2D().to3D());
        LOG.debug(Vector3D_Double.divInt2(v3d, 2).to2D().to3D());
        LOG.debug(Vector3D_Double.divInt2(v3d, 2.0).to2D().to3D());
    }

    public static void testOperators() {
        testCortege2D();
        testCortege3D();
        testCortege2DChildren();
        testCortege3DChildren();
    }
}
