package com.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.hospital.Appointment;
import com.demo.hospital.Doctor;
import com.demo.service.DoctorService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/register")
    public ResponseEntity<String> registerDoctor(@RequestParam String name, @RequestParam String password,
            @RequestParam String department) {
        int doctorId = doctorService.registerDoctor(name, password, department);
        return ResponseEntity.ok("医生注册成功！您的职工ID是: " + doctorId);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginDoctor(@RequestParam int id, @RequestParam String password) {
        // int number = Integer.parseInt(id);
        Doctor doctor = doctorService.login(id, password);
        if (doctor != null) {
            return ResponseEntity.ok("医生登录成功！欢迎 " + doctor.getName() + " 医生！");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录失败，职工ID或密码错误！");
        }
    }

    @PostMapping("/limit")
    public ResponseEntity<String> updateAppointmentLimit(@RequestParam int id, @RequestParam int limit) {
        if (!doctorService.searchpermission(id)) {
            // 统一类型为 Object，传递错误信息
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("您没有权限，请联系管理员");
        }
        doctorService.updateAppointmentLimit(id, limit);
        return ResponseEntity.ok("医生每小时最大挂号数修改成功！");
    }

    // 医生查找对应日期的所有预约
    @GetMapping("/query")
    public ResponseEntity<Object> queryAppointmentsByDate(@RequestParam int id, @RequestParam String date) {
        LocalDate queryDate = LocalDate.parse(date);
        List<Appointment> appointments = doctorService.queryAppointmentsByDate(id, queryDate);
        // 成功时传递预约列表
        return ResponseEntity.ok(appointments);
    }

    // 增加医生工作日
    @PostMapping("/workDaysadd")
    public ResponseEntity<Object> DoctorworkDaysadd(@RequestParam int id, @RequestParam String startTime,
            @RequestParam String endTime) {

        if (!doctorService.searchpermission(id)) {
            // 统一类型为 Object，传递错误信息
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("您没有权限，请联系管理员");
        }
        // 输出日志以检查输入

        // int num = Integer.parseInt(id);
        LocalDate startDate = LocalDate.parse(startTime);
        LocalDate endDate = LocalDate.parse(endTime);

        List<LocalDate> datesInRange = new ArrayList<>();

        // 从 startDate 开始，逐个增加日期，直到 endDate
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            datesInRange.add(currentDate);
            currentDate = currentDate.plusDays(1); // 增加一天
        }

        // 将得到的日期列表传递给 administratorService.workDaysadd() 方法
        for (LocalDate date : datesInRange) {
            doctorService.workDayadd(id, date);
        }

        return ResponseEntity.ok().build();
    }

    // 删去工作日
    @PostMapping("/workDaysdel")
    public ResponseEntity<Object> DoctorworkDaysdel(@RequestParam int id, @RequestParam String startTime,
            @RequestParam String endTime) {
        if (!doctorService.searchpermission(id)) {
            // 统一类型为 Object，传递错误信息
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("您没有权限，请联系管理员");
        }
        LocalDate startDate = LocalDate.parse(startTime);
        LocalDate endDate = LocalDate.parse(endTime);

        List<LocalDate> datesInRange = new ArrayList<>();

        // 从 startDate 开始，逐个增加日期，直到 endDate
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            datesInRange.add(currentDate);
            currentDate = currentDate.plusDays(1); // 增加一天
        }

        // 将得到的日期列表传递给 administratorService.workDaysadd() 方法
        for (LocalDate date : datesInRange) {
            doctorService.workDaydel(id, date);
        }

        return ResponseEntity.ok().build();
    }

    // 消息提醒
    @GetMapping("/poll")
    public ResponseEntity<List<Appointment>> queryAppointmentsSince(@RequestParam int id) {
        List<Appointment> appointments = doctorService.queryAppointmentsSince(id);
        return ResponseEntity.ok(appointments);
    }
}
