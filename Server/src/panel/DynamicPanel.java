package panel;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.*;

public class DynamicPanel extends GlowPanel {

    private JPanel currentPanel;
    private JPanel logWrapperPanel;
    private JTextPane logPane;
    private StyledDocument doc;

    public DynamicPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initLogPanel();
        currentPanel = logWrapperPanel;
        add(currentPanel, BorderLayout.CENTER);
    }

    private void initLogPanel() {
        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setOpaque(false);
        logPane.setFont(new Font("Monospaced", Font.PLAIN, 18));
        logPane.setForeground(Color.WHITE);
        doc = logPane.getStyledDocument();

        JScrollPane scroll = new JScrollPane(logPane);
        scroll.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        logWrapperPanel = new JPanel(new BorderLayout());
        logWrapperPanel.setOpaque(false);
        logWrapperPanel.add(scroll, BorderLayout.CENTER);
    }

    public void resetToLogPanel() {
        setContent(logWrapperPanel);
    }

    public void setContent(JPanel newPanel) {
        slideLeftTransition(newPanel);
    }

    public void appendLog(String rawText) {
        SwingUtilities.invokeLater(() -> appendAnsiColoredText(rawText));
    }

    public void redirectSystemOutToPanel() {
        try {
            PrintStream ps = new PrintStream(new OutputStream() {
                @Override
                public void write(byte[] b, int off, int len) {
                    String raw = new String(b, off, len);
                    SwingUtilities.invokeLater(() -> appendAnsiColoredText(raw));
                }

                @Override
                public void write(int b) {
                    SwingUtilities.invokeLater(() -> appendAnsiColoredText(String.valueOf((char) b)));
                }
            }, true, "UTF-8");

            System.setOut(ps);
            System.setErr(ps);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void appendAnsiColoredText(String text) {
        String[] parts = text.split("(\u001B\\[[;\\d]*m)");
        Matcher matcher = Pattern.compile("\u001B\\[[;\\d]*m").matcher(text);

        if (!matcher.find()) {
            appendText(text + "\n", Color.LIGHT_GRAY);
            return;
        }

        matcher.reset();
        int i = 0;
        Color currentColor = Color.RED;

        while (matcher.find()) {
            String code = matcher.group();
            appendText(parts[i++], currentColor);
            currentColor = getColorFromAnsi(code);
        }

        if (i < parts.length) {
            appendText(parts[i] + "\n", currentColor);
        }
    }

    private void appendText(String text, Color color) {
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);
        try {
            doc.insertString(doc.getLength(), text, attrs);
            logPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private Color getColorFromAnsi(String ansi) {
        switch (ansi) {
            case "\u001B[0;30m": return Color.BLACK;
            case "\u001B[0;31m": return Color.RED;
            case "\u001B[0;32m": return Color.GREEN;
            case "\u001B[0;33m": return Color.YELLOW;
            case "\u001B[0;34m": return Color.BLUE;
            case "\u001B[0;35m": return Color.MAGENTA;
            case "\u001B[0;36m": return Color.CYAN;
            case "\u001B[0;37m": return Color.LIGHT_GRAY;
            case "\u001B[1;31m": return Color.PINK;
            case "\u001B[1;33m": return Color.ORANGE;
            default: return Color.LIGHT_GRAY;
        }
    }

    private void slideLeftTransition(JPanel newPanel) {
        final int duration = 250; // faster than before
        final int fps = 60;
        final int steps = duration * fps / 1000;
        final int panelWidth = getWidth();

        JPanel oldPanel = currentPanel;
        currentPanel = newPanel;

        oldPanel.setBounds(0, 0, panelWidth, getHeight());
        newPanel.setBounds(panelWidth, 0, panelWidth, getHeight());

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setBounds(0, 0, getWidth(), getHeight());
        layeredPane.add(oldPanel, Integer.valueOf(0));
        layeredPane.add(newPanel, Integer.valueOf(1));

        remove(oldPanel);
        add(layeredPane, BorderLayout.CENTER);
        revalidate();
        repaint();

        Timer timer = new Timer(1000 / fps, null);
        final int[] step = {0};

        timer.addActionListener(e -> {
            float progress = (float) step[0] / steps;
            int dx = (int) (-progress * panelWidth);

            oldPanel.setLocation(dx, 0);
            newPanel.setLocation(dx + panelWidth, 0);

            step[0]++;
            if (step[0] >= steps) {
                timer.stop();
                layeredPane.removeAll();
                remove(layeredPane);
                add(newPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        });

        timer.start();
    }
}
