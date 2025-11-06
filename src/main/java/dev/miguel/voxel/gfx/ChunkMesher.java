package dev.miguel.voxel.gfx;

import dev.miguel.voxel.world.BlockType;
import dev.miguel.voxel.world.Chunk;

public class ChunkMesher {
    public static Mesh build(Chunk chunk, ShaderProgram shader) {
        int maxFaces = Chunk.W * Chunk.H * Chunk.D * 6;
        int maxVerts = maxFaces * 4;
        int maxIndices = maxFaces * 6;

        float[] vertices = new float[maxVerts * 8];
        int[] indices = new int[maxIndices];

        int vertexCount = 0;
        int indexCount = 0;

        for (int y = 0; y < Chunk.H; y++) {
            for (int z = 0; z < Chunk.D; z++) {
                for (int x = 0; x < Chunk.W; x++) {
                    BlockType type = chunk.get(x, y, z);
                    if (!type.isSolid()) continue;
                    for (BlockType.Face face : BlockType.Face.values()) {
                        TextureRegion region = type.getRegionForFace(face);
                        int added = addFace(vertices, indices, vertexCount, indexCount, x, y, z, face, region);
                        vertexCount += added * 8;
                        indexCount += 6;
                    }
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

    private static boolean isFaceVisible(Chunk chunk, int x, int y, int z, BlockType.Face face) {
        int nx = x, ny = y, nz = z;
        switch (face) {
            case TOP: ny++; break;
            case BOTTOM: ny--; break;
            case FRONT: nz++; break;
            case BACK: nz--; break;
            case LEFT: nx--; break;
            case RIGHT: nx++; break;
        }
        return !chunk.get(nx, ny, nz).isSolid();
    }

    private static int addFace(float[] verts, int[] inds,
                               int vertexCursor, int indexCursor,
                               int x, int y, int z,
                               BlockType.Face face, TextureRegion region) {
        float[][] pos;
        float[] normal;

        switch (face) {
            case TOP:
                pos = new float[][] { {0,1,1}, {1,1,1}, {1,1,0}, {0,1,0} };
                normal = new float[] {0,1,0};
                break;
            case BOTTOM:
                pos = new float[][] { {0,0,0}, {1,0,0}, {1,0,1}, {0,0,1} };
                normal = new float[] {0,-1,0};
                break;
            case FRONT:
                pos = new float[][] { {0,0,1}, {1,0,1}, {1,1,1}, {0,1,1} };
                normal = new float[] {0,0,1};
                break;
            case BACK:
                pos = new float[][] { {1,0,0}, {0,0,0}, {0,1,0}, {1,1,0} };
                normal = new float[] {0,0,-1};
                break;
            case LEFT:
                pos = new float[][] { {0,0,0}, {0,0,1}, {0,1,1}, {0,1,0} };
                normal = new float[] {-1,0,0};
                break;
            case RIGHT:
                pos = new float[][] { {1,0,1}, {1,0,0}, {1,1,0}, {1,1,1} };
                normal = new float[] {1,0,0};
                break;
            default:
                throw new IllegalArgumentException("unexpected face: " + face);
        }

        float[][] uvs = {
                {region.getU0(), region.getV1()},
                {region.getU1(), region.getV1()},
                {region.getU1(), region.getV0()},
                {region.getU0(), region.getV0()},
        };

        int startVertex = vertexCursor / 8;
        for (int i = 0; i < 4; i++) {
            int base = vertexCursor + i * 8;
            verts[base] = x + pos[i][0];
            verts[base + 1] = y + pos[i][1];
            verts[base + 2] = z + pos[i][2];
            verts[base + 3] = normal[0];
            verts[base + 4] = normal[1];
            verts[base + 5] = normal[2];
            verts[base + 6] = uvs[i][0];
            verts[base + 7] = uvs[i][1];
        }

        inds[indexCursor]     = startVertex;
        inds[indexCursor + 1] = startVertex + 1;
        inds[indexCursor + 2] = startVertex + 2;
        inds[indexCursor + 3] = startVertex;
        inds[indexCursor + 4] = startVertex + 2;
        inds[indexCursor + 5] = startVertex + 3;

        return 4;
    }
}
