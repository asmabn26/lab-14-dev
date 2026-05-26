# LAB 14 – Sauvegarde locale sécurisée sous Android

## Objectif

Ce lab présente une application Android développée en Java permettant de gérer plusieurs types de stockage local :

- SharedPreferences pour les données non sensibles.
- EncryptedSharedPreferences pour le token.
- Fichiers internes pour les données JSON.
- Cache temporaire.
- Stockage externe app-specific pour l’export.

## Fonctionnalités réalisées

L’application permet de :

- sauvegarder le nom, la langue et le thème ;
- stocker un token de manière chiffrée ;
- restaurer les préférences sauvegardées ;
- créer et lire un fichier JSON interne ;
- exporter un fichier vers le stockage externe propre à l’application ;
- nettoyer toutes les données locales.

## Sécurité appliquée

Les bonnes pratiques suivantes ont été respectées :

- le token n’est jamais affiché en clair dans l’interface ;
- le token n’apparaît pas dans Logcat ;
- seule la longueur du token est affiché ;
- les fichiers internes utilisent le stockage privé de l’application ;
- le cache peut être purgé ;
- un bouton permet de supprimer les données locales.

## Preuve Logcat

La capture suivante montre les logs générés pendant les tests.  
On remarque que les actions sont bien tracées : restauration des préférences, création des fichiers internes, lecture JSON, export externe et nettoyage complet.  
Aucun token n’est affiché en clair.

![Capture Logcat](01.png)

## Vidéo démonstration

Une vidéo de démonstration accompagne ce projet.  
Elle montre les étapes suivantes :

1. sauvegarde des préférences ;
2. restauration des données ;
3. stockage sécurisé du token ;
4. création et lecture du fichier JSON ;
5. export externe app-specific ;
6. vérification dans Logcat ;
7. nettoyage complet des données.

## Conclusion

Ce lab montre comment utiliser correctement les mécanismes de stockage local sous Android tout en respectant les bonnes pratiques de sécurité, notamment la séparation entre données simples et données sensibles.
