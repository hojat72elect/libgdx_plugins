package com.badlogic.gdx.ai.tests.pfa.tests.tiled.hrchy;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.tests.pfa.tests.tiled.TiledNode;
import com.badlogic.gdx.utils.Array;

/**
 * A node for a {@link HierarchicalTiledGraph}.
 *
 * 
 */
public class HierarchicalTiledNode extends TiledNode<HierarchicalTiledNode> {
    /**
     * The index of this tile.
     */
    public final int index;

    public HierarchicalTiledNode(int x, int y, int type, int index, int connectionCapacity) {
        super(x, y, type, new Array<Connection<HierarchicalTiledNode>>(connectionCapacity));
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public HierarchicalTiledNode getLowerLevelNode() {
        return null;
    }
}
