package server;

//import EmtiManager.EmtiManager;
import consts.cn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.prefs.Preferences;
import jdbc.daos.EventDAO;
import models.Consign.ConsignShopManager;
import network.SessionManager;
import network.server.EmtiSessionManager;

import services.ClanService;
import utils.Logger;

public class ServerManagerUI extends JFrame {

    private Preferences preferences;
    private JLabel ssCountLabel;
    private JLabel plCountLabel;
    private JLabel threadCountLabel;
    private JTextField minutesField;
    private JLabel messageLabel;
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private int remainingSeconds;
    private ButtonGroup maintenanceGroup;
// Thêm checkbox
    private JCheckBox maintenanceOption1;
    private JCheckBox maintenanceOption2;
    private JLabel info;

    public ServerManagerUI() {
        preferences = Preferences.userNodeForPackage(ServerManagerUI.class);
        setTitle("Chương trình Bảo trì Server" + cn.SV + "");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
        JPanel panel = new JPanel();
        getContentPane().add(panel);

//        JButton startButton = new JButton("Start Server");
//        startButton.addActionListener(e -> startServer());
//        panel.add(startButton);
        JButton maintenanceButton = new JButton("Bảo trì");
        maintenanceButton.addActionListener(e -> showMaintenanceDialog());
        panel.add(maintenanceButton);
//        JLabel jLabel = new JLabel("Cài đặt số phút bảo trì");
//        panel.add(jLabel);
//        minutesField = new JTextField(5);
//        panel.add(minutesField);
//
//        JButton scheduleButton = new JButton("Hẹn phút bảo trì");
//        scheduleButton.addActionListener(e -> scheduleMaintenance());
//        panel.add(scheduleButton);
        JLabel jLabel2 = new JLabel("Cài đặt giờ bảo trì");
        panel.add(jLabel2);
        info = new JLabel("");
        // Đọc giá trị từ tệp tin
        try ( BufferedReader reader = new BufferedReader(new FileReader("maintenanceConfig.txt"))) {
            String hoursLine = reader.readLine();
            String minutesLine = reader.readLine();
            String secondsLine = reader.readLine();

            int hours = Integer.parseInt(hoursLine);
            int minutes = Integer.parseInt(minutesLine);
            int seconds = Integer.parseInt(secondsLine);

            // Thêm giá trị vào DefaultComboBoxModel
            DefaultComboBoxModel<Integer> hoursModel = new DefaultComboBoxModel<>();
            for (int i = -1; i < 24; i++) {
                hoursModel.addElement(i);
            }
            JComboBox<Integer> hoursComboBox = new JComboBox<>(hoursModel);
            panel.add(hoursComboBox);
            hoursComboBox.setSelectedItem(hours);

            // Thêm giá trị vào DefaultComboBoxModel
            DefaultComboBoxModel<Integer> minutesModel = new DefaultComboBoxModel<>();
            for (int i = -1; i < 60; i++) {
                minutesModel.addElement(i);
            }
            JComboBox<Integer> minutesComboBox = new JComboBox<>(minutesModel);
            panel.add(minutesComboBox);
            minutesComboBox.setSelectedItem(minutes);

            // Thêm giá trị vào DefaultComboBoxModel
            DefaultComboBoxModel<Integer> secondsModel = new DefaultComboBoxModel<>();
            for (int i = -1; i < 60; i++) {
                secondsModel.addElement(i);
            }
            JComboBox<Integer> secondsComboBox = new JComboBox<>(secondsModel);
            panel.add(secondsComboBox);
            secondsComboBox.setSelectedItem(seconds);
            JButton scheduleButton2 = new JButton("Hẹn giờ bảo trì");
            scheduleButton2.addActionListener(e -> scheduleMaintenance(hoursComboBox, minutesComboBox, secondsComboBox));
            panel.add(scheduleButton2);
            if (hours != -1 && minutes != -1 && seconds != -1) {
                scheduleMaintenance(hoursComboBox, minutesComboBox, secondsComboBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JButton saveButton = new JButton("Lưu Data");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Logger.success("Đang tiến hành lưu data");
                network.server.EMTIServer.gI().stopConnect();

                Maintenance.isRunning = false;
                try {
                    Logger.error("Đang tiến hành lưu data bang hội");
                    ClanService.gI().close();
                    Thread.sleep(1000);
                    Logger.success("Lưu dữ liệu bang hội thành công");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.error("Lỗi lưu dữ liệu bang hội");
                }
                try {
                    Logger.error("Đang tiến hành lưu data ký gửi");
                    ConsignShopManager.gI().save();
                    Thread.sleep(1000);
                    Logger.success("Lưu dữ liệu ký gửi thành công");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.error("Lỗi lưu dữ liệu ký gửi");
                }

                try {
                    Logger.error("Đang tiến hành đẩy người chơi");
                    Client.gI().close();
                    EventDAO.save();
                    Thread.sleep(1000);
                    Logger.success("Lưu dữ liệu người dùng thành công");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Logger.error("Lỗi lưu dữ liệu người dùng");
                }
                System.exit(0);
            }
        });
        panel.add(saveButton);

//        
        JButton clearFw = new JButton("clearFw");
        clearFw.addActionListener((ActionEvent e) -> {
//            network.server.EMTIServer.firewall.clear();
//            network.server.EMTIServer.firewallDownDataGame.clear();
            JOptionPane.showMessageDialog(null, "Đã clear firewall");
        });
        panel.add(clearFw);
        // Thêm checkbox vào JPanel
        messageLabel = new JLabel();
        panel.add(messageLabel);

        countdownLabel = new JLabel();
        panel.add(countdownLabel);

        panel.add(info);
        threadCountLabel = new JLabel("Số Thread : ");
        panel.add(threadCountLabel);
        plCountLabel = new JLabel("Online :");
        panel.add(plCountLabel);
        ssCountLabel = new JLabel("Session :");
        panel.add(ssCountLabel);

        ScheduledExecutorService threadCountExecutor = Executors.newSingleThreadScheduledExecutor();
        threadCountExecutor.scheduleAtFixedRate(() -> {
            int threadCount = Thread.activeCount();
            threadCountLabel.setText("Số thread: " + threadCount);
        }, 1, 1, TimeUnit.SECONDS);

        ScheduledExecutorService plCountExecutor = Executors.newSingleThreadScheduledExecutor();
        plCountExecutor.scheduleAtFixedRate(() -> {
            int plcount = Client.gI().getPlayers().size();
            plCountLabel.setText("Online : " + plcount);
        }, 5, 1, TimeUnit.SECONDS);

        ScheduledExecutorService ssCountExecutor = Executors.newSingleThreadScheduledExecutor();
        ssCountExecutor.scheduleAtFixedRate(() -> {
            int sscount = SessionManager.gI().getSessions().size();
            ssCountLabel.setText("Session : " + sscount);
        }, 5, 1, TimeUnit.SECONDS);
        setVisible(true);
        messageLabel.setText("Server đang chạy");

        ServerManager.gI().run();
//        EmtiManager.getInstance().startAutoSave();
        // Đọc giá trị từ tệp
    }

