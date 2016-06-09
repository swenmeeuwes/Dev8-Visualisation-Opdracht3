/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jesseenswen.floodsimulation.models.datastructures.kdtree;

/**
 *
 * @author swenm_000
 */
public interface INode<T> {
    boolean isEmpty();
    T getValue();
    INode<T> getLeft();
    INode<T> getRight();
}
