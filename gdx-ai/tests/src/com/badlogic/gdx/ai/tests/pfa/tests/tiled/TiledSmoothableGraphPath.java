package com.badlogic.gdx.ai.tests.pfa.tests.tiled;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.SmoothableGraphPath;
import com.badlogic.gdx.math.Vector2;

/**
 * A smoothable path for a generic tiled graph.
 *
 * @param <N> Type of node, either flat or hierarchical, extending the {@link TiledNode} class
 * 
 */
public class TiledSmoothableGraphPath<N extends TiledNode<N>> extends DefaultGraphPath<N> implements
        SmoothableGraphPath<N, Vector2> {

    private final Vector2 tmpPosition = new Vector2();

    /**
     * Returns the position of the node at the given index.
     * <p>
     * <b>Note that the same Vector2 instance is returned each time this method is called.</b>
     *
     * @param index the index of the node you want to know the position
     */
    @Override
    public Vector2 getNodePosition(int index) {
        N node = nodes.get(index);
        return tmpPosition.set(node.x, node.y);
    }

    @Override
    public void swapNodes(int index1, int index2) {
// x.swap(index1, index2);
// y.swap(index1, index2);
        nodes.set(index1, nodes.get(index2));
    }

    @Override
    public void truncatePath(int newLength) {
        nodes.truncate(newLength);
    }
}
