package com.example.safestorage14.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

/**
 * Gestionnaire de préférences chiffrées utilisant AndroidX Security.
 * Permet de stocker des données sensibles comme des tokens d'accès.
 */
public final class SecurePrefs {

    // Nom du fichier de stockage chiffré
    private static final String SECURE_PREFS_FILE = "encrypted_user_secrets";
    // Clé pour le token
    private static final String KEY_TOKEN = "encrypted_access_token";

    private SecurePrefs() {}

    /**
     * Initialise et retourne une instance de SharedPreferences chiffrée.
     * @param context Contexte de l'application
     * @return SharedPreferences avec chiffrement matériel si disponible
     */
    private static SharedPreferences getSecurePrefs(Context context) throws Exception {
        // Création ou récupération de la clé maîtresse stockée dans le Keystore
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        // Création de l'instance SharedPreferences sécurisée
        return EncryptedSharedPreferences.create(
                context,
                SECURE_PREFS_FILE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    /**
     * Enregistre un token de manière sécurisée.
     */
    public static void saveToken(Context context, String token) throws Exception {
        getSecurePrefs(context).edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    /**
     * Lit le token chiffré.
     * @return Le token en clair après déchiffrement automatique.
     */
    public static String readToken(Context context) throws Exception {
        return getSecurePrefs(context).getString(KEY_TOKEN, "");
    }

    /**
     * Supprime toutes les données chiffrées.
     */
    public static void clear(Context context) throws Exception {
        getSecurePrefs(context).edit().clear().apply();
    }
}
