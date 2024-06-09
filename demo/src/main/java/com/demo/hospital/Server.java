package com.demo.hospital;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {
    private static final String DOCTORS_FILE_PATH = "./src/main/java/com/demo/resources/doctors.json";
    private AppointmentSystem appointmentSystem;
    private JTable doctorTable;
    private DefaultTableModel doctorTableModel;
    private int currentPage = 1;
    private int pageSize = 10;

    public Server() {
        // 创建预约系统实例并加载医生数据
        appointmentSystem = new AppointmentSystem();
        appointmentSystem.loadDoctorsFromJson(DOCTORS_FILE_PATH);

        // 设置窗口标题、大小和关闭操作
        setTitle("医院管理员系统");
        setSize(800, 600);
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

    private void initUI() {
        // 创建主面板
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // 创建医生表格和表格模型
        doctorTableModel = new DefaultTableModel(new Object[] { "职工ID", "姓名", "科室" }, 0);
        doctorTable = new JTable(doctorTableModel);
        loadDoctorData(); // 加载医生数据到表格

        // 添加鼠标点击事件监听器
        doctorTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow();
                    int id = (currentPage - 1) * pageSize + row + 1; // 获取选中行的职工ID
                    Doctor doctor = appointmentSystem.findDoctorById(id);
                    if (doctor != null) {
                        showDoctorDetailDialog(doctor); // 显示医生详细信息对话框
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 添加分页按钮面板
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevButton = new JButton("上一页");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage--;
                if (currentPage < 1) {
                    currentPage = 1;
                }
                loadDoctorData();
            }
        });
        JButton nextButton = new JButton("下一页");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int maxPage = (int) Math.ceil((double) appointmentSystem.getDoctors().size() / pageSize);
                currentPage++;
                if (currentPage > maxPage) {
                    currentPage = maxPage;
                }
                loadDoctorData();
            }
        });
        // 添加增加医生按钮
        JButton addDoctorButton = new JButton("增加医生");
        addDoctorButton.setPreferredSize(new Dimension(150, 50)); // 设置按钮的大小
        addDoctorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 弹出添加医生对话框
                showAddDoctorDialog();
            }
        });
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);
        JPanel addDoctorPanel = new JPanel();
        addDoctorPanel.add(addDoctorButton);

        JPanel buttonContainerPanel = new JPanel(new BorderLayout());
        buttonContainerPanel.add(addDoctorPanel, BorderLayout.SOUTH);
        buttonContainerPanel.add(paginationPanel, BorderLayout.NORTH);

        panel.add(buttonContainerPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗口
        add(panel);
    }

    // 加载医生数据到表格
    private void loadDoctorData() {
        doctorTableModel.setRowCount(0); // 清空表格模型的所有行
        List<Doctor> doctors = appointmentSystem.getDoctors();
        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, doctors.size());
        for (int i = startIndex; i < endIndex; i++) {
            Doctor doctor = doctors.get(i);
            // 将每个医生的信息添加到表格模型中
            doctorTableModel.addRow(new Object[] { doctor.getId(), doctor.getName(), doctor.getDepartment() });
        }
    }

    private void showAddDoctorDialog() {
        // 创建对话框
        JDialog dialog = new JDialog(this, "添加医生", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        // 创建面板
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2, 10, 10)); // 七行两列的网格布局

        // 添加标签和文本框
        JLabel idLabel = new JLabel("职工ID:");
        JTextField idField = new JTextField();
        JLabel nameLabel = new JLabel("姓名:");
        JTextField nameField = new JTextField();
        JLabel passwordLabel = new JLabel("登录密码:");
        JPasswordField passwordField = new JPasswordField();
        JLabel departmentLabel = new JLabel("所属科室:");
        JTextField departmentField = new JTextField();
        JLabel maxAppointmentsLabel = new JLabel("每小时最大预约人数:");
        JTextField maxAppointmentsField = new JTextField();
        JLabel permissionLabel = new JLabel("权限:");
        JCheckBox permissionCheckBox = new JCheckBox();

        // 添加组件到面板，每个输入单独一行
        panel.add(idLabel);
        panel.add(idField);
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(departmentLabel);
        panel.add(departmentField);
        panel.add(maxAppointmentsLabel);
        panel.add(maxAppointmentsField);
        panel.add(permissionLabel);
        panel.add(permissionCheckBox);

        // 添加确认按钮
        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 获取输入的医生信息
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                String password = new String(passwordField.getPassword());
                String department = departmentField.getText();
                int maxAppointments = Integer.parseInt(maxAppointmentsField.getText());
                boolean permission = permissionCheckBox.isSelected();

                // 检查是否存在重复的职工 ID
                if (appointmentSystem.findDoctorById(id) != null) {
                    JOptionPane.showMessageDialog(dialog, "已存在相同职工 ID 的医生，请输入其他职工 ID！", "错误",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 创建医生对象
                Doctor doctor = new Doctor(id, name, password, department, new ArrayList<>(), maxAppointments,
                        permission);
                // 添加医生到预约系统中
                appointmentSystem.addDoctor(doctor);
                // 保存数据到文件中
                saveData();
                // 更新表格数据
                loadDoctorData();
                // 关闭对话框
                dialog.dispose();
                // 提示添加成功
                JOptionPane.showMessageDialog(null, "医生添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 添加取消按钮
        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // 关闭对话框
            }
        });

        // 添加组件到面板
        panel.add(confirmButton);
        panel.add(cancelButton);

        // 将面板添加到对话框
        dialog.add(panel);

        // 显示对话框
        dialog.setVisible(true);
    }

    // 显示医生详细信息对话框
    private void showDoctorDetailDialog(Doctor doctor) {
        // 创建对话框
        JDialog dialog = new JDialog(this, "医生详细信息", true);
        dialog.setSize(900, 650);
        dialog.setLocationRelativeTo(this);

        // 创建面板
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // 使用垂直布局

        // 第一行的属性
        JLabel idLabel = new JLabel("职工ID: " + doctor.getId());
        JLabel nameLabel = new JLabel("姓名: " + doctor.getName());
        JLabel departmentLabel = new JLabel("科室: " + doctor.getDepartment());

        // 第二行的属性
        JLabel permissionLabel = new JLabel("权限: " + (doctor.getpermission() ? "是" : "否"));
        JLabel passwordLabel = new JLabel("密码: " + doctor.getPassword());
        JLabel maxAppointmentsLabel = new JLabel("每小时最大挂号数: " + doctor.getMaxAppointmentsPerHour());

        // 设置边框
        Border border = BorderFactory.createLineBorder(Color.BLACK); // 黑色边框
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10))); // 设置边框和内边距

        // 第一行
        JPanel firstRowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        firstRowPanel.add(idLabel);
        firstRowPanel.add(nameLabel);
        firstRowPanel.add(departmentLabel);

        // 第二行
        JPanel secondRowPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        secondRowPanel.add(permissionLabel);
        secondRowPanel.add(passwordLabel);
        secondRowPanel.add(maxAppointmentsLabel);

        panel.add(firstRowPanel);
        panel.add(secondRowPanel);

        // 添加工作日期信息到面板
        JPanel workDaysPanel = new JPanel(new BorderLayout());
        workDaysPanel.setBorder(BorderFactory.createTitledBorder("工作日期"));
        JTextArea workDaysTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(workDaysTextArea);
        for (LocalDate workDay : doctor.getWorkDays()) {
            workDaysTextArea.append("- " + workDay + "\n");
        }
        workDaysPanel.add(scrollPane, BorderLayout.CENTER);
        panel.add(workDaysPanel);

        // 添加按钮面板，使用 FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 添加修改权限按钮
        JButton modifyPermissionButton = new JButton("修改权限");
        modifyPermissionButton.setPreferredSize(new Dimension(120, 50)); // 设置按钮的大小
        modifyPermissionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 弹出对话框
                int option = JOptionPane.showConfirmDialog(null, "是否授予权限？", "修改权限", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    // 用户点击了“是”按钮，执行授予权限操作
                    doctor.setpermission(true);
                    JOptionPane.showMessageDialog(null, "权限修改成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // 用户点击了“否”按钮，取消授予权限
                    doctor.setpermission(false);
                    JOptionPane.showMessageDialog(null, "已取消修改权限", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        buttonPanel.add(modifyPermissionButton);

        // 添加修改密码按钮
        JButton modifyPasswordButton = new JButton("修改密码");
        modifyPasswordButton.setPreferredSize(new Dimension(120, 50)); // 设置按钮的大小
        modifyPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 创建密码修改对话框
                JPanel panel = new JPanel();
                panel.setLayout(new GridLayout(3, 2, 5, 5));

                // 添加密码输入框和标签
                JLabel newPasswordLabel = new JLabel("请输入新密码:");
                JPasswordField newPasswordField = new JPasswordField();
                JLabel retypePasswordLabel = new JLabel("请重新输入新密码:");
                JPasswordField retypePasswordField = new JPasswordField();

                // 添加组件到面板
                panel.add(newPasswordLabel);
                panel.add(newPasswordField);
                panel.add(retypePasswordLabel);
                panel.add(retypePasswordField);

                int result = JOptionPane.showConfirmDialog(null, panel, "修改密码", JOptionPane.OK_CANCEL_OPTION);

                // 确认按钮按下
                if (result == JOptionPane.OK_OPTION) {
                    // 获取输入的密码
                    String newPassword = new String(newPasswordField.getPassword());
                    String retypePassword = new String(retypePasswordField.getPassword());

                    // 判断是否为空
                    if (newPassword.isEmpty() || retypePassword.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "输入不可为空！", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // 判断两次输入是否一致
                    if (!newPassword.equals(retypePassword)) {
                        JOptionPane.showMessageDialog(null, "两次输入不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // 修改密码
                    doctor.setpassword(retypePassword);

                    // 弹出修改成功对话框
                    JOptionPane.showMessageDialog(null, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        buttonPanel.add(modifyPasswordButton);

        // 添加修改每小时最大挂号数按钮
        JButton modifyMaxAppointmentsButton = new JButton("修改每小时最大挂号数");
        modifyMaxAppointmentsButton.setPreferredSize(new Dimension(160, 50)); // 设置按钮的大小
        modifyMaxAppointmentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 创建输入对话框
                String inputValue = JOptionPane.showInputDialog("请输入新的每小时最大挂号数：");

                // 如果点击了取消按钮或者输入为空，则不执行任何操作
                if (inputValue == null) {
                    return;
                }
                // 如果输入为空，则弹出错误提示
                if (inputValue.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "输入不可为空！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 将输入的字符串转换为整数
                int newValue;
                try {
                    newValue = Integer.parseInt(inputValue);
                } catch (NumberFormatException ex) {
                    // 如果无法转换为整数，则弹出错误提示
                    JOptionPane.showMessageDialog(null, "输入无效，请输入一个有效的整数！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 检查输入是否小于等于0
                if (newValue <= 0) {
                    JOptionPane.showMessageDialog(null, "输入不可小于等于0！", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                doctor.setMaxAppointmentsPerHour(newValue);
                // 这里添加修改每小时最大挂号数的逻辑，例如更新数据库中的值

                // 弹出修改成功对话框
                JOptionPane.showMessageDialog(null, "每小时最大挂号数修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        buttonPanel.add(modifyMaxAppointmentsButton);

        // 添加添加工作日期按钮
        JButton addWorkDateButton = new JButton("添加工作日期");
        addWorkDateButton.setPreferredSize(new Dimension(120, 50)); // 设置按钮的大小

        addWorkDateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 弹出输入对话框要求用户输入日期
                String dateString = JOptionPane.showInputDialog(null, "请输入要添加的日期（yyyy,MM,dd）：");
                // 检查用户输入是否有效
                if (dateString != null && dateString.matches("\\d{4},\\d{2},\\d{2}")) {
                    // 解析用户输入的日期字符串
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM,dd");
                    LocalDate date = LocalDate.parse(dateString, formatter);
                    // 执行添加工作日期的操作逻辑，可以在这里调用相关方法实现具体逻辑
                    doctor.addWorkDay(date);
                } else {
                    // 用户输入无效，弹出错误提示
                    JOptionPane.showMessageDialog(null, "请输入有效的日期格式（yyyy,MM,dd）！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(addWorkDateButton);

        // 添加删除工作日期按钮
        JButton deleteWorkDateButton = new JButton("删除工作日期");
        deleteWorkDateButton.setPreferredSize(new Dimension(120, 50)); // 设置按钮的大小
        deleteWorkDateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 弹出输入对话框要求用户输入要删除的日期
                String dateString = JOptionPane.showInputDialog(null, "请输入要删除的日期（yyyy,MM,dd）：");
                // 检查用户输入是否有效
                if (dateString != null && dateString.matches("\\d{4},\\d{2},\\d{2}")) {
                    // 解析用户输入的日期字符串
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy,MM,dd");
                    LocalDate date = LocalDate.parse(dateString, formatter);
                    // 执行删除工作日期的操作逻辑，可以在这里调用相关方法实现具体逻辑
                    doctor.removeWorkDay(date);
                } else {
                    // 用户输入无效，弹出错误提示
                    JOptionPane.showMessageDialog(null, "请输入有效的日期格式（yyyy,MM,dd）！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        buttonPanel.add(deleteWorkDateButton);

        // 添加删除该医生所有信息按钮
        JButton deleteButton = new JButton("注销该医生");
        deleteButton.setPreferredSize(new Dimension(120, 50)); // 设置按钮的大小
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 确认用户是否要删除医生信息
                int option = JOptionPane.showConfirmDialog(null, "确定要删除该医生吗？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    // 执行删除医生操作
                    appointmentSystem.removeDoctor(doctor);

                    // 保存数据到文件中
                    saveData();

                    // 更新表格数据
                    loadDoctorData();

                    // 提示删除成功
                    JOptionPane.showMessageDialog(null, "医生删除成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        buttonPanel.add(deleteButton);

        panel.add(buttonPanel);

        // 将面板添加到对话框
        dialog.add(panel);

        // 显示对话框
        dialog.setVisible(true);

    }

    // 保存数据到文件的方法
    private void saveData() {
        appointmentSystem.saveDoctorsToJson(DOCTORS_FILE_PATH); // 保存医生数据到JSON文件
    }

    public static void main(String[] args) {
        // 在事件分派线程中创建并显示服务器窗口
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Server server = new Server();
                server.setVisible(true);
            }
        });
    }
}
