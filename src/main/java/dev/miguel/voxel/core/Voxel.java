package dev.miguel.voxel.core;


import dev.miguel.voxel.gfx.*;
import dev.miguel.voxel.input.Input;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Voxel {
    private static GLFWErrorCallback errorCallback = GLFWErrorCallback.createPrint(System.err);

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static int uniModel;
    private static float angle = 0f;
    private static float anglePerSecond = 2f;

    private static float lastFrameTime = 0.0f;
    private static Window window;
    private static Camera camera = new Camera();

    private static ShaderProgram program;

    public static void update(float delta) {
        angle += delta * anglePerSecond;
    }

    public static void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT);

        Matrix4f model = new Matrix4f().rotate(angle, 0f, 0f, 1f);
        Matrix4f view = camera.getViewMatrix();
        program.setUniform(program.getUniformLocation("model"), model);
        program.setUniform(program.getUniformLocation("view"), view);

        glDrawArrays(GL_TRIANGLES, 0, 31);
    }

    public static void main(String[] args) throws IOException {
        glfwSetErrorCallback(errorCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }
        window = new Window();
        Input.init(window.getHandle());
        Input.setCursorVisible(false);

        Input.centerCursor();

        VertexArrayObject vao = new VertexArrayObject();
        BufferObject vbo = new BufferObject();

        float[] vertices = {
            -0.6f, -0.4f, 0f, 1f, 0f, 0f,
            0.6f, -0.4f, 0f, 1f, 0f, 0f,
                0f, 0.6f, 0f, 0f, 0f, 1f
        };
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices);
        vertexBuffer.flip();

        vao.bind();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        Shader vertex = Shader.loadFromFile(GL_VERTEX_SHADER, "shaders/vertex.glsl");
        Shader fragment = Shader.loadFromFile(GL_FRAGMENT_SHADER, "shaders/fragment.glsl");
        program = new ShaderProgram();
        program.attachShader(vertex);
        program.attachShader(fragment);
        program.link();

        program.use();

        int floatSize = Float.BYTES;

        int posAttrib = program.getAttributeLocation("position");
        program.pointVertexAttribute(posAttrib, 3, 6 * floatSize, 0);
        program.enableVertexAttribute(posAttrib);

        int colorAttrib = program.getAttributeLocation("color");
        program.pointVertexAttribute(colorAttrib, 3, 6 * floatSize, 3 * floatSize);
        program.enableVertexAttribute(colorAttrib);

        float ratio = WIDTH / (float) HEIGHT;
        Matrix4f projection = new Matrix4f().perspective(Math.toRadians(45.0f), ratio, 0.1f, 1000f);

        program.setUniform(program.getUniformLocation("projection"), projection);

        while (!window.shouldClose()) {
            double time = glfwGetTime();
            float deltaTime = (float) time - lastFrameTime;
            lastFrameTime = (float) time;

            update(deltaTime);
            Input.update();
            processInput(deltaTime);

            camera.processMouseMovement((float) Input.getMouseDeltaX(), (float) Input.getMouseDeltaY(), false);

            render(0);

            window.swap();
            window.poll();
        }

        vao.delete();
        vbo.delete();
        vertex.delete();
        fragment.delete();
        program.delete();

        window.destroy();
        glfwTerminate();
        errorCallback.free();
    }

    public static void processInput(float delta) {
        if (Input.isKeyDown(GLFW_KEY_ESCAPE))
           window.setShouldClose(true);
        if (Input.isKeyDown(GLFW_KEY_W))
            camera.processKeyboard(CameraDirection.FORWARD, delta);
        if (Input.isKeyDown(GLFW_KEY_S))
            camera.processKeyboard(CameraDirection.BACKWARD, delta);
        if (Input.isKeyDown(GLFW_KEY_A))
            camera.processKeyboard(CameraDirection.LEFT, delta);
        if (Input.isKeyDown(GLFW_KEY_D))
            camera.processKeyboard(CameraDirection.RIGHT, delta);

    }
}