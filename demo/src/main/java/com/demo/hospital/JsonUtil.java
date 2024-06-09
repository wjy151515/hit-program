package com.demo.hospital;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    // 从JSON文件读取医生列表
    public static List<Doctor> readDoctorsFromJson(String jsonFilePath) {
        List<Doctor> doctors = new ArrayList<>();
        try (InputStream is = new FileInputStream(jsonFilePath)) {
            JSONArray jsonArray = new JSONArray(new JSONTokener(is));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                String password = jsonObject.getString("password");
                String department = jsonObject.getString("department");
                JSONArray workDaysArray = jsonObject.getJSONArray("workDays");
                List<LocalDate> workDays = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (int j = 0; j < workDaysArray.length(); j++) {
                    LocalDate workDay = LocalDate.parse(workDaysArray.getString(j), formatter);
                    workDays.add(workDay);
                }
                int maxAppointmentsPerHour = jsonObject.getInt("maxAppointmentsPerHour");
                boolean permission = jsonObject.getBoolean("permission");
                doctors.add(new Doctor(id, name, password, department, workDays, maxAppointmentsPerHour, permission));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doctors;
    }

    // 从JSON文件读取病人列表
    public static List<Patient> readPatientsFromJson(String jsonFilePath) {
        List<Patient> patients = new ArrayList<>();
        try (InputStream is = new FileInputStream(jsonFilePath)) {
            JSONArray jsonArray = new JSONArray(new JSONTokener(is));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String name = jsonObject.getString("name");
                String password = jsonObject.getString("password");
                patients.add(new Patient(id, name, password));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patients;
    }

    // 从JSON文件读取预约列表
    public static List<Appointment> readAppointmentsFromJson(String jsonFilePath) {
        List<Appointment> appointments = new ArrayList<>();
        try (InputStream is = new FileInputStream(jsonFilePath)) {
            JSONArray jsonArray = new JSONArray(new JSONTokener(is));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String dateString = jsonObject.getString("date");
                LocalDate date = LocalDate.parse(dateString);

                String time = jsonObject.getString("time");
                String department = jsonObject.getString("department");
                int doctorId = jsonObject.getInt("doctorId");
                int patientId = jsonObject.getInt("patientId");
                long lastUpdateTime = jsonObject.getLong("lastUpdateTime");
                appointments.add(new Appointment(date, time, department, doctorId, patientId, lastUpdateTime));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // 将医生列表写入JSON文件
    public static void writeDoctorsToJson(String jsonFilePath, List<Doctor> doctors) {
        JSONArray jsonArray = new JSONArray();
        for (Doctor doctor : doctors) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", doctor.getId());
            jsonObject.put("name", doctor.getName());
            jsonObject.put("password", doctor.getPassword());
            jsonObject.put("department", doctor.getDepartment());
            JSONArray workDaysArray = new JSONArray(doctor.getWorkDays());
            jsonObject.put("workDays", workDaysArray);
            jsonObject.put("maxAppointmentsPerHour", doctor.getMaxAppointmentsPerHour());
            jsonObject.put("permission", doctor.getpermission());
            jsonArray.put(jsonObject);
        }
        try (FileWriter file = new FileWriter(jsonFilePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将病人列表写入JSON文件
    public static void writePatientsToJson(String jsonFilePath, List<Patient> patients) {
        JSONArray jsonArray = new JSONArray();
        for (Patient patient : patients) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", patient.getId());
            jsonObject.put("name", patient.getName());
            jsonObject.put("password", patient.getPassword());
            jsonArray.put(jsonObject);
        }
        try (FileWriter file = new FileWriter(jsonFilePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将预约列表写入JSON文件
    public static void writeAppointmentsToJson(String jsonFilePath, List<Appointment> appointments) {
        JSONArray jsonArray = new JSONArray();
        for (Appointment appointment : appointments) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", appointment.getDate());
            jsonObject.put("time", appointment.getTime());
            jsonObject.put("department", appointment.getDepartment());
            jsonObject.put("doctorId", appointment.getDoctor());
            jsonObject.put("patientId", appointment.getPatient());
            jsonObject.put("lastUpdateTime", appointment.getLastUpdateTime());
            jsonArray.put(jsonObject);
        }
        try (FileWriter file = new FileWriter(jsonFilePath)) {
            file.write(jsonArray.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
