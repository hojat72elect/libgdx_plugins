package com.badlogic.gdx.ai.tests.steer.scene2d.tests;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.ai.steer.limiters.NullLimiter;
import com.badlogic.gdx.ai.tests.SteeringBehaviorsTest;
import com.badlogic.gdx.ai.tests.steer.scene2d.Scene2dSteeringTest;
import com.badlogic.gdx.ai.tests.steer.scene2d.Scene2dTargetInputProcessor;
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
 * A class to test and experiment with the {@link LookWhereYouAreGoing} behavior.
 *
 * @autor davebaol
 */
public class Scene2dLookWhereYouAreGoingTest extends Scene2dSteeringTest {

    SteeringActor character;
    SteeringActor target;

    public Scene2dLookWhereYouAreGoingTest(SteeringBehaviorsTest container) {
        super(container, "Look Where You're Going");
    }

    @Override
    public void create() {
        super.create();

        character = new SteeringActor(container.badlogicSmall, true);
        character.setPosition(container.stageWidth / 2, container.stageHeight / 2, Align.center);
        character.setMaxLinearAcceleration(100);
        character.setMaxLinearSpeed(100);
        character.setMaxAngularAcceleration(40);
        character.setMaxAngularSpeed(15);

        target = new SteeringActor(container.target);
        target.setPosition(MathUtils.random(container.stageWidth), MathUtils.random(container.stageHeight), Align.center);

        inputProcessor = new Scene2dTargetInputProcessor(target);

        final LookWhereYouAreGoing<Vector2> lookWhereYouAreGoingSB = new LookWhereYouAreGoing<Vector2>(character) //
                .setTimeToTarget(0.1f) //
                .setAlignTolerance(0.001f) //
                .setDecelerationRadius(MathUtils.PI);

        final Arrive<Vector2> arriveSB = new Arrive<Vector2>(character, target) //
                .setTimeToTarget(0.1f) //
                .setArrivalTolerance(0.001f) //
                .setDecelerationRadius(80);

        BlendedSteering<Vector2> blendedSteering = new BlendedSteering<Vector2>(character) //
                .setLimiter(NullLimiter.NEUTRAL_LIMITER) //
                .add(arriveSB, 1f) //
                .add(lookWhereYouAreGoingSB, 1f);
        character.setSteeringBehavior(blendedSteering);

        testTable.addActor(character);
        testTable.addActor(target);

        Table detailTable = new Table(container.skin);

        detailTable.row();
        addMaxAngularAccelerationController(detailTable, character, 0, 50, 1);

        detailTable.row();
        addMaxAngularSpeedController(detailTable, character, 0, 20, 1);

        detailTable.row();
        final Label labelDecelerationRadius = new Label("Deceleration Radius [" + lookWhereYouAreGoingSB.getDecelerationRadius()
                + "]", container.skin);
        detailTable.add(labelDecelerationRadius);
        detailTable.row();
        Slider decelerationRadius = new Slider(0, MathUtils.PI2, MathUtils.degreesToRadians, false, container.skin);
        decelerationRadius.setValue(lookWhereYouAreGoingSB.getDecelerationRadius());
        decelerationRadius.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                lookWhereYouAreGoingSB.setDecelerationRadius(slider.getValue());
                labelDecelerationRadius.setText("Deceleration Radius [" + slider.getValue() + "]");
            }
        });
        detailTable.add(decelerationRadius);

        detailTable.row();
        final Label labelAlignTolerance = new Label("Align tolerance [" + lookWhereYouAreGoingSB.getAlignTolerance() + "]",
                container.skin);
        detailTable.add(labelAlignTolerance);
        detailTable.row();
        Slider alignTolerance = new Slider(0, 1, 0.0001f, false, container.skin);
        alignTolerance.setValue(lookWhereYouAreGoingSB.getAlignTolerance());
        alignTolerance.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                lookWhereYouAreGoingSB.setAlignTolerance(slider.getValue());
                labelAlignTolerance.setText("Align tolerance [" + slider.getValue() + "]");
            }
        });
        detailTable.add(alignTolerance);

        detailTable.row();
        final Label labelTimeToTarget = new Label("Time to Target [" + lookWhereYouAreGoingSB.getTimeToTarget() + " sec.]",
                container.skin);
        detailTable.add(labelTimeToTarget);
        detailTable.row();
        Slider timeToTarget = new Slider(0.01f, 3, 0.1f, false, container.skin);
        timeToTarget.setValue(lookWhereYouAreGoingSB.getTimeToTarget());
        timeToTarget.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                lookWhereYouAreGoingSB.setTimeToTarget(slider.getValue());
                labelTimeToTarget.setText("Time to Target [" + slider.getValue() + " sec.]");
            }
        });
        detailTable.add(timeToTarget);

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
