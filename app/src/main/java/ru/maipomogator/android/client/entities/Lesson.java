package ru.maipomogator.android.client.entities;

import ru.maipomogator.android.client.enums.LessonType;
import lombok.Data;
import ru.maipomogator.android.client.PropForTime;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Data
public class Lesson {

    private Integer id;

    private String name;

    private LessonType type;


    private LocalDate day;


    private Short number;

    private List<Professor> professors = new ArrayList<>();

    private List<Group> groups = new ArrayList<>();

    private List<String> rooms = new ArrayList<>();

    public void setProfessors(List<Professor> professors) {
        this.professors = professors;
        professors.forEach(p -> p.addLesson(this));
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
        groups.forEach(g -> g.addLesson(this));
    }

    public void addGroup(Group gr) {
        if (!getGroups().contains(gr)) {
            groups.add(gr);
            gr.addLesson(this);
        }
    }

    public void addProfessor(Professor pr) {
        if (!getProfessors().contains(pr)) {
            professors.add(pr);
            pr.addLesson(this);
        }
    }

    public String getNumberToTime() {
        return switch (this.getNumber()) {
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

    @Override
    public String toString() {
        return "Lesson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type.getName() +
                ", day=" + day +
                ", time=" + getNumberToTime() +
                ", rooms=" + rooms +
                '}';
    }

    public static List<Group> lessonRequest(String query) {
        List<Group> groups = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                PropForTime.connection = DriverManager.getConnection(PropForTime.url, PropForTime.user, PropForTime.pass);
                ResultSet resultSet = PropForTime.connection.createStatement().executeQuery(query);
                while (resultSet.next()) {
                    Group newGroup = new Group();
                    newGroup.setId(resultSet.getInt("group_id"));
                    newGroup.setCourse(resultSet.getInt("group_course"));
                    newGroup.setFaculty(resultSet.getInt("group_faculty"));
                    newGroup.setName(resultSet.getString("group_name"));
                    groups.add(newGroup);
                }
                PropForTime.status = true;
                System.out.println(groups.size());
                System.out.println("connection status:" + PropForTime.status);
            } catch (Exception e) {
                e.printStackTrace();
                PropForTime.status = false;
            }

        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            PropForTime.status = false;
        }
        return groups;
    }
}
