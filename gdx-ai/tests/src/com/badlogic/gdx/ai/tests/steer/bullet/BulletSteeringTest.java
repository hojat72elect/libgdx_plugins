package com.badlogic.gdx.ai.tests.steer.bullet;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.steer.Limiter;
import com.badlogic.gdx.ai.tests.SteeringBehaviorsTest;
import com.badlogic.gdx.ai.tests.steer.SteeringTestBase;
import com.badlogic.gdx.ai.tests.utils.bullet.BulletConstructor;
import com.badlogic.gdx.ai.tests.utils.bullet.BulletWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.linearmath.LinearMath;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw.DebugDrawModes;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Base class for bullet steering behavior tests.
 *
 * 
 */
@SuppressWarnings("deprecation")
public abstract class BulletSteeringTest extends SteeringTestBase {

    public PerspectiveCamera camera;
    protected Viewport viewport;
    protected CameraInputController cameraController;

    // Set this to the path of the lib to use it on desktop instead of default lib.
    private final static String customDesktopLib = null; // "C:\\Xoppa\\code\\libgdx\\extensions\\gdx-bullet\\jni\\vs\\gdxBullet\\Debug\\gdxBullet.dll";

    private static boolean initialized = false;

    public static boolean shadows = false;

    public static void init() {
        if (initialized) return;
        // Need to initialize bullet before using it.
        if (Gdx.app.getType() == ApplicationType.Desktop && customDesktopLib != null) {
            System.load(customDesktopLib);
        } else
            Bullet.init();
        Gdx.app.log("Bullet", "Version = " + LinearMath.btGetVersion());
        initialized = true;
    }

    public Environment environment;
    public DirectionalLight light;
    public ModelBatch shadowBatch;

    public BulletWorld world;
    public ObjLoader objLoader = new ObjLoader();
    public ModelBuilder modelBuilder = new ModelBuilder();
    public ModelBatch modelBatch;
    public Array<Disposable> disposables = new Array<Disposable>();
    private final int debugMode = DebugDrawModes.DBG_NoDebug; // DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE;

    protected final static Vector3 tmpV1 = new Vector3(), tmpV2 = new Vector3();

    protected boolean spaceToMoveTarget;

    public BulletSteeringTest(SteeringBehaviorsTest container, String name) {
        super(container, "Bullet", name);
    }

    @Override
    public String getHelpMessage() {
        return spaceToMoveTarget ? "Keep SPACE pressed to move the target" : "";
    }

    public BulletWorld createWorld() {
        return new BulletWorld(new Vector3(0, -9.81f, 0));
    }

    protected void setNewTargetInputProcessor(SteeringBulletEntity target, Vector3 offset) {
        BulletTargetInputProcessor bulletTargetInputProcessor = new BulletTargetInputProcessor(target, offset, viewport,
                world.collisionWorld);
        setInputProcessor(new InputMultiplexer(bulletTargetInputProcessor, cameraController));
        spaceToMoveTarget = true;
    }

    @Override
    public void create() {
        init();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
        light = shadows ? new DirectionalShadowLight(1024, 1024, 20f, 20f, 1f, 300f) : new DirectionalLight();
        light.set(0.8f, 0.8f, 0.8f, -0.5f, -1f, 0.7f);
        environment.add(light);
        if (shadows) environment.shadowMap = (DirectionalShadowLight) light;
        shadowBatch = new ModelBatch(new DepthShaderProvider());

        modelBatch = new ModelBatch();

        world = createWorld();

        final float width = Gdx.graphics.getWidth();
        final float height = Gdx.graphics.getHeight();
        if (width > height)
            camera = new PerspectiveCamera(67f, 3f * width / height, 3f);
        else
            camera = new PerspectiveCamera(67f, 3f, 3f * height / width);
        camera.position.set(25f, 20f, 25f);
        camera.lookAt(0, 0, 0);
        camera.update();

        viewport = new ScreenViewport(camera);

        cameraController = new CameraInputController(camera);
        inputProcessor = cameraController;

        // Create some simple models
        float size = 200f;
        final Model groundModel = modelBuilder.createRect(
                size,
                0f,
                -size,
                -size,
                0f,
                -size,
                -size,
                0f,
                size,
                size,
                0f,
                size,
                0,
                1,
                0,
                new Material(ColorAttribute.createDiffuse(Color.WHITE), ColorAttribute.createSpecular(Color.WHITE), FloatAttribute
                        .createShininess(16f)), Usage.Position | Usage.Normal);
        disposables.add(groundModel);
        final Model boxModel = modelBuilder.createBox(1f, 5f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), Usage.Position | Usage.Normal);
        disposables.add(boxModel);

