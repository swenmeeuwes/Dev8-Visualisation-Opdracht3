package com.jesseenswen.floodsimulation.data;

import com.jesseenswen.floodsimulation.Canvas;
import com.jesseenswen.floodsimulation.models.Vector2;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import com.jesseenswen.floodsimulation.models.Vector3;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.util.Collections;

/**
 *
 * @author swenm_000
 */
public class DataProvider {
    
    private final Vector2<Float> HR_LOCATION = new Vector2<>(92850f, 436926f);

    public void getDataAsyncFastSplitList(Canvas canvas) {
        Thread getPointsWithin1000m = new Thread(new Runnable() {
            public void run() {
                try {
                    ClassLoader classLoader = getClass().getClassLoader();
                    
                    FileInputStream fileWest = new FileInputStream(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_west.csv").getFile());
                    FileInputStream fileOost = new FileInputStream(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_oost.csv").getFile());

                    SequenceInputStream seq = new SequenceInputStream(fileWest, fileOost);

                    BufferedReader br = new BufferedReader(new InputStreamReader(seq));
                    Iterator lines = br.lines().iterator();

                    // Skip first line, these are the column names
                  
                    while (lines.hasNext()) {
                        String line = (String) lines.next();
                        if(line.contains("X"))
                            continue;
                        String[] values = line.split(",");

                        if (inRange(Float.parseFloat(values[0]), Float.parseFloat(values[1]), 1000)) {
                            Vector3<Float> vector = new Vector3<Float>();
                            vector.setX(Float.parseFloat(values[0]));
                            vector.setY(Float.parseFloat(values[1]));
                            vector.setZ(Float.parseFloat(values[2]));
                            canvas.data1000.add(vector);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("IO Exception occured while reading the data!");
                }

            }
        });

        getPointsWithin1000m.start();

        Thread getPointsWithin500m = new Thread(new Runnable() {
            public void run() {
                for (Vector3<Float> current : canvas.data1000) {
                    if (inRange(current.getX(), current.getY(), 500)) {
                        Vector3<Float> vector = new Vector3<Float>();
                        vector.setX(current.getX());
                        vector.setY(current.getY());
                        vector.setZ(current.getZ());
                        canvas.data500.add(vector);
                    }
                }

            }
        });

        getPointsWithin500m.start();

        System.out.println("All threads started");
    }

    public boolean inRange(float rdX, float rdY, float threshold) {
        return (HR_LOCATION.getX() < rdX + threshold && HR_LOCATION.getX() > rdX - threshold
                && HR_LOCATION.getY() < rdY + threshold && HR_LOCATION.getY() > rdY - threshold);
    }

//    public void getDataAsyncFast(Canvas canvas) {
//        int linesToSkip = 25;
//
//        Thread thread = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    ClassLoader classLoader = getClass().getClassLoader();
//                    File fileWest = new File(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_west.csv").getFile());
//
//                    BufferedReader br = new BufferedReader(new InputStreamReader(fileWest.toURI().toURL().openStream()));
//                    Iterator lines = br.lines().iterator();
//
//                    // Skip first line, these are the column names
//                    lines.next();
//                    while (lines.hasNext()) {
//                        String line = (String) lines.next();
//                        String[] values = line.split(",");
//
//                        Vector3<Float> vector = new Vector3<Float>();
//                        vector.setX(Float.parseFloat(values[0]));
//                        vector.setY(Float.parseFloat(values[1]));
//                        vector.setZ(Float.parseFloat(values[2]));
//                        canvas.data.add(vector);
//
//                        for (int i = 0; i < linesToSkip; i++) {
//                            if (lines.hasNext()) {
//                                lines.next();
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    System.err.println("IO Exception occured while reading the data!");
//                }
//            }
//        });
//        thread.setPriority(Thread.MIN_PRIORITY);
//
//        Thread thread2 = new Thread(new Runnable() {
//            public void run() {
//                try {
//                    ClassLoader classLoader = getClass().getClassLoader();
//                    File fileOost = new File(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_oost.csv").getFile());
//
//                    BufferedReader br = new BufferedReader(new InputStreamReader(fileOost.toURI().toURL().openStream()));
//                    Iterator lines = br.lines().iterator();
//
//                    // Skip first line, these are the column names
//                    lines.next();
//                    while (lines.hasNext()) {
//                        String line = (String) lines.next();
//                        String[] values = line.split(",");
//
//                        Vector3<Float> vector = new Vector3<Float>();
//                        vector.setX(Float.parseFloat(values[0]));
//                        vector.setY(Float.parseFloat(values[1]));
//                        vector.setZ(Float.parseFloat(values[2]));
//                        canvas.data.add(vector);
//
//                        for (int i = 0; i < linesToSkip; i++) {
//                            if (lines.hasNext()) {
//                                lines.next();
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    System.err.println("IO Exception occured while reading the data!");
//                }
//            }
//        });
//        thread2.setPriority(Thread.MIN_PRIORITY);
//        thread.start();
//        thread2.start();
//    }
//
//    public void getDataAsync(Canvas canvas) {
//        int linesToSkip = 25;
//        try {
//            ClassLoader classLoader = getClass().getClassLoader();
//            File fileWest = new File(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_west.csv").getFile());
//            File fileOost = new File(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_oost.csv").getFile());
//            SequenceInputStream inputStream = new SequenceInputStream(fileWest.toURI().toURL().openStream(), fileOost.toURI().toURL().openStream());
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
//            Iterator lines = br.lines().iterator();
//
//            // Skip first line, these are the column names
//            lines.next();
//            while (lines.hasNext()) {
//                String line = (String) lines.next();
//                String[] values = line.split(",");
//
//                Vector3<Float> vector = new Vector3<Float>();
//                vector.setX(Float.parseFloat(values[0]));
//                vector.setY(Float.parseFloat(values[1]));
//                vector.setZ(Float.parseFloat(values[2]));
//                canvas.data.add(vector);
//
//                for (int i = 0; i < linesToSkip; i++) {
//                    if (lines.hasNext()) {
//                        lines.next();
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("IO Exception occured while reading the data!");
//        }
//    }
//
//    // Needs mutable list :c
//    public void getDataAsync(List<Vector3<Float>> editList) {
//        try {
//            ClassLoader classLoader = getClass().getClassLoader();
//            File file = new File(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_west.csv").getFile());
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(file.toURI().toURL().openStream()));
//            Iterator lines = br.lines().iterator();
//
//            // Skip first line, these are the column names
//            lines.next();
//            while (lines.hasNext()) {
//                String line = (String) lines.next();
//                String[] values = line.split(",");
//
//                Vector3<Float> vector = new Vector3<Float>();
//                vector.setX(Float.parseFloat(values[0]));
//                vector.setY(Float.parseFloat(values[1]));
//                vector.setZ(Float.parseFloat(values[2]));
//                editList.add(vector);
//            }
//        } catch (IOException e) {
//            System.err.println("IO Exception occured while reading the data!");
//        }
//    }
//
//    @Deprecated
//    public String getData(URL url) {
//        String allData = "";
//        try {
//            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
//            Iterator lines = br.lines().iterator();
//            while (lines.hasNext()) {
//                String line = (String) lines.next();
//                allData += line + "\n";
//            }
//        } catch (IOException e) {
//            System.err.println("IO Exception occured while reading the data!");
//        }
//        return allData;
//    }
//
//    @Deprecated
//    public List<Vector3<Float>> getDataList() {
//        List<Vector3<Float>> vectorList = new ArrayList<>();
//        String data = "";
//        try {
//            ClassLoader classLoader = getClass().getClassLoader();
//            File file = new File(classLoader.getResource("rotterdamopendata_hoogtebestandtotaal_west.csv").getFile());
//            data = getData(file.toURI().toURL());
//        } catch (IOException e) {
//            System.err.println("IO Exception occured while transforming data to list!");
//        }
//
//        Scanner fileScanner = new Scanner(data);
//        fileScanner.useDelimiter("\n");
//
//        // Skip first row with column names
//        fileScanner.next();
//
//        while (fileScanner.hasNext()) {
//            String line = fileScanner.next();
//            Scanner lineScanner = new Scanner(line);
//            lineScanner.useDelimiter(",");
//            Vector3<Float> vector = new Vector3<Float>();
//            vector.setX(Float.parseFloat(lineScanner.next()));
//            vector.setY(Float.parseFloat(lineScanner.next()));
//            vector.setZ(Float.parseFloat(lineScanner.next()));
//            vectorList.add(vector);
//        }
//
//        return vectorList;
//    }
}
