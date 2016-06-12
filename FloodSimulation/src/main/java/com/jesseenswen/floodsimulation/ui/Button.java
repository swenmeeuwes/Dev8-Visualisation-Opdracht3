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
public class Button extends Clickable {
    private ClickListener clickListener;
    
    public Button(PApplet applet, String text, Rect<Integer> area) {
        super(applet, text, area);
    }
    
    public Button(PApplet applet, String text, Vector2<Integer> position) {
        super(applet, text, position);
    }
    
    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }
    
    @Override
    public void draw() {
        applet.stroke(55);
        applet.fill(235);
        applet.rect(area.getX(), area.getY(), area.getWidth(), area.getHeight(), 7);
        applet.fill(0);
        
        super.draw();
    }

    protected void onClick() { }
}
