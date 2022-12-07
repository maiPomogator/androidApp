package ru.maipomogator.android.client.entities;

import lombok.Data;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ru.maipomogator.android.client.PropForTime.connection;
import static ru.maipomogator.android.client.PropForTime.pass;
import static ru.maipomogator.android.client.PropForTime.status;
import static ru.maipomogator.android.client.PropForTime.url;
import static ru.maipomogator.android.client.PropForTime.user;

@Data
public class Schedule {
    private String group_name;
    private Date lesson_date;
    private String name;
    private String professor;
    private Integer lesson_number;
    private Integer lesson_type;
    private String room;

    public static List<Schedule> scheduleRequest(String query) {
        List<Schedule> allSchedule = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, pass);
                ResultSet resultSet = connection.createStatement().executeQuery(query);
                while (resultSet.next()) {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setGroup_name(resultSet.getString("group_name"));
                    newSchedule.setLesson_date(resultSet.getDate("lesson_day"));
                    newSchedule.setName(resultSet.getString("lesson_name"));
                    newSchedule.setProfessor(resultSet.getString("professor_lastname") + " " + resultSet.getString("professor_firstname") + " " + resultSet.getString("professor_secondname"));
                    newSchedule.setLesson_number(resultSet.getInt("lesson_number"));
                    newSchedule.setLesson_type(resultSet.getInt("lesson_type"));
                    newSchedule.setRoom(resultSet.getString("rooms"));
                    allSchedule.add(newSchedule);
                }
                status = true;
                System.out.println(allSchedule.size());
                System.out.println("connection status:" + status);
            } catch (Exception e) {
                e.printStackTrace();
                status = false;
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            status = false;
        }
        return allSchedule;
    }

    public String getNumberToTime() {
        return switch (this.getLesson_number()) {
            case 0 -> "9:00 - 10:30";
            case 1 -> "10:45 - 12:15";
            case 2 -> "13:00 - 14:30";
            case 3 -> "14:45 - 16:15";
            case 4 -> "16:30 - 18:00";
            case 5 -> "18:15 - 19:45";
            case 6 -> "20:00 - 21:30";
            case 7 -> "21:45 - 23:15";
            default -> "не найдено";
        };
    }

    public String getLessonType() {
        return switch (this.getLesson_type()) {
            case 1 -> "Лекция";
            case 2 -> "Практика";
            case 3 -> "Лабораторная";
            default -> "";
        };
    }
}
