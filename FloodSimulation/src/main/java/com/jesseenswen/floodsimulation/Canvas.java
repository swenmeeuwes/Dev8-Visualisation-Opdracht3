package com.jesseenswen.floodsimulation;

import com.jesseenswen.floodsimulation.animation.Animation;
import com.jesseenswen.floodsimulation.data.DataProvider;
import com.jesseenswen.floodsimulation.models.Rect;
import com.jesseenswen.floodsimulation.models.Vector2;
import com.jesseenswen.floodsimulation.models.Vector3;
import com.jesseenswen.floodsimulation.models.datastructures.comparators.DepthComparator;
import com.jesseenswen.floodsimulation.ui.Button;
import com.jesseenswen.floodsimulation.ui.ToggleButton;
import com.jesseenswen.floodsimulation.ui.UIOverlay;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import processing.core.PApplet;
import processing.core.PImage;

/**
 *
 * @author swenm_000
 */
public class Canvas extends PApplet {

    public List<Vector3<Float>> data = Collections.synchronizedList(new ArrayList<Vector3<Float>>());
    public List<Vector3<Float>> data1000 = Collections.synchronizedList(new ArrayList<Vector3<Float>>()); // SynchronizedList is thread safe
    public List<Vector3<Float>> data500 = Collections.synchronizedList(new ArrayList<Vector3<Float>>());

    public SimulationState state = SimulationState.LOADING;

    private UIOverlay ui;
    
    private Animation catAnimation;

    private final Rect<Integer> simulationArea = new Rect<>(12, 48, 500, 500);
    private Rect<Integer> mappingArea = new Rect<>(56082, 447014, 101861, 428548); // X: 56082, 101861     Y: 447014, 428548
    private final Vector2<Integer> locationWijnhaven = new Vector2<>(92850, 436926);

    private float pixelSize = 1.0f;

