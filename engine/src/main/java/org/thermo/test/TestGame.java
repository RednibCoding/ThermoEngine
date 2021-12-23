package org.thermo.test;

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.thermo.core.*;
import org.thermo.core.entity.Entity;
import org.thermo.core.entity.Model;
import org.thermo.core.entity.Texture;

import static org.thermo.core.utils.Constants.*;

public class TestGame implements ILogic {

    private final RenderManager renderer;
    private final ObjectLoader loader;
    private final WindowManager window;

    private Entity entity;
    private Camera camera;

    Vector3f cameraVec;

    public TestGame() {
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraVec = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        Model model = loader.loadObjModel("/models/wolf/wolf.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/wolf.png")), 1f);
        entity = new Entity(model, new Vector3f(0, 0, -5), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        cameraVec.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraVec.z = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraVec.z = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraVec.x = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraVec.x = 1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_Y))
            cameraVec.z = -1;
        if (window.isKeyPressed(GLFW.GLFW_KEY_X))
            cameraVec.z = 1;
    }

    @Override
    public void update(float deltaTime, MouseInput mouseInput) {
        camera.move(cameraVec.x * CAMERA_MOVE_SPEED, cameraVec.y * CAMERA_MOVE_SPEED, cameraVec.z * CAMERA_MOVE_SPEED);

        if (mouseInput.isRightButtonDown()) {
            Vector2i rotVec = mouseInput.getSpeed();
            camera.rotate(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
        entity.incRotation(0.0f, 0.25f, 0.0f);
    }

    @Override
    public void render() {
        renderer.render(entity, camera);
    }

    @Override
    public void free() {
        renderer.free();
        loader.free();
    }
}
