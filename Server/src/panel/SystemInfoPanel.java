package panel;

import EMTI.SystemMetrics;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import network.SessionManager;
import server.Client;
import server.ServerManager;

public class SystemInfoPanel extends GlowPanel {
    private JTextArea infoArea;
    private Timer updateTimer;
    
    public SystemInfoPanel() {
        setLayout(new BorderLayout());
        
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setForeground(Color.GREEN);
        infoArea.setBackground(new Color(0, 0, 0, 150)); // nền đen mờ
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Tăng font size
        infoArea.setLineWrap(true); // Cho phép xuống dòng tự động
        infoArea.setWrapStyleWord(true); // Xuống dòng theo từ
        
        // Bỏ thanh cuộn (scrollbar)
        add(infoArea, BorderLayout.CENTER);
        
        // Cập nhật thông tin mỗi giây
        updateTimer = new Timer(1000, e -> updateSystemInfo());
        updateTimer.start();
        
        // Hiển thị thông tin ban đầu
        updateSystemInfo();
    }
    
    public void updateSystemInfo() {
        // Sử dụng thông tin từ dòng bạn cung cấp
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String currentTime = sdf.format(new Date());
        
        StringBuilder info = new StringBuilder();
        info.append("⏱️ Time: ").append(currentTime).append("\n");
        info.append("🕒 Uptime: ").append(ServerManager.timeStart).append("\n");
        info.append("👥 Clients: ").append(Client.gI().getPlayers().size()).append(" người chơi\n");
        info.append("🔗 Sessions: ").append(SessionManager.gI().getNumSession()).append("\n");
        info.append("⚙️ Threads: ").append(Thread.activeCount()).append(" luồng\n");
        info.append("🖥️ ").append(SystemMetrics.ToString());
        
        infoArea.setText(info.toString());
    }
}