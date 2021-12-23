package org.thermo.core.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.thermo.core.Camera;
import org.thermo.core.entity.Entity;

public class Transformation {
    public static Matrix4f createTransformationMatrix(Entity entity) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity().translate(entity.getPosition()).
                rotateX((float) Math.toRadians(entity.getRotation().x)).
                rotateY((float) Math.toRadians(entity.getRotation().y)).
                rotateZ((float) Math.toRadians(entity.getRotation().z)).
                scale(entity.getScale());
        return matrix;
    }

    public static Matrix4f getViewMatrix(Camera camera) {
        Vector3f pos = camera.getPosition();
        Vector3f rot = camera.getRotation();
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(rot.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rot.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rot.z), new Vector3f(0, 0, 1));
        // Move all objects in the opposite direction as the camera (in 3D not the camera is moving but all the objects around it
        matrix.translate(-pos.x, -pos.y, -pos.z);
        return matrix;
    }
}
