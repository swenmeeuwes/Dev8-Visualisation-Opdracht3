package com.jesseenswen.floodsimulation;

import com.jesseenswen.floodsimulation.data.DataProvider;
import com.jesseenswen.floodsimulation.models.Vector2;
import com.jesseenswen.floodsimulation.models.Vector3;
import com.jesseenswen.floodsimulation.ui.Button;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 *
 * @author swenm_000
 */
public class Canvas extends PApplet {

    public List<Vector3<Float>> data = Collections.synchronizedList(new ArrayList<Vector3<Float>>()); // To-Do: Why synchronizedList?? Search!!!!

    private SimulationState state;

    private DataProvider dataProvider;
    private int lastDrawnPoint = 0;
    private int mappedWaterLevel;
    private float waterLevel = -10f;

    private Button buttonSizeSmall;
    private Button buttonSizeLarge;

    public void setup() {
        dataProvider = new DataProvider();

        Canvas canvas = this;

        Thread thread = new Thread(new Runnable() {
            public void run() {
                dataProvider.getDataAsyncFast(canvas);
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

        buttonSizeSmall = new Button(this, "Size: 500", new Vector2<>(50, 50));
        buttonSizeLarge = new Button(this, "Size: 1000", new Vector2<>(50, 100));

        size(500, 500);
        clear();

        frame.setTitle("Jesse and Swen - Development 8 - Assignment 3 - Flood Simulation");

        mappedWaterLevel = (int) map(waterLevel, -10, 14, 0, 255);

        state = SimulationState.PROMPT;

//        noSmooth(); // Might make drawing faster
    }

    public void draw() {
        switch (state) {

            case PROMPT:
                promptSize();
                break;
            case LOADING:
                loadStaticMap();

                fill(255, 125, 0);
                ellipse(map(92850, 56082, 101861, 0, width), map(436926, 447014, 428548, 40, height - 40), 10, 10);
                break;
            default:
                // yooooo
                break;
        }
    }

    public void mousePressed() {
        Vector2<Integer> clickPosition = new Vector2<>(mouseX, mouseY);
        buttonSizeLarge.checkClick(clickPosition);
        buttonSizeSmall.checkClick(clickPosition);
    }

    public void keyPressed() {
        if (keyCode == UP) {
            waterLevel += 0.5f;
        }
        if (keyCode == DOWN) {
            waterLevel -= 0.5f;
        }
    }

    public void keyReleased() {
        if (keyCode == UP || keyCode == DOWN) {
            lastDrawnPoint = 0;
        }
    }

    private void loadStaticMap() {
        fill(255);
        rect(0, 0, width, 32);
        fill(0);
        textSize(12);
        textAlign(LEFT, TOP);
        text("Loading record " + data.size() + "      Drawing point: " + lastDrawnPoint + "      Water level: " + waterLevel, 8, 8);

        int dataLength = data.size();

        // Load until there's no data left
        while (lastDrawnPoint < dataLength) {
            Vector3<Float> vector = data.get(lastDrawnPoint);

            if (vector == null) {
                System.out.println("!@#$ at " + lastDrawnPoint);
                lastDrawnPoint++;
                continue;
            }

            // Area size?????
            // 1 --- 2
            // |     |
            // 3 --- 4
            // 1 = 62682, 446614
            // 2 = 94361, 442373
            // 3 = 62913, 438514
            // 4 = 93733, 431548
            Vector2<Float> mappedVector = new Vector2();
            mappedVector.setX(map(vector.getX(), 56082, 101861, 0, width));
            mappedVector.setY(map(vector.getY(), 447014, 428548, 40, height - 40));

            noStroke();
            int colorValue = (int) map(vector.getZ(), -10, 14, 0, 255);
            if (vector.getZ() > waterLevel) {
                fill(0, colorValue, 255 - colorValue);
            } else //                fill(mappedWaterLevel - colorValue, 0, 0);
            {
                fill(255, 55, 0);
            }

            ellipse(mappedVector.getX(), mappedVector.getY(), 1f, 1f); // Scale size with linesToSkip in dataprovider

            lastDrawnPoint++;
        }
    }

    private void promptSize() {
        buttonSizeLarge.draw();
        buttonSizeSmall.draw();
    }
}
