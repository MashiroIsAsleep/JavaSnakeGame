import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 1000;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static int DELAY = 175;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean paused = false;
    boolean directionChanged = false; // Flag to prevent 180-degree turn
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        initializeGame();
    }

    public void initializeGame() {
        spawnApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics Graphics) {
        super.paintComponent(Graphics);
        render(Graphics);
    }

    public void render(Graphics Graphics) {
        if (running) {
            Graphics.setColor(Color.red);
            Graphics.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // Changed to fillRect for square apple

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    Graphics.setColor(Color.green);
                    Graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    Graphics.setColor(new Color(45, 180, 0));
                    Graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            Graphics.setColor(Color.red);
            Graphics.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(Graphics.getFont());
            Graphics.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, Graphics.getFont().getSize());
        } else {
            displayGameOver(Graphics);
        }
    }

    public void spawnApple() {
        boolean validPosition;
        do {
            validPosition = true;
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

            // Check if apple spawns on snake body
            for (int i = 0; i < bodyParts; i++) {
                if ((x[i] == appleX) && (y[i] == appleY)) {
                    validPosition = false;
                    break;
                }
            }
        } while (!validPosition);
    }

    public void moveSnake() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] -= UNIT_SIZE;
                break;
            case 'D':
                y[0] += UNIT_SIZE;
                break;
            case 'L':
                x[0] -= UNIT_SIZE;
                break;
            case 'R':
                x[0] += UNIT_SIZE;
                break;
        }
        directionChanged = false; // Reset the flag after moving
    }

    public void checkAppleCollision() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            spawnApple();
            speedUp();
            swapHeadAndTail();
        }
    }

    public void swapHeadAndTail() {
        int[] tempX = new int[bodyParts];
        int[] tempY = new int[bodyParts];

        for (int i = 0; i < bodyParts; i++) {
            tempX[i] = x[i];
            tempY[i] = y[i];
        }

        for (int i = 0; i < bodyParts; i++) {
            x[i] = tempX[bodyParts - 1 - i];
            y[i] = tempY[bodyParts - 1 - i];
        }

        // Adjust the direction based on new head position
        if (x[0] < x[1]) {
            direction = 'L';
        } else if (x[0] > x[1]) {
            direction = 'R';
        } else if (y[0] < y[1]) {
            direction = 'U';
        } else if (y[0] > y[1]) {
            direction = 'D';
        }
    }

    public void speedUp() {
        if (DELAY > 50) {
            DELAY -= 5;
            timer.setDelay(DELAY);
        }
    }

    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        // Check if head touches borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void displayGameOver(Graphics Graphics) {
        // Display score
        Graphics.setColor(Color.red);
        Graphics.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(Graphics.getFont());
        Graphics.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, Graphics.getFont().getSize());
        // Display Game Over text
        Graphics.setColor(Color.red);
        Graphics.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(Graphics.getFont());
        Graphics.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            moveSnake();
            checkAppleCollision();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!directionChanged) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                            directionChanged = true;
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                            directionChanged = true;
                        }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                            directionChanged = true;
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (direction != 'U') {
                            direction = 'D';
                            directionChanged = true;
                        }
                        break;
                    case KeyEvent.VK_SPACE:
                        paused = !paused;
                        break;
                }
            }
        }
    }
}
