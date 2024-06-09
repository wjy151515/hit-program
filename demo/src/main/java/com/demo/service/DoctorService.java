package com.demo.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.cache.UpdateCache;
import com.demo.hospital.Appointment;
import com.demo.hospital.AppointmentSystem;
import com.demo.hospital.Doctor;
import com.demo.repository.AppointmentRepository;

@Service
public class DoctorService {
    @Autowired
    private AppointmentSystem appointmentSystem;
    private static final String DOCTORS_FILE_PATH = "./src/main/java/com/demo/resources/doctors.json";
    private static final String APPOINTMENTS_FILE_PATH = "./src/main/java/com/demo/resources/appointments.json";

    // 注册医生的方法
    public int registerDoctor(String name, String password, String department) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);
        // 自动生成唯一的医生ID
        int id = generateUniqueDoctorId();
        // 创建医生对象
        Doctor doctor = new Doctor(id, name, password, department, new ArrayList<>(), 1, false);
        // 添加医生到预约系统中
        appointmentSystem.addDoctor(doctor);
        // 保存数据到文件中
        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH);
        return id; // 返回医生的ID
    }

    // 医生登录的方法
    public Doctor login(int id, String password) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);
        // 遍历医生列表
        for (Doctor doctor : appointmentSystem.getDoctors()) {
            // 检查医生ID和密码是否匹配
            if (doctor.getId() == id && doctor.getPassword().equals(password)) {
                return doctor; // 如果匹配，返回医生对象
            }
        }
        return null; // 如果未找到匹配的医生，返回null
    }

    // 查询医生权限的方法
    public boolean searchpermission(int doctorId) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);

        Doctor doctor = appointmentSystem.findDoctorById(doctorId);

        if (doctor.getpermission()) {
            return true;
        }
        return false;
    }

    // 修改医生每小时最大挂号数的方法
    public void updateAppointmentLimit(int doctorId, int limit) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);

        Doctor doctor = appointmentSystem.findDoctorById(doctorId);

        if (doctor != null) {
            doctor.setMaxAppointmentsPerHour(limit);
            appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH);
        }
    }

    // 查询某日挂号信息的方法
    public List<Appointment> queryAppointmentsByDate(int doctorId, LocalDate date) {

        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);

        Doctor doctor = appointmentSystem.findDoctorById(doctorId);
        if (doctor == null) {
            return Collections.emptyList(); // 如果未找到医生，返回空列表
        }

        List<Appointment> appointments = appointmentSystem.getAppointmentsByDoctor(doctor);
        if (date != null) {
            // 根据日期过滤预约信息
            appointments = appointments.stream()
                    .filter(a -> a.getDate().equals(date))
                    .collect(Collectors.toList());
        }

        return appointments;
    }

    // 增加工作日
    public void workDayadd(int id, LocalDate date) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        Doctor doctor = appointmentSystem.findDoctorById(id);

        doctor.addWorkDay(date);

        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
    }

    // 删除工作日
    public void workDaydel(int id, LocalDate date) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        Doctor doctor = appointmentSystem.findDoctorById(id);

        doctor.removeWorkDay(date);

        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
    }

    // 生成唯一医生ID的方法
    private int generateUniqueDoctorId() {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);
        // 获取医生列表中最大的ID，如果列表为空，则返回0
        int maxId = appointmentSystem.getDoctors().stream().mapToInt(Doctor::getId).max().orElse(0);
        return maxId + 1; // 返回最大ID加1作为新医生的ID
    }

    // @Autowired
    // private AppointmentRepository appointmentRepository;

    // @Autowired
    // private UpdateCache updateCache;

    public List<Appointment> queryAppointmentsSince(int doctorId) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);
        long currentQueryTime = Instant.now().getEpochSecond();
        Long lastQueryTime = currentQueryTime - 20;
        // Long lastQueryTime = updateCache.getLastQueryTime(doctorId);

        List<Appointment> appointments = appointmentSystem.findByDoctorIdAndLastUpdateTimeBetween(doctorId,
                lastQueryTime, currentQueryTime);
        // updateCache.setLastQueryTime(doctorId, currentQueryTime);

        return appointments;
    }

}
