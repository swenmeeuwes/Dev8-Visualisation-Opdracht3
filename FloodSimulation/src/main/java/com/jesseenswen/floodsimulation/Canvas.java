package com.jesseenswen.floodsimulation;

import com.jesseenswen.floodsimulation.data.DataProvider;
import com.jesseenswen.floodsimulation.models.Vector2;
import com.jesseenswen.floodsimulation.models.Vector3;
import com.jesseenswen.floodsimulation.models.datastructures.comparators.DepthComparator;
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

    public List<Vector3<Float>> data = Collections.synchronizedList(new ArrayList<Vector3<Float>>()); // To-Do: Why synchronizedList?? Search!!!!
    // Answer: SynchronizedList is thread safe

    //public PriorityBlockingQueue<Vector3<Float>> data;
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

//        size(500, 500);
        size(1366, 768);
        clear();

        frame.setTitle("Jesse and Swen - Development 8 - Assignment 3 - Flood Simulation");

        mappedWaterLevel = (int) map(waterLevel, -10, 14, 0, 255);

//        noSmooth(); // Might make drawing faster
    }

    public void draw() {
        if (!isFinished) {
            loadStaticMap();
        }

        //fill(255, 125, 0);
//        ellipse(map(92850, 56082, 101861, 0, width), map(436926, 447014, 428548, 40, height - 40), 10, 10);
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
        redrawMap();
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

    public void redrawMap() {
        Collections.sort(data, comparator);
        Vector3<Float> current = data.get(lastDrawnPoint);
        while (current.getZ() < waterLevel) {
            Vector2<Float> mappedVector = new Vector2();
            mappedVector.setX(map(current.getX(), 56082, 101861, 0, width));
            mappedVector.setY(map(current.getY(), 447014, 428548, 40, height - 40));

            noStroke();
            int colorValue = (int) map(current.getZ(), -10, 14, 0, 255);
            fill(mappedWaterLevel - colorValue, 0, 0);
            ellipse(mappedVector.getX(), mappedVector.getY(), 1f, 1f);

            lastDrawnPoint++;
            current = data.get(lastDrawnPoint);
        }
    }
}
