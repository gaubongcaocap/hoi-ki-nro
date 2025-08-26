package panel;

import javax.swing.*;
import java.awt.*;

public class GlowPanel extends JPanel {
    public GlowPanel() {
        setOpaque(false); // để thấy nền sau
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        int glowSize = 8;
        Color glowColor = new Color(0, 255, 255, 100); // xanh nhạt trong suốt

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = glowSize; i >= 1; i--) {
            g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 25));
            g2.setStroke(new BasicStroke(i * 2));
            g2.drawRoundRect(i, i, getWidth() - i * 2 - 1, getHeight() - i * 2 - 1, 20, 20);
        }

        g2.setColor(new Color(0, 255, 255));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(glowSize, glowSize, getWidth() - glowSize * 2 - 1, getHeight() - glowSize * 2 - 1, 20, 20);

        g2.dispose();
    }
}
