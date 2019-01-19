package com.gamegraphics;

import com.gametools.ResourceManager;

import java.awt.image.BufferedImage;

// TODO Delete Dis
public class SpriteManager extends ResourceManager {
    private BufferedImage image;

    public SpriteManager(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
