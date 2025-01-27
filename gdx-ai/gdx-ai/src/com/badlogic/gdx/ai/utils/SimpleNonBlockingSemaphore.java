package com.badlogic.gdx.ai.utils;

/**
 * A non-blocking semaphore that does not ensure the atomicity of its operations, meaning that it's not tread-safe.
 *
 * 
 */
public class SimpleNonBlockingSemaphore implements NonBlockingSemaphore {

    String name;
    int maxResources;
    int acquiredResources;

    /**
     * Creates a {@code SimpleNonBlockingSemaphore} with the given name and number of resources.
     *
     * @param name         the name of this semaphore
     * @param maxResources the number of resources
     */
    public SimpleNonBlockingSemaphore(String name, int maxResources) {
        this.name = name;
        this.maxResources = maxResources;
        this.acquiredResources = 0;
    }

    @Override
    public boolean acquire() {
        return acquire(1);
    }

    @Override
    public boolean acquire(int resources) {
        if (acquiredResources + resources <= maxResources) {
            acquiredResources += resources;
            // System.out.println("sem." + name + ": acquired = TRUE, acquiredResources = " + acquiredResources);
            return true;
        }
        // System.out.println("sem." + name + ": acquired = FALSE, acquiredResources = " + acquiredResources);
        return false;
    }

    @Override
    public boolean release() {
        return release(1);
    }

    @Override
    public boolean release(int resources) {
        if (acquiredResources - resources >= 0) {
            acquiredResources -= resources;
            // System.out.println("sem." + name + ": released = TRUE, acquiredResources = " + acquiredResources);
            return true;
        }
        // System.out.println("sem." + name + ": released = FALSE, acquiredResources = " + acquiredResources);
        return false;
    }

    /**
     * A concrete factory that can create instances of {@link SimpleNonBlockingSemaphore}.
     *
     * 
     */
    public static class Factory implements NonBlockingSemaphore.Factory {

        public Factory() {
        }

        @Override
        public NonBlockingSemaphore createSemaphore(String name, int maxResources) {
            return new SimpleNonBlockingSemaphore(name, maxResources);
        }
    }
}
