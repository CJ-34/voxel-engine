package dev.miguel.voxel.gfx;

import lombok.Getter;

@Getter
public class TextureRegion {
    private final float u0, v0, u1, v1;

    public TextureRegion(int tileX, int tileY, int tilesPerRow) {
        float tileSize = 1.0f / tilesPerRow;
        this.u0 = tileX * tileSize;
        this.v0 = tileY * tileSize;
        this.u1 = u0 + tileSize;
        this.v1 = v0 + tileSize;
    }
}
