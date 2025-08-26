package panel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;

public class LoggerUI extends JFrame {

    private static LoggerUI instance;
    private JTextPane logPane;
    private StyledDocument doc;
    private BufferedImage originalBg;
    private JLabel backgroundLabel;

    public static LoggerUI gI() {
        if (instance == null) {
            instance = new LoggerUI();
        }
        return instance;
    }

    private LoggerUI() {
        setTitle("NTD Game Server Log");
        setSize(600, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load background
        String imagePath = "data/cr.jpg";
        File imgFile = new File(imagePath);

        if (!imgFile.exists()) {
            JOptionPane.showMessageDialog(null,
                    "Không tìm thấy ảnh nền tại đường dẫn:\n" + imagePath,
                    "Lỗi Ảnh Nền", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try {
            originalBg = ImageIO.read(imgFile);
            if (originalBg == null)
                throw new IOException("ImageIO trả về null.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Lỗi khi đọc ảnh nền:\n" + imagePath + "\nChi tiết: " + e.getMessage(),
                    "Lỗi Ảnh Nền", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setOpaque(false);

        // Log Pane
        logPane = new JTextPane();
        logPane.setOpaque(false);
        logPane.setEditable(false);
        logPane.setFont(new Font("Monospaced", Font.BOLD, 14));
        doc = logPane.getStyledDocument();

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        overlay.add(scrollPane, BorderLayout.CENTER);

        // Control buttons
        JPanel controls = new JPanel();
        controls.setOpaque(false);

        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener((ActionEvent e) -> logPane.setText(""));

        JButton copyBtn = new JButton("Copy");
        copyBtn.addActionListener((ActionEvent e) -> {
            StringSelection selection = new StringSelection(logPane.getText());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
        });

        controls.add(clearBtn);
        controls.add(copyBtn);
        overlay.add(controls, BorderLayout.SOUTH);

        backgroundLabel.add(overlay, BorderLayout.CENTER);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                updateBackground();
            }
        });

        updateBackground();
        redirectSystemOut();
        setVisible(true);
    }

    private void updateBackground() {
        int width = getWidth();
        int height = getHeight();
        if (originalBg != null) {
            Image scaled = originalBg.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaled));
        }
    }

    private void appendAnsiColoredText(String text) {
        String[] parts = text.split("(\u001B\\[[;\\d]*m)");
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\u001B\\[[;\\d]*m").matcher(text);

        int i = 0;
        Color currentColor = Color.WHITE;

        while (matcher.find()) {
            String code = matcher.group();
            appendText(parts[i++], currentColor);
            currentColor = getColorFromAnsi(code);
        }

        if (i < parts.length) appendText(parts[i], currentColor);
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
            case "\u001B[0m": default: return Color.WHITE;
        }
    }

    private void redirectSystemOut() {
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
        });
        System.setOut(ps);
        System.setErr(ps);
    }
}
