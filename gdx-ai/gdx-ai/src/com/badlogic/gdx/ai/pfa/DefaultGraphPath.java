package com.badlogic.gdx.ai.pfa;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Default implementation of a {@link GraphPath} that uses an internal {@link Array} to store nodes or connections.
 *
 * @param <N> Type of node
 * 
 */
public class DefaultGraphPath<N> implements GraphPath<N> {
    public final Array<N> nodes;

    /**
     * Creates a {@code DefaultGraphPath} with no nodes.
     */
    public DefaultGraphPath() {
        this(new Array<N>());
    }

    /**
     * Creates a {@code DefaultGraphPath} with the given capacity and no nodes.
     */
    public DefaultGraphPath(int capacity) {
        this(new Array<N>(capacity));
    }

    /**
     * Creates a {@code DefaultGraphPath} with the given nodes.
     */
    public DefaultGraphPath(Array<N> nodes) {
        this.nodes = nodes;
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public int getCount() {
        return nodes.size;
    }

    @Override
    public void add(N node) {
        nodes.add(node);
    }

    @Override
    public N get(int index) {
        return nodes.get(index);
    }

    @Override
    public void reverse() {
        nodes.reverse();
    }

    @Override
    public Iterator<N> iterator() {
        return nodes.iterator();
    }
}
