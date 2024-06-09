package com.demo.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
// import java.util.Collections;
import java.util.List;
// import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.controller.PatientController;
import com.demo.hospital.Appointment;
import com.demo.hospital.AppointmentSystem;
import com.demo.hospital.Doctor;
import com.demo.hospital.Patient;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

@Service
public class PatientService {
    @Autowired
    private AppointmentSystem appointmentSystem;
    private static final String PATIENTS_FILE_PATH = "./src/main/java/com/demo/resources/patients.json";
    private static final String DOCTORS_FILE_PATH = "./src/main/java/com/demo/resources/doctors.json";
    private static final String APPOINTMENTS_FILE_PATH = "./src/main/java/com/demo/resources/appointments.json";

    // 注册
    public int registerPatient(String name, String password) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadPatientsFromJson(PATIENTS_FILE_PATH);

        int id = generateUniquePatientId(); // 生成唯一的患者ID
        Patient patient = new Patient(id, name, password); // 创建患者对象
        appointmentSystem.addPatient(patient); // 添加患者到系统

        appointmentSystem.savePatientsToJson(PATIENTS_FILE_PATH);
        return id;
    }

    // 登录
    public Patient login(String name, String password) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadPatientsFromJson(PATIENTS_FILE_PATH);

        // 遍历患者列表
        for (Patient patient : appointmentSystem.getPatients()) {

            // 如果找到匹配的ID和密码
            if (patient.getName().equals(name) && patient.getPassword().equals(password)) {
                return patient; // 返回该患者对象
            }
        }
        return null; // 未找到匹配的患者，返回 null
    }

    // 使用 SLF4J 的 LoggerFactory 获取 LoggerContext
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    // 通过 LoggerContext 获取 logback 的 Logger
    private static final Logger logger = loggerContext.getLogger(PatientController.class);

    // 病人根据科室和日期查询值班医生
    // 返回类型：内容如下的列表：
    // doctor.getName() + ", " + doctor.getId() + ", " + time + ", " +
    // remainingSlotsText
    public List<String> search(String department, LocalDate date) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);

        List<String> availableDoctorsInfo = new ArrayList<>();

        // 获取特定日期和科室的可用医生列表
        List<Doctor> availableDoctors = appointmentSystem.getAvailableDoctors(date, department);

        // 遍历可用医生列表
        for (Doctor doctor : availableDoctors) {
            logger.info("3{}", date);
            // 假设工作时间为上午8点到下午5点
            for (int hour = 8; hour <= 16; hour++) {
                // 中午12点跳过午休时间
                if (hour == 12)
                    continue;
                // 构建时间段字符串
                String time = String.format("%02d:00-%02d:00", hour, hour + 1);
                // 计算已预约的数量
                long bookedCount = appointmentSystem.getAppointmentsByDoctor(doctor).stream()
                        .filter(a -> a.getDate().equals(date) && a.getTime().equals(time))
                        .count();
                // 计算剩余可用预约数
                int remainingSlots = doctor.getMaxAppointmentsPerHour() - (int) bookedCount;
                // 如果剩余可用预约数大于0
                if (remainingSlots > 0) {
                    // 构建剩余预约数文本
                    String remainingSlotsText = String.format("%d/%d", bookedCount,
                            doctor.getMaxAppointmentsPerHour());
                    // 添加医生信息到结果列表
                    availableDoctorsInfo
                            .add(doctor.getName() + ", " + doctor.getId() + ", " + time + ", " + remainingSlotsText);
                }
            }
        }
        return availableDoctorsInfo;
    }

    // 预约挂号
    public void reserve(String doctorName, String patientName, String time, LocalDate date) {
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadPatientsFromJson(PATIENTS_FILE_PATH);
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);

        Patient currentpatient = null;
        // 遍历患者列表
        for (Patient patient : appointmentSystem.getPatients()) {

            // 如果找到匹配的名字
            if (patient.getName().equals(patientName)) {
                currentpatient = patient; // 返回该患者对象
            }
        }

        Doctor currentdoctor = null;
        // 遍历医生列表
        for (Doctor doctor : appointmentSystem.getDoctors()) {

            // 如果找到匹配的名字
            if (doctor.getName().equals(doctorName)) {
                currentdoctor = doctor; // 返回该患者对象
            }
        }

        // Doctor doctor = appointmentSystem.findDoctorById(doctorId);
        long timestamp = Instant.now().getEpochSecond(); // 生成当前时间戳
        Appointment appointment = new Appointment(date, time, currentdoctor.getDepartment(), currentdoctor.getId(),
                currentpatient.getId(), timestamp); // 创建预约对象
        appointmentSystem.addAppointment(appointment); // 添加预约到系统

        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH);
        appointmentSystem.savePatientsToJson(PATIENTS_FILE_PATH);
        appointmentSystem.saveAppointmentsToJson(APPOINTMENTS_FILE_PATH);
    }

    private int generateUniquePatientId() {
        // 获取患者列表中的最大ID
        int maxId = appointmentSystem.getPatients().stream().mapToInt(Patient::getId).max().orElse(0);
        return maxId + 1; // 返回一个新的唯一ID
    }

}
