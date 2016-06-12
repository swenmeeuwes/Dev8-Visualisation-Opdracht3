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
public class Node<T> implements INode<T> {
    private T value;
    private INode<T> left;
    private INode<T> right;

    public Node(T value, INode<T> left, INode<T> right) {
        this.value = value;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public INode<T> getLeft() {
        return left;
    }

    @Override
    public INode<T> getRight() {
        return right;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setLeft(INode<T> left) {
        this.left = left;
    }

    public void setRight(INode<T> right) {
        this.right = right;
    }
}
