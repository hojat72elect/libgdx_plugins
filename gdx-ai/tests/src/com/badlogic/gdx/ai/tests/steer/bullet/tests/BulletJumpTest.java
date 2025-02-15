package com.badlogic.gdx.ai.tests.steer.bullet.tests;

import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.behaviors.Jump;
import com.badlogic.gdx.ai.steer.behaviors.Jump.GravityComponentHandler;
import com.badlogic.gdx.ai.steer.behaviors.Jump.JumpCallback;
import com.badlogic.gdx.ai.steer.behaviors.Jump.JumpDescriptor;
import com.badlogic.gdx.ai.steer.limiters.LinearLimiter;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath.LinePathParam;
import com.badlogic.gdx.ai.tests.SteeringBehaviorsTest;
import com.badlogic.gdx.ai.tests.steer.bullet.BulletSteeringTest;
import com.badlogic.gdx.ai.tests.steer.bullet.SteeringBulletEntity;
import com.badlogic.gdx.ai.tests.utils.bullet.BulletEntity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

/**
 * A class to test and experiment with the {@link Jump} behavior.
 *
 * 
 */
public class BulletJumpTest extends BulletSteeringTest {

    static final GravityComponentHandler<Vector3> GRAVITY_COMPONENT_HANDLER = new GravityComponentHandler<Vector3>() {

        @Override
        public float getComponent(Vector3 vector) {
            return vector.y;
        }

        @Override
        public void setComponent(Vector3 vector, float value) {
            vector.y = value;
        }
    };

    boolean drawDebug;
    ShapeRenderer shapeRenderer;

    SteeringBulletEntity character;

    Array<Vector3> wayPoints;
    LinePath<Vector3> linePath;
    FollowPath<Vector3, LinePathParam> followPathSB;

    JumpDescriptor<Vector3> jumpDescriptor;
    Jump<Vector3> jumpSB;

    int airbornePlanarVelocityToUse = 0;
    float runUpLength = 3.5f;

    public BulletJumpTest(SteeringBehaviorsTest container) {
        super(container, "Jump");
    }

