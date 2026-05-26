package com.example.safestorage14.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gestionnaire de préférences partagées (SharedPreferences) classiques.
 * Utilisé pour stocker les réglages non sensibles de l'application.
 */
public final class AppPrefs {

    // Nom du fichier de préférences
    private static final String PREFS_FILE = "user_settings_prefs";
    
    // Clés de stockage
    private static final String KEY_USERNAME = "username";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_DISPLAY_MODE = "display_mode";

    private AppPrefs() {}

    /**
     * Enregistre les réglages de l'utilisateur.
     * @param useCommit Si true, utilise commit() (synchrone), sinon apply() (asynchrone).
     */
    public static boolean saveSettings(Context context, String username, String language, String displayMode, boolean useCommit) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_LANGUAGE, language)
                .putString(KEY_DISPLAY_MODE, displayMode);

        if (useCommit) {
            return editor.commit(); // Retourne true si l'écriture a réussi
        } else {
            editor.apply(); // Écriture en arrière-plan
            return true;
        }
    }

    /**
     * Charge les réglages depuis le stockage.
     * @return Un objet UserSettings contenant les valeurs récupérées.
     */
    public static UserSettings loadSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);

        String username = prefs.getString(KEY_USERNAME, "");
        String language = prefs.getString(KEY_LANGUAGE, "fr");
        String displayMode = prefs.getString(KEY_DISPLAY_MODE, "light");

        return new UserSettings(username, language, displayMode);
    }

    /**
     * Supprime toutes les préférences classiques.
     */
    public static void clear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    /**
     * Classe interne pour encapsuler les réglages utilisateur.
     */
    public static final class UserSettings {
        public final String username;
        public final String language;
        public final String displayMode;

        public UserSettings(String username, String language, String displayMode) {
            this.username = username;
            this.language = language;
            this.displayMode = displayMode;
        }
    }
}