    private DataProvider dataProvider;
    private int lastDrawnPoint = 0;
    private float waterLevel = -10f;

//    private DepthComparator comparator = new DepthComparator();
    public void setup() {
        dataProvider = new DataProvider();

        Canvas canvas = this;

        Thread thread = new Thread(new Runnable() {
            public void run() {
                dataProvider.getDataAsyncFastSplitList(canvas);
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();

        mappingArea = new Rect<>(locationWijnhaven.getX() - 500, locationWijnhaven.getY() + 500, locationWijnhaven.getX() + 500, locationWijnhaven.getY() - 500);

        initUI();

        size(712, 608);
        clear();

        frame.setTitle("Jesse and Swen - Development 8 - Assignment 3 - Flood Simulation");

        catAnimation = new Animation(this, "cat_animation/cat_animation", 25);
        frameRate(5);
                
//        PImage catImage = loadImage("cat_idle.gif");
//        image(catImage, 50, 150);
//        fill(255);
//        textAlign(CENTER, TOP);
//        textSize(18);
//        text("Please wait while we load data ...", 250, 350);

//        mappedWaterLevel = (int) map(waterLevel, -10, 14, 0, 255);
    }

    public void draw() {
//        Vector2<Integer> mousePosition = new Vector2<>(mouseX, mouseY);

        switch (state) {
            case LOADING:
                clear();
//                fill(255);
//                textAlign(CENTER, BOTTOM);
//                textSize(20);
//                text("Loading height data ...", simulationArea.getX() + simulationArea.getWidth() / 2, simulationArea.getY() - textAscent() - 8);
                catAnimation.display(62, 210);
                fill(255);
                textAlign(CENTER, TOP);
                textSize(18);
                text("Please wait while we load data ...", 250, 410);
                break;
            case DONE:
                clear();
                frameRate(30);
                state = SimulationState.READY;
                break;
            case READY:
                fill(255);
                textAlign(CENTER, BOTTOM);
                textSize(20);
                text("Height map of Wijnhaven Rotterdam (" + (mappingArea.getWidth() - mappingArea.getX()) + " m\u00B2)", simulationArea.getX() + simulationArea.getWidth() / 2, simulationArea.getY() - 16);
                loadStaticMap();
                fill(255, 125, 0);
                ellipse(map(locationWijnhaven.getX(), mappingArea.getX(), mappingArea.getWidth(), simulationArea.getX(), simulationArea.getX() + simulationArea.getWidth()), map(locationWijnhaven.getY(), mappingArea.getY(), mappingArea.getHeight(), simulationArea.getY(), simulationArea.getY() + simulationArea.getHeight()), 10, 10);
                break;
            default:
                break;
        }

        showDetails();
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
//        fill(255);
//        rect(0, 0, width, 32);
//        fill(0);
//        textSize(12);
//        textAlign(LEFT, TOP);
//        text("Loading record " + data1000.size() + "      Drawing point: " + lastDrawnPoint + "      Water level: " + waterLevel, 8, 8);

        int dataLength = data1000.size();

        // Load until there's no data left
        while (dataLength > lastDrawnPoint) {
            Vector3<Float> vector = data1000.get(lastDrawnPoint);

//            if(vector.getX() < mappingArea.getX() || vector.getX() > mappingArea.getX() || vector.getY() > mappingArea.getY() || vector.getY() < mappingArea.getHeight()) {
//                lastDrawnPoint++;
//                continue;
//            }
            // Area size?????
            // 1 --- 2
            // |     |
            // 3 --- 4
            // 1 = 62682, 446614
            // 2 = 94361, 442373
            // 3 = 62913, 438514
            // 4 = 93733, 431548
            Vector2<Float> mappedVector = new Vector2();
            mappedVector.setX(map(vector.getX(), mappingArea.getX(), mappingArea.getWidth(), simulationArea.getX(), simulationArea.getX() + simulationArea.getWidth()));
            if (mappedVector.getX() > simulationArea.getX() + simulationArea.getWidth() || mappedVector.getX() < simulationArea.getX()) {
                lastDrawnPoint++;
                continue;
            }
            mappedVector.setY(map(vector.getY(), mappingArea.getY(), mappingArea.getHeight(), simulationArea.getY(), simulationArea.getY() + simulationArea.getHeight()));
            if (mappedVector.getY() > simulationArea.getY() + simulationArea.getHeight() || mappedVector.getY() < simulationArea.getY()) {
                lastDrawnPoint++;
                continue;
            }

            noStroke();
            int colorValue = (int) map(vector.getZ(), -10, 20, 0, 255); // 140 max maybe
            if (vector.getZ() > waterLevel) {
                fill(0, colorValue, 255 - colorValue, 255);
            } else //                fill(mappedWaterLevel - colorValue, 0, 0);
            {
                fill(255, 55, 0);
            }

            ellipse(mappedVector.getX(), mappedVector.getY(), pixelSize, pixelSize); // Scale size with linesToSkip in dataprovider
            lastDrawnPoint++;
        }
    }
    
    private void showDetails() {
        fill(255);
        textAlign(LEFT, TOP);
        textSize(12);
        text("Loading record: " + data1000.size(), simulationArea.getX() + simulationArea.getWidth() + 12, 120);
        text("Drawing point: " + lastDrawnPoint, simulationArea.getX() + simulationArea.getWidth() + 12, 120 + 16);
        text("Water level: " + waterLevel, simulationArea.getX() + simulationArea.getWidth() + 12, 120 + 16 * 2);
        
        noFill();
        stroke(255);
        strokeWeight(4);
        rect(simulationArea.getX() - 4, simulationArea.getY() - 4, simulationArea.getWidth() + 4, simulationArea.getHeight() + 4);
        
        strokeWeight(1);
    }

    private void initUI() {
        ui = new UIOverlay(this);
        
        ui.pushElement(new ToggleButton(this, "Size: 500", "Size: 1000", new Vector2<>(simulationArea.getX() + simulationArea.getWidth() + 12, 250)) {
            @Override
            public void onToggleOn() {
                ui.clear();
                // Switch to size: 500
                pixelSize = 2f;
                mappingArea = new Rect<>(locationWijnhaven.getX() - 250, locationWijnhaven.getY() + 250, locationWijnhaven.getX() + 250, locationWijnhaven.getY() - 250);
                clear();
                lastDrawnPoint = 0;
            }

            @Override
            public void onToggleOff() {
                ui.clear();
                // Switch to size: 1000
                pixelSize = 1f;
                mappingArea = new Rect<>(locationWijnhaven.getX() - 500, locationWijnhaven.getY() + 500, locationWijnhaven.getX() + 500, locationWijnhaven.getY() - 500);
                clear();
                lastDrawnPoint = 0;
            }
        });
        ui.pushElement(new ToggleButton(this, "Smooth", "No smooth", new Vector2<>(simulationArea.getX() + simulationArea.getWidth() + 12, 285)) {
            @Override
            public void onToggleOn() {
                noSmooth();
            }

            @Override
            public void onToggleOff() {
                ui.clear();
                smooth();
            }
        });
        ui.pushElement(new Button(this, "Increase water level", new Vector2<>(simulationArea.getX() + simulationArea.getWidth() + 12, 385)) {
            @Override
            public void onClick() {
                waterLevel += 0.5f;
            }
        });
        ui.pushElement(new Button(this, "Decrease water level", new Vector2<>(simulationArea.getX() + simulationArea.getWidth() + 12, 420)) {
            @Override
            public void onClick() {
                waterLevel -= 0.5f;
            }
        });
        ui.pushElement(new Button(this, "Play", new Rect<>(simulationArea.getX() + 64, simulationArea.getY() + simulationArea.getHeight() + 12, 96, 28)) {
            @Override
            public void onClick() {
                lastDrawnPoint = 0;
            }
        });
        ui.pushElement(new Button(this, "Pause", new Rect<>(simulationArea.getX() + 192, simulationArea.getY() + simulationArea.getHeight() + 12, 96, 28)) {
            @Override
            public void onClick() {
                // Pause
            }
        });
        ui.pushElement(new Button(this, "Stop", new Rect<>(simulationArea.getX() + 320,  simulationArea.getY() + simulationArea.getHeight() + 12, 96, 28)) {
            @Override
            public void onClick() {
                // Pause
            }
        });
    }
}
