import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Random;


public class UniversalAntiAFK {
    private JFrame frame;
    private JButton startButton;
    private JButton stopButton;
    private DefaultListModel<String> logListModel;
    private JList<String> logList;
    private JScrollPane logScrollPane;
    private Thread afkThread;

    public UniversalAntiAFK() {
        frame = new JFrame("Universal Anti-AFK");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        startButton = createStyledButton("Start");
        stopButton = createStyledButton("Stop");
        stopButton.setEnabled(false);
        stopButton.setBackground(new Color(150, 150, 150));

        logListModel = new DefaultListModel<>();
        logList = new JList<>(logListModel);
        logList.setFont(new Font("Arial", Font.PLAIN, 12));
        logList.setBackground(Color.DARK_GRAY); // Set background color
        logList.setForeground(Color.WHITE); // Set text color
        logScrollPane = new JScrollPane(logList);
        logScrollPane.setPreferredSize(new Dimension(380, 180));
        logScrollPane.setBackground(Color.DARK_GRAY); // Set background color

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                startButton.setBackground(new Color(150, 150, 150));
                stopButton.setEnabled(true);
                stopButton.setBackground(new Color(50, 50, 50));
                if (afkThread == null || !afkThread.isAlive()) {
                    afkThread = new Thread(new AntiAFKRunnable());
                    afkThread.start();
                }
            }
        });

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopButton.setEnabled(false);
                stopButton.setBackground(new Color(150, 150, 150));
                startButton.setEnabled(true);
                startButton.setBackground(new Color(50, 50, 50));
                if (afkThread != null && afkThread.isAlive()) {
                    afkThread.interrupt();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.add(startButton);
        panel.add(stopButton);

        JPanel logPanel = new JPanel();
        logPanel.setBackground(Color.DARK_GRAY);
        logPanel.add(logScrollPane);

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add(logPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50)); // Set background color
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UniversalAntiAFK();
            }
        });
    }

    private class AntiAFKRunnable implements Runnable {
        private Random random = new Random();
        private Robot robot;

        public AntiAFKRunnable() {
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                logMessage("Anti-AFK started.");
                while (!Thread.interrupted()) {
                    int randomInterval = random.nextInt(9000) + 1000;
                    Thread.sleep(randomInterval);

                    int[] keys = {KeyEvent.VK_W, KeyEvent.VK_A, KeyEvent.VK_S, KeyEvent.VK_D, KeyEvent.VK_CONTROL};
                    int randomKeyCode = keys[random.nextInt(keys.length)];

                    int pressDuration = random.nextInt(150) + 50;

                    robot.keyPress(randomKeyCode);
                    logMessage("Pressed random key: " + KeyEvent.getKeyText(randomKeyCode));

                    Thread.sleep(pressDuration);

                    robot.keyRelease(randomKeyCode);
                }
            } catch (InterruptedException e) {
                logMessage("Anti-AFK stopped.");
            }
        }

        private void logMessage(String message) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    logListModel.addElement(message);

                    int lastIndex = logListModel.getSize() - 1;
                    if (lastIndex >= 0) {
                        logList.ensureIndexIsVisible(lastIndex);
                    }
                }
            });
        }
    }
}
