package com.badlogic.gdx.ai.tests.steer.scene2d.tests;

import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.tests.SteeringBehaviorsTest;
import com.badlogic.gdx.ai.tests.steer.scene2d.Scene2dSteeringTest;
import com.badlogic.gdx.ai.tests.steer.scene2d.SteeringActor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

/**
 * A class to test and experiment with the {@link Pursue} behavior.
 *
 * @autor davebaol
 */
public class Scene2dPursueTest extends Scene2dSteeringTest {
    SteeringActor character;
    SteeringActor prey;

    public Scene2dPursueTest(SteeringBehaviorsTest container) {
        super(container, "Pursue");
    }

    @Override
    public void create() {
        super.create();

        character = new SteeringActor(container.badlogicSmall, false);
        character.setPosition(MathUtils.random(container.stageWidth), MathUtils.random(container.stageHeight), Align.center);
        character.setMaxLinearSpeed(100);
        character.setMaxLinearAcceleration(600);

        prey = new SteeringActor(container.target, false);
        prey.setPosition(MathUtils.random(container.stageWidth), MathUtils.random(container.stageHeight), Align.center);
        prey.setMaxLinearSpeed(100);
        prey.setMaxLinearAcceleration(250);
        prey.setMaxAngularAcceleration(0); // used by Wander; set to 0 because independent facing is disabled
        prey.setMaxAngularSpeed(5);

        final Pursue<Vector2> pursueSB = new Pursue<Vector2>(character, prey, 0.3f);
        character.setSteeringBehavior(pursueSB);

        Wander<Vector2> wanderSB = new Wander<Vector2>(prey) //
                // Don't use Face internally because independent facing is off
                .setFaceEnabled(false) //
                // No need to call setAlignTolerance, setDecelerationRadius and setTimeToTarget for the same reason
                .setWanderOffset(110) //
                .setWanderOrientation(10) //
                .setWanderRadius(64) //
                .setWanderRate(MathUtils.PI2 * 3.5f);
        prey.setSteeringBehavior(wanderSB);

        testTable.addActor(character);
        testTable.addActor(prey);

        Table detailTable = new Table(container.skin);

        detailTable.row();
        addMaxLinearAccelerationController(detailTable, character, 0, 2000, 20);

        detailTable.row();
        final Label labelMaxPredictionTime = new Label("Max.Prediction Time[" + pursueSB.getMaxPredictionTime() + "] sec.",
                container.skin);
        detailTable.add(labelMaxPredictionTime);
        detailTable.row();
        Slider maxPredictionTime = new Slider(0, 5, .1f, false, container.skin);
        maxPredictionTime.setValue(pursueSB.getMaxPredictionTime());
        maxPredictionTime.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                pursueSB.setMaxPredictionTime(slider.getValue());
                labelMaxPredictionTime.setText("Max.Prediction Time [" + slider.getValue() + "] sec.");
            }
        });
        detailTable.add(maxPredictionTime);

        detailTable.row();
        addSeparator(detailTable);

        detailTable.row();
        addMaxLinearSpeedController(detailTable, character, 0, 300, 10);

        detailWindow = createDetailWindow(detailTable);
    }

    @Override
    public void draw() {
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
