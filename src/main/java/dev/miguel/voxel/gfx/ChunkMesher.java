package dev.miguel.voxel.gfx;

import dev.miguel.voxel.world.BlockType;
import dev.miguel.voxel.world.Chunk;

public class ChunkMesher {
    public static Mesh build(Chunk chunk, ShaderProgram shader) {
        int maxFaces = Chunk.W * Chunk.H * Chunk.D * 6;
        int maxVerts = maxFaces * 4;
        int maxIndices = maxFaces * 6;

        float[] vertices = new float[maxVerts * 6];
        float[] indices = new float[maxIndices];

        int vertexCount = 0;
        int indexCount = 0;

        for (int y = 0; y < Chunk.H; y++) {
            for (int z = 0; z < Chunk.D; z++) {
                for (int x = 0; x < Chunk.W; x++) {
                    BlockType type = chunk.get(x, y, z);
                    if (!type.solid) continue;

                }
            }
        }

        float[] v = new float[vertexCount];
        System.arraycopy(vertices, 0, v, 0, vertexCount);
        int[] i = new int[indexCount];
        System.arraycopy(indices, 0, i, 0, indexCount);

        Mesh mesh = new Mesh();
        mesh.upload(v, i, shader);
        return mesh;
    }
}
