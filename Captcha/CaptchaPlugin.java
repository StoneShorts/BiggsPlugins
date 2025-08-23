package net.runelite.client.live.inDevelopment.biggs.Captcha;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;

@PluginDescriptor(
        name = "RuneCaptcha",
        description = "Forces you to complete a silly captcha when certain actions are triggered.",
        tags = {"captcha", "troll", "bank", "popup", "biggs"}
)
public class CaptchaPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    private boolean captchaActive = false;
    private boolean wasBankOpen = false;
    private long lastCaptchaTime = 0;
    private static final long CAPTCHA_COOLDOWN_MS = 15 * 60 * 1000; // 15 minutes


    @Override
    protected void startUp() {
        captchaActive = false;
    }

    @Override
    protected void shutDown() {
        captchaActive = false;
        wasBankOpen = false;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        boolean isBankOpen = isBankInterfaceOpen();
        boolean isDepositBoxOpen = isBankDepositBoxInterfaceOpen();

        long now = System.currentTimeMillis();

        if (!wasBankOpen && isBankOpen && !captchaActive && now - lastCaptchaTime > CAPTCHA_COOLDOWN_MS) {
            showMiniGameCaptcha();
            lastCaptchaTime = now;
        }

        if (!wasBankOpen && isDepositBoxOpen && !captchaActive && now - lastCaptchaTime > CAPTCHA_COOLDOWN_MS) {
            showInvisibleMazeCaptcha();
            lastCaptchaTime = now;
        }


        wasBankOpen = isBankOpen;
    }

    private boolean isBankInterfaceOpen()
    {
        Widget bankWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        return bankWidget != null && !bankWidget.isHidden();
    }

    private boolean isBankDepositBoxInterfaceOpen()
    {
        Widget bankWidget = client.getWidget(12582913);
        return bankWidget != null && !bankWidget.isHidden();
    }

    private void triggerCaptcha()
    {
        captchaActive = true;

        SwingUtilities.invokeLater(() -> {
            JDialog captchaDialog = new JDialog((Frame) null, "RuneCaptcha Verification", true); // modal = true
            captchaDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            captchaDialog.setSize(400, 220);
            captchaDialog.setLayout(new BorderLayout());
            captchaDialog.setAlwaysOnTop(true);

            JPanel messagePanel = new JPanel(new GridLayout(2, 1));
            JLabel title = new JLabel("Bot Detection Captcha", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 18));

            JLabel instruction = new JLabel("Trying to use the bank? Please answer this question first!", SwingConstants.CENTER);
            instruction.setFont(new Font("Arial", Font.PLAIN, 14));

            messagePanel.add(title);
            messagePanel.add(instruction);

            JLabel prompt = new JLabel("What is 4 * 4 + 3?", SwingConstants.CENTER);
            JTextField answerField = new JTextField();
            JButton verify = new JButton("Verify");

            verify.addActionListener(e -> {
                String input = answerField.getText().trim();
                if (input.equals("19")) {
                    captchaDialog.dispose();
                    captchaActive = false;
                    JOptionPane.showMessageDialog(null, "✅ Captcha passed. You're free to go.");
                } else {
                    prompt.setText("Try again. Hint: It's under 20.");
                }
            });

            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.add(prompt, BorderLayout.NORTH);
            centerPanel.add(answerField, BorderLayout.CENTER);

            captchaDialog.add(messagePanel, BorderLayout.NORTH);
            captchaDialog.add(centerPanel, BorderLayout.CENTER);
            captchaDialog.add(verify, BorderLayout.SOUTH);
            captchaDialog.setVisible(true);
        });
    }
    private void showMiniGameCaptcha()
    {
        captchaActive = true;

        SwingUtilities.invokeLater(() -> {
            JDialog gameDialog = new JDialog((Frame) null, "Beat this to use the bank", true);
            gameDialog.setSize(400, 400);
            gameDialog.setLayout(new BorderLayout());
            gameDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            GamePanel panel = new GamePanel(gameDialog);
            gameDialog.add(panel, BorderLayout.CENTER);

            gameDialog.setVisible(true);
        });
    }

    class GamePanel extends JPanel {
        private Rectangle target = new Rectangle(50, 50, 50, 50);
        private Timer timer;
        private int timeLeft = 10;
        private final JDialog parent;

        public GamePanel(JDialog parent) {
            this.parent = parent;
            setFocusable(true);

            timer = new Timer(1000, e -> {
                timeLeft--;
                if (timeLeft <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "❌ You took too long!");
                    parent.dispose();
                }
                repaint();
            });

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    if (target.contains(evt.getPoint())) {
                        timer.stop();
                        JOptionPane.showMessageDialog(parent, "✅ Nice shot!");
                        parent.dispose();
                        captchaActive = false;
                    }
                }
            });

            //Randomize target every second
            new Timer(900, e -> {
                target.setLocation((int)(Math.random() * 300), (int)(Math.random() * 300));
                repaint();
            }).start();

            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLUE);
            g.fillRect(target.x, target.y, target.width, target.height);
            g.setColor(Color.BLACK);
            g.drawString("Click the blue box! Time left: " + timeLeft, 10, 20);
        }
    }
    private void showInvisibleMazeCaptcha() {
        captchaActive = true;

        SwingUtilities.invokeLater(() -> {
            JDialog mazeDialog = new JDialog((Frame) null, "[Jagex Captcha] Invisible Maze", true);
            mazeDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            mazeDialog.setSize(400, 400);
            mazeDialog.setResizable(false);
            mazeDialog.setLocationRelativeTo(null);

            mazeDialog.add(new InvisibleMazePanel(mazeDialog));
            mazeDialog.setVisible(true);
        });
    }

    class InvisibleMazePanel extends JPanel {
        private final Rectangle[] walls = {
                new Rectangle(50, 50, 300, 10),
                new Rectangle(50, 50, 10, 300),
                new Rectangle(340, 50, 10, 250),
                new Rectangle(50, 340, 300, 10),
                new Rectangle(100, 100, 200, 10),
                new Rectangle(100, 100, 10, 200),
                new Rectangle(100, 290, 150, 10),
                new Rectangle(240, 150, 10, 150),
                new Rectangle(150, 150, 90, 10)
        };

        private final Rectangle startZone = new Rectangle(60, 60, 30, 30);
        private final Rectangle endZone = new Rectangle(310, 310, 30, 30);
        private final JDialog parent;
        private boolean started = false;

        public InvisibleMazePanel(JDialog parent) {
            this.parent = parent;
            setLayout(null);
            setBackground(Color.WHITE);

            JLabel startLabel = new JLabel("Start");
            startLabel.setForeground(Color.GREEN);
            startLabel.setBounds(startZone);
            add(startLabel);

            JLabel endLabel = new JLabel("Finish");
            endLabel.setForeground(Color.RED);
            endLabel.setBounds(endZone);
            add(endLabel);

            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    Point p = e.getPoint();

                    if (!started && startZone.contains(p)) {
                        started = true;
                    }

                    if (started) {
                        for (Rectangle wall : walls) {
                            if (wall.contains(p)) {
                                JOptionPane.showMessageDialog(parent, "❌ You hit a wall. Try again.");
                                started = false;
                                return;
                            }
                        }

                        if (endZone.contains(p)) {
                            JOptionPane.showMessageDialog(parent, "✅ You escaped the maze!");
                            captchaActive = false;
                            parent.dispose();
                        }
                    }
                }
            });
        }
    }


}
