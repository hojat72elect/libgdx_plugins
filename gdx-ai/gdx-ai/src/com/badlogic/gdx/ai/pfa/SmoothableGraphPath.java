package com.badlogic.gdx.ai.pfa;

import com.badlogic.gdx.math.Vector;

/**
 * A path that can be smoothed by a {@link PathSmoother}.
 *
 * @param <N> Type of node
 * @param <V> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
 * 
 */
public interface SmoothableGraphPath<N, V extends Vector<V>> extends GraphPath<N> {

    /**
     * Returns the position of the node at the given index.
     *
     * @param index the index of the node you want to know the position
     */
    V getNodePosition(int index);

    /**
     * Swaps the specified nodes of this path.
     *
     * @param index1 index of the first node to swap
     * @param index2 index of the second node to swap
     */
    void swapNodes(int index1, int index2);

    /**
     * Reduces the size of this path to the specified length (number of nodes). If the path is already smaller than the specified
     * length, no action is taken.
     *
     * @param newLength the new length
     */
    void truncatePath(int newLength);
}
