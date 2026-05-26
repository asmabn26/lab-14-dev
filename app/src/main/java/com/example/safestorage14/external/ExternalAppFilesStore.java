package com.example.safestorage14.external;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Gestionnaire pour l'exportation de fichiers vers le stockage externe spécifique à l'application.
 * Les fichiers ici sont accessibles via un explorateur de fichiers mais sont supprimés à la désinstallation.
 */
public final class ExternalAppFilesStore {

    private ExternalAppFilesStore() {}

    /**
     * Exporte du contenu textuel vers un fichier sur le stockage externe.
     * @param fileName Nom du fichier d'export
     * @param content Texte à enregistrer
     * @return Le chemin absolu du fichier créé, ou null si le stockage n'est pas disponible.
     */
    public static String exportText(Context context, String fileName, String content) throws Exception {
        // Accès au répertoire externe privé de l'application (Android/data/com.example.../files)
        File directory = context.getExternalFilesDir(null);

        if (directory == null) {
            return null;
        }

        File file = new File(directory, fileName);

        // Écriture physique du fichier
        try (FileOutputStream output = new FileOutputStream(file)) {
            output.write(content.getBytes(StandardCharsets.UTF_8));
        }

        return file.getAbsolutePath();
    }

    /**
     * Supprime un fichier exporté du stockage externe.
     * @return true si la suppression a réussi.
     */
    public static boolean deleteExport(Context context, String fileName) {
        File directory = context.getExternalFilesDir(null);

        if (directory == null) {
            return false;
        }

        File file = new File(directory, fileName);
        return file.delete();
    }
}
