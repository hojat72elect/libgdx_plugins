package com.badlogic.gdx.ai;

import com.badlogic.gdx.Gdx;

/**
 * Environment class holding references to the {@link Timepiece}, {@link Logger} and {@link FileSystem} instances. The references
 * are held in static fields which allows static access to all sub systems.
 * <p>
 * Basically, this class is the locator of the service locator design pattern. The locator contains references to the services and
 * encapsulates the logic that locates them. Being a decoupling pattern, the service locator provides a global point of access to
 * a set of services without coupling users to the concrete classes that implement them.
 * <p>
 * The gdx-ai framework internally uses the service locator to give you the ability to use the framework out of a libgdx
 * application. In this scenario, the libgdx jar must still be in the classpath but you don't need native libraries since the
 * libgdx environment is not initialized at all.
 * <p>
 * Also, this service locator automatically configures itself with proper service providers in the situations below:
 * <ul>
 * <li>Libgdx application: if a running libgdx environment (regardless of the particular backend) is detected.</li>
 * <li>Non-libgdx desktop application: if no running libgdx environment is found.</li>
 * </ul>
 * This means that if you want to use gdx-ai in Android (or any other non desktop platform) out of a lbgdx application then you
 * have to implement and set proper providers.
 *
 * 
 */
public final class GdxAI {

    private GdxAI() {
    }

    private static Timepiece timepiece = new DefaultTimepiece();
    private static Logger logger = Gdx.app == null ? new NullLogger() : new GdxLogger();
    private static FileSystem fileSystem = Gdx.files == null ? new StandaloneFileSystem() : new GdxFileSystem();

    /**
     * Returns the timepiece service.
     */
    public static Timepiece getTimepiece() {
        return timepiece;
    }

    /**
     * Sets the timepiece service.
     */
    public static void setTimepiece(Timepiece timepiece) {
        GdxAI.timepiece = timepiece;
    }

    /**
     * Returns the logger service.
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Sets the logger service.
     */
    public static void setLogger(Logger logger) {
        GdxAI.logger = logger;
    }

    /**
     * Returns the filesystem service.
     */
    public static FileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * Sets the filesystem service.
     */
    public static void setFileSystem(FileSystem fileSystem) {
        GdxAI.fileSystem = fileSystem;
    }
}
