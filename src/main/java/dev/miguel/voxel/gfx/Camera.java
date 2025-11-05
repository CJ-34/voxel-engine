package dev.miguel.voxel.gfx;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import org.joml.Math;

public class Camera {
    private static final float YAW = -90.0f;
    private static final float PITCH = 0.0f;
    private static final float SPEED = 2.5f;
    private static final float SENSITIVITY = 0.1f;
    private static final float ZOOM = 45.0f;

    private Vector3f position;
    private Vector3f front;
    private Vector3f up;
    private Vector3f right;
    private Vector3f worldUp;

    private float yaw;
    private float pitch;
    private float movementSpeed;
    private float mouseSensitivity;
    private float zoom;

    public Camera() {
        this(new Vector3f(0.0f, 0.0f, 3.0f), new Vector3f(0.0f, 1.0f, 0.0f), YAW, PITCH);
    }

    public Camera(Vector3f position, Vector3f up, float yaw, float pitch) {
        this.position = position;
        this.worldUp = up;
        this.yaw = yaw;
        this.pitch = pitch;

        front = new Vector3f(0.0f, 0.0f, -1.0f);
        right = new Vector3f();
        this.up = new Vector3f();
        movementSpeed = SPEED;
        mouseSensitivity = SENSITIVITY;
        zoom = ZOOM;

        updateCameraVectors();
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        Vector3f target = new Vector3f(position).add(front);
        viewMatrix.setLookAt(position, target, up);
        return viewMatrix;
    }

    public void processKeyboard(CameraDirection direction, float delta) {
        float velocity = movementSpeed * delta;
        if (direction == CameraDirection.FORWARD)
            position.add(new Vector3f(front).mul(velocity));
        if (direction == CameraDirection.BACKWARD)
            position.sub(new Vector3f(front).mul(velocity));
        if (direction == CameraDirection.RIGHT)
            position.add(new Vector3f(right).mul(velocity));
        if (direction == CameraDirection.LEFT)
            position.sub(new Vector3f(right).mul(velocity));
    }

    public void processMouseMovement(float xoffset, float yoffset, boolean constrainPitch)
    {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        if (constrainPitch) {
            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;
        }

        updateCameraVectors();
    }

    private void updateCameraVectors() {
        front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.normalize();

        // right = normalize(cross(front, worldUp))
        right.set(front).cross(worldUp).normalize();

        // up = normalize(cross(right, front))
        up.set(right).cross(front).normalize();
    }
}
