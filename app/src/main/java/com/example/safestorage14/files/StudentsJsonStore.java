package com.example.safestorage14.files;

import android.content.Context;

import com.example.safestorage14.model.Student;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class StudentsJsonStore {

    public static final String FILE_NAME = "class_group.json";

    private StudentsJsonStore() {}

    public static void saveStudents(Context context, List<Student> students) throws Exception {
        if (context == null) {
            throw new IllegalArgumentException("Context ne doit pas être null");
        }

        if (students == null) {
            students = new ArrayList<>();
        }

        JSONArray array = new JSONArray();

        for (Student student : students) {
            if (student == null) {
                continue;
            }

            JSONObject object = new JSONObject();
            object.put("id", student.id);
            object.put("fullName", student.fullName);
            object.put("age", student.age);

            array.put(object);
        }

        InternalTextStore.writeText(context, FILE_NAME, array.toString());
    }

    public static List<Student> loadStudents(Context context) {
        List<Student> students = new ArrayList<>();

        if (context == null) {
            return students;
        }

        try {
            String json = InternalTextStore.readText(context, FILE_NAME);

            if (json == null || json.trim().isEmpty()) {
                return students;
            }

            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);

                int id = object.optInt("id", 0);
                String fullName = object.optString("fullName", "Nom inconnu");
                int age = object.optInt("age", 0);

                students.add(new Student(id, fullName, age));
            }

        } catch (Exception e) {
            return new ArrayList<>();
        }

        return students;
    }

    public static boolean delete(Context context) {
        if (context == null) {
            return false;
        }

        return context.deleteFile(FILE_NAME);
    }
}