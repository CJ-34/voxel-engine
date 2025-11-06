package dev.miguel.voxel.utils;

import org.lwjgl.BufferUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

public class FileUtils {

    public static String readFile(String path) throws IOException {
        try (InputStream in = FileUtils.class.getClassLoader().getResourceAsStream(path)) {
           if (in == null) {
               throw new FileNotFoundException("Could not find file: " + path);
           }
           byte[] buffer = in.readAllBytes();
           return new String(buffer, StandardCharsets.UTF_8);
        }
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        // Try loading from classpath
        try (InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
            if (source == null) {
                // Fallback: treat it as a file path
                try (FileChannel fc = FileChannel.open(Paths.get(resource), StandardOpenOption.READ)) {
                    buffer = memAlloc((int) fc.size() + 1);
                    while (fc.read(buffer) != -1) {}
                }
            } else {
                try (ReadableByteChannel rbc = Channels.newChannel(source)) {
                    buffer = memAlloc(bufferSize);
                    while (true) {
                        int bytes = rbc.read(buffer);
                        if (bytes == -1) break;
                        if (!buffer.hasRemaining()) {
                            ByteBuffer newBuffer = memAlloc(buffer.capacity() * 2);
                            buffer.flip();
                            newBuffer.put(buffer);
                            memFree(buffer);
                            buffer = newBuffer;
                        }
                    }
                }
            }
        }

        buffer.flip();
        return buffer;
    }
}
