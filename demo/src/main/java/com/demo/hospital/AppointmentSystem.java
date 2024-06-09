package com.demo.hospital;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AppointmentSystem {
    // 保存医生、病人和预约信息的列表
    private List<Doctor> doctors;
    private List<Patient> patients;
    private List<Appointment> appointments;

    // 构造函数，初始化列表
    public AppointmentSystem() {
        this.doctors = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    // 添加医生到列表
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    // 添加病人到列表
    public void addPatient(Patient patient) {
        patients.add(patient);
    }

    // 添加预约到列表
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public void removeDoctor(Doctor deledoctor) {
        // 1. 从医生列表中删除医生
        doctors.remove(deledoctor);

        // 2. 删除医生相关的预约信息
        List<Appointment> appointmentsToRemove = getAppointmentsByDoctor(deledoctor);
        appointments.removeAll(appointmentsToRemove);

    }

    // 获取所有医生列表
    public List<Doctor> getDoctors() {
        return doctors;
    }

    // 获取所有病人列表
    public List<Patient> getPatients() {
        return patients;
    }

    // 获取所有预约列表
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<Appointment> findByDoctorIdAndLastUpdateTimeBetween(int doctorId, long startTime, long endTime) {
        return appointments.stream()
                .filter(a -> a.getDoctor() == doctorId &&
                        a.getLastUpdateTime() > startTime &&
                        a.getLastUpdateTime() <= endTime)
                .collect(Collectors.toList());
    }

    // 获取指定日期和科室的空闲医生
    public List<Doctor> getAvailableDoctors(LocalDate date, String department) {
        List<Doctor> availableDoctors = new ArrayList<>();
        // datastr =
        for (Doctor doctor : doctors) {
            if (doctor.getDepartment().equals(department) && doctor.getWorkDays().contains(date)) { // 如果该医生是这个科室，且该日工作
                long bookedAppointments = getAppointmentsByDoctor(doctor).stream()
                        .filter(a -> a.getDate().equals(date))
                        .count();
                int availableSlots = (doctor.getMaxAppointmentsPerHour() * 8) - (int) bookedAppointments;
                if (availableSlots > 0) {
                    availableDoctors.add(doctor);
                }
            }
        }
        return availableDoctors;
    }

    // 通过ID获取医生
    public Doctor findDoctorById(int id) {
        for (Doctor doctor : doctors) {
            if (doctor.getId() == id) {
                return doctor;
            }
        }
        return null; // 如果未找到匹配的医生，返回null
    }

    // 通过ID获取病人
    public Patient findpatientById(int id) {
        for (Patient patient : patients) {
            if (patient.getId() == id) {
                return patient;
            }
        }
        return null; // 如果未找到匹配的医生，返回null
    }

    // 获取某医生的所有预约
    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return appointments.stream().filter(a -> a.getDoctor() == doctor.getId()).collect(Collectors.toList());

    }

    // 获取某病人的所有预约
    public List<Appointment> getAppointmentsByPatient(Patient patient) {
        return appointments.stream().filter(a -> a.getPatient() == patient.getId()).collect(Collectors.toList());
    }

    // 从 JSON 文件加载医生信息
    public void loadDoctorsFromJson(String jsonFilePath) {
        doctors = JsonUtil.readDoctorsFromJson(jsonFilePath);
    }

    // 从 JSON 文件加载病人信息
    public void loadPatientsFromJson(String jsonFilePath) {
        patients = JsonUtil.readPatientsFromJson(jsonFilePath);
    }

    // 从 JSON 文件加载预约信息
    public void loadAppointmentsFromJson(String jsonFilePath) {
        appointments = JsonUtil.readAppointmentsFromJson(jsonFilePath);
    }

    // 将医生信息保存到 JSON 文件
    public void saveDoctorsToJson(String jsonFilePath) {
        JsonUtil.writeDoctorsToJson(jsonFilePath, doctors);
    }

    // 将病人信息保存到 JSON 文件
    public void savePatientsToJson(String jsonFilePath) {
        JsonUtil.writePatientsToJson(jsonFilePath, patients);
    }

    // 将预约信息保存到 JSON 文件
    public void saveAppointmentsToJson(String jsonFilePath) {
        JsonUtil.writeAppointmentsToJson(jsonFilePath, appointments);
    }
}
