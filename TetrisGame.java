import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;

public class TetrisGame extends JPanel {

    public static void main (String[] args) {

        JFrame f = new JFrame("TetrisGame");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(20*26+10, 26*24+10);
        f.setVisible(true);

        final TetrisGame game = new TetrisGame();
        game.init();
        f.add(game);

        f.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_UP:
                        game.rotate(-1);
                        break;
                    case KeyEvent.VK_DOWN:
                        game.rotate(+1);
                        break;
                    case KeyEvent.VK_LEFT:
                        game.move(-1);
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.move(+1);
                        break;
                    case KeyEvent.VK_SPACE:
                        game.drop();
                        game.score+=1;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        new Thread() {
            public void run(){
                while(true){
                    try {
                        Thread.sleep(game.mil);
                        game.drop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private final Point[][][] myPoint = {
            {
                    //I
                    {new Point(0,1),new Point(1,1), new Point(2,1), new Point(3,1)},
                    {new Point(1,0),new Point(1,1), new Point(1,2), new Point(1,3)},
                    {new Point(0,1),new Point(1,1), new Point(2,1), new Point(3,1)},
                    {new Point(1,0),new Point(1,1), new Point(1,2), new Point(1,3)}
            },
            {
                    //J
                    {new Point(0,1),new Point(1,1), new Point(2,1), new Point(2,0)},
                    {new Point(1,0),new Point(1,1), new Point(1,2), new Point(2,2)},
                    {new Point(0,1),new Point(1,1), new Point(2,1), new Point(0,2)},
                    {new Point(1,0),new Point(1,1), new Point(1,2), new Point(0,0)}
            },
            {
                    //L
                    {new Point(0,1),new Point(1,1), new Point(2,1), new Point(2,2)},
                    {new Point(1,0),new Point(1,1), new Point(1,2), new Point(0,2)},
                    {new Point(0,1),new Point(1,1), new Point(2,1), new Point(0,0)},
                    {new Point(1,0),new Point(1,1), new Point(1,2), new Point(2,0)}
            },
            {
                    //O
                    {new Point(0,0),new Point(0,1), new Point(1,0), new Point(1,1)},
                    {new Point(0,0),new Point(0,1), new Point(1,0), new Point(1,1)},
                    {new Point(0,0),new Point(0,1), new Point(1,0), new Point(1,1)},
                    {new Point(0,0),new Point(0,1), new Point(1,0), new Point(1,1)}
            },
            {
                    //Z
                    {new Point(0,2),new Point(1,2), new Point(1,1), new Point(2,1)},
                    {new Point(0,0),new Point(0,1), new Point(1,1), new Point(1,2)},
                    {new Point(0,2),new Point(1,2), new Point(1,1), new Point(2,1)},
                    {new Point(0,0),new Point(0,1), new Point(1,1), new Point(1,2)}
            },
            {
                    //S
                    {new Point(1,2),new Point(2,2), new Point(1,1), new Point(0,1)},
                    {new Point(0,2),new Point(0,1), new Point(1,1), new Point(1,0)},
                    {new Point(1,2),new Point(2,2), new Point(1,1), new Point(0,1)},
                    {new Point(0,2),new Point(0,1), new Point(1,1), new Point(1,0)}
            },
            {
                    //T
                    {new Point(0,1),new Point(1,1), new Point(1,2), new Point(2,1)},
                    {new Point(1,0),new Point(1,1), new Point(0,1), new Point(1,2)},
                    {new Point(0,1),new Point(1,1), new Point(1,0), new Point(2,1)},
                    {new Point(1,0),new Point(1,1), new Point(2,1), new Point(1,2)}
            }

    };

    private final Color[] myColor = {Color.cyan, Color.magenta, Color.orange, Color.yellow, Color.blue, Color.green, Color.gray, Color.black, Color.pink, Color.red};

    private int totalRows;
    private int level = 1;
    private int mil = 1000;
    private Point pt;
    private int currentPiece;
    private int followingPiece;
    private int rotation;
    private ArrayList<Integer> nextPiece = new ArrayList<Integer>();
    private long score;
    private Color[][] well;

    private void init(){
        well = new Color[12][24];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 23; j++) {
                if(i==0||i==11||j==22){
                    well[i][j] = Color.pink;
                } else {
                    well[i][j] = Color.black;
                }
            }
        }
        newPiece();
        nextPiece();
    }

    public void newPiece(){
        pt = new Point(5,2);
        rotation = 0;
        if (nextPiece.isEmpty()){
            Collections.addAll(nextPiece, 0,1,2,3,4,5,6);
            Collections.shuffle(nextPiece);
            if (collidesAt(pt.x, pt.y, rotation)) {
                 score -= 10000;
            }
//            gameOverCheck();

        }
        followingPiece = nextPiece.get(0);
//        currentPiece = nextPiece.get(0);
        nextPiece.remove(0);
    }

    public void nextPiece(){
        currentPiece = followingPiece;
        newPiece();
    }

    private boolean collidesAt(int x, int y, int rotation){
        for (Point p: myPoint[currentPiece][rotation]){
            if(well[p.x+x][p.y+y] != Color.black){
                return true;
            }
        }
        return false;
    }

    private void rotate(int i){
        int newRotation = (rotation + i) % 4;
        if(newRotation < 0) {
            newRotation = 3;
        }
        if(!collidesAt(pt.x, pt.y, newRotation)) {
            rotation = newRotation;
        }
        repaint();
    }

    public void move(int i){
        if(!collidesAt(pt.x + i, pt.y, rotation)){
            pt.x += i;
        }
        repaint();
    }

    public void drop(){
        if (!collidesAt(pt.x, pt.y + 1, rotation)) {
            pt.y += 1;
        } else {
            fixToWell();
        }
        repaint();
    }

    public void fixToWell(){
        for(Point p: myPoint[currentPiece][rotation]){
            well[pt.x + p.x][pt.y + p.y] = myColor[currentPiece];
        }
        clearRows();
        nextPiece();

//        newPiece();
    }

    public void deleteRow(int row) {
        for(int j = row-1; j > 0; j--){
            for(int i = 1; i < 11; i++){
                well[i][j+1] = well[i][j];
            }
        }
    }

    private void clearRows(){
        boolean gap;
        int numClear = 0;
        for(int j = 21; j > 0; j--){
            gap = false;
            for(int i = 1; i < 11; i++){
                if(well[i][j] == Color.black){
                    gap = true;
                    break;
                }
            }
            if(!gap) {
                deleteRow(j);
                j += 1;
                numClear += 1;
                totalRows += 1;
                if (totalRows == 5) {
                    totalRows = 0;
                    level += 1;
                    mil -= 50;
                    if (mil < 100){
                        mil = 100;
                    }
                }
            }
        }
        switch (numClear){
            case 1:
                score += 100;
                break;
            case 2:
                score += 300;
                break;
            case 3:
                score += 500;
                break;
            case 4:
                score += 800;
                break;
        }
    }

    private void drawPiece(Graphics g){
        g.setColor(myColor[currentPiece]);
        for(Point p: myPoint[currentPiece][rotation]){
            g.fillRect((pt.x+p.x)*26, (pt.y+p.y)*26, 25,25);
        }
        g.setColor(Color.white);
        g.fillRect(312, 0, 8*26, 26*24);

        g.setColor(myColor[followingPiece]);
        for(Point p: myPoint[followingPiece][0]){
            g.fillRect((p.x)*26+350, (p.y)*26+215,25, 25);
        }
        g.setColor(Color.black);
        Font scoreFont = new Font("Arial", Font.BOLD, 20);
        Font titleFont = new Font("Arial", Font.BOLD, 40);
        g.setFont(titleFont);
        g.drawString("TETRIS",32*11-7, 40);
        g.setFont(scoreFont);
        g.drawString("Level : " + level, 30*11+3, 100);
        g.drawString("Score is : " + score, 30*11+3, 140);
        g.drawString("Next piece is:", 30*11+3, 190);

    }

    public void paintComponent(Graphics g){
        g.fillRect(0,0,26*12, 26*23);
        for(int i = 0; i < 12; i++){
            for(int j = 0; j <23; j++){
                g.setColor(well[i][j]);
                g.fillRect(26*i, 26*j, 25,25);
            }
        }

        drawPiece(g);
                if (collidesAt(pt.x, pt.y, rotation)) {
            g.setColor(Color.RED);
            g.fillRect(0,0,26*12, 26*23);
            g.setColor(Color.black);
            Font myFont = new Font ("Arial", 1, 40);
            g.setFont(myFont);
            g.drawString("GAME OVER!", 20, 200);
        }
    }

//    public void gameOverCheck (Graphics g) {
//        if (collidesAt(pt.x, pt.y, rotation)) {
//            g.setColor(Color.RED);
//            g.fillRect(0,0,26*12, 26*23);
//            g.setColor(Color.black);
//            g.drawString("GAME OVER!", 100, 200);
//        }
//    }
}




























