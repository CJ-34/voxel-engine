package dev.miguel.voxel.gfx;

import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL33.*;

@Getter
public class ShaderProgram {
    private final int id;

    public ShaderProgram() {
        id = glCreateProgram();
    }

    public void attachShader(Shader shader) {
        glAttachShader(id, shader.getId());
    }

    public void bindFragmentDataLoc(int number, String name) {
        glBindFragDataLocation(id, number, name);
    }

    public void link() {
        glLinkProgram(id);

        checkStatus();
    }

    public void checkStatus() {
        int status = glGetProgrami(id, GL_LINK_STATUS);
        if (status != GL_TRUE) {
            throw new RuntimeException(glGetProgramInfoLog(id));
        }
    }

    public int getAttributeLocation(String name) {
        return glGetAttribLocation(id, name);
    }

    public void enableVertexAttribute(int location) {
        glEnableVertexAttribArray(location);
    }

    public void disableVertexAttribute(int location) {
        glDisableVertexAttribArray(location);
    }

    public void pointVertexAttribute(int location, int size, int stride, int offset) {
        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
    }

    public int getUniformLocation(String name) {
        return glGetUniformLocation(id, name);
    }

    public void setUniform(int location, int value) {
        glUniform1i(location, value);
    }

    public void setUniform(int location, Vector2f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform2fv(location, value.get(stack.mallocFloat(2)));
        }
    }

    public void setUniform(int location, Vector3f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform3fv(location, value.get(stack.mallocFloat(3)));
        }
    }

    public void setUniform(int location, Matrix4f value) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(location, false, value.get(stack.mallocFloat(16)));
        }
    }

    public void use() {
        glUseProgram(id);
    }

    public void delete() {
        glDeleteProgram(id);
    }

    public static ShaderProgram fromFiles(String vertexPath, String fragPath) {
        Shader vert = Shader.loadFromFile(GL_VERTEX_SHADER, vertexPath);
        Shader frag = Shader.loadFromFile(GL_FRAGMENT_SHADER, fragPath);

        ShaderProgram program = new ShaderProgram();
        program.attachShader(vert);
        program.attachShader(frag);
        program.link();

        vert.delete();
        frag.delete();
        return program;
    }
}
