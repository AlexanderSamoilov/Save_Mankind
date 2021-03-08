package com.company.gamegraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO; // https://habr.com/post/145433/

import com.company.gamegeom._3d.Parallelepiped;
import com.company.gamethread.M_Thread;

public class Sprite {
//    private static Logger LOG = LogManager.getLogger(Sprite.class.getName());

    //private String fileName;
    private BufferedImage image = null;

    // Store the Angle of rotation of sprite
    private AffineTransform rotation;

    public Sprite(String fileName) {
        if (fileName != null && !fileName.equals("")) {
            // Leave a possibility to create an empty Sprite and fill it later
            this.setImage(fileName);
        }

        this.rotation = new AffineTransform();
    }

    public void setImage(String filename) {
        if (filename == null) {
            throw new NullPointerException("setImage: filename is null");
        }

        try {
            this.image = ImageIO.read(new File("./res/drawable/" + filename));
        } catch (IOException e) {
            M_Thread.terminateNoGiveUp(e,
                    1000,
                    "Could not set Sprite: ./res/drawable/" + filename + " could not be read/found."
            );
        }

        //this.fileName = filename;
    }

    public void draw(Graphics g, Parallelepiped ppd, double rotation_angle) {
        AffineTransform saveAT = ((Graphics2D) g).getTransform();

        if (rotation_angle != 0) {
            this.rotation.setToIdentity();
            this.rotation.rotate(rotation_angle, ppd.getAbsCenterDouble().x(), ppd.getAbsCenterDouble().y());
            ((Graphics2D) g).transform(rotation);
        }

        g.setColor(Color.GREEN);
        g.drawImage(image, ppd.loc.x(), ppd.loc.y(), ppd.dim.x(), ppd.dim.y(), null);
        ((Graphics2D) g).setTransform(saveAT);
    }
}
