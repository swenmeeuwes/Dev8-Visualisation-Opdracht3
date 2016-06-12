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
public class EmptyNode<T> implements INode<T> {
    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public T getValue() {
        throw new NotImplementedException();
    }

    @Override
    public INode<T> getLeft() {
        throw new NotImplementedException();
    }

    @Override
    public INode<T> getRight() {
        throw new NotImplementedException();
    }
}
