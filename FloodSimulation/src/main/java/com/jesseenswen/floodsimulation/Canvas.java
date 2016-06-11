package com.jesseenswen.floodsimulation;

import com.jesseenswen.floodsimulation.data.DataProvider;
import com.jesseenswen.floodsimulation.models.Rect;
import com.jesseenswen.floodsimulation.models.Vector2;
import com.jesseenswen.floodsimulation.models.Vector3;
import com.jesseenswen.floodsimulation.models.datastructures.comparators.DepthComparator;
import com.jesseenswen.floodsimulation.ui.Button;
import com.jesseenswen.floodsimulation.ui.UIOverlay;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import processing.core.PApplet;

/**
 *
 * @author swenm_000
 */
public class Canvas extends PApplet {

    public List<Vector3<Float>> data = Collections.synchronizedList(new ArrayList<Vector3<Float>>()); // SynchronizedList is thread safe

    private UIOverlay ui;
    private Rect<Integer> simulationArea = new Rect<>(0, 40, 500, 500);

    private DataProvider dataProvider;
    private int lastDrawnPoint = 0;
    private int mappedWaterLevel;
    private float waterLevel = -10f;

    private DepthComparator comparator = new DepthComparator();

    private boolean isFinished = false;

    private Thread staticMapThread;
    
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
        isFinished = false;

        ui = new UIOverlay(this);
        ui.pushElement(new Button(this, "Size: 500", new Vector2<>(simulationArea.getX() + simulationArea.getWidth()+ 12, 50)) {
            @Override
            public void onClick() {
                // Switch to size: 500
            }
        });
        ui.pushElement(new Button(this, "Size: 1000", new Vector2<>(simulationArea.getX() + simulationArea.getWidth()+ 12, 100)) {
            @Override
            public void onClick() {
                // Switch to size: 1000
            }
        });
        ui.pushElement(new Button(this, "Increase water level", new Vector2<>(simulationArea.getX() + simulationArea.getWidth()+ 12, 200)) {
            @Override
            public void onClick() {
                waterLevel += 0.5f;
            }
        });
        ui.pushElement(new Button(this, "Decrease water level", new Vector2<>(simulationArea.getX() + simulationArea.getWidth()+ 12, 250)) {
            @Override
            public void onClick() {
                waterLevel -= 0.5f;
            }
        });
        ui.pushElement(new Button(this, "Play", new Vector2<>(simulationArea.getX() + 32, simulationArea.getY() + simulationArea.getHeight()+ 12)) {
            @Override
            public void onClick() {
                lastDrawnPoint = 0;
            }
        });

        size(700, 600);
        clear();

        frame.setTitle("Jesse and Swen - Development 8 - Assignment 3 - Flood Simulation");

        mappedWaterLevel = (int) map(waterLevel, -10, 14, 0, 255);

//        noSmooth(); // Might make drawing faster
    }

    public void draw() {
//        Vector2<Integer> mousePosition = new Vector2<>(mouseX, mouseY);
        
        loadStaticMap();

        fill(255, 125, 0);
        ellipse(map(92850, 56082, 101861, simulationArea.getX(), simulationArea.getWidth()), map(436926, 447014, 428548, simulationArea.getY(), simulationArea.getHeight()), 10, 10);
        
        ui.draw();
    }

    public void mousePressed() {
        Vector2<Integer> clickPosition = new Vector2<>(mouseX, mouseY);
        ui.checkClick(clickPosition);
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
        while (dataLength > lastDrawnPoint) {
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
            mappedVector.setX(map(vector.getX(), 56082, 101861, simulationArea.getX(), simulationArea.getWidth()));
            mappedVector.setY(map(vector.getY(), 447014, 428548, simulationArea.getY(), simulationArea.getHeight()));

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
        ui.draw();
    }
}