    public void close(long delay) {
        network.server.EMTIServer.gI().stopConnect();

        Maintenance.isRunning = false;
        ServerManager.gI().close();
        System.exit(0);
        Logger.error("BEGIN MAINTENANCE...............................\n");

    }

    private void showMaintenanceDialog() {
        try {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(this, "Bắt đầu bảo trì?", "Bảo trì", dialogButton);
            if (dialogResult == 0) {
                Logger.error("Server tiến hành bảo trì");
                Maintenance.gI().start(15);

            } else {
                System.out.println("No Option");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void scheduleMaintenance() {
        String minutesStr = minutesField.getText();
        try {
            int minutes = Integer.parseInt(minutesStr);
            if (minutes <= 0) {
                messageLabel.setText("Số phút phải lớn hơn 0");
                return;
            }
            // Lưu giá trị vào tệp
            try {
                File file = new File("maintenanceTime.txt");
                FileWriter fw = new FileWriter(file);
                fw.write(String.valueOf(minutes));
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            long delay = minutes * 60L * 1000L;
            remainingSeconds = minutes * 60;
            countdownLabel.setText("Thời gian còn lại: " + formatTime(remainingSeconds));
            countdownTimer = new Timer(1000, e -> {
                remainingSeconds--;
                countdownLabel.setText("Thời gian còn lại: " + formatTime(remainingSeconds));
                if (remainingSeconds == 0) {
                    countdownTimer.stop();
                    Maintenance.gI().start(15);
                    messageLabel.setText("");
                    countdownLabel.setText("");
                }
            });
            countdownTimer.start();

            messageLabel.setText("Đã hẹn bảo trì sau " + minutes + " phút");
        } catch (NumberFormatException e) {

            String error = e.getMessage();
            if (error.equals("For input string: \"\"")) {
                JOptionPane.showMessageDialog(null, "Không được để trống");
            } else {
                JOptionPane.showMessageDialog(null, "Bạn nhập sai phút");
            }

        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private void confirmExit() {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn thoát chương trình?", "Thoát", dialogButton);
        if (dialogResult == 0) {
            System.exit(0);
        }
    }

    @Override
    public void setDefaultCloseOperation(int operation) {
        if (operation == JFrame.EXIT_ON_CLOSE) {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    confirmExit();
                }
            });
        } else {
            super.setDefaultCloseOperation(operation);
        }
    }

    private void scheduleMaintenance(JComboBox<Integer> hoursComboBox, JComboBox<Integer> minutesComboBox, JComboBox<Integer> secondsComboBox) {
        int hours = hoursComboBox.getItemAt(hoursComboBox.getSelectedIndex());
        int minutes = minutesComboBox.getItemAt(minutesComboBox.getSelectedIndex());
        int seconds = secondsComboBox.getItemAt(secondsComboBox.getSelectedIndex());
        if (minutes == -1 || hours == -1 || seconds == -1) {
//            JOptionPane.showMessageDialog(this, "Thời gian sai");
            JOptionPane.showMessageDialog(this, "Chạy sever không cần hẹn bảo trì ?");
            return;
        }
        // Ghi giá trị vào tệp tin
        try ( BufferedWriter writer = new BufferedWriter(new FileWriter("maintenanceConfig.txt"))) {
            writer.write(hours + "\n");
            writer.write(minutes + "\n");
            writer.write(seconds + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AtomicBoolean timeReached = new AtomicBoolean(false); // Sử dụng AtomicBoolean để đảm bảo tính nhất quán trong thread
        info.setText("Đã cài đặt quá trình bảo trì tự động" + " vào lúc " + hours + ":" + minutes + ":" + seconds);
        Thread.startVirtualThread(() -> {
            while (!timeReached.get()) { // Kiểm tra điều kiện dừng
                try {
                    LocalTime currentTime = LocalTime.now();
                    int hourss = hoursComboBox.getItemAt(hoursComboBox.getSelectedIndex());
                    int minutess = minutesComboBox.getItemAt(minutesComboBox.getSelectedIndex());
                    int secondss = secondsComboBox.getItemAt(secondsComboBox.getSelectedIndex());
                    int hour_now = currentTime.getHour();
                    int minute_now = currentTime.getMinute();
                    int seconds_now = currentTime.getSecond();

                    if (hourss == hour_now && minutess == minute_now && secondss == seconds_now) {
                        performMaintenance();
                        timeReached.set(true); // Gán giá trị true để dừng vòng lặp
                    }
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private long calculateDelay(int hours, int minutes, int seconds) {
        long currentMillis = System.currentTimeMillis();
        long scheduledMillis = currentMillis + (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000);
        return scheduledMillis - currentMillis;
    }

    private void performMaintenance() {
        Maintenance.gI().start(15);

    }
}
