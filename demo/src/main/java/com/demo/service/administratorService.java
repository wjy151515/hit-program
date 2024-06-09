package com.demo.service;

import java.time.LocalDate;
import java.util.ArrayList;
// import java.util.Collections;
import java.util.List;
// import java.util.stream.Collectors;

// import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.RequestParam;

// import com.demo.hospital.Appointment;
import com.demo.hospital.AppointmentSystem;
import com.demo.hospital.Doctor;
import com.demo.hospital.PasswordManager;

@Service
public class administratorService {
    @Autowired
    private AppointmentSystem appointmentSystem;
    private static final String DOCTORS_FILE_PATH = "./src/main/java/com/demo/resources/doctors.json";
    private static final String APPOINTMENTS_FILE_PATH = "./src/main/java/com/demo/resources/appointments.json";

    // 登入
    public boolean login(String password) {
        PasswordManager passwordManager = PasswordManager.getInstance();
        if (passwordManager.getPassword().equals(password)) {
            return true; // 如果匹配
        }
        return false; // 如果未匹配
    }

    // 修改密码
    public void change(String newPassword) {
        PasswordManager passwordManager = PasswordManager.getInstance();
        passwordManager.setPassword(newPassword);
    }

    // 返回医生信息的方法
    public List<String> getDoctorList() {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);

        List<Doctor> doctors = appointmentSystem.getDoctors();
        List<String> doctorInfoList = new ArrayList<>();
        for (Doctor doctor : doctors) {
            // 构建医生信息字符串并添加到结果列表
            String doctorInfo = String.format("%s, %d, %s, %d, %s, %s",
                    doctor.getName(),
                    doctor.getId(),
                    doctor.getDepartment(),
                    doctor.getMaxAppointmentsPerHour(),
                    doctor.getpermission(),
                    doctor.getPassword());
            doctorInfoList.add(doctorInfo);
        }

        return doctorInfoList;
    }

    // 添加医生的方法
    public boolean adddct(int id, String name, String password, String department, int maxAppointmentsPerHour,
            boolean permission) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        // 检查是否存在重复的职工 ID
        if (appointmentSystem.findDoctorById(id) != null) {
            return false;
        }

        // 创建医生对象
        Doctor doctor = new Doctor(id, name, password, department, new ArrayList<>(), maxAppointmentsPerHour,
                permission);
        // 添加医生到预约系统中
        appointmentSystem.addDoctor(doctor);
        // 保存数据到文件中
        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
        return true;
    }

    // 删除医生的方法
    public void deldct(int id) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);
        appointmentSystem.removeDoctor(appointmentSystem.findDoctorById(id));
        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
        appointmentSystem.saveAppointmentsToJson(APPOINTMENTS_FILE_PATH); // 保存医生数据到JSON文件
    }

    // 修改权限
    public void cpermission(int id, int val) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        Doctor doctor = appointmentSystem.findDoctorById(id);
        if (val == 1) {
            doctor.setpermission(true);
        } else {
            doctor.setpermission(false);
        }
        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
    }

    // 修改密码
    public void cpassword(int id, String password) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        Doctor doctor = appointmentSystem.findDoctorById(id);

        doctor.setpassword(password);

        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
    }

    // 增加工作日
    public void workDaysadd(int id, LocalDate date) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        Doctor doctor = appointmentSystem.findDoctorById(id);

        doctor.addWorkDay(date);

        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
    }

    // 删除工作日
    public void workDaysdel(int id, LocalDate date) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        Doctor doctor = appointmentSystem.findDoctorById(id);

        doctor.removeWorkDay(date);

        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
    }

    // 修改每小时最大挂号数
    public void max(int id, int val) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        Doctor doctor = appointmentSystem.findDoctorById(id);

        doctor.setMaxAppointmentsPerHour(val);

        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件

    }
}
