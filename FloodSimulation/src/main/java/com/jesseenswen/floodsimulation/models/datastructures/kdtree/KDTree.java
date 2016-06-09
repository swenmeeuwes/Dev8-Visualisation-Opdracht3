/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jesseenswen.floodsimulation.models.datastructures.kdtree;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author swenm_000
 */
public class KDTree<T> {
    private INode<T> root;

    public KDTree() {
        root = new EmptyNode<>();
    }
    
    public void insert() {
        throw new NotImplementedException();
    }
}
