package com.badlogic.gdx.ai.steer.utils.rays;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;

/**
 * A {@code ParallelSideRayConfiguration} uses two rays parallel to the direction of motion. The rays have the same length and
 * opposite side offset.
 * <p>
 * The parallel configuration works well in areas where corners are highly obtuse but is very susceptible to the <a
 * href="../behaviors/RaycastObstacleAvoidance.html">corner trap</a>.
 *
 * @param <T> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
 * 
 */
public class ParallelSideRayConfiguration<T extends Vector<T>> extends RayConfigurationBase<T> {

    private static final float HALF_PI = MathUtils.PI * 0.5f;

    private float length;
    private float sideOffset;

    /**
     * Creates a {@code ParallelSideRayConfiguration} for the given owner where the two rays have the specified length and side
     * offset.
     *
     * @param owner      the owner of this ray configuration
     * @param length     the length of the rays.
     * @param sideOffset the side offset of the rays.
     */
    public ParallelSideRayConfiguration(Steerable<T> owner, float length, float sideOffset) {
        super(owner, 2);
        this.length = length;
        this.sideOffset = sideOffset;
    }

    @Override
    public Ray<T>[] updateRays() {
        float velocityAngle = owner.vectorToAngle(owner.getLinearVelocity());

        // Update ray 0
        owner.angleToVector(rays[0].start, velocityAngle - HALF_PI).scl(sideOffset).add(owner.getPosition());
        rays[0].end.set(owner.getLinearVelocity()).nor().scl(length); // later we'll add rays[0].start;

        // Update ray 1
        owner.angleToVector(rays[1].start, velocityAngle + HALF_PI).scl(sideOffset).add(owner.getPosition());
        rays[1].end.set(rays[0].end).add(rays[1].start);

        // add start position to ray 0
        rays[0].end.add(rays[0].start);

        return rays;
    }

    /**
     * Returns the length of the rays.
     */
    public float getLength() {
        return length;
    }

    /**
     * Sets the length of the rays.
     */
    public void setLength(float length) {
        this.length = length;
    }

    /**
     * Returns the side offset of the rays.
     */
    public float getSideOffset() {
        return sideOffset;
    }

    /**
     * Sets the side offset of the rays.
     */
    public void setSideOffset(float sideOffset) {
        this.sideOffset = sideOffset;
    }
}
