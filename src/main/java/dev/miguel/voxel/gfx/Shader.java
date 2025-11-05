package dev.miguel.voxel.gfx;

import dev.miguel.voxel.utils.FileUtils;
import lombok.Getter;

import java.io.IOException;

import static org.lwjgl.opengl.GL33.*;

@Getter
public class Shader {
    private final int id;

    Shader(int type) {
        id = glCreateShader(type);
    }

    public void source(String source) {
        glShaderSource(id, source);
    }

    public void compile() {
        glCompileShader(id);
    }

    public void checkStatus() {
        int status = glGetShaderi(id, GL_COMPILE_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException("Could not compile shader " + id);
        }
    }

    public void delete() {
        glDeleteShader(id);
    }

    public static Shader createShader(int type, String source) {
        Shader shader = new Shader(type);
        shader.source(source);
        shader.compile();
        shader.checkStatus();

        return shader;
    }

    public static Shader loadFromFile(int type, String path) {
        String source = "";
        try {
            source = FileUtils.readFile(path);
        } catch (IOException exception) {
           throw new RuntimeException("Failed to load a shader file!" + System.lineSeparator() + exception.getMessage());
        }
        return createShader(type, source);
    }
}
