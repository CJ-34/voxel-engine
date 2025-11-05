package dev.miguel.voxel.gfx;

import lombok.Getter;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

@Getter
public class BufferObject {
    private final int id;

    public BufferObject() {
       id = glGenBuffers();
    }

    public void bind(int target) {
        glBindBuffer(target, id);
    }

    public void unbind(int target) {
        glBindBuffer(target, 0);
    }

    public void uploadData(int target, FloatBuffer buffer, int usage) {
        glBufferData(target, buffer, usage);
    }

    public void uploadData(int target, long size, int usage) {
        glBufferData(target, size, usage);
    }

    public void uploadSubData(int target, long offset, FloatBuffer buffer) {
        glBufferSubData(target, offset, buffer);
    }
    public void uploadData(int target, IntBuffer buffer, int usage) {
       glBufferData(target, buffer, usage);
    }

    public void delete() {
        glDeleteBuffers(id);
    }

}
