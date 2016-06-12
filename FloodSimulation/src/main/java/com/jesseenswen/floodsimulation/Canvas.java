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
import javax.swing.JOptionPane;
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

    private final Vector2<Integer> locationZadkineStatue = new Vector2<>(92796, 436960);
    private final Vector2<Integer> locationWijnhaven = new Vector2<>(92850, 436926);

    private float pixelSize = 1.8f;

    private DataProvider dataProvider;
    private int lastDrawnPoint = 0;
    private float waterLevel = -10f;
    private float simulationWaterLevel = -10f;
    private float waterIncrement = 0.01f;

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

        mappingArea = new Rect<>(locationZadkineStatue.getX() - 250, locationZadkineStatue.getY() + 250, locationZadkineStatue.getX() + 250, locationZadkineStatue.getY() - 250);

        initUI();

        size(712, 608);
        clear();

        frame.setTitle("Jesse and Swen - Development 8 - Assignment 3 - Flood Simulation");

        catAnimation = new Animation(this, "cat_animation/cat_animation", 25);
        frameRate(5);
    }

    public void draw() {
        switch (state) {
            case LOADING:
                clear();
                catAnimation.display(62, 210);
                fill(255);
                textAlign(CENTER, TOP);
                textSize(18);
                text("Please wait while we load some data ...", 250, 410);
                break;
            case DONE:
                clear();
                fill(0);
                rect(simulationArea.getX(), simulationArea.getY(), simulationArea.getWidth(), simulationArea.getHeight());
                frameRate(60);
                state = SimulationState.READY;
                break;
            case READY:
                loadStaticMap();
                fill(255, 125, 0);
                ellipse(map(locationWijnhaven.getX(), mappingArea.getX(), mappingArea.getWidth(), simulationArea.getX(), simulationArea.getX() + simulationArea.getWidth()), map(locationWijnhaven.getY(), mappingArea.getY(), mappingArea.getHeight(), simulationArea.getY(), simulationArea.getY() + simulationArea.getHeight()), 10, 10);
                break;
            case RUNNING:
                new Thread(new Runnable() {
                    public void run() {
                        drawWater();
                        waterLevel += waterIncrement;
                    }
                }).start();
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

//    public void keyPressed() {
//        if (keyCode == UP) {
//            waterLevel += 0.5f;
//        }
//        if (keyCode == DOWN) {
//            waterLevel -= 0.5f;
//        }
//
//    }
//
//    public void keyReleased() {
//        if (keyCode == UP || keyCode == DOWN) {
//            lastDrawnPoint = 0;
//        }
//    }
    private void loadStaticMap() {
        fill(255);
        textAlign(CENTER, BOTTOM);
        textSize(20);
        text("Height map of Wijnhaven Rotterdam (" + (mappingArea.getWidth() - mappingArea.getX()) + " m\u00B2)", simulationArea.getX() + simulationArea.getWidth() / 2, simulationArea.getY() - 16);

        int dataLength = data1000.size();

        // Load until there's no data left
        while (dataLength > lastDrawnPoint) {
            Vector3<Float> vector = data1000.get(lastDrawnPoint);

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
            int colorValue = (int) map(vector.getZ(), -8, 40, 0, 255); // 140 max maybe // -10 20
            if (vector.getZ() > waterLevel) {
                fill(255 - colorValue, 255, 100);
//                fill(255, 255 - colorValue, 0);
            } else {
                fill(0, 150, 255);
            }

            ellipse(mappedVector.getX(), mappedVector.getY(), pixelSize, pixelSize);

            lastDrawnPoint++;
        }

        if (state == SimulationState.LOADING) {
            textAlign(LEFT, CENTER);
            textSize(12);
            fill(255);
            text("Water level: " + Math.round(simulationWaterLevel * 100f) / 100f, simulationArea.getX() + simulationArea.getWidth() - 12 - textWidth("Water level: " + simulationWaterLevel), simulationArea.getY() + simulationArea.getHeight() - 18);
        }
    }

    private void showDetails() {
        fill(0);
        noStroke();
        rect(simulationArea.getX() + simulationArea.getWidth() + 4, 100, width - simulationArea.getX() + simulationArea.getWidth(), height);

        fill(255);
        textAlign(LEFT, TOP);
        textSize(12);
        text("Simulation state: " + state.toString(), simulationArea.getX() + simulationArea.getWidth() + 12, 120);
        text("Loading record: " + data1000.size(), simulationArea.getX() + simulationArea.getWidth() + 12, 120 + 16);
        text("Drawing point: " + lastDrawnPoint, simulationArea.getX() + simulationArea.getWidth() + 12, 120 + 16 * 2);
        text("Water level: " + Math.round(waterLevel * 100f) / 100f, simulationArea.getX() + simulationArea.getWidth() + 12, 120 + 16 * 4);

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
                // Switch to size: 1000
                pixelSize = 1f;
                mappingArea = new Rect<>(locationZadkineStatue.getX() - 500, locationZadkineStatue.getY() + 500, locationZadkineStatue.getX() + 500, locationZadkineStatue.getY() - 500);
                clear();
                simulationWaterLevel = waterLevel;
                lastDrawnPoint = 0;
            }

            @Override
            public void onToggleOff() {
                ui.clear();
                // Switch to size: 500
                pixelSize = 1.8f;
                mappingArea = new Rect<>(locationZadkineStatue.getX() - 250, locationZadkineStatue.getY() + 250, locationZadkineStatue.getX() + 250, locationZadkineStatue.getY() - 250);
                clear();
                simulationWaterLevel = waterLevel;
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
        ui.pushElement(new ToggleButton(this, "Speed: x1", "Speed: x10", new Vector2<>(simulationArea.getX() + simulationArea.getWidth() + 12, 455)) {
            @Override
            public void onToggleOn() {
                waterIncrement = 0.1f;
            }

            @Override
            public void onToggleOff() {
                waterIncrement = 0.01f;
            }
        });
        ui.pushElement(new Button(this, "Play", new Rect<>(simulationArea.getX() + 64, simulationArea.getY() + simulationArea.getHeight() + 12, 96, 28)) {
            @Override
            public void onClick() {
                frameRate(15);
                state = SimulationState.RUNNING;
            }
        });
        ui.pushElement(new Button(this, "Pause", new Rect<>(simulationArea.getX() + 192, simulationArea.getY() + simulationArea.getHeight() + 12, 96, 28)) {
            @Override
            public void onClick() {
                // Pause
                state = SimulationState.PAUSED;
            }
        });
        ui.pushElement(new Button(this, "Stop", new Rect<>(simulationArea.getX() + 320, simulationArea.getY() + simulationArea.getHeight() + 12, 96, 28)) {
            @Override
            public void onClick() {
                // Stop
                state = SimulationState.READY;
                waterLevel = -10f;
                clear();
                simulationWaterLevel = waterLevel;
                lastDrawnPoint = 0;
                loadStaticMap();
            }
        });
        ui.pushElement(new Button(this, "Save this image", new Vector2<>(simulationArea.getX() + simulationArea.getWidth()+ 12, simulationArea.getY() + simulationArea.getHeight()+ 12)){
            @Override
            public void onClick(){
                PImage mapImage = get(simulationArea.getX(), simulationArea.getY(), simulationArea.getWidth(), simulationArea.getHeight());
                mapImage.save("/target/screenshot.jpg");
                JOptionPane.showMessageDialog(applet, "Your screenshot is saved in the /target folder.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    public void drawWater() {
        int dataLength = data1000.size();
        int lastDrawnWaterPoint = 0;

        while (dataLength > lastDrawnWaterPoint) {
            Vector3<Float> vector = data1000.get(lastDrawnWaterPoint);

            Vector2<Float> mappedVector = new Vector2();
            mappedVector.setX(map(vector.getX(), mappingArea.getX(), mappingArea.getWidth(), simulationArea.getX(), simulationArea.getX() + simulationArea.getWidth()));
            if (mappedVector.getX() > simulationArea.getX() + simulationArea.getWidth() || mappedVector.getX() < simulationArea.getX()) {
                lastDrawnWaterPoint++;
                continue;
            }
            mappedVector.setY(map(vector.getY(), mappingArea.getY(), mappingArea.getHeight(), simulationArea.getY(), simulationArea.getY() + simulationArea.getHeight()));
            if (mappedVector.getY() > simulationArea.getY() + simulationArea.getHeight() || mappedVector.getY() < simulationArea.getY()) {
                lastDrawnWaterPoint++;
                continue;
            }

            if (vector.getZ() <= waterLevel) {
                noStroke();
                fill(0, 150, 255);
                ellipse(mappedVector.getX(), mappedVector.getY(), pixelSize, pixelSize);
            }
            lastDrawnWaterPoint++;
        }
    }
}