        final Model wallModel = modelBuilder.createBox(20f, 6f, 1f, new Material(ColorAttribute.createDiffuse(Color.WHITE),
                ColorAttribute.createSpecular(Color.WHITE), FloatAttribute.createShininess(64f)), Usage.Position | Usage.Normal);
        disposables.add(wallModel);

        final Model coneModel = modelBuilder.createCone(1f, 2f, 1f, 10, GL20.GL_TRIANGLES, new Material(ColorAttribute.createDiffuse(Color.YELLOW)), Usage.Position | Usage.Normal);
        disposables.add(coneModel);

        // Create a visual representation of the character.
        // Note that we don't use the physics part of BulletEntity, we'll do that manually
        final Texture texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        disposables.add(texture);
        final Material material = new Material(TextureAttribute.createDiffuse(texture), ColorAttribute.createSpecular(1, 1, 1, 1),
                FloatAttribute.createShininess(8f));
        final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
        final Model capsule = modelBuilder.createCapsule(0.49f, 2.9f, 16, material, attributes);
        disposables.add(capsule);

        // Add the constructors
        world.addConstructor("capsule", new BulletConstructor(capsule, 0.5f, new btCapsuleShape(0.5f, 2f)));
        world.addConstructor("ground", new BulletConstructor(groundModel, 0f)); // mass = 0: static body
        world.addConstructor("box", new BulletConstructor(boxModel, 1f)); // mass = 1kg: dynamic body
        world.addConstructor("staticbox", new BulletConstructor(boxModel, 0f)); // mass = 0: static body
        world.addConstructor("staticwall", new BulletConstructor(wallModel, 0f)); // mass = 0: static body
        world.addConstructor("cone", new BulletConstructor(coneModel, 1f, new btConeShape(0.5f, 2f))); // mass = 1kg: dynamic body
    }

    @Override
    public void update() {
        world.update();
        cameraController.update();
    }

    @Override
    public void draw() {
        render(false);
    }

    public void render(boolean update) {
        if (update) update();

        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        beginRender(true);
        renderWorld();

        if (debugMode != DebugDrawModes.DBG_NoDebug) world.setDebugMode(debugMode);
    }

    protected void beginRender(boolean lighting) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
    }

    protected void renderWorld() {
        if (shadows) {
            ((DirectionalShadowLight) light).begin(Vector3.Zero, camera.direction);
            shadowBatch.begin(((DirectionalShadowLight) light).getCamera());
            world.render(shadowBatch, null);
            shadowBatch.end();
            ((DirectionalShadowLight) light).end();
        }

        modelBatch.begin(camera);
        world.render(modelBatch, environment);
        modelBatch.end();
    }

    @Override
    public void dispose() {
        world.dispose();
        world = null;

        for (Disposable disposable : disposables)
            disposable.dispose();
        disposables.clear();

        modelBatch.dispose();
        modelBatch = null;

        shadowBatch.dispose();
        shadowBatch = null;

        if (shadows) ((DirectionalShadowLight) light).dispose();
        light = null;
    }

    protected void addMaxLinearSpeedController(Table table, final Limiter limiter) {
        addMaxLinearSpeedController(table, limiter, 0, 50, 0.1f);
    }

    protected void addMaxLinearAccelerationController(Table table, final Limiter limiter) {
        addMaxLinearAccelerationController(table, limiter, 0, 50, 0.1f);
    }
}
