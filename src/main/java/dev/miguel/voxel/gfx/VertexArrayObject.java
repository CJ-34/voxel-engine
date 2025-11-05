package dev.miguel.voxel.gfx;

import lombok.Getter;

import static org.lwjgl.opengl.GL33.*;

@Getter
public class VertexArrayObject {
    private final int id;

    public VertexArrayObject() {
        id = glGenVertexArrays();
    }

    public void bind() {
        glBindVertexArray(id);
    }

    public void unbind() {
        glBindVertexArray(0);
    }

    public void delete() {
        glDeleteVertexArrays(id);
    }
}
