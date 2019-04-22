package com.company.gamecontent;

import com.company.gamegeom.Parallelepiped;

import java.awt.*;

public interface Renderable {
    void render(Graphics g, Parallelepiped parallelepiped, double angle);
}
