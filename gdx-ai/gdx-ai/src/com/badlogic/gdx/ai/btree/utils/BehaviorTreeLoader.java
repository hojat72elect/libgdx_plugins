package com.badlogic.gdx.ai.btree.utils;

import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StreamUtils;

import java.io.Reader;

/**
 * {@link AssetLoader} for {@link BehaviorTree} instances. The behavior tree is loaded asynchronously.
 *
 * 
 */
@SuppressWarnings("rawtypes")
public class BehaviorTreeLoader extends AsynchronousAssetLoader<BehaviorTree, BehaviorTreeLoader.BehaviorTreeParameter> {

    public BehaviorTreeLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    BehaviorTree behaviorTree;

    @SuppressWarnings("unchecked")
    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file, BehaviorTreeParameter parameter) {
        this.behaviorTree = null;

        Object blackboard = null;
        BehaviorTreeParser parser = null;
        if (parameter != null) {
            blackboard = parameter.blackboard;
            parser = parameter.parser;
        }

        if (parser == null) parser = new BehaviorTreeParser();

        Reader reader = null;
        try {
            reader = file.reader();
            this.behaviorTree = parser.parse(reader, blackboard);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
    }

    @Override
    public BehaviorTree loadSync(AssetManager manager, String fileName, FileHandle file, BehaviorTreeParameter parameter) {
        BehaviorTree bundle = this.behaviorTree;
        this.behaviorTree = null;
        return bundle;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, BehaviorTreeParameter parameter) {
        return null;
    }

    public static class BehaviorTreeParameter extends AssetLoaderParameters<BehaviorTree> {
        public final Object blackboard;
        public final BehaviorTreeParser parser;

        public BehaviorTreeParameter() {
            this(null);
        }

        public BehaviorTreeParameter(Object blackboard) {
            this(blackboard, null);
        }

        public BehaviorTreeParameter(Object blackboard, BehaviorTreeParser parser) {
            this.blackboard = blackboard;
            this.parser = parser;
        }
    }
}
