package com.company.gamegraphics;

import com.company.gametools.ResourceManager;

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
