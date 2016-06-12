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
public class ToggleButton extends Clickable {
    private ClickListener clickListener;
    private boolean toggled = false;
    
    private String text;
    private String toggleText;
    
    public ToggleButton(PApplet applet, String text, String toggleText, Rect<Integer> area) {
        super(applet, text, area);
        
        this.text = text;
        this.toggleText = toggleText;
    }
    
    public ToggleButton(PApplet applet, String text, String toggleText, Vector2<Integer> position) {
        super(applet, text, position);
        
        this.text = text;
        this.toggleText = toggleText;
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

    protected void onClick() {
        if(toggled) {
            toggled = false;
            onToggleOff();
            setText(text);
        }
        else 
        {
            toggled = true;
            onToggleOn();
            setText(toggleText);
        }
    }
    
    protected void onToggleOn() {
        
    }
    
    protected void onToggleOff() {
        
    }
}
