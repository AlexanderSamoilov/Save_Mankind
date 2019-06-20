package com.company.gamegraphics;

import java.awt.*;

public abstract class GraphExtensions {

        public static void putpixel(Graphics g, int x, int y, Color color) {
            Color origColor = g.getColor();
            g.setColor(color);
            g.drawRect(x, y, 0, 0);
            g.setColor(origColor);
        }

        public static void fillRect(Graphics g, Rectangle rect, int pattern) {
            switch (pattern) {
                case 0:
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                    break;
                case 1: // goroshek
                    for (int y = rect.y; y <= rect.y + rect.height - 1; y+=5) {
                        for (int x = rect.x; x <= rect.x + rect.width - 1; x+=5)
                        putpixel(g, x, y, g.getColor());
                    }
                    break;
                case 2: // linii 45%
                    for (int y = rect.y; y <= rect.y + rect.height - 1; y++) {
                        for (int x = rect.x +  (y - rect.y) % 5; x <= rect.x + rect.width - 1; x+=5)
                            putpixel(g, x, y, g.getColor());
                    }
                    break;
                default:
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }


}
