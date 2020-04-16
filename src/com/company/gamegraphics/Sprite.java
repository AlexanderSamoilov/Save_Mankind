// https://habr.com/post/145433/
package com.company.gamegraphics;

import com.company.gamegeom._3d.Parallelepiped;

import com.company.gamethread.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
//    private static Logger LOG = LogManager.getLogger(Sprite.class.getName());

    private String fileName;
    private BufferedImage image = null;

    // Store the Angle of rotation of sprite
    public AffineTransform rotation;

    public Sprite(String fileName) {
        if (fileName != null && !fileName.equals("")) {
            // Leave a possibility to create an empty Sprite and fill it later
            this.setImage(fileName);
        }

        this.rotation = new AffineTransform();
    }

    // FIXME Getter() to Class.attr
    public BufferedImage getImage() {
        return this.image;
    }

    //TODO We must wait loading of image
    public void setImage(String filename) {
        try {
            this.image = ImageIO.read(new File("./res/drawable/" + filename));
        } catch (IOException e) {
            // FIXME -> Main.terminateNoGiveUp()
            Main.terminateNoGiveUp(e,
                    1000,
                    "Could not set Sprite: ./res/drawable/" + filename + " could not be read/found."
            );
        }

        this.fileName = filename;
    }

    public void draw(Graphics g, Parallelepiped parallelepiped, double rotation_angle) {
        AffineTransform saveAT = ((Graphics2D) g).getTransform();

        if (rotation_angle != 0) {
            this.rotation.setToIdentity();
            this.rotation.rotate(rotation_angle, parallelepiped.getAbsCenterDouble().x(), parallelepiped.getAbsCenterDouble().y());
            ((Graphics2D) g).transform(rotation);
        }

        g.setColor(Color.GREEN);
        g.drawImage(image, parallelepiped.getAbsLoc().x(), parallelepiped.getAbsLoc().y(), parallelepiped.getAbsDim().x(), parallelepiped.getAbsDim().y(), null);
        ((Graphics2D) g).setTransform(saveAT);
    }
}
