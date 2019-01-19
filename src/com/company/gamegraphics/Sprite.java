// https://habr.com/post/145433/
package com.gamegraphics;

import com.gamethread.Main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Sprite {
    private String fileName;
    private BufferedImage image = null;

    public Sprite(String fileName) {
        if (fileName != null && !fileName.equals("")) {
            // Leave a possibility to create an empty Sprite and fill it later
            this.setImage(fileName);
        }
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
            e.printStackTrace();
            Main.terminateNoGiveUp(
                    1000,
                    "Could not set Sprite: ./res/drawable/" + filename + " could not be read/found."
            );
        }

        this.fileName = filename;
    }

    public void render(Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.GREEN);
        g.drawImage(this.image, x, y, w, h, null);
    }
}
