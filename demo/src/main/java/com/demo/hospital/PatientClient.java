package com.demo.hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
// import java.util.ArrayList;
// import java.util.Arrays;

public class PatientClient extends JFrame {
    private static final String DOCTORS_FILE_PATH = "./src/main/java/com/demo/resources/doctors.json";
    private static final String PATIENTS_FILE_PATH = "./src/main/java/com/demo/resources/patients.json";
    private static final String APPOINTMENTS_FILE_PATH = "./src/main/java/com/demo/resources/appointments.json";
    private AppointmentSystem appointmentSystem;
    private Patient loggedInPatient;
    private JTable doctorTable;
    private DefaultTableModel doctorTableModel;
    public LocalDate cdata;// 用于存储查询的日期

    public PatientClient() {
        // 初始化病人客户端
        appointmentSystem = new AppointmentSystem(); // 创建一个预约系统实例
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH); // 从 JSON 文件加载医生信息
        appointmentSystem.loadPatientsFromJson(PATIENTS_FILE_PATH); // 从 JSON 文件加载病人信息
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH); // 从 JSON 文件加载预约信息

        setTitle("病人端"); // 设置窗口标题为“病人端”
        setSize(800, 600); // 设置窗口大小为 800x600 像素
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置默认关闭操作为退出应用程序
        setLocationRelativeTo(null); // 将窗口定位在屏幕中央

        initUI(); // 初始化用户界面

        // 添加窗口关闭事件监听器，保存数据并执行默认的窗口关闭操作
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData(); // 保存数据
                super.windowClosing(e);
            }
        });
    }

    // 初始化用户界面
    private void initUI() {
        JPanel panel = new JPanel(); // 创建面板
        panel.setLayout(new CardLayout()); // 使用卡片布局管理器

        // 创建登录面板、注册面板和主面板
        JPanel loginPanel = createLoginPanel(panel);
        JPanel registerPanel = createRegisterPanel(panel);
        JPanel mainPanel = createMainPanel();

        // 将面板添加到主面板
        panel.add(loginPanel, "Login");
        panel.add(registerPanel, "Register");
        panel.add(mainPanel, "Main");

        add(panel); // 将主面板添加到窗口

        showCard(panel, "Login"); // 默认显示登录面板
    }

    // 创建登录面板
    private JPanel createLoginPanel(JPanel mainPanel) {
        JPanel loginPanel = new JPanel(); // 创建登录面板
        loginPanel.setLayout(new GridLayout(4, 2, 10, 10)); // 使用网格布局管理器，4 行 2 列，水平和垂直间距均为 10 像素

        // 创建标签和文本框
        JLabel idLabel = new JLabel("注册ID:");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField();

        // 创建登录按钮
        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText()); // 获取输入的注册ID
                String password = new String(passwordField.getPassword()); // 获取输入的密码
                Patient patient = login(id, password); // 登录验证
                if (patient != null) {
                    loggedInPatient = patient;
                    showCard(mainPanel, "Main"); // 切换到主面板
                } else {
                    JOptionPane.showMessageDialog(PatientClient.this, "登录失败，注册ID或密码错误！"); // 显示登录失败提示对话框
                }
            }
        });

        // 创建切换到注册面板的按钮
        JButton switchToRegisterButton = new JButton("注册");
        switchToRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard(mainPanel, "Register"); // 切换到注册面板
            }
        });

        // 将组件添加到登录面板
        loginPanel.add(idLabel);
        loginPanel.add(idField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(switchToRegisterButton);

        return loginPanel; // 返回登录面板
    }

    private JPanel createRegisterPanel(JPanel mainPanel) {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel nameLabel = new JLabel("姓名:"); // 姓名标签
        JTextField nameField = new JTextField(); // 姓名文本框
        JLabel passwordLabel = new JLabel("密码:"); // 密码标签
        JPasswordField passwordField = new JPasswordField(); // 密码文本框

        JButton registerButton = new JButton("注册"); // 注册按钮
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText(); // 获取输入的姓名
                String password = new String(passwordField.getPassword()); // 获取输入的密码
                int id = generateUniquePatientId(); // 生成唯一的患者ID
                Patient patient = new Patient(id, name, password); // 创建患者对象
                appointmentSystem.addPatient(patient); // 添加患者到系统
                saveData(); // 保存数据
                JOptionPane.showMessageDialog(PatientClient.this, "注册成功！您的注册ID是: " + id); // 弹出注册成功的消息对话框，显示注册ID
                showCard(mainPanel, "Login"); // 显示登录界面
            }
        });

        JButton switchToLoginButton = new JButton("登录"); // 切换到登录按钮
        switchToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard(mainPanel, "Login"); // 显示登录界面
            }
        });

        registerPanel.add(nameLabel); // 添加姓名标签到注册面板
        registerPanel.add(nameField); // 添加姓名文本框到注册面板
        registerPanel.add(passwordLabel); // 添加密码标签到注册面板
        registerPanel.add(passwordField); // 添加密码文本框到注册面板
        registerPanel.add(registerButton); // 添加注册按钮到注册面板
        registerPanel.add(switchToLoginButton); // 添加切换到登录按钮到注册面板

        return registerPanel; // 返回注册面板
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(); // 创建主面板
        mainPanel.setLayout(new BorderLayout()); // 设置主面板布局为边界布局

        doctorTableModel = new DefaultTableModel(new Object[] { "职工ID", "姓名", "科室", "时间段", "剩余号数" }, 0); // 创建医生表格模型
        doctorTable = new JTable(doctorTableModel); // 创建医生表格

        JScrollPane scrollPane = new JScrollPane(doctorTable); // 创建滚动面板，并将医生表格添加到滚动面板中
        mainPanel.add(scrollPane, BorderLayout.CENTER); // 将滚动面板添加到主面板中间位置

        JPanel inputPanel = new JPanel(); // 创建输入面板
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 设置输入面板布局为3行2列的网格布局

        JLabel departmentLabel = new JLabel("科室:"); // 科室标签
        JTextField departmentField = new JTextField(); // 科室文本框
        JLabel dateLabel = new JLabel("日期（yyyy,MM,dd）:"); // 日期标签
        JTextField dateField = new JTextField(); // 日期文本框
        JButton queryButton = new JButton("查询空闲医生"); // 查询空闲医生按钮
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String inputDate = dateField.getText(); // 获取输入的日期
                if (inputDate != null && inputDate.matches("\\d{4},\\d{2},\\d{2}")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM,dd");
                    LocalDate date = LocalDate.parse(inputDate, formatter);
                    cdata = LocalDate.parse(inputDate, formatter);
                    loadDoctorData(departmentField.getText(), date); // 加载医生数据
                } else {
                    JOptionPane.showMessageDialog(null, "请输入有效的日期格式（yyyy,MM,dd）！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        inputPanel.add(departmentLabel); // 将科室标签添加到输入面板
        inputPanel.add(departmentField); // 将科室文本框添加到输入面板
        inputPanel.add(dateLabel); // 将日期标签添加到输入面板
        inputPanel.add(dateField); // 将日期文本框添加到输入面板
        inputPanel.add(queryButton); // 将查询按钮添加到输入面板

        JButton bookButton = new JButton("预约"); // 预约按钮
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = doctorTable.getSelectedRow(); // 获取选中的行
                if (selectedRow != -1) { // 如果有选中的行
                    int doctorId = (int) doctorTableModel.getValueAt(selectedRow, 0); // 获取医生ID
                    String time = (String) doctorTableModel.getValueAt(selectedRow, 3); // 获取时间段

                    LocalDate date = cdata; // 获取日期
                    Doctor doctor = appointmentSystem.getDoctors().stream().filter(d -> d.getId() == doctorId)
                            .findFirst().orElse(null); // 根据医生ID查找医生
                    if (doctor != null) { // 如果医生存在
                        long timestamp = Instant.now().getEpochSecond(); // 生成当前时间戳
                        Appointment appointment = new Appointment(date, time, doctor.getDepartment(), doctorId,
                                loggedInPatient.getId(), timestamp); // 创建预约对象
                        appointmentSystem.addAppointment(appointment); // 添加预约到系统
                        saveData(); // 保存数据
                        loadDoctorData(departmentField.getText(), date); // 加载医生数据
                        JOptionPane.showMessageDialog(PatientClient.this, "预约成功！"); // 弹出预约成功的消息对话框
                    }
                } else { // 如果没有选中的行
                    JOptionPane.showMessageDialog(PatientClient.this, "请选择一个医生！"); // 弹出提示选择医生的消息对话框
                }
            }
        });

        mainPanel.add(inputPanel, BorderLayout.NORTH); // 将输入面板添加到主面板北部位置
        mainPanel.add(bookButton, BorderLayout.SOUTH); // 将预约按钮添加到主面板南部位置

        return mainPanel; // 返回主面板
    }

    private Patient login(int id, String password) {
        // 遍历患者列表
        for (Patient patient : appointmentSystem.getPatients()) {
            // 如果找到匹配的ID和密码
            if (patient.getId() == id && patient.getPassword().equals(password)) {
                return patient; // 返回该患者对象
            }
        }
        return null; // 未找到匹配的患者，返回 null
    }

    private int generateUniquePatientId() {
        // 获取患者列表中的最大ID
        int maxId = appointmentSystem.getPatients().stream().mapToInt(Patient::getId).max().orElse(0);
        return maxId + 1; // 返回一个新的唯一ID
    }

    private void loadDoctorData(String department, LocalDate date) {
        doctorTableModel.setRowCount(0); // 清空医生表格模型的数据
        // 获取特定日期和科室的可用医生列表
        List<Doctor> availableDoctors = appointmentSystem.getAvailableDoctors(date, department);
        // 遍历可用医生列表
        for (Doctor doctor : availableDoctors) {
            boolean hasSlots = false; // 标记医生是否有可用的预约时段
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
                    String remainingSlotsText = String.format("%d/%d", remainingSlots,
                            doctor.getMaxAppointmentsPerHour());
                    // 添加医生信息到医生表格模型
                    doctorTableModel.addRow(new Object[] { doctor.getId(), doctor.getName(), doctor.getDepartment(),
                            time, remainingSlotsText });
                    hasSlots = true; // 标记医生有可用的预约时段
                }
            }
            // 如果医生整天都没有可用的预约时段，则从列表中移除
            if (!hasSlots) {
                for (int row = doctorTableModel.getRowCount() - 1; row >= 0; row--) {
                    if ((int) doctorTableModel.getValueAt(row, 0) == doctor.getId()) {
                        doctorTableModel.removeRow(row);
                    }
                }
            }
        }
    }

    private void showCard(JPanel panel, String cardName) {
        // 获取卡片布局对象
        CardLayout cl = (CardLayout) (panel.getLayout());
        // 显示指定名称的卡片
        cl.show(panel, cardName);
    }

    private void saveData() {
        // 将医生、患者和预约信息保存到JSON文件
        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH);
        appointmentSystem.savePatientsToJson(PATIENTS_FILE_PATH);
        appointmentSystem.saveAppointmentsToJson(APPOINTMENTS_FILE_PATH);
    }

    public static void main(String[] args) {
        // 创建并显示患者客户端界面
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PatientClient client = new PatientClient();
                client.setVisible(true);
            }
        });
    }

}
