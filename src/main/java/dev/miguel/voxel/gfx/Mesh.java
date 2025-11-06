package dev.miguel.voxel.gfx;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;

public class Mesh {
    private final VertexArrayObject vao;
    private final BufferObject vbo;
    private final BufferObject ebo;
    private int indexCount;

    public Mesh() {
       vao =  new VertexArrayObject();
       vbo = new BufferObject();
       ebo = new BufferObject();
    }

    public void upload(float[] vertices, int[] indices, ShaderProgram shader) {
        indexCount = indices.length;
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
        indexBuffer.put(indices);
        indexBuffer.flip();
        vertexBuffer.put(vertices);
        vertexBuffer.flip();

        vao.bind();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        ebo.bind(GL_ELEMENT_ARRAY_BUFFER);
        ebo.uploadData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        int stride = 8 * Float.BYTES;
        int offset = 0;

        int posLoc = shader.getAttributeLocation("position");
        int normalLoc = shader.getAttributeLocation("normal");
        int uvLoc = shader.getAttributeLocation("uv");

        shader.enableVertexAttribute(posLoc);
        shader.pointVertexAttribute(posLoc, 3, stride, offset);

        offset += 3 * Float.BYTES;
        shader.enableVertexAttribute(normalLoc);
        shader.pointVertexAttribute(normalLoc, 3, stride, offset);

        offset += 3 * Float.BYTES;
        shader.enableVertexAttribute(uvLoc);
        shader.pointVertexAttribute(uvLoc, 2, stride, offset);

        vao.unbind();
    }

    public void draw() {
        vao.bind();
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        vao.unbind();
    }

    public void delete() {
        vao.delete();
        vbo.delete();
        ebo.delete();
    }
}
