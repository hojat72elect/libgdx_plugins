package com.badlogic.gdx.ai.tests.steer.scene2d.tests;

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
 * A class to test and experiment with the {@link Wander} behavior.
 *
 * @autor davebaol
 */
public class Scene2dWanderTest extends Scene2dSteeringTest {
    boolean drawDebug;
    ShapeRenderer shapeRenderer;

    SteeringActor character;
    Wander<Vector2> wanderSB;

    public Scene2dWanderTest(SteeringBehaviorsTest container) {
        super(container, "Wander");
    }

    @Override
    public void create() {
        super.create();

        drawDebug = true;

        shapeRenderer = new ShapeRenderer();

        character = new SteeringActor(container.badlogicSmall, true);
        character.setMaxLinearAcceleration(50);
        character.setMaxLinearSpeed(80);
        character.setMaxAngularAcceleration(10); // greater than 0 because independent facing is enabled
        character.setMaxAngularSpeed(5);

        this.wanderSB = new Wander<Vector2>(character) //
                .setFaceEnabled(true) // We want to use Face internally (independent facing is on)
                .setAlignTolerance(0.001f) // Used by Face
                .setDecelerationRadius(5) // Used by Face
                .setTimeToTarget(0.1f) // Used by Face
                .setWanderOffset(90) //
                .setWanderOrientation(10) //
                .setWanderRadius(40) //
                .setWanderRate(MathUtils.PI2 * 4);
        character.setSteeringBehavior(wanderSB);

        testTable.addActor(character);

        character.setPosition(container.stageWidth / 2, container.stageHeight / 2, Align.center);

        Table detailTable = new Table(container.skin);

        detailTable.row();
        addMaxLinearAccelerationController(detailTable, character, 0, 10000, 1);

        detailTable.row();
        addMaxAngularAccelerationController(detailTable, character, 0, 50, 1);

        detailTable.row();
        addMaxAngularSpeedController(detailTable, character, 0, 20, 1);

        detailTable.row();
        final Label labelWanderOffset = new Label("Wander Offset [" + wanderSB.getWanderOffset() + "]", container.skin);
        detailTable.add(labelWanderOffset);
        detailTable.row();
        Slider wanderOffset = new Slider(0, 300, 1, false, container.skin);
        wanderOffset.setValue(wanderSB.getWanderOffset());
        wanderOffset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                wanderSB.setWanderOffset(slider.getValue());
                labelWanderOffset.setText("Wander Offset [" + slider.getValue() + "]");
            }
        });
        detailTable.add(wanderOffset);

        detailTable.row();
        final Label labelWanderRadius = new Label("Wander Radius [" + wanderSB.getWanderRadius() + "]", container.skin);
        detailTable.add(labelWanderRadius);
        detailTable.row();
        Slider wanderRadius = new Slider(0, 200, 1, false, container.skin);
        wanderRadius.setValue(wanderSB.getWanderRadius());
        wanderRadius.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                wanderSB.setWanderRadius(slider.getValue());
                labelWanderRadius.setText("Wander Radius [" + slider.getValue() + "]");
            }
        });
        detailTable.add(wanderRadius);

        detailTable.row();
        final Label labelWanderRate = new Label("Wander Rate [" + wanderSB.getWanderRate() + "]", container.skin);
        detailTable.add(labelWanderRate);
        detailTable.row();
        Slider wanderRate = new Slider(0, MathUtils.PI2 * 10, MathUtils.degreesToRadians, false, container.skin);
        wanderRate.setValue(wanderSB.getWanderRate());
        wanderRate.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                wanderSB.setWanderRate(slider.getValue());
                labelWanderRate.setText("Wander Rate [" + slider.getValue() + "]");
            }
        });
        detailTable.add(wanderRate);

        detailTable.row();
        addSeparator(detailTable);

        detailTable.row();
        CheckBox debug = new CheckBox("Draw circle", container.skin);
        debug.setChecked(drawDebug);
        debug.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                CheckBox checkBox = (CheckBox) event.getListenerActor();
                drawDebug = checkBox.isChecked();
            }
        });
        detailTable.add(debug);

        detailTable.row();
        addSeparator(detailTable);

        detailTable.row();
        addMaxLinearSpeedController(detailTable, character);

        detailWindow = createDetailWindow(detailTable);
    }

    @Override
    public void draw() {
        if (drawDebug) {
            // Draw circle
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.circle(wanderSB.getWanderCenter().x, wanderSB.getWanderCenter().y, wanderSB.getWanderRadius());
            shapeRenderer.end();

            // Draw target
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.circle(wanderSB.getInternalTargetPosition().x, wanderSB.getInternalTargetPosition().y, 4);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
    }
}
