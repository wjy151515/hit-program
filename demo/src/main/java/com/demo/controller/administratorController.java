package com.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// import javax.swing.table.DefaultTableModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.demo.hospital.Appointment;
import com.demo.hospital.Doctor;
// import com.demo.service.DoctorService;
import com.demo.service.administratorService;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/administrator")
public class administratorController {
    @Autowired
    private administratorService administratorService;

    // 管理员登入
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String password) {

        if (administratorService.login(password)) {
            return ResponseEntity.ok("登录成功！欢迎 ");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("登录失败，职工ID或密码错误！");
        }
    }

    // 修改管理员密码
    @PostMapping("/change")
    public ResponseEntity<String> change(@RequestParam String password) {
        administratorService.change(password);
        return ResponseEntity.ok("登录成功！欢迎 ");

    }

    // 返回所有医生全部信息
    @PostMapping("/list")
    public ResponseEntity<List<String>> loadDoctorData() {
        List<String> doctorList = administratorService.getDoctorList();
        return new ResponseEntity<>(doctorList, HttpStatus.OK);
    }

    // 增加医生
    @PostMapping("/add")
    public ResponseEntity<String> addDoctor(@RequestParam int id, @RequestParam String name,
            @RequestParam String password,
            @RequestParam String department, @RequestParam int maxAppointmentsPerHour,
            @RequestParam boolean permission) {
        boolean success = administratorService.adddct(id, name, password, department, maxAppointmentsPerHour,
                permission);
        if (success) {
            return ResponseEntity.ok("医生添加成功！");
        } else {
            return ResponseEntity.badRequest().body("医生添加失败，可能已存在相同职工 ID 的医生！");
        }
    }

    // 删除医生
    @PostMapping("/del")
    public ResponseEntity<List<Doctor>> delDoctor(@RequestParam int id) {
        administratorService.deldct(id);
        return ResponseEntity.ok().build();
    }

    // 修改权限
    @PostMapping("/permission")
    public ResponseEntity<List<Doctor>> Doctorpermission(@RequestParam int id, @RequestParam int val) {
        administratorService.cpermission(id, val);
        return ResponseEntity.ok().build();
    }

    // 修改密码
    @PostMapping("/password")
    public ResponseEntity<List<Doctor>> Doctorpassword(@RequestParam int id, @RequestParam String password) {
        administratorService.cpassword(id, password);
        return ResponseEntity.ok().build();
    }

    // 增加工作日
    @PostMapping("/workDaysadd")
    public ResponseEntity<List<Doctor>> DoctorworkDaysadd(@RequestParam int id, @RequestParam String startTime,
            @RequestParam String endTime) {

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
            administratorService.workDaysadd(id, date);
        }

        return ResponseEntity.ok().build();
    }

    // 删去工作日
    @PostMapping("/workDaysdel")
    public ResponseEntity<List<Doctor>> DoctorworkDaysdel(@RequestParam int id, @RequestParam String startTime,
            @RequestParam String endTime) {

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
            administratorService.workDaysdel(id, date);
        }

        return ResponseEntity.ok().build();
    }

    // 修改每小时最大挂号数
    @PostMapping("/max")
    public ResponseEntity<List<Doctor>> Doctormax(@RequestParam int id, @RequestParam int val) {
        administratorService.max(id, val);
        return ResponseEntity.ok().build();
    }

}
