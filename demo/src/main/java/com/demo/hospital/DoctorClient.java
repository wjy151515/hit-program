package com.demo.hospital;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorClient extends JFrame {
    // 医生和预约数据的文件路径
    private static final String DOCTORS_FILE_PATH = "./src/resources/doctors.json";
    private static final String APPOINTMENTS_FILE_PATH = "./src/resources/appointments.json";
    // 预约系统实例
    private AppointmentSystem appointmentSystem;
    // 当前登录的医生
    private Doctor loggedInDoctor;
    // 预约信息表格和表格模型
    private JTable appointmentTable;
    private DefaultTableModel appointmentTableModel;

    // 构造方法
    public DoctorClient() {
        // 初始化预约系统，并加载医生和预约数据
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);
        appointmentSystem.loadAppointmentsFromJson(APPOINTMENTS_FILE_PATH);

        // 设置窗口标题、大小和关闭操作
        setTitle("医生端");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 初始化用户界面
        initUI();

        // 添加窗口关闭事件监听器，用于保存数据
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
                super.windowClosing(e);
            }
        });
    }

    // 初始化用户界面
    private void initUI() {
        // 创建主面板
        JPanel panel = new JPanel();
        panel.setLayout(new CardLayout());

        // 创建登录、注册和主界面面板
        JPanel loginPanel = createLoginPanel(panel); // 创建登录面板
        JPanel registerPanel = createRegisterPanel(panel); // 创建注册面板
        JPanel mainPanel = createMainPanel(); // 创建主面板

        // 将面板添加到主面板
        panel.add(loginPanel, "Login"); // 添加登录面板
        panel.add(registerPanel, "Register"); // 添加注册面板
        panel.add(mainPanel, "Main"); // 添加主面板

        // 将主面板添加到窗口
        add(panel);

        // 默认显示登录界面
        showCard(panel, "Login");
    }

    // 创建登录界面
    private JPanel createLoginPanel(JPanel mainPanel) {
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel idLabel = new JLabel("职工ID:");
        JTextField idField = new JTextField();
        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField();

        JButton loginButton = new JButton("登录");
        // 登录按钮点击事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id = Integer.parseInt(idField.getText());
                String password = new String(passwordField.getPassword());
                Doctor doctor = login(id, password);
                if (doctor != null) {
                    loggedInDoctor = doctor;
                    loadAppointmentData();
                    showCard(mainPanel, "Main");
                } else {
                    JOptionPane.showMessageDialog(DoctorClient.this, "登录失败，职工ID或密码错误！");
                }
            }
        });

        JButton switchToRegisterButton = new JButton("注册");
        // 切换到注册界面按钮点击事件
        switchToRegisterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard(mainPanel, "Register");
            }
        });

        // 添加组件到登录面板
        loginPanel.add(idLabel);
        loginPanel.add(idField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(switchToRegisterButton);

        return loginPanel;
    }

    // 创建注册界面
    private JPanel createRegisterPanel(JPanel mainPanel) {
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel nameLabel = new JLabel("姓名:");
        JTextField nameField = new JTextField();
        JLabel passwordLabel = new JLabel("密码:");
        JPasswordField passwordField = new JPasswordField();
        JLabel departmentLabel = new JLabel("科室:");
        JTextField departmentField = new JTextField();

        JButton registerButton = new JButton("注册");
        // 注册按钮点击事件
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());
                String department = departmentField.getText();
                // 自动生成唯一的医生ID
                int id = generateUniqueDoctorId();
                // 创建医生对象
                Doctor doctor = new Doctor(id, name, password, department, new ArrayList<>(), 10, false);
                // 添加医生到预约系统中
                appointmentSystem.addDoctor(doctor);
                // 保存数据到文件中
                saveData();
                // 提示注册成功，并显示医生ID
                JOptionPane.showMessageDialog(DoctorClient.this, "注册成功！您的职工ID是: " + id);
                // 切换到登录界面
                showCard(mainPanel, "Login");
            }
        });

        JButton switchToLoginButton = new JButton("登录");
        // 切换到登录界面按钮点击事件
        switchToLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCard(mainPanel, "Login");
            }
        });

        // 添加组件到注册面板
        registerPanel.add(nameLabel);
        registerPanel.add(nameField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(departmentLabel);
        registerPanel.add(departmentField);
        registerPanel.add(registerButton);
        registerPanel.add(switchToLoginButton);

        return registerPanel;
    }

    // 创建主界面
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 创建预约表格和滚动面板
        appointmentTableModel = new DefaultTableModel(new Object[] { "日期", "时间", "病人ID", "科室" }, 0);
        appointmentTable = new JTable(appointmentTableModel);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        // 创建查询输入面板
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2, 10, 10));

        JLabel dateLabel = new JLabel("查询日期（yyyy,MM,dd）:");
        JTextField dateField = new JTextField();

        JButton queryButton = new JButton("查询预约情况");

        // 查询按钮点击事件
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String inputDate = dateField.getText(); // 获取输入的日期
                if (inputDate != null && inputDate.matches("\\d{4},\\d{2},\\d{2}")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM,dd");
                    LocalDate date = LocalDate.parse(inputDate, formatter);
                    loadAppointmentData(date);// 加载数据
                } else {
                    JOptionPane.showMessageDialog(null, "请输入有效的日期格式（yyyy,MM,dd）！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton adjustWorkButton = new JButton("工作调整");
        // 工作调整按钮点击事件
        adjustWorkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (loggedInDoctor.getpermission()) {
                    showWorkAdjustmentDialog();
                } else {
                    JOptionPane.showMessageDialog(null, "您没有权限！请联系管理员。");
                }
            }
        });

        // 添加组件到查询
        // 添加组件到查询输入面板
        inputPanel.add(dateLabel); // 添加日期标签
        inputPanel.add(dateField); // 添加日期输入框
        inputPanel.add(queryButton); // 添加查询按钮
        inputPanel.add(adjustWorkButton); // 添加工作调整按钮

        // 将滚动面板和查询输入面板添加到主面板
        mainPanel.add(scrollPane, BorderLayout.CENTER); // 将表格滚动面板添加到主面板中央位置
        mainPanel.add(inputPanel, BorderLayout.NORTH); // 将查询输入面板添加到主面板北部位置

        return mainPanel; // 返回主面板
    }

    // 显示工作调整对话框
    private void showWorkAdjustmentDialog() {
        JFrame adjustmentFrame = new JFrame("工作调整");
        adjustmentFrame.setSize(300, 150);
        adjustmentFrame.setLocationRelativeTo(null);

        JPanel adjustmentPanel = new JPanel();
        adjustmentPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JButton adjustDateButton = new JButton("工作日期调整");
        JButton setAppointmentLimitButton = new JButton("设置每小时可挂号人数");

        // 工作日期调整按钮点击事件
        adjustDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWorkDateAdjustmentDialog();
            }
        });

        // 设置每小时可挂号人数按钮点击事件
        setAppointmentLimitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAppointmentLimitSettingDialog();
            }
        });

        adjustmentPanel.add(adjustDateButton);
        adjustmentPanel.add(setAppointmentLimitButton);

        adjustmentFrame.add(adjustmentPanel);
        adjustmentFrame.setVisible(true);
    }

    // 显示工作日期调整对话框
    private void showWorkDateAdjustmentDialog() {
        JFrame dateAdjustmentFrame = new JFrame("工作日期调整");
        dateAdjustmentFrame.setSize(300, 150);
        dateAdjustmentFrame.setLocationRelativeTo(null);

        JPanel dateAdjustmentPanel = new JPanel();
        dateAdjustmentPanel.setLayout(new GridLayout(2, 1, 10, 10));

        JButton addDateButton = new JButton("添加该日期");
        JButton deleteDateButton = new JButton("删除该日期");

        // 添加该日期按钮点击事件
        addDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dateString = JOptionPane.showInputDialog(null, "请输入要添加的日期（yyyy,MM,dd）：");
                if (dateString != null && dateString.matches("\\d{4},\\d{2},\\d{2}")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM,dd");
                    LocalDate date = LocalDate.parse(dateString, formatter);
                    loggedInDoctor.addWorkDay(date);
                    JOptionPane.showMessageDialog(null, "日期添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "请输入有效的日期格式（yyyy,MM,dd）！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 删除该日期按钮点击事件
        deleteDateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dateString = JOptionPane.showInputDialog(null, "请输入要删除的日期（yyyy,MM,dd）：");
                if (dateString != null && dateString.matches("\\d{4},\\d{2},\\d{2}")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM,dd");
                    LocalDate date = LocalDate.parse(dateString, formatter);
                    loggedInDoctor.removeWorkDay(date);
                    JOptionPane.showMessageDialog(null, "日期删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "请输入有效的日期格式（yyyy,MM,dd）！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        dateAdjustmentPanel.add(addDateButton);
        dateAdjustmentPanel.add(deleteDateButton);

        dateAdjustmentFrame.add(dateAdjustmentPanel);
        dateAdjustmentFrame.setVisible(true);
    }

    // 显示设置每小时可挂号人数对话框
    private void showAppointmentLimitSettingDialog() {
        JFrame limitSettingFrame = new JFrame("设置每小时可挂号人数");
        limitSettingFrame.setSize(300, 100);
        limitSettingFrame.setLocationRelativeTo(null);

        JPanel limitSettingPanel = new JPanel();
        limitSettingPanel.setLayout(new FlowLayout());

        JTextField limitField = new JTextField(10);
        JButton setLimitButton = new JButton("确认");

        setLimitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // 从文本字段中获取用户输入的值，并转换为整数
                    int limit = Integer.parseInt(limitField.getText());
                    // 调用 loggedInDoctor 的方法，将限制值设置为用户输入的值
                    if (limit <= 0)
                        JOptionPane.showMessageDialog(null, "请输入有效的整数值！", "错误", JOptionPane.ERROR_MESSAGE);
                    loggedInDoctor.setMaxAppointmentsPerHour(limit);
                } catch (NumberFormatException ex) {
                    // 如果用户输入的不是有效的整数，则在这里处理异常（例如显示错误消息）
                    JOptionPane.showMessageDialog(null, "请输入有效的整数值！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        limitSettingPanel.add(new JLabel("每小时可挂号人数:"));
        limitSettingPanel.add(limitField);
        limitSettingPanel.add(setLimitButton);

        limitSettingFrame.add(limitSettingPanel);
        limitSettingFrame.setVisible(true);
    }

    // 登录方法
    private Doctor login(int id, String password) {
        // 遍历医生列表
        for (Doctor doctor : appointmentSystem.getDoctors()) {
            // 检查医生ID和密码是否匹配
            if (doctor.getId() == id && doctor.getPassword().equals(password)) {
                return doctor; // 如果匹配，返回医生对象
            }
        }
        return null; // 如果未找到匹配的医生，返回null
    }

    // 生成唯一医生ID的方法
    private int generateUniqueDoctorId() {
        // 获取医生列表中最大的ID，如果列表为空，则返回0
        int maxId = appointmentSystem.getDoctors().stream().mapToInt(Doctor::getId).max().orElse(0);
        return maxId + 1; // 返回最大ID加1作为新医生的ID
    }

    // 加载预约数据的方法
    private void loadAppointmentData() {
        loadAppointmentData(null); // 调用重载方法，默认加载所有预约数据
    }

    private void loadAppointmentData(LocalDate date) {
        appointmentTableModel.setRowCount(0); // 清空表格模型的所有行
        List<Appointment> appointments = appointmentSystem.getAppointmentsByDoctor(loggedInDoctor); // 获取当前医生的所有预约信息
        if (date != null) { // 如果日期不为空
            // 根据日期过滤预约信息
            appointments = appointments.stream().filter(a -> a.getDate().equals(date)).collect(Collectors.toList());
        }
        for (Appointment appointment : appointments) { // 遍历过滤后的预约信息列表
            // 获取病人信息
            // 添加每个预约信息到表格模型中
            appointmentTableModel.addRow(new Object[] {
                    appointment.getDate(),
                    appointment.getTime(),
                    appointment.getPatient(), // 病人ID
                    appointment.getDepartment()
            });
        }
    }

    // 显示指定面板的方法
    private void showCard(JPanel panel, String cardName) {
        // 获取卡片布局
        CardLayout cl = (CardLayout) (panel.getLayout());
        // 显示指定名称的面板
        cl.show(panel, cardName);
    }

    // 保存数据到文件的方法
    private void saveData() {
        System.out.println("正在保存数据..."); // 输出保存数据的提示信息
        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生信息到JSON文件
        appointmentSystem.saveAppointmentsToJson(APPOINTMENTS_FILE_PATH); // 保存预约信息到JSON文件
        System.out.println("数据保存完毕"); // 输出保存完毕的提示信息
    }

    // 主方法
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DoctorClient client = new DoctorClient(); // 创建医生端客户端对象
                client.setVisible(true); // 设置客户端可见
            }
        });
    }
}
