package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimatedPanel extends JPanel {

    private float alpha = 1f; // độ trong suốt (1 = hiện đầy đủ, 0 = mờ hoàn toàn)

    public AnimatedPanel(JPanel newContent) {
        setLayout(new BorderLayout());
        add(newContent, BorderLayout.CENTER);
        setOpaque(false);
    }

    public void playFadeIn(Runnable onFinish) {
        Timer timer = new Timer(15, null);
        alpha = 0f;

        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    timer.stop();
                    if (onFinish != null) onFinish.run();
                }
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (alpha < 1f) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2);
            g2.dispose();
        } else {
            super.paintComponent(g);
        }
    }
}
