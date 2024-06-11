//import awt, swing, and random
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

//Create GamePanel class with JPanel and Listeners
public class GamePanel extends JPanel implements ActionListener {

    //Set the size of the window.
    static final int SCREEN_WIDTH = 1000;
    static final int SCREEN_HEIGHT = 750;

    //Set the size of each game unit/pixel
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);

    //Set the time between each render/snake movement
    static int DELAY = 175;

    //Represent every game unit with x and y coordinates using arrays
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    
    //Snake initial length
    int bodyParts = 6;

    //Score
    int applesEaten;

    //Coordinates for apple
    int appleX;
    int appleY;

    //Set snake initial direction
    char direction = 'R';

    //Game initialization variables
    boolean running = false;
    boolean paused = false;

    // Flag to prevent 180-degree turn
    boolean directionChanged = false; 

    Timer timer;
    Random random;

    GamePanel() {
        //Sets the random function used for apple spawning
        random = new Random();

        //Game panel settings
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        //Allow window to recieve key input
        this.setFocusable(true);
        //Key listener
        this.addKeyListener(new MyKeyAdapter());
        initializeGame();
    }

    public void initializeGame() {
        spawnApple();
        //Start to run game
        running = true;
        //Initialize and start timer to render game
        timer = new Timer(DELAY, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics Graphics) {
        //Call paintComponent method in parent class
        super.paintComponent(Graphics);
        //Render
        render(Graphics);
    }

    public void render(Graphics Graphics) {
        //If the game is on (snake alive)
        if (running) {
            //Apple is red
            Graphics.setColor(Color.red);
            //Fill the corresponding square resembling apple
            Graphics.fillRect(appleX, appleY, UNIT_SIZE, UNIT_SIZE); 
            
            //Loop to illustrate entire snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    //Color snake's head
                    Graphics.setColor(Color.green);
                    Graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    //Color snake's body
                    Graphics.setColor(new Color(45, 180, 0));
                    Graphics.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            //Sets the score displayer
            Graphics.setColor(Color.red);
            //Set font and size
            Graphics.setFont(new Font("Serif", Font.BOLD, 40));
            //Recieves the current font
            FontMetrics metrics = getFontMetrics(Graphics.getFont());
            //Calculate string length and draw the string
            Graphics.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, Graphics.getFont().getSize());
        } else {
            //If game is not running/snake dead, display over screen
            displayGameOver(Graphics);
        }
    }

    //Spawn apple method
    public void spawnApple() {
        //Flag for valid position checking
        boolean validPosition;
        //Post conditional loop
        do {
            validPosition = true;
            //Randomly generate apple coordinates
            appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;

            // Check if apple spawns on snake body
            for (int i = 0; i < bodyParts; i++) {
                if ((x[i] == appleX) && (y[i] == appleY)) {
                    //If on snake body, change the flag to loop again
                    validPosition = false;
                    break;
                }
            }
        } while (!validPosition);
    }

    //Snake movement method
    public void moveSnake() {
        //Proceeding body part goes to preceeding body part
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        
        //Use the current direction to decide where should the head go
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
        //Flag used later to make sure no 2 consecutive turns in a single render cycle (cause suicide)
        directionChanged = false; // Reset the flag after moving
    }   

    //Method checking if apple is eaten or not
    public void checkAppleCollision() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            //Elongates body and adds score
            bodyParts++;
            applesEaten++;
            //spawn new apple
            spawnApple();
            //speeds up snake movement
            speedUp();
            //swaps snake head and tail
            swapHeadAndTail();
        }
    }

    public void swapHeadAndTail() {
        //two temp arrays to store snake body parts' x and y coordinate to swicth
        int[] tempX = new int[bodyParts];
        int[] tempY = new int[bodyParts];

        //Assign the current positions to temp
        for (int i = 0; i < bodyParts; i++) {
            tempX[i] = x[i];
            tempY[i] = y[i];
        }
        
        //Use the temp to reverse the order of each body part so that head is tail and tail is head
        for (int i = 0; i < bodyParts; i++) {
            x[i] = tempX[bodyParts - 1 - i];
            y[i] = tempY[bodyParts - 1 - i];
        }

        //Adjust the direction based on new head position
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

    //Speeds up snake movement
    public void speedUp() {
        //Decrement the delay until a max
        if (DELAY > 50) {
            DELAY -= 5;
            timer.setDelay(DELAY);
        }
    }

    public void checkCollisions() {
        //Check if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //Check if head touches borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
        
        //Stops the timer when game ends
        if (!running) {
            timer.stop();
        }
    }

    //Game over screen
    public void displayGameOver(Graphics Graphics) {
        //Display score
        //Set word color
        Graphics.setColor(Color.red);
        //Set word font and size
        Graphics.setFont(new Font("Serif", Font.BOLD, 40));
        //Gets the font used
        FontMetrics metrics1 = getFontMetrics(Graphics.getFont());
        //Draws the string at correct position 
        Graphics.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, Graphics.getFont().getSize());
        //Display Game Over text
        Graphics.setColor(Color.red);
        //sets the font and size
        Graphics.setFont(new Font("Serif", Font.BOLD, 75));
        //Gets the font used
        FontMetrics metrics2 = getFontMetrics(Graphics.getFont());
        //Draws the string at correct position 
        Graphics.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //If the game is running
        if (running && !paused) {
            //Snake moves
            moveSnake();
            //Check if apple eaten
            checkAppleCollision();
            //Check if snake dies
            checkCollisions();
        }
        //Rerender
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        //Key listeners, checks for keys pressed
        public void keyPressed(KeyEvent e) {
            //If in this render period no direction change had been performed (prevent suicide)
            if (!directionChanged) {
                switch (e.getKeyCode()) {
                    //Case for each arrow keys, sets the direction to the correspoding when key pressed unless its a 180 degrees turn
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
                    //Space for pause and unpause
                    case KeyEvent.VK_SPACE:
                        paused = !paused;
                        break;
                }
            }
        }
    }
}
