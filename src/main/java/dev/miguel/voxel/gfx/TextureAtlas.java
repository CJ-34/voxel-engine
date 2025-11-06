package dev.miguel.voxel.gfx;

import dev.miguel.voxel.utils.FileUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL33.*;

public class TextureAtlas {
    private final int id;
    private final int tilesPerRow;
    private final Map<String, TextureRegion> regions = new HashMap<>();

    public TextureAtlas(String path, int tilesPerRow) {
        this.tilesPerRow = tilesPerRow;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer imageBuffer = FileUtils.ioResourceToByteBuffer(path, 8 * 1024);

            STBImage.stbi_set_flip_vertically_on_load(false);

            ByteBuffer data = STBImage.stbi_load_from_memory(imageBuffer, w, h, channels, 4);
            if (data == null)
                throw new RuntimeException("Failed to load texture atlas at " + path);

            id = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, id);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, w.get(), h.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            glGenerateMipmap(GL_TEXTURE_2D);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

            System.out.println(id);

            STBImage.stbi_image_free(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void delete() {
        glDeleteTextures(id);
    }

    public void addRegion(String name, int tileX, int tileY) {
        regions.put(name, new TextureRegion(tileX, tileY, tilesPerRow));
    }

    public TextureRegion getRegion(String name) {
        return regions.get(name);
    }
}
