package com.badlogic.gdx.ai.steer.behaviors;

import com.badlogic.gdx.ai.steer.Limiter;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector;

/**
 * {@code Face} behavior makes the owner look at its target. It delegates to the {@link ReachOrientation} behavior to perform the
 * rotation but calculates the target orientation first based on target and owner position.
 *
 * @param <T> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
 * 
 */
public class Face<T extends Vector<T>> extends ReachOrientation<T> {

    /**
     * Creates a {@code Face} behavior for the specified owner.
     *
     * @param owner the owner of this behavior.
     */
    public Face(Steerable<T> owner) {
        this(owner, null);
    }

    /**
     * Creates a {@code Face} behavior for the specified owner and target.
     *
     * @param owner  the owner of this behavior
     * @param target the target of this behavior.
     */
    public Face(Steerable<T> owner, Location<T> target) {
        super(owner, target);
    }

    @Override
    protected SteeringAcceleration<T> calculateRealSteering(SteeringAcceleration<T> steering) {
        return face(steering, target.getPosition());
    }

    protected SteeringAcceleration<T> face(SteeringAcceleration<T> steering, T targetPosition) {
        // Get the direction to target
        T toTarget = steering.linear.set(targetPosition).sub(owner.getPosition());

        // Check for a zero direction, and return no steering if so
        if (toTarget.isZero(getActualLimiter().getZeroLinearSpeedThreshold())) return steering.setZero();

        // Calculate the orientation to face the target
        float orientation = owner.vectorToAngle(toTarget);

        // Delegate to ReachOrientation
        return reachOrientation(steering, orientation);
    }

    //
    // Setters overridden in order to fix the correct return type for chaining
    //

    @Override
    public Face<T> setOwner(Steerable<T> owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public Face<T> setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Sets the limiter of this steering behavior. The given limiter must at least take care of the maximum angular speed and
     * acceleration.
     *
     * @return this behavior for chaining.
     */
    @Override
    public Face<T> setLimiter(Limiter limiter) {
        this.limiter = limiter;
        return this;
    }

    @Override
    public Face<T> setTarget(Location<T> target) {
        this.target = target;
        return this;
    }

    @Override
    public Face<T> setAlignTolerance(float alignTolerance) {
        this.alignTolerance = alignTolerance;
        return this;
    }

    @Override
    public Face<T> setDecelerationRadius(float decelerationRadius) {
        this.decelerationRadius = decelerationRadius;
        return this;
    }

    @Override
    public Face<T> setTimeToTarget(float timeToTarget) {
        this.timeToTarget = timeToTarget;
        return this;
    }
}
