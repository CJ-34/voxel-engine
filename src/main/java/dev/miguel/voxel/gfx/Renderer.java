package dev.miguel.voxel.gfx;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL33.*;

public class Renderer {
    public Renderer() {
       glEnable(GL_DEPTH_TEST);
       glEnable(GL_CULL_FACE);
       glCullFace(GL_BACK);
       glFrontFace(GL_CCW);
    }

    public void clear(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Mesh mesh, ShaderProgram shader,
                       Matrix4f projection, Matrix4f view, Matrix4f model) {
        shader.use();
        shader.setUniform(shader.getUniformLocation("projection"),  projection);
        shader.setUniform(shader.getUniformLocation("view"),  view);
        shader.setUniform(shader.getUniformLocation("model"),  model);

        mesh.draw();
    }
}
