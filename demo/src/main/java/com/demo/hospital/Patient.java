package com.demo.hospital;

import java.util.List;

public class Patient {
    // [病人id，病人姓名，登录密码]
    private int id;
    private String name;
    private String password;

    public Patient(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
