package com.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.hospital.Appointment;
// import com.demo.hospital.Appointment;
// import com.demo.hospital.Doctor;
import com.demo.hospital.Patient;
import com.demo.service.PatientService;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // 注册病人
    @PostMapping("/register")
    public ResponseEntity<String> registerDoctor(@RequestParam String name, @RequestParam String password) {
        int patientId = patientService.registerPatient(name, password);
        return ResponseEntity.ok("注册成功！您的登录ID是: " + patientId);
    }

    // 病人登录
    @PostMapping("/login")
    public ResponseEntity<String> loginDoctor(@RequestParam String name, @RequestParam String password) {
        Patient patient = patientService.login(name, password);
        if (patient != null) {
            return ResponseEntity.ok("登录成功！欢迎 ");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录失败，您的ID或密码错误！");
        }
    }

    // 使用 SLF4J 的 LoggerFactory 获取 LoggerContext
    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    // 通过 LoggerContext 获取 logback 的 Logger
    private static final Logger logger = loggerContext.getLogger(PatientController.class);

    @PostMapping("/search")
    public ResponseEntity<List<String>> searchDoctors(@RequestParam String department,
            @RequestParam String date) {
        logger.info("department{}", department);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate Date = LocalDate.parse(date, formatter);
        try {
            List<String> availableDoctorsInfo = patientService.search(department, Date);
            logger.info("1{}", availableDoctorsInfo);
            return ResponseEntity.ok(availableDoctorsInfo);
        } catch (Exception e) {
            logger.error("e", e);
            return ResponseEntity.status(500).build(); // 返回服务器错误的响应
        }
    }

    // 病人根据科室和日期查询值班医生
    // @PostMapping("/search")
    // public ResponseEntity<List<String>> searchDoctors(@RequestParam String
    // department,
    // @RequestParam String date) {
    // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    // LocalDate Date = LocalDate.parse(date, formatter);
    // // LocalDate Date = LocalDate.parse(date);
    // try {
    // List<String> availableDoctorsInfo = patientService.search(department, Date);
    // return ResponseEntity.ok(availableDoctorsInfo);
    // } catch (Exception e) {
    // return ResponseEntity.status(500).build(); // 返回服务器错误的响应
    // }
    // }

    // 病人挂号预约
    @PostMapping("/query")
    public ResponseEntity<String> queryAppointmentsByDate(@RequestParam String doctorName,
            @RequestParam String patientName, @RequestParam String time, @RequestParam String date) {

        LocalDate Date = LocalDate.parse(date);
        patientService.reserve(doctorName, patientName, time, Date);
        return ResponseEntity.ok().build();

    }
}
