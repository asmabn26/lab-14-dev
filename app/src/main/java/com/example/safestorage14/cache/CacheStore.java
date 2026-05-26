package com.example.safestorage14.cache;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Gestionnaire du cache de l'application.
 * Les fichiers stockés ici peuvent être supprimés par le système si l'espace est insuffisant.
 */
public final class CacheStore {

    private CacheStore() {}

    /**
     * Écrit des données temporaires dans le répertoire de cache.
     * @param fileName Nom du fichier temporaire
     * @param content Contenu à mettre en cache
     */
    public static void writeCache(Context context, String fileName, String content) throws Exception {
        // Accès au dossier de cache interne de l'application
        File file = new File(context.getCacheDir(), fileName);

        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(content.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Lit un fichier depuis le cache.
     * @return Contenu du fichier ou null s'il n'existe pas.
     */
    public static String readCache(Context context, String fileName) throws Exception {
        File file = new File(context.getCacheDir(), fileName);

        if (!file.exists()) {
            return null;
        }

        try (FileInputStream input = new FileInputStream(file);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] data = new byte[1024];
            int count;

            while ((count = input.read(data)) != -1) {
                buffer.write(data, 0, count);
            }

            return buffer.toString(StandardCharsets.UTF_8.name());
        }
    }

    /**
     * Supprime tous les fichiers du cache interne.
     * @return Le nombre de fichiers supprimés.
     */
    public static int purgeCache(Context context) {
        File[] files = context.getCacheDir().listFiles();

        if (files == null) {
            return 0;
        }

        int deletedFiles = 0;

        for (File file : files) {
            if (file.delete()) {
                deletedFiles++;
            }
        }

        return deletedFiles;
    }
}
