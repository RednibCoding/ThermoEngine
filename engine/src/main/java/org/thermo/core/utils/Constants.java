package org.thermo.core.utils;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Constants {

    public static final String TITLE = "Thermo Engine";

    public static final float FOV = (float) Math.toRadians(72); // 72
    public static final float Z_NEAR = 0.01f;
    public static final float Z_FAR = 1000f;

    public static final float MOUSE_SENSITIVITY = 0.2f;
    public static final float CAMERA_MOVE_SPEED = 0.05f;

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Vector3f AMBIENT_LIGHT = new Vector3f(1.3f, 1.3f, 1.3f);
}
