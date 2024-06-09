package com.demo.hospital;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Doctor {
    // [职工id，名字，登录密码，科室，工作日期（多日），每小时最大挂号数，权限]
    private int id;
    private String name;
    private String password;
    private String department;
    private List<LocalDate> workDays;
    private int maxAppointmentsPerHour;
    private boolean permission;

    public Doctor(int id, String name, String password, String department, List<LocalDate> workDays,
            int maxAppointmentsPerHour, boolean permission) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.department = department;
        this.workDays = workDays;
        this.maxAppointmentsPerHour = maxAppointmentsPerHour;
        this.permission = permission;
    }

    // 提供了获得医生的六种属性的功能
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getDepartment() {
        return department;
    }

    public List<LocalDate> getWorkDays() {
        return workDays;
    }

    public int getMaxAppointmentsPerHour() {
        return maxAppointmentsPerHour;
    }

    public boolean getpermission() {
        return permission;
    }

    // 提供了逐个增加工作日的功能
    public void addWorkDay(LocalDate workDay) {
        if (this.workDays == null) {
            this.workDays = new ArrayList<>();
        }

        // 检查是否已经存在相同的工作日
        if (!this.workDays.contains(workDay)) {
            this.workDays.add(workDay);
        }
    }

    // 提供了逐个删除工作日的功能
    public void removeWorkDay(LocalDate workDay) {
        if (this.workDays != null) {
            this.workDays.remove(workDay);
        }
    }

    // 提供了设置每小时最大挂号数的功能
    public void setMaxAppointmentsPerHour(int maxAppointmentsPerHour) {
        this.maxAppointmentsPerHour = maxAppointmentsPerHour;
    }

    // 提供了修改权限功能
    public void setpermission(boolean permission) {
        this.permission = permission;
    }

    // 提供了修改密码的功能
    public void setpassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", department='" + department + '\'' +
                ", workDays=" + workDays +
                ", maxAppointmentsPerHour=" + maxAppointmentsPerHour +
                '}';
    }

    public Object getDate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDate'");
    }
}
