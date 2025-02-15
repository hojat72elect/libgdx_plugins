package com.badlogic.gdx.ai.steer.utils.rays;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Ray;
import com.badlogic.gdx.math.Vector;

/**
 * A {@code CentralRayWithWhiskersConfiguration} uses a long central ray and two shorter whiskers.
 * <p>
 * A central ray with short whiskers is often the best initial configuration to try but can make it impossible for the character
 * to move down tight passages. Also, it is still susceptible to the <a
 * href="../behaviors/RaycastObstacleAvoidance.html#cornerTrap">corner trap</a>, far less than the parallel configuration though.
 *
 * @param <T> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
 * 
 */
public class CentralRayWithWhiskersConfiguration<T extends Vector<T>> extends RayConfigurationBase<T> {

    private float rayLength;
    private float whiskerLength;
    private float whiskerAngle;

    /**
     * Creates a {@code CentralRayWithWhiskersConfiguration} for the given owner where the central ray has the specified length and
     * the two whiskers have the specified length and angle.
     *
     * @param owner         the owner of this configuration
     * @param rayLength     the length of the central ray
     * @param whiskerLength the length of the two whiskers (usually shorter than the central ray)
     * @param whiskerAngle  the angle in radians of the whiskers from the central ray
     */
    public CentralRayWithWhiskersConfiguration(Steerable<T> owner, float rayLength, float whiskerLength, float whiskerAngle) {
        super(owner, 3);
        this.rayLength = rayLength;
        this.whiskerLength = whiskerLength;
        this.whiskerAngle = whiskerAngle;
    }

    @Override
    public Ray<T>[] updateRays() {
        T ownerPosition = owner.getPosition();
        T ownerVelocity = owner.getLinearVelocity();

        float velocityAngle = owner.vectorToAngle(ownerVelocity);

        // Update central ray
        rays[0].start.set(ownerPosition);
        rays[0].end.set(ownerVelocity).nor().scl(rayLength).add(ownerPosition);

        // Update left ray
        rays[1].start.set(ownerPosition);
        owner.angleToVector(rays[1].end, velocityAngle - whiskerAngle).scl(whiskerLength).add(ownerPosition);

        // Update right ray
        rays[2].start.set(ownerPosition);
        owner.angleToVector(rays[2].end, velocityAngle + whiskerAngle).scl(whiskerLength).add(ownerPosition);

        return rays;
    }

    /**
     * Returns the length of the central ray.
     */
    public float getRayLength() {
        return rayLength;
    }

    /**
     * Sets the length of the central ray.
     */
    public void setRayLength(float rayLength) {
        this.rayLength = rayLength;
    }

    /**
     * Returns the length of the two whiskers.
     */
    public float getWhiskerLength() {
        return whiskerLength;
    }

    /**
     * Sets the length of the two whiskers.
     */
    public void setWhiskerLength(float whiskerLength) {
        this.whiskerLength = whiskerLength;
    }

    /**
     * Returns the angle in radians of the whiskers from the central ray.
     */
    public float getWhiskerAngle() {
        return whiskerAngle;
    }

    /**
     * Sets the angle in radians of the whiskers from the central ray.
     */
    public void setWhiskerAngle(float whiskerAngle) {
        this.whiskerAngle = whiskerAngle;
    }
}
