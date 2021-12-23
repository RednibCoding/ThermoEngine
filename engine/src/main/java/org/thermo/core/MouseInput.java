package org.thermo.core;

import com.sun.tools.javac.Main;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    private final Vector2d previousPos, currentPos;
    private final Vector2i mouseSpeed;
    private final WindowManager window;

    private boolean inWindow = false, leftButtonDown = false, rightButtonDown = false;

    public MouseInput(WindowManager window) {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        mouseSpeed = new Vector2i();
        this.window = window;
    }

    public void init() {
        GLFW.glfwSetCursorPosCallback(window.getWindow(), (wnd, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });

        GLFW.glfwSetCursorEnterCallback(window.getWindow(), (wnd, entered) -> {
            inWindow = entered;
        });

        GLFW.glfwSetMouseButtonCallback(window.getWindow(), (wnd, button, action, mods) -> {
            leftButtonDown = button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS;
            rightButtonDown = button == GLFW.GLFW_MOUSE_BUTTON_2 && action == GLFW.GLFW_PRESS;
        });
    }

    public void input() {
        mouseSpeed.x = 0;
        mouseSpeed.y = 0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double x = currentPos.x - previousPos.x;
            double y = currentPos.y - previousPos.y;
            boolean movedInX = x != 0;
            boolean movedInY = y != 0;
            if (movedInX) {
                mouseSpeed.y = (int) x;
            }
            if (movedInY) {
                mouseSpeed.x = (int) y;
            }
        }
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public Vector2i getSpeed() {
        return mouseSpeed;
    }

    public Vector2d getPosition() {
        return currentPos;
    }

    public boolean isLeftButtonDown() {
        return leftButtonDown;
    }

    public boolean isRightButtonDown() {
        return rightButtonDown;
    }
}
