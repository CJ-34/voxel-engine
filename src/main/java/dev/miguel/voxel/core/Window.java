package dev.miguel.voxel.core;

import lombok.Data;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

@Data
public class Window {
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    private int height;
    private int width;
    private long handle;
    private GLCapabilities caps;

    Window() {
        this("Voxel", DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    Window(String title, int w, int h) {
        this.width = w;
        this.height = h;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);

        handle = glfwCreateWindow(width, height, title, NULL, NULL);

        if (handle == NULL) throw new RuntimeException("Failed to create the GLFW window.");
        glfwMakeContextCurrent(handle);

        caps = GL.createCapabilities();
    }

    public void swap() {
        glfwSwapBuffers(handle);
    }

    public void poll() {
        glfwPollEvents();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void setShouldClose(boolean value) {
        glfwSetWindowShouldClose(handle, value);
    }

    public void destroy() {
        glfwDestroyWindow(handle);
    }
}
