/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jesseenswen.floodsimulation.models.datastructures.comparators;

import com.jesseenswen.floodsimulation.models.Vector3;
import java.util.Comparator;

/**
 *
 * @author Jesse
 */
public class DepthComparator implements Comparator<Vector3> {

    @Override
    public int compare(Vector3 v1, Vector3 v2) {
        return v1.compareTo(v2);
    }

}
