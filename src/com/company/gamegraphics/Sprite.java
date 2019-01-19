// https://habr.com/post/145433/
package com.gamegraphics;

import com.gamethread.Main;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Sprite {
    private final static Map<String, SpriteManager> spriteMap = new HashMap<String, SpriteManager>();
    private String fileName;
    private SpriteManager spriteManager;

    public Sprite(String fileName) throws IIOException, IOException {
        this.fileName = fileName;

        if (fileName != null) { // leave a possibility to create an empty Sprite and fill it later
            SpriteManager oldSprite = spriteMap.get(fileName);
            if (oldSprite != null) {
                this.spriteManager = oldSprite;
                this.spriteManager.addReference();
            } else {
                try {
                    this.spriteManager = new SpriteManager(
                            ImageIO.read(new File("./res/drawable/" + fileName))
                    );
                } catch (IIOException e) {
                    Main.printMsg("Could not initialize Sprite: the picture " + "./res/drawable/" + fileName + " could not be read/found.");
                    throw e;
                } catch (IOException e) {
                    Main.printMsg("Could not initialize Sprite: the picture " + "./res/drawable/" + fileName + " could not be read/found.");
                    throw e;
                }
            }
        }
    }

    public void setImage(String filename) throws IIOException, IOException {
        this.fileName = filename;
        SpriteManager oldSprite = spriteMap.get(fileName);
        if (oldSprite != null) {
            this.spriteManager = oldSprite;
            this.spriteManager.addReference();
        } else {
            this.spriteManager = new SpriteManager(null);
        }
        try {
            this.spriteManager.setImage(ImageIO.read(new File("./res/drawable/" + fileName)));
        } catch (IIOException e) {
            Main.printMsg("Could not reinitialize Sprite: the picture " + "./res/drawable/" + fileName + " could not be read/found.");
            throw e;
        } catch (IOException e) {
            Main.printMsg("Could not reinitialize Sprite: the picture " + "./res/drawable/" + fileName + " could not be read/found.");
            throw e;
        }
    }

    //FIXME альтернатива java.lang.ref.Cleaner или PhantomReference
    @Override
    protected void finalize() throws Throwable {
        if (spriteManager.removeReference() && !fileName.isEmpty()) {
            spriteMap.remove(fileName);
        }
        super.finalize();
    }

    public int getWidth() {
        return spriteManager.getImage().getWidth();
    }

    public int getHeight() {
        return spriteManager.getImage().getHeight();
    }

    public void render(Graphics g, int x, int y, int wid, int len) {
        g.setColor(Color.GREEN);
        g.drawImage(spriteManager.getImage(), x, y, wid, len, null);
    }
}
