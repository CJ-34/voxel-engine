package dev.miguel.voxel.world;

import dev.miguel.voxel.gfx.TextureAtlas;
import dev.miguel.voxel.gfx.TextureRegion;
import lombok.Getter;

@Getter
public enum BlockType {
    AIR(false, null, null, null),
    GRASS(true, "grass_top", "grass_side", "dirt"),
    DIRT(true, "dirt", "dirt", "dirt"),
    STONE(true, "stone", "stone", "stone");

    private final boolean solid;
    private final String topName;
    private final String sideName;
    private final String bottomName;

    private TextureRegion topRegion, sideRegion, bottomRegion;

    BlockType(boolean solid, String top, String side, String bottom) {
        this.solid = solid;
        this.topName = top;
        this.sideName = side;
        this.bottomName = bottom;
    }

    public void bindRegions(TextureAtlas atlas) {
        if (topRegion == null) topRegion = atlas.getRegion(topName);
        if (sideRegion == null) sideRegion = atlas.getRegion(sideName);
        if (bottomRegion == null) bottomRegion = atlas.getRegion(bottomName);
    }

    public TextureRegion getRegionForFace(Face face) {
        return switch (face) {
            case TOP -> topRegion;
            case BOTTOM -> bottomRegion;
            default -> sideRegion;
        };
    }

    public enum Face {
        TOP, BOTTOM, LEFT, RIGHT, FRONT, BACK
    }
}
