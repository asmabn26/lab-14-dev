package com.example.safestorage14.files;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class InternalTextStore {

    private InternalTextStore() {}

    public static void writeText(Context context, String fileName, String content) throws IOException {
        if (context == null) {
            throw new IllegalArgumentException("Context ne doit pas être null");
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom du fichier invalide");
        }

        if (content == null) {
            content = "";
        }

        try (FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            outputStream.write(bytes);
        }
    }

    public static String readText(Context context, String fileName) throws IOException {
        if (context == null) {
            throw new IllegalArgumentException("Context ne doit pas être null");
        }

        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nom du fichier invalide");
        }

        try (FileInputStream inputStream = context.openFileInput(fileName);
             ByteArrayOutputStream result = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString(StandardCharsets.UTF_8.name());
        }
    }

    public static boolean deleteText(Context context, String fileName) {
        if (context == null || fileName == null || fileName.trim().isEmpty()) {
            return false;
        }

        return context.deleteFile(fileName);
    }
}