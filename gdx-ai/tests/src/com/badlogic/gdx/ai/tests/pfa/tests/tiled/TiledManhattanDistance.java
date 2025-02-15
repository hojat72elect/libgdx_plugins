package com.badlogic.gdx.ai.tests.pfa.tests.tiled;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * A Manhattan distance heuristic for a {@link TiledGraph}. It simply calculates the Manhattan distance between two given
 * tiles.
 *
 * @param <N> Type of node, either flat or hierarchical, extending the {@link TiledNode} class
 * 
 */
public class TiledManhattanDistance<N extends TiledNode<N>> implements Heuristic<N> {

    public TiledManhattanDistance() {
    }

    @Override
    public float estimate(N node, N endNode) {
        return Math.abs(endNode.x - node.x) + Math.abs(endNode.y - node.y);
    }
}
