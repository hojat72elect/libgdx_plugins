package com.badlogic.gdx.ai.btree.leaf;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.Timepiece;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.badlogic.gdx.ai.btree.annotation.TaskAttribute;
import com.badlogic.gdx.ai.utils.random.ConstantFloatDistribution;
import com.badlogic.gdx.ai.utils.random.FloatDistribution;

/**
 * {@code Wait} is a leaf that keeps running for the specified amount of time then succeeds.
 *
 * @param <E> type of the blackboard object that tasks use to read or modify game state
 * 
 */
public class Wait<E> extends LeafTask<E> {

    /**
     * Mandatory task attribute specifying the random distribution that determines the timeout in seconds.
     */
    @TaskAttribute(required = true)
    public FloatDistribution seconds;

    private float startTime;
    private float timeout;

    /**
     * Creates a {@code Wait} task that immediately succeeds.
     */
    public Wait() {
        this(ConstantFloatDistribution.ZERO);
    }

    /**
     * Creates a {@code Wait} task running for the specified number of seconds.
     *
     * @param seconds the number of seconds to wait for
     */
    public Wait(float seconds) {
        this(new ConstantFloatDistribution(seconds));
    }

    /**
     * Creates a {@code Wait} task running for the specified number of seconds.
     *
     * @param seconds the random distribution determining the number of seconds to wait for
     */
    public Wait(FloatDistribution seconds) {
        this.seconds = seconds;
    }

    /**
     * Draws a value from the distribution that determines the seconds to wait for.
     * <p>
     * This method is called when the task is entered. Also, this method internally calls {@link Timepiece#getTime()
     * GdxAI.getTimepiece().getTime()} to get the current AI time. This means that
     * <ul>
     * <li>if you forget to {@link Timepiece#update(float) update the timepiece} this task will keep running indefinitely.</li>
     * <li>the timepiece should be updated before this task runs.</li>
     * </ul>
     */
    @Override
    public void start() {
        timeout = seconds.nextFloat();
        startTime = GdxAI.getTimepiece().getTime();
    }

    /**
     * Executes this {@code Wait} task.
     *
     * @return {@link Status#SUCCEEDED} if the specified timeout has expired; {@link Status#RUNNING} otherwise.
     */
    @Override
    public Status execute() {
        return GdxAI.getTimepiece().getTime() - startTime < timeout ? Status.RUNNING : Status.SUCCEEDED;
    }

    @Override
    protected Task<E> copyTo(Task<E> task) {
        ((Wait<E>) task).seconds = seconds;
        return task;
    }

    @Override
    public void reset() {
        seconds = ConstantFloatDistribution.ZERO;
        startTime = 0;
        timeout = 0;
        super.reset();
    }
}
