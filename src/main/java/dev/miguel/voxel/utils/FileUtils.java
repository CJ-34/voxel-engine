package dev.miguel.voxel.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

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
}
