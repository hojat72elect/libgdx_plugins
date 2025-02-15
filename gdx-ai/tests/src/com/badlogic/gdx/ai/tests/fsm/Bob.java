package com.badlogic.gdx.ai.tests.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;

/**
 * 
 */
public class Bob implements Telegraph {
    // the amount of gold a miner must have before he feels comfortable
    final public static int COMFORT_LEVEL = 5;
    // the amount of nuggets a miner can carry
    final public static int MAX_NUGGETS = 3;
    // above this value a miner is thirsty
    final public static int THIRST_LEVEL = 5;
    // above this value a miner is sleepy
    final public static int TIREDNESS_THRESHOLD = 5;

    private final StateMachine<Bob, BobState> stateMachine;
    private Location location;
    // how many nuggets the miner has in his pockets
    private int goldCarried;
    private int moneyInBank;
    // the higher the value, the thirstier the miner
    private int thirst;
    // the higher the value, the more tired the miner
    private int fatigue;

    Elsa elsa;

    public Bob() {
        this(null);
    }

    public Bob(Elsa elsa) {
        this.elsa = elsa;
        location = Location.SHACK;
        goldCarried = 0;
        moneyInBank = 0;
        thirst = 0;
        fatigue = 0;

        stateMachine = new DefaultStateMachine<Bob, BobState>(this, BobState.GO_HOME_AND_SLEEP_TILL_RESTED);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }

    public void update(float delta) {
        thirst += 1;
        stateMachine.update();
    }

    public StateMachine<Bob, BobState> getStateMachine() {
        return stateMachine;
    }

    public Elsa getElsa() {
        return elsa;
    }

    public void setElsa(Elsa elsa) {
        this.elsa = elsa;
    }

    public void addToGoldCarried(int val) {
        goldCarried += val;

        if (goldCarried < 0) {
            goldCarried = 0;
        }
    }

    public void addToWealth(int val) {
        moneyInBank += val;

        if (moneyInBank < 0) {
            moneyInBank = 0;
        }
    }

    public boolean isThirsty() {
        return thirst >= THIRST_LEVEL;
    }

    public boolean isFatigued() {
        return fatigue > TIREDNESS_THRESHOLD;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getGoldCarried() {
        return goldCarried;
    }

    public void setGoldCarried(int val) {
        goldCarried = val;
    }

    public boolean isPocketsFull() {
        return goldCarried >= MAX_NUGGETS;
    }

    public void decreaseFatigue() {
        fatigue--;
    }

    public void increaseFatigue() {
        fatigue++;
    }

    public int getWealth() {
        return moneyInBank;
    }

    public void setWealth(int val) {
        moneyInBank = val;
    }

    public void buyAndDrinkAWhiskey() {
        thirst = 0;
        moneyInBank -= 2;
    }
}
