package com.demo.hospital;

import java.time.LocalDate;

public class Appointment {
    // [日期，具体时间（小时），科室，医生，病人]
    private LocalDate date;
    private String time;
    private String department;
    private int doctorid;
    private int patientid;
    private long lastUpdateTime;

    public Appointment(LocalDate date, String time, String department, int doctorid, int patientid,
            long lastUpdateTime) {
        this.date = date;
        this.time = time;
        this.department = department;
        this.doctorid = doctorid;
        this.patientid = patientid;
        this.lastUpdateTime = lastUpdateTime;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDepartment() {
        return department;
    }

    public int getDoctor() {
        return doctorid;
    }

    public int getPatient() {
        return patientid;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", department='" + department + '\'' +
                ", doctor=" + doctorid +
                ", patient=" + patientid +
                ", lastUpdateTime" + lastUpdateTime +
                '}';
    }
}
