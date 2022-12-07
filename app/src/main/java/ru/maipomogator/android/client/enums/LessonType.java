package ru.maipomogator.android.client.enums;

public enum LessonType {
    DEFAULT(""),
    LECTURE("Лекция"),
    PRACTICE("Практическое занятие"),
    LABORATORY("Лабораторная работа"),
    CONSULTATION("Консультация"),
    EXAM("Экзамен"),
    CREDIT("Зачет");

    private String name;

    private LessonType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}