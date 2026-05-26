package com.example.safestorage14.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.safestorage14.R;
import com.example.safestorage14.cache.CacheStore;
import com.example.safestorage14.external.ExternalAppFilesStore;
import com.example.safestorage14.files.InternalTextStore;
import com.example.safestorage14.files.StudentsJsonStore;
import com.example.safestorage14.model.Student;
import com.example.safestorage14.prefs.AppPrefs;
import com.example.safestorage14.prefs.SecurePrefs;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SafeStorage14_Debug";

    private static final String NOTE_FILE = "storage_note.txt";
    private static final String CACHE_FILE = "last_screen_state.txt";
    private static final String EXPORT_FILE = "export_lab.txt";

    private EditText inputUsername;
    private EditText inputSecretToken;
    private AutoCompleteTextView spinnerLanguage;
    private SwitchMaterial switchDarkMode;
    private TextView textResult;

    private final List<String> languages = Arrays.asList("fr", "en", "ar", "es");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applySavedTheme();

        setContentView(R.layout.activity_main);

        inputUsername = findViewById(R.id.inputUsername);
        inputSecretToken = findViewById(R.id.inputSecretToken);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        textResult = findViewById(R.id.textResult);

        setupLanguageDropdown();
        updateUIFromStorage();

        switchDarkMode.setOnClickListener(v -> {
            boolean isDark = switchDarkMode.isChecked();
            String mode = isDark ? "dark" : "light";

            String user = inputUsername.getText().toString().trim();
            String lang = getSelectedLanguage();

            AppPrefs.saveSettings(this, user, lang, mode, true);

            AppCompatDelegate.setDefaultNightMode(
                    isDark
                            ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        findViewById(R.id.buttonSavePrefs).setOnClickListener(v -> handleSavePrefs());
        findViewById(R.id.buttonLoadPrefs).setOnClickListener(v -> handleLoadPrefs());
        findViewById(R.id.buttonSaveJson).setOnClickListener(v -> handleSaveJson());
        findViewById(R.id.buttonLoadJson).setOnClickListener(v -> handleLoadJson());
        findViewById(R.id.buttonExport).setOnClickListener(v -> handleExport());
        findViewById(R.id.buttonClearAll).setOnClickListener(v -> handleClearAll());
    }

    private void applySavedTheme() {
        AppPrefs.UserSettings settings = AppPrefs.loadSettings(this);

        int targetMode = "dark".equals(settings.displayMode)
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;

        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode);
        }
    }

    private void setupLanguageDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                languages
        );

        spinnerLanguage.setAdapter(adapter);

        // La liste doit s'afficher même si l'utilisateur n'a rien tapé
        spinnerLanguage.setThreshold(0);

        // Empêche l'utilisateur d'écrire une langue qui n'existe pas
        spinnerLanguage.setInputType(0);

        // Affiche la liste quand on clique sur le champ langue
        spinnerLanguage.setOnClickListener(v -> spinnerLanguage.showDropDown());

        // Affiche la liste quand le champ reçoit le focus
        spinnerLanguage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                spinnerLanguage.showDropDown();
            }
        });

        // Valeur par défaut
        if (spinnerLanguage.getText().toString().trim().isEmpty()) {
            spinnerLanguage.setText("fr", false);
        }
    }

    private String getSelectedLanguage() {
        String lang = spinnerLanguage.getText().toString().trim();

        if (lang.isEmpty() || !languages.contains(lang)) {
            return "fr";
        }

        return lang;
    }

    private void updateUIFromStorage() {
        AppPrefs.UserSettings settings = AppPrefs.loadSettings(this);

        inputUsername.setText(settings.username);

        String lang = settings.language;
        if (lang == null || lang.trim().isEmpty() || !languages.contains(lang)) {
            lang = "fr";
        }

        spinnerLanguage.setText(lang, false);
        switchDarkMode.setChecked("dark".equals(settings.displayMode));

        /*
         * Sécurité :
         * On ne remet pas le token dans le champ après restauration.
         * Le token reste stocké chiffré, mais il n'est pas réaffiché.
         */
        inputSecretToken.setText("");
    }

    private void handleSavePrefs() {
        String user = inputUsername.getText().toString().trim();
        String lang = getSelectedLanguage();
        String mode = switchDarkMode.isChecked() ? "dark" : "light";

        boolean saved = AppPrefs.saveSettings(this, user, lang, mode, false);

        String token = inputSecretToken.getText().toString().trim();
        int tokenLength = 0;

        if (!token.isEmpty()) {
            try {
                SecurePrefs.saveToken(this, token);
                tokenLength = token.length();

                /*
                 * Option sécurité :
                 * On vide le champ après sauvegarde pour éviter de laisser le token affiché.
                 */
                inputSecretToken.setText("");

            } catch (Exception e) {
                textResult.setText("Erreur : impossible de sauvegarder le token de manière sécurisée.");
                return;
            }
        } else {
            try {
                String existingToken = SecurePrefs.readToken(this);
                tokenLength = existingToken == null ? 0 : existingToken.length();
            } catch (Exception ignored) {}
        }

        try {
            String cacheContent = "Dernière sauvegarde UI : user="
                    + user
                    + ", lang="
                    + lang
                    + ", mode="
                    + mode;

            CacheStore.writeCache(this, CACHE_FILE, cacheContent);
        } catch (Exception ignored) {}

        Log.d(TAG, "Préférences sauvegardées : saved="
                + saved
                + ", user="
                + user
                + ", lang="
                + lang
                + ", mode="
                + mode
                + ", tokenLength="
                + tokenLength);

        textResult.setText(
                "Configuration enregistrée.\n\n"
                        + "Utilisateur : " + (user.isEmpty() ? "Anonyme" : user) + "\n"
                        + "Langue : " + lang + "\n"
                        + "Mode : " + mode + "\n"
                        + "Token : " + (tokenLength > 0 ? "présent et chiffré" : "absent") + "\n"
                        + "Longueur du token : " + tokenLength + "\n"
                        + "Cache : état temporaire sauvegardé"
        );
    }

    private void handleLoadPrefs() {
        updateUIFromStorage();

        AppPrefs.UserSettings settings = AppPrefs.loadSettings(this);

        String tokenStatus = "absent";
        int tokenLength = 0;

        try {
            String token = SecurePrefs.readToken(this);

            if (token != null && !token.isEmpty()) {
                tokenStatus = "présent";
                tokenLength = token.length();
            }

        } catch (Exception ignored) {}

        String cacheContent;

        try {
            cacheContent = CacheStore.readCache(this, CACHE_FILE);
            if (cacheContent == null || cacheContent.trim().isEmpty()) {
                cacheContent = "cache vide";
            }
        } catch (Exception e) {
            cacheContent = "cache indisponible";
        }

        textResult.setText(
                "Préférences restaurées depuis le stockage.\n\n"
                        + "Utilisateur : " + (settings.username.isEmpty() ? "Anonyme" : settings.username) + "\n"
                        + "Langue : " + settings.language + "\n"
                        + "Mode : " + settings.displayMode + "\n"
                        + "Token : " + tokenStatus + "\n"
                        + "Longueur du token : " + tokenLength + "\n\n"
                        + "Contenu du cache :\n"
                        + cacheContent
        );

        Log.d(TAG, "Préférences restaurées : user="
                + settings.username
                + ", lang="
                + settings.language
                + ", mode="
                + settings.displayMode
                + ", tokenLength="
                + tokenLength);
    }

    private void handleSaveJson() {
        try {
            String user = inputUsername.getText().toString().trim();

            if (user.isEmpty()) {
                user = "Utilisateur de démonstration";
            }

            /*
             * Ici, on ne met plus Étudiant A / Étudiant B en dur.
             * Le JSON est généré à partir du nom saisi dans l'interface.
             */
            List<Student> students = new ArrayList<>();
            students.add(new Student(1, user, 21));

            StudentsJsonStore.saveStudents(this, students);

            InternalTextStore.writeText(
                    this,
                    NOTE_FILE,
                    "Fichier interne créé en UTF-8 pour le Lab 14."
            );

            textResult.setText(
                    "Fichiers internes créés avec succès.\n\n"
                            + "JSON : class_group.json\n"
                            + "Note : " + NOTE_FILE + "\n"
                            + "Nombre d'étudiants sauvegardés : " + students.size()
            );

            Log.d(TAG, "Fichiers internes créés : class_group.json et " + NOTE_FILE);

        } catch (Exception e) {
            textResult.setText("Erreur : impossible de créer les fichiers internes.");
        }
    }

    private void handleLoadJson() {
        List<Student> students = StudentsJsonStore.loadStudents(this);

        String note;

        try {
            note = InternalTextStore.readText(this, NOTE_FILE);
        } catch (Exception e) {
            note = "Note interne absente.";
        }

        if (students.isEmpty()) {
            textResult.setText(
                    "Le fichier JSON est vide ou inexistant.\n\n"
                            + "Note : " + note
            );
            return;
        }

        StringBuilder builder = new StringBuilder();

        builder.append("Contenu des fichiers internes :\n\n");
        builder.append("Note : ").append(note).append("\n\n");
        builder.append("Contenu JSON :\n");

        for (Student student : students) {
            builder.append("- ID : ")
                    .append(student.id)
                    .append(" | Nom : ")
                    .append(student.fullName)
                    .append(" | Âge : ")
                    .append(student.age)
                    .append("\n");
        }

        textResult.setText(builder.toString());

        Log.d(TAG, "Lecture JSON terminée : students=" + students.size());
    }

    private void handleExport() {
        try {
            String user = inputUsername.getText().toString().trim();

            if (user.isEmpty()) {
                user = "Anonyme";
            }

            String content = "Rapport d'export Lab 14\n"
                    + "Utilisateur : " + user + "\n"
                    + "Type : stockage externe app-specific\n"
                    + "Aucune donnée sensible exportée.";

            String path = ExternalAppFilesStore.exportText(this, EXPORT_FILE, content);

            if (path == null) {
                textResult.setText("Export impossible : stockage externe indisponible.");
                return;
            }

            textResult.setText(
                    "Export externe terminé.\n\n"
                            + "Fichier : " + EXPORT_FILE + "\n"
                            + "Chemin :\n"
                            + path
            );

            Log.d(TAG, "Export externe terminé : " + path);

        } catch (Exception e) {
            textResult.setText("Erreur : échec de l'export externe.");
        }
    }

    private void handleClearAll() {
        AppPrefs.clear(this);

        try {
            SecurePrefs.clear(this);
        } catch (Exception ignored) {}

        StudentsJsonStore.delete(this);
        InternalTextStore.deleteText(this, NOTE_FILE);
        ExternalAppFilesStore.deleteExport(this, EXPORT_FILE);

        int deletedCacheFiles = CacheStore.purgeCache(this);

        inputUsername.setText("");
        inputSecretToken.setText("");
        spinnerLanguage.setText("fr", false);
        switchDarkMode.setChecked(false);

        textResult.setText(
                "Nettoyage complet terminé.\n\n"
                        + "Préférences simples : supprimées\n"
                        + "Préférences chiffrées : supprimées\n"
                        + "Fichier JSON : supprimé\n"
                        + "Note interne : supprimée\n"
                        + "Export externe : supprimé si existant\n"
                        + "Cache supprimé : " + deletedCacheFiles + " fichier(s)"
        );

        Log.d(TAG, "Nettoyage complet terminé. Aucun secret loggé.");

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}