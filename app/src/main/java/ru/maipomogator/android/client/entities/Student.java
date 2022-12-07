package ru.maipomogator.android.client.entities;

import lombok.Data;


@Data

public class Student {


    private Integer id;


    private String firstName;


    private String lastName;


    private String secondName;


    private Group group;


    private boolean isHeadman = false;


}