    @Override
    public void create() {
        super.create();
        drawDebug = true;

        shapeRenderer = new ShapeRenderer();

        Jump.DEBUG_ENABLED = true;

        BulletEntity ground = world.add("ground", 0f, 0f, 0f);
        ground.setColor(0.25f + 0.5f * (float) Math.random(), 0.25f + 0.5f * (float) Math.random(),
                0.25f + 0.5f * (float) Math.random(), 1f);
        System.out.println("ground.body.getFriction()" + ground.body.getFriction());
        ground.body.setFriction(0);

        BulletEntity characterBase = world.add("capsule", new Matrix4());

        character = new SteeringBulletEntity(characterBase) {
            @Override
            public void update(float deltaTime) {
                super.update(deltaTime);
                // Should the character switch to Jump behavior?
                if (character.getSteeringBehavior() == followPathSB) {
                    float d1 = followPathSB.getPathParam().getDistance();
                    float d2 = linePath.getSegments().get(linePath.getSegments().size - 2).getCumulativeLength();
                    float distFromTakeoffPoint = Math.abs(d1 - d2);
                    if (distFromTakeoffPoint < runUpLength) {
                        System.out.println("Switched to Jump behavior. Taking a run up...");
                        System.out.println("run up length = " + distFromTakeoffPoint);
                        character.body.setDamping(0, 0);
                        System.out.println("friction: " + character.body.getFriction());
                        character.body.setFriction(0);
                        System.out.println("owner.linearVelocity = " + character.getLinearVelocity() + "; owner.linearSpeed = "
                                + character.getLinearVelocity().len());
                        character.setSteeringBehavior(jumpSB);
                    }
                }
            }
        };
        character.setMaxLinearAcceleration(100);
        character.setMaxLinearSpeed(5);

        // Remove all stuff that causes jump failure
        // Notice that you might remove this on takeoff and restore on landing
        character.body.setSleepingThresholds(0, 0);
        character.body.setDamping(0, 0);
        character.body.setFriction(0);
// character.body.setMassProps(1, new Vector3(0,0,0));
// character.body.setAnisotropicFriction(new Vector3(0,0,0)); // ???

        wayPoints = createRandomPath(6, 20, 20, 30, 30, 1.5f);

        setCharacterPositionOnPath();

        linePath = new LinePath<Vector3>(wayPoints, false);
        followPathSB = new FollowPath<Vector3, LinePathParam>(character, linePath, 0.5f) //
                // Setters below are only useful to arrive at the end of an open path
                .setArriveEnabled(false) //
                .setTimeToTarget(0.1f) //
                .setArrivalTolerance(0.5f) //
                .setDecelerationRadius(3);

        character.setSteeringBehavior(followPathSB);

        Vector3 takeoffPoint = wayPoints.peek();
        Vector3 landingPoint = wayPoints.first();
        System.out.println("takeoffPoint: " + takeoffPoint);
        System.out.println("landingPoint: " + landingPoint);
        jumpDescriptor = new JumpDescriptor<Vector3>(takeoffPoint, landingPoint);

        JumpCallback jumpCallback = new JumpCallback() {
            final JumpDescriptor<Vector3> newJumpDescriptor = new JumpDescriptor<Vector3>(new Vector3(), new Vector3());

            @Override
            public void reportAchievability(boolean achievable) {
                System.out.println("Jump Achievability = " + achievable);
            }

            @Override
            public void takeoff(float maxVerticalVelocity, float time) {
                System.out.println("Take off!!!");
                System.out.println("Character Velocity = " + character.getLinearVelocity() + "; Speed = "
                        + character.getLinearVelocity().len());
                float h = maxVerticalVelocity * maxVerticalVelocity / (-2f * jumpSB.getGravity().y);
                System.out.println("jump height = " + h);
                switch (airbornePlanarVelocityToUse) {
                    case 0: // Use character velocity on takeoff
                        character.body.setLinearVelocity(character.body.getLinearVelocity().add(0, maxVerticalVelocity, 0));
                        break;
                    case 1: // Use predicted velocity. We are cheating!!!
                        Vector3 targetLinearVelocity = jumpSB.getTarget().getLinearVelocity();
                        character.body.setLinearVelocity(newJumpDescriptor.takeoffPosition.set(targetLinearVelocity.x,
                                maxVerticalVelocity, targetLinearVelocity.z));
                        break;
                    case 2: // Calculate and use exact velocity. We are shamelessly cheating!!!
                        Vector3 newLinearVelocity = character.body.getLinearVelocity();
                        newJumpDescriptor.set(character.getPosition(), jumpSB.getJumpDescriptor().landingPosition);
                        System.out.println("character.pos = " + character.getPosition());
                        time = jumpSB.calculateAirborneTimeAndVelocity(newLinearVelocity, newJumpDescriptor, jumpSB.getLimiter()
                                .getMaxLinearSpeed());
                        character.body.setLinearVelocity(newLinearVelocity.add(0, maxVerticalVelocity, 0));
                        break;
                }
                Telegraph telegraph = new Telegraph() {
                    @Override
                    public boolean handleMessage(Telegram telegram) {
                        if (telegram.message == 1) {
                            System.out.println("Switching to FollowPath");
                            System.out.println("owner.linearVelocity = " + character.getLinearVelocity() + "; owner.linearSpeed = "
                                    + character.getLinearVelocity().len());
                            character.setSteeringBehavior(followPathSB);

                            jumpSB.setJumpDescriptor(jumpDescriptor); // prepare for a new jump
                            return true;
                        }
                        return false;
                    }
                };
                MessageManager.getInstance().dispatchMessage(time, telegraph, telegraph, 1);
            }
        };
        jumpSB = new Jump<Vector3>(character, jumpDescriptor, world.gravity, GRAVITY_COMPONENT_HANDLER, jumpCallback) //
                .setMaxVerticalVelocity(9) //
                .setTakeoffPositionTolerance(.3f) //
                .setTakeoffVelocityTolerance(2f) //
                .setTimeToTarget(.1f);

        // Setup the limiter for the run up
        jumpSB.setLimiter(new LinearLimiter(Float.POSITIVE_INFINITY, character.getMaxLinearSpeed() * 3));

        Table detailTable = new Table(container.skin);

        detailTable.row();
        final Label labelRunUpLenght = new Label("Run Up Length [" + runUpLength + "]", container.skin);
        detailTable.add(labelRunUpLenght);
        detailTable.row();
        Slider sliderRunUpLenght = new Slider(0.1f, 4f, 0.1f, false, container.skin);
        sliderRunUpLenght.setValue(runUpLength);
        sliderRunUpLenght.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                runUpLength = slider.getValue();
                labelRunUpLenght.setText("Run Up Length [" + slider.getValue() + "]");
            }
        });
        detailTable.add(sliderRunUpLenght);

        detailTable.row();
        final Label labelTakeoffPosTol = new Label("Takeoff Pos.Tolerance [" + jumpSB.getTakeoffPositionTolerance() + "]",
                container.skin);
        detailTable.add(labelTakeoffPosTol);
        detailTable.row();
        Slider takeoffPosTol = new Slider(0.1f, 5f, 0.1f, false, container.skin);
        takeoffPosTol.setValue(jumpSB.getTakeoffPositionTolerance());
        takeoffPosTol.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                jumpSB.setTakeoffPositionTolerance(slider.getValue());
                labelTakeoffPosTol.setText("Takeoff Pos.Tolerance [" + slider.getValue() + "]");
            }
        });
        detailTable.add(takeoffPosTol);

        detailTable.row();
        final Label labelTakeoffVelTol = new Label("Takeoff Vel.Tolerance [" + jumpSB.getTakeoffVelocityTolerance() + "]",
                container.skin);
        detailTable.add(labelTakeoffVelTol);
        detailTable.row();
        Slider takeoffVelTol = new Slider(0.1f, 10f, 0.1f, false, container.skin);
        takeoffVelTol.setValue(jumpSB.getTakeoffVelocityTolerance());
        takeoffVelTol.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                jumpSB.setTakeoffVelocityTolerance(slider.getValue());
                labelTakeoffVelTol.setText("Takeoff Vel.Tolerance [" + slider.getValue() + "]");
            }
        });
        detailTable.add(takeoffVelTol);

        detailTable.row();
        final Label labelMaxVertVel = new Label("Max.Vertical Vel. [" + jumpSB.getMaxVerticalVelocity() + "]", container.skin);
        detailTable.add(labelMaxVertVel);
        detailTable.row();
        Slider maxVertVel = new Slider(1f, 15f, 0.5f, false, container.skin);
        maxVertVel.setValue(jumpSB.getMaxVerticalVelocity());
        maxVertVel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slider slider = (Slider) actor;
                jumpSB.setMaxVerticalVelocity(slider.getValue());
                labelMaxVertVel.setText("Max.Vertical Vel. [" + slider.getValue() + "]");
            }
        });
        detailTable.add(maxVertVel);

        detailTable.row();
        addSeparator(detailTable);

        detailTable.row();
        final Label labelJumpVel = new Label("Airborne Planar Velocity To Use", container.skin);
        detailTable.add(labelJumpVel);
        detailTable.row();
        SelectBox<String> jumpVel = new SelectBox<String>(container.skin);
        jumpVel.setItems("Character Velocity on Takeoff", "Predicted Velocity (Cheat!!!)",
                "Calculate Exact Velocity (Cheat!!!)");
        jumpVel.setSelectedIndex(0);
        jumpVel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                @SuppressWarnings("unchecked")
                SelectBox<String> selectBox = (SelectBox<String>) actor;
                airbornePlanarVelocityToUse = selectBox.getSelectedIndex();
            }
        });
        detailTable.add(jumpVel);

        detailTable.row();
        addSeparator(detailTable);

        detailTable.row();
        Button buttonRestart = new TextButton("Restart", container.skin);
        buttonRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setCharacterPositionOnPath();
                character.setSteeringBehavior(followPathSB);
            }
        });
        detailTable.add(buttonRestart);

        detailWindow = createDetailWindow(detailTable);
    }

    @Override
    public void update() {
        MessageManager.getInstance().update();
        character.update(GdxAI.getTimepiece().getDeltaTime());

        super.update();
    }

    @Override
    public void draw() {
        super.draw();

        if (drawDebug) {
            // Draw path
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(0, 1, 0, 1);
            shapeRenderer.setProjectionMatrix(camera.combined);
            for (int i = 0; i < wayPoints.size; i++) {
                int next = (i + 1) % wayPoints.size;
                if (next != 0 || !linePath.isOpen()) shapeRenderer.line(wayPoints.get(i), wayPoints.get(next));
            }
            shapeRenderer.end();

            // Draw hole to jump
            shapeRenderer.begin(ShapeType.Line);
            shapeRenderer.setColor(1, 0, 0, 1);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.line(jumpDescriptor.takeoffPosition, jumpDescriptor.landingPosition);
            shapeRenderer.end();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        MessageManager.getInstance().clear();
    }

    private void setCharacterPositionOnPath() {
        character.transform.setToTranslation(wayPoints.get(1));
        character.body.setWorldTransform(character.transform);
    }

    private static final Matrix3 tmpMatrix3 = new Matrix3();
    private static final Vector2 tmpVector2 = new Vector2();

    /**
     * Creates a random path which is bound by rectangle described by the min/max values
     */
    private static Array<Vector3> createRandomPath(int numWaypoints, float minX, float minY, float maxX, float maxY, float height) {
        Array<Vector3> wayPoints = new Array<Vector3>(numWaypoints);
        wayPoints.size = numWaypoints;

        float midX = (maxX + minX) / 2f;
        float midY = (maxY + minY) / 2f;

        float smaller = Math.min(midX, midY);

        float spacing = MathUtils.PI2 / (numWaypoints);

        for (int i = 0; i < numWaypoints - 2; i++) {
            float radialDist = MathUtils.random(smaller * 0.2f, smaller);
            tmpVector2.set(radialDist, 0.0f);

            // rotates the specified vector angle rads around the origin
            // init and rotate the transformation matrix
            tmpMatrix3.idt().rotateRad(i * spacing);
            // now transform the object's vertices
            tmpVector2.mul(tmpMatrix3);

            wayPoints.set(i + 1, new Vector3(tmpVector2.x, height, tmpVector2.y));
            System.out.println((i + 1) + ": " + wayPoints.get(i + 1));
        }

        Vector3 midpoint = new Vector3(wayPoints.get(1)).add(wayPoints.get(numWaypoints - 2)).scl(0.5f);
        System.out.println("midpoint = " + midpoint);
        // Set the landing point
        wayPoints.set(0, new Vector3(wayPoints.get(1)).add(midpoint).scl(1f / 3f));
        wayPoints.get(0).y = height;
        // Set the takeoff point
        wayPoints.set(numWaypoints - 1, new Vector3(midpoint).add(wayPoints.get(numWaypoints - 2)).scl(1f / 3f));
        wayPoints.get(numWaypoints - 1).y = height;
        System.out.println("0: " + wayPoints.first());
        System.out.println((numWaypoints - 1) + ": " + wayPoints.peek());

        return wayPoints;
    }
}
