import javax.swing.JFrame;

public class GameFrame extends JFrame {

    GameFrame() {
        //Create game panel
        GamePanel panel = new GamePanel();
        this.add(panel);
        //Make title
        this.setTitle("Snake game but you swicth head and tail");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Turn off window resizable
        this.setResizable(false);
        
        //Pack and set visible
        this.pack();
        this.setVisible(true);
    }
}
