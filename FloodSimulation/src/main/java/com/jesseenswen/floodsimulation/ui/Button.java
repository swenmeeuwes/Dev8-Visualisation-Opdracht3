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
    public Button(PApplet applet, String text, Rect<Integer> area) {
        super(applet, text, area);
    }
    
    public Button(PApplet applet, String text, Vector2<Integer> position) {
        super(applet, text, position);
    }

    @Override
    protected void onClick() {
        System.out.println("You clicked the button with the following text: " + text);
    }
}
