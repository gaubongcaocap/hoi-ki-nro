package panel;

import javax.swing.*;
import java.awt.*;

public class UIUtils {

    public static ImageIcon getScaledIcon(String path, int maxWidth, int maxHeight) {
        ImageIcon originalIcon = new ImageIcon(path);
        int width = originalIcon.getIconWidth();
        int height = originalIcon.getIconHeight();

        // Tránh crash nếu ảnh lỗi
        if (width <= 0 || height <= 0) return new ImageIcon();

        double scale = Math.min((double) maxWidth / width, (double) maxHeight / height);
        int newW = (int) (width * scale);
        int newH = (int) (height * scale);

        Image scaledImg = originalIcon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    public static JButton createFancyButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(new Color(70, 130, 180));
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(60, 60, 60));
                } else {
                    g2.setColor(new Color(40, 40, 40));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0, 255, 180));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
