package dev.miguel.voxel.input;

import lombok.Getter;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;

public final class Input {
    private static long window;

    private static final boolean[] keys = new boolean[GLFW_KEY_LAST + 1];

    private static final boolean[] keysPrev = new boolean[GLFW_KEY_LAST + 1];

    private static final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    private static final boolean[] mouseButtonsPrev = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    private static double mouseX, mouseY;
    private static double lastMouseX, lastMouseY;
    @Getter
    private static double mouseDeltaX, mouseDeltaY;

    private static double scrollx, scrolly;

    private static boolean initialized = false;

    private Input() {}

    public static void init(long windowHandle) {
        if (initialized) return;
        initialized = true;

        window = windowHandle;

        glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key < 0 || key > GLFW_KEY_LAST) return;

            boolean isDown = action != GLFW_RELEASE;
            keys[key] = isDown;
        });

        glfwSetMouseButtonCallback(window, (win, button, action, mods) -> {
            if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return;

            boolean isDown = action != GLFW_RELEASE;
            mouseButtons[button] = isDown;
        });

        glfwSetCursorPosCallback(window, (win, x, y) -> {
            mouseX = x;
            mouseY = y;
        });

        glfwSetScrollCallback(window, (win, x, y) -> {
            scrollx += x;
            scrolly += y;
        });

        try (MemoryStack stack = MemoryStack.stackPush()) {
           DoubleBuffer px = stack.mallocDouble(1);
           DoubleBuffer py = stack.mallocDouble(1);
           glfwGetCursorPos(window, px, py);
           mouseX = lastMouseX = px.get(0);
           mouseY = lastMouseY = py.get(0);
        }
    }

    public static void update() {
        System.arraycopy(keys, 0, keysPrev, 0, keys.length);
        System.arraycopy(mouseButtons, 0, mouseButtonsPrev, 0, mouseButtons.length);

        mouseDeltaX = mouseX - lastMouseX;
        mouseDeltaY = lastMouseY - mouseY;
        lastMouseX = mouseX;
        lastMouseY = mouseY;

        scrollx = 0.0;
        scrolly = 0.0;
    }

    public static boolean isKeyDown(int key) {
        if (key < 0 || key > GLFW_KEY_LAST) return false;
        return keys[key];
    }

    public static boolean isKeyPressed(int key) {
        if (key < 0 || key > GLFW_KEY_LAST) return false;
        return keys[key] && !keysPrev[key];
    }

    public static boolean isKeyReleased(int key) {
       if (key < 0 || key > GLFW_KEY_LAST) return false;
       return !keys[key] && keysPrev[key];
    }

    public static boolean isMouseDown(int button) {
        if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return false;
        return mouseButtons[button];
    }

    public static boolean isMousePressed(int button) {
        if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return false;
        return mouseButtons[button] && !mouseButtonsPrev[button];
    }

    public static boolean isMouseReleased(int button) {
        if (button < 0 || button > GLFW_MOUSE_BUTTON_LAST) return false;
        return !mouseButtons[button] && mouseButtonsPrev[button];
    }

    public static void centerCursor() {
        int[] width = new int[1];
        int[] height = new int[1];

        glfwGetWindowSize(window, width, height);
        double cx = width[0] / 2.0;
        double cy = height[0] / 2.0;

        glfwSetCursorPos(window, cx, cy);
        mouseX = lastMouseX = cx;
        mouseY = lastMouseY = cy;
        mouseDeltaX = mouseDeltaY = 0.0;
    }

    public static void setCursorVisible(boolean visible) {
        glfwSetInputMode(window, GLFW_CURSOR, visible ? GLFW_CURSOR_NORMAL : GLFW_CURSOR_DISABLED);
    }
}
