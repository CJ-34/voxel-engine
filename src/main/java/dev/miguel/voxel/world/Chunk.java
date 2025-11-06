package dev.miguel.voxel.world;

public class Chunk {
    public static final int W = 16, H = 256, D = 16;
    private final BlockType[] data = new BlockType[W * H * D];

    public Chunk() {
        for (int y = 0; y < H; y++) {
            for (int z = 0; z < D; z++) {
                for (int x = 0; x < W; x++) {
                   set(x, y, z, y < 48 ? BlockType.STONE : y == 48 ? BlockType.GRASS : BlockType.AIR);
                }
            }
        }
        for (int z = 0; z < D; z++) {
            for (int x = 0; x < W; x++) {
                set(x, 47, z, BlockType.AIR);
            }
        }
    }

    private int idx(int x, int y, int z) {
        return (y * D + z) * W + x;
    }

    public BlockType get(int x, int y, int z) {
        if (x < 0 || x >= W || y < 0 || y >= H || z < 0 || z >= W) return BlockType.AIR;
        return data[idx(x, y, z)];
    }

    public void set(int x, int y, int z, BlockType type) {
       data[idx(x, y, z)] = type;
    }
}
