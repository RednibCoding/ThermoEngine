package org.thermo.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.thermo.core.utils.Constants;
import org.thermo.test.Launcher;

public class EngineManager {
    public static final long NANOSECONDS = 1000000000L;

    private static int fps;
    private static float framerate = 1000;
    private static float frametime = 1.0f / framerate;
    public static float deltaTime = 0;
    private boolean isRunning;

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput(window);
        window.init();
        gameLogic.init();
        mouseInput.init();
    }

    public void start() throws Exception {
        init();
        if(isRunning) {
            return;
        }
        run();
    }

    public void run() {
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECONDS;
            frameCounter += passedTime;

            input();

            while(unprocessedTime > frametime) {
                render = true;
                unprocessedTime -= frametime;

                if (window.windowShouldClose()) {
                    stop();
                }

                if (frameCounter >= NANOSECONDS) {
                    setFps(frames);
                    deltaTime = 1.0f / fps;
                    window.setTitle(Constants.TITLE + " FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update(deltaTime);
                render();
                frames++;
            }
        }

        free();
    }

    private void stop() {
        if(!isRunning) {
            return;
        }
        isRunning = false;
    }

    private void input() {
        mouseInput.input();
        gameLogic.input();
    }

    private void render() {
        gameLogic.render();
        window.update();
    }

    private void update(float deltaTime) {
        gameLogic.update(deltaTime, mouseInput);
    }

    private void free() {
        window.close();
        gameLogic.free();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }
}
