package ru.maipomogator.android.client.entities;

import lombok.Data;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static ru.maipomogator.android.client.PropForTime.connection;
import static ru.maipomogator.android.client.PropForTime.pass;
import static ru.maipomogator.android.client.PropForTime.status;
import static ru.maipomogator.android.client.PropForTime.url;
import static ru.maipomogator.android.client.PropForTime.user;

@Data
public class Group {
    private Integer id;

    private String name;

    private Integer course;

    private Integer faculty;

    private Integer headman;
    private Set<Lesson> lessons = new TreeSet();

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
        lessons.forEach((l) -> {
            l.addGroup(this);
        });
    }

    public void addLesson(Lesson lsn) {
        if (!this.getLessons().contains(lsn)) {
            this.lessons.add(lsn);
            lsn.addGroup(this);
        }
    }

    public Set<Lesson> getLessons() {
        return this.lessons;
    }


    @Override
    public String toString() {
        return name;
    }


    public static List<Group> groupRequest(String query) {
        List<Group> groups = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, pass);
                ResultSet resultSet = connection.createStatement().executeQuery(query);
                while (resultSet.next()) {
                    Group newGroup = new Group();
                    newGroup.setId(resultSet.getInt("group_id"));
                    newGroup.setCourse(resultSet.getInt("group_course"));
                    newGroup.setFaculty(resultSet.getInt("group_faculty"));
                    newGroup.setName(resultSet.getString("group_name"));
                    groups.add(newGroup);
                }
                status = true;
                System.out.println(groups.size());
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
        return groups;
    }

}