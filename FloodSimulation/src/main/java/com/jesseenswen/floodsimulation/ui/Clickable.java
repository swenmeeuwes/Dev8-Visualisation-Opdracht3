/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jesseenswen.floodsimulation.ui;

import com.jesseenswen.floodsimulation.models.Rect;
import com.jesseenswen.floodsimulation.models.Vector2;
import processing.core.PApplet;

/**
 *
 * @author swenm_000
 */
public abstract class Clickable extends UIElement {
    public Clickable(PApplet applet, String text, Rect<Integer> area) {
        super(applet, text, area);
    }
    
    public Clickable(PApplet applet, String text, Vector2<Integer> position) {
        super(applet, text, position);
    }
    
    public void checkClick(Vector2<Integer> clickPosition) {
        if(isInArea(clickPosition))
            onClick();
    }
    
    protected abstract void onClick();
    
    private boolean isInArea(Vector2<Integer> clickPosition) {
        return (area.getX() <= clickPosition.getX() && area.getX() + area.getWidth() >= clickPosition.getX() && area.getY() <= clickPosition.getY() && area.getY() + area.getHeight() >= clickPosition.getY());
    }
}
