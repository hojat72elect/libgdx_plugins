package com.badlogic.gdx.ai.tests.steer.bullet.tests;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.behaviors.Face;
import com.badlogic.gdx.ai.tests.SteeringBehaviorsTest;
import com.badlogic.gdx.ai.tests.steer.bullet.BulletSteeringTest;
import com.badlogic.gdx.ai.tests.steer.bullet.SteeringBulletEntity;
import com.badlogic.gdx.ai.tests.utils.bullet.BulletEntity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * A class to test and experiment with the {@link Face} behavior.
 *
 * @autor davebaol
 */
public class BulletFaceTest extends BulletSteeringTest {

    SteeringBulletEntity character;
    SteeringBulletEntity target;

    public BulletFaceTest(SteeringBehaviorsTest container) {
        super(container, "Face");
    }

    @Override
    public void create() {
        super.create();

        BulletEntity ground = world.add("ground", 0f, 0f, 0f);
        ground.setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 1f);
        ground.body.userData = "ground";

        BulletEntity characterBase = world.add("capsule", new Matrix4());

        character = new SteeringBulletEntity(characterBase, true);
        character.setMaxAngularAcceleration(20);
        character.setMaxAngularSpeed(10);

        BulletEntity targetBase = world.add("staticbox", new Matrix4().setToTranslation(new Vector3(5f, 1.5f, 5f)));
        targetBase.body.setCollisionFlags(targetBase.body.getCollisionFlags()
                | btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
        target = new SteeringBulletEntity(targetBase);

        setNewTargetInputProcessor(target, new Vector3(0, 1.5f, 0));

        final Face<Vector3> faceSB = new Face<Vector3>(character, target) //
                .setAlignTolerance(.01f) //
                .setDecelerationRadius(MathUtils.PI) //
                .setTimeToTarget(.18f);

        character.setSteeringBehavior(faceSB);

        Table detailTable = new Table(container.skin);

        detailTable.row();
        addMaxAngularAccelerationController(detailTable, character, 0, 100, 1);

        detailTable.row();
        addMaxAngularSpeedController(detailTable, character, 0, 30, 1);

        detailTable.row();
        final Label labelDecelerationRadius = new Label("Deceleration Radius [" + faceSB.getDecelerationRadius() + "]",
                container.skin);
        detailTable.add(labelDecelerationRadius);
        detailTable.row();
        Slider decelerationRadius = new Slider(0, MathUtils.PI2, MathUtils.degreesToRadians, false, container.skin);
        decelerationRadius.setValue(faceSB.getDecelerationRadius());
        decelerationRadius.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                faceSB.setDecelerationRadius(slider.getValue());
                labelDecelerationRadius.setText("Deceleration Radius [" + slider.getValue() + "]");
            }
        });
        detailTable.add(decelerationRadius);

        detailTable.row();
        final Label labelAlignTolerance = new Label("Align tolerance [" + faceSB.getAlignTolerance() + "]", container.skin);
        detailTable.add(labelAlignTolerance);
        detailTable.row();
        Slider alignTolerance = new Slider(0, 1, 0.0001f, false, container.skin);
        alignTolerance.setValue(faceSB.getAlignTolerance());
        alignTolerance.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                faceSB.setAlignTolerance(slider.getValue());
                labelAlignTolerance.setText("Align tolerance [" + slider.getValue() + "]");
            }
        });
        detailTable.add(alignTolerance);

        detailTable.row();
        final Label labelTimeToTarget = new Label("Time to Target [" + faceSB.getTimeToTarget() + " sec.]", container.skin);
        detailTable.add(labelTimeToTarget);
        detailTable.row();
        Slider timeToTarget = new Slider(0, 1, 0.01f, false, container.skin);
        timeToTarget.setValue(faceSB.getTimeToTarget());
        timeToTarget.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                faceSB.setTimeToTarget(slider.getValue());
                labelTimeToTarget.setText("Time to Target [" + slider.getValue() + " sec.]");
            }
        });
        detailTable.add(timeToTarget);

        detailWindow = createDetailWindow(detailTable);
    }

    @Override
    public void update() {
        character.update(GdxAI.getTimepiece().getDeltaTime());

        super.update();
    }

    @Override
    public void draw() {
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
