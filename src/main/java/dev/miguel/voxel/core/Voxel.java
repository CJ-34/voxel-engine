package dev.miguel.voxel.core;


import dev.miguel.voxel.gfx.*;
import dev.miguel.voxel.input.Input;
import dev.miguel.voxel.world.BlockType;
import dev.miguel.voxel.world.Chunk;
import org.joml.Math;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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

    private static Renderer renderer;

    public static void update(float delta) {
        angle += delta * anglePerSecond;
    }

    public static void render(float alpha) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        Matrix4f model = new Matrix4f().rotate(angle, 0f, 0f, 1f);
        Matrix4f view = camera.getViewMatrix();
        program.setUniform(program.getUniformLocation("model"), model);
        program.setUniform(program.getUniformLocation("view"), view);

        glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0);
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

        glEnable(GL_DEPTH_TEST);

        program = ShaderProgram.fromFiles("shaders/vertex.glsl",  "shaders/fragment.glsl");

        TextureAtlas atlas = new TextureAtlas("textures/minecraft_texture_atlas.png", 16);
        atlas.addRegion("grass_top", 0, 0);
        atlas.addRegion("grass_side", 1, 0);
        atlas.addRegion("dirt", 2, 0);
        atlas.addRegion("stone", 3, 0);

        for (BlockType t : BlockType.values()) {
            t.bindRegions(atlas);
        }

        program.use();

        Chunk chunk = new Chunk();
        Mesh chunkMesh = ChunkMesher.build(chunk, program);

        renderer = new Renderer();

        float ratio = WIDTH / (float) HEIGHT;
        Matrix4f projection = new Matrix4f().perspective(Math.toRadians(45.0f), ratio, 0.1f, 1500f);
        program.setUniform(program.getUniformLocation("projection"), projection);

        while (!window.shouldClose()) {
            double time = glfwGetTime();
            float deltaTime = (float) time - lastFrameTime;
            lastFrameTime = (float) time;

//            update(deltaTime);
            Input.update();
            processInput(deltaTime);

            camera.processMouseMovement((float) Input.getMouseDeltaX(), (float) Input.getMouseDeltaY(), false);

//            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

            renderer.clear(0.1f, 0.1f, 0.1f, 1.0f);

//            render(0);
            atlas.bind();
            program.use();
            program.setUniform(program.getUniformLocation("atlas"), 0);

//            glDisable(GL_CULL_FACE);

            renderer.render(chunkMesh, program, projection, camera.getViewMatrix(), new Matrix4f().translate(0f, -48, 0f));

            window.swap();
            window.poll();
        }

        chunkMesh.delete();
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