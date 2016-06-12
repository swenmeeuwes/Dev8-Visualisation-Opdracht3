/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jesseenswen.floodsimulation.animation;

import processing.core.PApplet;
import processing.core.PImage;

/**
 *
 * @author swenm_000
 */
public class Animation {
    private PApplet applet;
    
    private PImage[] images;
    private int imageCount;
    private int frame;

    public Animation(PApplet applet, String imagePrefix, int count) {
        this.applet = applet;
        
        imageCount = count;
        images = new PImage[imageCount];

        for (int i = 0; i < imageCount; i++) {
            // Use nf() to number format 'i' into four digits
            String filename = imagePrefix + applet.nf(i, 4) + ".gif";
            images[i] = applet.loadImage(filename);
        }
    }

    public void display(float xpos, float ypos) {
        frame = (frame + 1) % imageCount;
        applet.image(images[frame], xpos, ypos);
    }

    public int getWidth() {
        return images[0].width;
    }
}
