package com.badlogic.gdx.ai.pfa;

/**
 * A {@code GraphPath} represents a path in a {@link Graph}. Note that a path can be defined in terms of nodes or
 * {@link Connection connections} so that multiple edges between the same pair of nodes can be discriminated.
 *
 * @param <N> Type of node
 * 
 */
public interface GraphPath<N> extends Iterable<N> {

    /**
     * Returns the number of items of this path.
     */
    int getCount();

    /**
     * Returns the item of this path at the given index.
     */
    N get(int index);

    /**
     * Adds an item at the end of this path.
     */
    void add(N node);

    /**
     * Clears this path.
     */
    void clear();

    /**
     * Reverses this path.
     */
    void reverse();
}
