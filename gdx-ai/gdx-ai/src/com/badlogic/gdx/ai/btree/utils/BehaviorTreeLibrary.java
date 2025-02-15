package com.badlogic.gdx.ai.btree.utils;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.ai.FileSystem;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.TaskCloneException;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;

/**
 * A {@code BehaviorTreeLibrary} is a repository of behavior tree archetypes. Behavior tree archetypes never run. Indeed, they are
 * only cloned to create behavior tree instances that can run.
 *
 * 
 */
public class BehaviorTreeLibrary {

    protected ObjectMap<String, BehaviorTree<?>> repository;

    //	protected AssetManager assetManager;
    protected FileHandleResolver resolver;
    protected BehaviorTreeParser<?> parser;

    /**
     * Creates a {@code BehaviorTreeLibrary} using the new internal resolver returned by the call
     * {@link FileSystem#newResolver(FileType) GdxAI.getFileSystem().newResolver(FileType.Internal)}.
     */
    public BehaviorTreeLibrary() {
        this(BehaviorTreeParser.DEBUG_NONE);
    }

    /**
     * Creates a {@code BehaviorTreeLibrary} with the given debug level and using the new internal resolver returned by the call
     * {@link FileSystem#newResolver(FileType) GdxAI.getFileSystem().newResolver(FileType.Internal)}.
     *
     * @param parseDebugLevel the debug level the parser will use
     */
    public BehaviorTreeLibrary(int parseDebugLevel) {
        this(GdxAI.getFileSystem().newResolver(FileType.Internal), parseDebugLevel);
    }

    /**
     * Creates a {@code BehaviorTreeLibrary} with the given resolver.
     *
     * @param resolver the {@link FileHandleResolver}
     */
    public BehaviorTreeLibrary(FileHandleResolver resolver) {
        this(resolver, BehaviorTreeParser.DEBUG_NONE);
    }

    /**
     * Creates a {@code BehaviorTreeLibrary} with the given resolver and debug level.
     *
     * @param resolver        the {@link FileHandleResolver}
     * @param parseDebugLevel the debug level the parser will use
     */
    public BehaviorTreeLibrary(FileHandleResolver resolver, int parseDebugLevel) {
        this(resolver, null, parseDebugLevel);
    }

//	public BehaviorTreeLibrary (AssetManager assetManager) {
//		this(assetManager, BehaviorTreeParser.DEBUG_NONE);
//	}
//
//	public BehaviorTreeLibrary (AssetManager assetManager, int parserDebugLevel) {
//		this(null, assetManager, parserDebugLevel);
//	}

    @SuppressWarnings("rawtypes")
    private BehaviorTreeLibrary(FileHandleResolver resolver, AssetManager assetManager, int parseDebugLevel) {
        this.resolver = resolver;
//		this.assetManager = assetManager;
        this.repository = new ObjectMap<String, BehaviorTree<?>>();
        this.parser = new BehaviorTreeParser(parseDebugLevel);
    }

    /**
     * Creates the root task of {@link BehaviorTree} for the specified reference.
     *
     * @param treeReference the tree identifier, typically a path
     * @return the root task of the tree cloned from the archetype.
     * @throws SerializationException if the reference cannot be successfully parsed.
     * @throws TaskCloneException     if the archetype cannot be successfully parsed.
     */
    @SuppressWarnings("unchecked")
    public <T> Task<T> createRootTask(String treeReference) {
        return (Task<T>) retrieveArchetypeTree(treeReference).getChild(0).cloneTask();
    }

    /**
     * Creates the {@link BehaviorTree} for the specified reference.
     *
     * @param treeReference the tree identifier, typically a path
     * @return the tree cloned from the archetype.
     * @throws SerializationException if the reference cannot be successfully parsed.
     * @throws TaskCloneException     if the archetype cannot be successfully parsed.
     */
    public <T> BehaviorTree<T> createBehaviorTree(String treeReference) {
        return createBehaviorTree(treeReference, null);
    }

    /**
     * Creates the {@link BehaviorTree} for the specified reference and blackboard object.
     *
     * @param treeReference the tree identifier, typically a path
     * @param blackboard    the blackboard object (it can be {@code null}).
     * @return the tree cloned from the archetype.
     * @throws SerializationException if the reference cannot be successfully parsed.
     * @throws TaskCloneException     if the archetype cannot be successfully parsed.
     */
    @SuppressWarnings("unchecked")
    public <T> BehaviorTree<T> createBehaviorTree(String treeReference, T blackboard) {
        BehaviorTree<T> bt = (BehaviorTree<T>) retrieveArchetypeTree(treeReference).cloneTask();
        bt.setObject(blackboard);
        return bt;
    }

    /**
     * Retrieves the archetype tree from the library. If the library doesn't contain the archetype tree it is loaded and added to
     * the library.
     *
     * @param treeReference the tree identifier, typically a path
     * @return the archetype tree.
     * @throws SerializationException if the reference cannot be successfully parsed.
     */
    protected BehaviorTree<?> retrieveArchetypeTree(String treeReference) {
        BehaviorTree<?> archetypeTree = repository.get(treeReference);
        if (archetypeTree == null) {
//			if (assetManager != null) {
//				// TODO: fix me!!!
//				// archetypeTree = assetManager.load(name, BehaviorTree.class, null);
//				repository.put(treeReference, archetypeTree);
//				return null;
//			}
            archetypeTree = parser.parse(resolver.resolve(treeReference), null);
            registerArchetypeTree(treeReference, archetypeTree);
        }
        return archetypeTree;
    }

    /**
     * Registers the {@link BehaviorTree} archetypeTree with the specified reference. Existing archetypes in the repository with
     * the same treeReference will be replaced.
     *
     * @param treeReference the tree identifier, typically a path.
     * @param archetypeTree the archetype tree.
     * @throws IllegalArgumentException if the archetypeTree is null
     */
    public void registerArchetypeTree(String treeReference, BehaviorTree<?> archetypeTree) {
        if (archetypeTree == null) {
            throw new IllegalArgumentException("The registered archetype must not be null.");
        }
        repository.put(treeReference, archetypeTree);
    }

    /**
     * Returns {@code true} if an archetype tree with the specified reference is registered in this library.
     *
     * @param treeReference the tree identifier, typically a path.
     * @return {@code true} if the archetype is registered already; {@code false} otherwise.
     */
    public boolean hasArchetypeTree(String treeReference) {
        return repository.containsKey(treeReference);
    }

    /**
     * Dispose behavior tree obtain by this library.
     *
     * @param treeReference the tree identifier.
     * @param behaviorTree  the tree to dispose.
     */
    public void disposeBehaviorTree(String treeReference, BehaviorTree<?> behaviorTree) {
        if (Task.TASK_CLONER != null) {
            Task.TASK_CLONER.freeTask(behaviorTree);
        }
    }
}
