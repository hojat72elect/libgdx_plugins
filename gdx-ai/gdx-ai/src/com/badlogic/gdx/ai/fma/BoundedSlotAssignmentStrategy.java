package com.badlogic.gdx.ai.fma;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.utils.Array;

/**
 * {@code BoundedSlotAssignmentStrategy} is an abstract implementation of {@link SlotAssignmentStrategy} that supports roles.
 * Generally speaking, there are hard and soft roles. Hard roles cannot be broken, soft roles can.
 * <p>
 * This abstract class provides an implementation of the {@link #calculateNumberOfSlots(Array) calculateNumberOfSlots} method that
 * is more general (and costly) than the simplified implementation in {@link FreeSlotAssignmentStrategy}. It scans the assignment
 * list to find the number of filled slots, which is the highest slot number in the assignments.
 *
 * @param <T> Type of vector, either 2D or 3D, implementing the {@link Vector} interface
 * 
 */
public abstract class BoundedSlotAssignmentStrategy<T extends Vector<T>> implements SlotAssignmentStrategy<T> {

    @Override
    public abstract void updateSlotAssignments(Array<SlotAssignment<T>> assignments);

    @Override
    public int calculateNumberOfSlots(Array<SlotAssignment<T>> assignments) {
        // Find the number of filled slots: it will be the
        // highest slot number in the assignments
        int filledSlots = -1;
        for (int i = 0; i < assignments.size; i++) {
            SlotAssignment<T> assignment = assignments.get(i);
            if (assignment.slotNumber >= filledSlots) filledSlots = assignment.slotNumber;
        }

        // Add one to go from the index of the highest slot to the number of slots needed.
        return filledSlots + 1;
    }

    @Override
    public void removeSlotAssignment(Array<SlotAssignment<T>> assignments, int index) {
        int sn = assignments.get(index).slotNumber;
        for (int i = 0; i < assignments.size; i++) {
            SlotAssignment<T> sa = assignments.get(i);
            if (sa.slotNumber >= sn) sa.slotNumber--;
        }
        assignments.removeIndex(index);
    }
}
