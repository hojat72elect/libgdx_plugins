package com.badlogic.gdx.ai.utils.random;

/**
 * 
 */
public final class ConstantIntegerDistribution extends IntegerDistribution {

    public static final ConstantIntegerDistribution NEGATIVE_ONE = new ConstantIntegerDistribution(-1);
    public static final ConstantIntegerDistribution ZERO = new ConstantIntegerDistribution(0);
    public static final ConstantIntegerDistribution ONE = new ConstantIntegerDistribution(1);

    private final int value;

    public ConstantIntegerDistribution(int value) {
        this.value = value;
    }

    @Override
    public int nextInt() {
        return value;
    }

    public int getValue() {
        return value;
    }
}
