package com.badlogic.gdx.ai.steer.proximities;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.Timepiece;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.math.Vector;

/**
 * {@code FieldOfViewProximity} emulates the peripheral vision of the owner as if it had eyes. Any agents contained in the
 * specified list that are within the field of view of the owner are considered owner's neighbors. The field of view is determined
 * by a radius and an angle in degrees.
 * <p>
 * Note that this implementation checks the AI time of the current frame through the {@link Timepiece#getTime()
 * GdxAI.getTimepiece().getTime()} method in order to calculate neighbors only once per frame (assuming delta time is always
 * greater than 0, if time has changed the frame has changed too). This means that
 * <ul>
 * <li>if you forget to {@link Timepiece#update(float) update the timepiece} on each frame the proximity instance will be
 * calculated only the very first time, which is not what you want of course.</li>
 * <li>ideally the timepiece should be updated before the proximity is updated by the {@link #findNeighbors(ProximityCallback)}
 * method.</li>
 * </ul>
 *
 * @param <T> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
 * 
 */
public class FieldOfViewProximity<T extends Vector<T>> extends ProximityBase<T> {

    /**
     * The radius of this proximity.
     */
    protected float radius;

    /**
     * The angle in radians of this proximity.
     */
    protected float angle;

    private float coneThreshold;
    private float lastTime;
    private final T ownerOrientation;
    private final T toAgent;

    /**
     * Creates a {@code FieldOfViewProximity} for the specified owner, agents and cone area defined by the given radius and angle
     * in radians.
     *
     * @param owner  the owner of this proximity
     * @param agents the agents
     * @param radius the radius of the cone area
     * @param angle  the angle in radians of the cone area
     */
    public FieldOfViewProximity(Steerable<T> owner, Iterable<? extends Steerable<T>> agents, float radius, float angle) {
        super(owner, agents);
        this.radius = radius;
        setAngle(angle);
        this.lastTime = 0;
        this.ownerOrientation = owner.getPosition().cpy().setZero();
        this.toAgent = owner.getPosition().cpy().setZero();
    }

    /**
     * Returns the radius of this proximity.
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the radius of this proximity.
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Returns the angle of this proximity in radians.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Sets the angle of this proximity in radians.
     */
    public void setAngle(float angle) {
        this.angle = angle;
        this.coneThreshold = (float) Math.cos(angle * 0.5f);
    }

    @Override
    public int findNeighbors(ProximityCallback<T> callback) {
        int neighborCount = 0;

        // If the frame is new then avoid repeating calculations
        // when this proximity is used by multiple group behaviors.
        float currentTime = GdxAI.getTimepiece().getTime();
        if (this.lastTime != currentTime) {
            // Save the current time
            this.lastTime = currentTime;

            T ownerPosition = owner.getPosition();

            // Transform owner orientation to a Vector
            owner.angleToVector(ownerOrientation, owner.getOrientation());

            // Scan the agents searching for neighbors
            for (Steerable<T> currentAgent : agents) {

                // Make sure the agent being examined isn't the owner
                if (currentAgent != owner) {

                    toAgent.set(currentAgent.getPosition()).sub(ownerPosition);

                    // The bounding radius of the current agent is taken into account
                    // by adding it to the radius proximity
                    float range = radius + currentAgent.getBoundingRadius();

                    float toAgentLen2 = toAgent.len2();

                    // Make sure the current agent is within the range.
                    // Notice we're working in distance-squared space to avoid square root.
                    if (toAgentLen2 < range * range) {

                        // If the current agent is within the field of view of the owner,
                        // report it to the callback and tag it for further consideration.
                        if (ownerOrientation.dot(toAgent) > coneThreshold) {
                            if (callback.reportNeighbor(currentAgent)) {
                                currentAgent.setTagged(true);
                                neighborCount++;
                                continue;
                            }
                        }
                    }
                }

                // Clear the tag
                currentAgent.setTagged(false);
            }
        } else {
            // Scan the agents searching for tagged neighbors
            for (Steerable<T> currentAgent : agents) {

                // Make sure the agent being examined isn't the owner and that
                // it's tagged.
                if (currentAgent != owner && currentAgent.isTagged()) {

                    if (callback.reportNeighbor(currentAgent)) {
                        neighborCount++;
                    }
                }
            }
        }

        return neighborCount;
    }
}
