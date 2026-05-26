package com.example.safestorage14.model;

/**
 * Modèle de données représentant un étudiant.
 * Utilisé pour la sérialisation JSON dans le cadre du Lab 14.
 */
public class Student {
    // Identifiant unique de l'étudiant
    public int id;
    // Nom complet de l'étudiant
    public String fullName;
    // Âge de l'étudiant
    public int age;

    /**
     * Constructeur pour initialiser un objet Student.
     * @param id Identifiant numérique
     * @param fullName Nom et prénom
     * @param age Âge en années
     */
    public Student(int id, String fullName, int age) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
    }
}
