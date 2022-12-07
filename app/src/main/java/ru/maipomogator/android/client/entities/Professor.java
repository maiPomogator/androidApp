package ru.maipomogator.android.client.entities;


import lombok.Data;
import ru.maipomogator.android.client.PropForTime;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;


@Data
public class Professor {


    private Integer id;

    private String firstName;

    private String lastName;

    private String secondName;

    private UUID siteId;

    private Set<Lesson> lessons = new TreeSet<>();

    public String getFullName() {
        return lastName + " " + firstName + " " + secondName;
    }

    public void setLessons(Set<Lesson> lessons) {
        this.lessons = lessons;
        lessons.forEach(l -> l.addProfessor(this));
    }

    public void addLesson(Lesson lsn) {
        if (!getLessons().contains(lsn)) {
            lessons.add(lsn);
            lsn.addProfessor(this);
        }
    }

    public static List<Professor> professorRequest(String query) {
        List<Professor> professors = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                PropForTime.connection = DriverManager.getConnection(PropForTime.url, PropForTime.user, PropForTime.pass);
                ResultSet resultSet = PropForTime.connection.createStatement().executeQuery(query);
                while (resultSet.next()) {
                    Professor newProfessor = new Professor();
                    newProfessor.setId(resultSet.getInt("professor_id"));
                    newProfessor.setFirstName(resultSet.getString("professor_firstname"));
                    newProfessor.setLastName(resultSet.getString("professor_lastname"));
                    newProfessor.setSecondName(resultSet.getString("professor_secondname"));
                    newProfessor.setSiteId(UUID.fromString(resultSet.getString("professor_siteid")));
                    professors.add(newProfessor);
                }
                PropForTime.status = true;
                System.out.println(professors.size());
                System.out.println("connection status:" + PropForTime.status);
            } catch (Exception e) {
                e.printStackTrace();
                PropForTime.status = false;
            }

        });
        thread.start();
        try {
            System.out.println("вошли в препода");
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            PropForTime.status = false;
        }
        return professors;
    }
}
