package com.badlogic.gdx.ai.utils;

import com.badlogic.gdx.utils.ObjectMap;

/**
 * 
 */
public class NonBlockingSemaphoreRepository {

    private static final ObjectMap<String, NonBlockingSemaphore> REPO = new ObjectMap<String, NonBlockingSemaphore>();

    private static NonBlockingSemaphore.Factory FACTORY = new SimpleNonBlockingSemaphore.Factory();

    public static void setFactory(NonBlockingSemaphore.Factory factory) {
        FACTORY = factory;
    }

    public static NonBlockingSemaphore addSemaphore(String name, int maxResources) {
        NonBlockingSemaphore sem = FACTORY.createSemaphore(name, maxResources);
        REPO.put(name, sem);
        return sem;
    }

    public static NonBlockingSemaphore getSemaphore(String name) {
        return REPO.get(name);
    }

    public static NonBlockingSemaphore removeSemaphore(String name) {
        return REPO.remove(name);
    }

    public static void clear() {
        REPO.clear();
    }
}
