package com.badlogic.gdx.ai.tests.steer.scene2d.tests;

import com.badlogic.gdx.ai.steer.behaviors.Interpose;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.tests.SteeringBehaviorsTest;
import com.badlogic.gdx.ai.tests.steer.scene2d.Scene2dSteeringTest;
import com.badlogic.gdx.ai.tests.steer.scene2d.SteeringActor;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * A class to test and experiment with the {@link Interpose} behavior.
 *
 * @autor davebaol
 */
public class Scene2dInterposeTest extends Scene2dSteeringTest {
    boolean drawDebug;
    ShapeRenderer shapeRenderer;

    Interpose<Vector2> interposeSB;

    public Scene2dInterposeTest(SteeringBehaviorsTest container) {
        super(container, "Interpose");
    }

    @Override
    public void create() {
        super.create();

        drawDebug = true;

        shapeRenderer = new ShapeRenderer();

        // Set character
        SteeringActor character = new SteeringActor(container.target, false);
        character.setPosition(MathUtils.random(container.stageWidth), MathUtils.random(container.stageHeight), Align.center);
        character.setMaxLinearSpeed(250);
        character.setMaxLinearAcceleration(700);

        // Set agentA
        SteeringActor c1 = new SteeringActor(container.greenFish, false);
        c1.setPosition(MathUtils.random(container.stageWidth), MathUtils.random(container.stageHeight), Align.center);
        c1.setMaxLinearSpeed(80);
        c1.setMaxLinearAcceleration(250);
        c1.setMaxAngularAcceleration(0); // set to 0 because independent facing is disabled
        c1.setMaxAngularSpeed(5);

        // Set agentB
        SteeringActor c2 = new SteeringActor(container.badlogicSmall, false);
        c2.setPosition(MathUtils.random(container.stageWidth), MathUtils.random(container.stageHeight), Align.center);
        c2.setMaxLinearSpeed(150);
        c2.setMaxLinearAcceleration(450);
        c2.setMaxAngularAcceleration(0); // set to 0 because independent facing is disabled
        c2.setMaxAngularSpeed(5);

        interposeSB = new Interpose<Vector2>(character, c1, c2, .5f) //
                .setTimeToTarget(0.1f) //
                .setArrivalTolerance(0.001f) //
                .setDecelerationRadius(20);
        character.setSteeringBehavior(interposeSB);

        Wander<Vector2> wanderSB1 = new Wander<Vector2>(c1) //
                .setFaceEnabled(false) // because independent facing is off
                // No need to call setAlignTolerance, setDecelerationRadius and setTimeToTarget for the same reason
                .setWanderOffset(110) //
                .setWanderOrientation(MathUtils.random(MathUtils.PI2)) //
                .setWanderRadius(64) //
                .setWanderRate(MathUtils.PI2 * 3.5f);
        c1.setSteeringBehavior(wanderSB1);

        Wander<Vector2> wanderSB2 = new Wander<Vector2>(c1) //
                .setFaceEnabled(false) // because independent facing is off
                // No need to call setAlignTolerance, setDecelerationRadius and setTimeToTarget for the same reason
                .setWanderOffset(70) //
                .setWanderOrientation(MathUtils.random(MathUtils.PI2)) //
                .setWanderRadius(94). //
                        setWanderRate(MathUtils.PI2 * 5);
        c2.setSteeringBehavior(wanderSB2);

        testTable.addActor(character);
        testTable.addActor(c1);
        testTable.addActor(c2);

        Table detailTable = new Table(container.skin);

        detailTable.row();
        addMaxLinearAccelerationController(detailTable, character, 0, 10000, 20);

        detailTable.row();
        addMaxLinearSpeedController(detailTable, character);

        detailTable.row();
        final Label labelInterpositionRatio = new Label("Interposition Ratio [" + interposeSB.getInterpositionRatio() + "]",
                container.skin);
        detailTable.add(labelInterpositionRatio);
        detailTable.row();
        Slider interpositionRatio = new Slider(0, 1, 0.1f, false, container.skin);
        interpositionRatio.setValue(interposeSB.getInterpositionRatio());
        interpositionRatio.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                interposeSB.setInterpositionRatio(slider.getValue());
                labelInterpositionRatio.setText("Interposition Ratio [" + slider.getValue() + "]");
            }
        });
        detailTable.add(interpositionRatio);

        detailTable.row();
        addSeparator(detailTable);

        detailTable.row();
        CheckBox debug = new CheckBox("Draw debug info", container.skin);
        debug.setChecked(drawDebug);
        debug.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CheckBox checkBox = (CheckBox) event.getListenerActor();
                drawDebug = checkBox.isChecked();
            }
        });
        detailTable.add(debug);

        detailWindow = createDetailWindow(detailTable);
    }

    Vector2 point = new Vector2();

    @Override
    public void draw() {
        if (drawDebug) {
            Vector2 posA = interposeSB.getAgentA().getPosition();
            Vector2 posB = interposeSB.getAgentB().getPosition();

            // Draw line between agents
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(1, 1, 0, 1);
            shapeRenderer.line(posA.x, posA.y, posB.x, posB.y);
            shapeRenderer.end();

            // Draw real target along the line between agents
            point.set(posB).sub(posA).scl(interposeSB.getInterpositionRatio()).add(posA);
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 0, 1);
            shapeRenderer.circle(point.x, point.y, 4);
            shapeRenderer.end();

            // Draw estimated target
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.circle(interposeSB.getInternalTargetPosition().x, interposeSB.getInternalTargetPosition().y, 4);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }
}
