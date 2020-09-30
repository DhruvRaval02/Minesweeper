import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.ScheduledExecutorService;
import java.io.*;

public class Minesweeper extends JPanel implements ActionListener, MouseListener{

    JFrame frame;
    JMenuBar menuBar;

    JMenu gameMenu;
    JMenuItem beginnerItem;
    JMenuItem intermediateItem;
    JMenuItem expertItem;

    JMenu iconMenu;
    JMenuItem defaultIcons;
    JMenuItem weebIcons;
    JMenuItem marioIcons;

    JMenu controlMenu;
    JMenuItem controlDescription;

    JLabel mineCount;
    JButton resetButton;
    JLabel timer;
    
    JPanel scoreboardPanel;
    JPanel topPanel;
    JPanel gamePanel;

    int mineNumber = 10;
    int rows = 0, cols = 0;
    int [][] gameArr;
    JToggleButton [][] buttonArr;
    boolean firstClick = true;
    boolean gameOver;
    int flagCounter = 0;
    int correctCount = 0;

    Timer time;
    int timeElapsed;

    ImageIcon flag = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/flag.png");
    ImageIcon mine = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/mine.png");
    ImageIcon normal = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/normal.png");
    ImageIcon lose = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/gameover.png");
    ImageIcon win = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/win.jpeg");

    ImageIcon naruto = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/naruto.png");
    ImageIcon sasuke = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/sasuke.jpeg");
    ImageIcon sakura = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/sakuranormal.jpeg");
    ImageIcon angrySakura= new ImageIcon("/Users/dhruv/Desktop/Minesweeper/sakuralose.jpeg");
    ImageIcon happySakura = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/sakurawin.jpeg");

    ImageIcon mario = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/mario.png");
    ImageIcon bowser = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/bowser.png");
    ImageIcon peach = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/peach.jpeg");
    ImageIcon angryPeach = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/sadpeach.jpeg");
    ImageIcon happyPeach = new ImageIcon("/Users/dhruv/Desktop/Minesweeper/winkpeach.jpeg");

    boolean original = true;
    boolean anime = false;
    boolean nintendo = false;


    public Minesweeper(){
        frame = new JFrame("Minesweeeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuBar = new JMenuBar();

        gameMenu = new JMenu("Game");
        beginnerItem = new JMenuItem("Beginner");
        intermediateItem = new JMenuItem("Intermediate");
        expertItem = new JMenuItem("Expert");

        beginnerItem.addActionListener(this);
        intermediateItem.addActionListener(this);
        expertItem.addActionListener(this);

        gameMenu.add(beginnerItem);
        gameMenu.add(intermediateItem);
        gameMenu.add(expertItem);

        iconMenu = new JMenu("Icons");
        defaultIcons = new JMenuItem("Default");
        weebIcons = new JMenuItem("Weeb");
        marioIcons = new JMenuItem("Mario");

        defaultIcons.addActionListener(this);
        weebIcons.addActionListener(this);
        marioIcons.addActionListener(this);

        iconMenu.add(defaultIcons);
        iconMenu.add(weebIcons);
        iconMenu.add(marioIcons);

        controlMenu = new JMenu("Controls");
        controlDescription = new JMenuItem("Description");

        controlDescription.addActionListener(this);

        controlMenu.add(controlDescription);

        menuBar.add(gameMenu, BorderLayout.WEST);
        menuBar.add(iconMenu, BorderLayout.CENTER);
        menuBar.add(controlMenu, BorderLayout.EAST);

        scoreboardPanel = new JPanel();
        scoreboardPanel.setLayout(new GridLayout(1,3));

        mineCount = new JLabel("010", SwingConstants.LEFT);
        resetButton = new JButton();
        resetButton.setIcon(normal);
        timer = new JLabel("000", SwingConstants.RIGHT);
        try{
            Font redFont = Font.createFont(Font.TRUETYPE_FONT,new File("/Users/dhruv/Desktop/Mine/digital-7.ttf"));
            redFont = redFont.deriveFont(Font.PLAIN,24);
            GraphicsEnvironment graphicsEnvironmentt = GraphicsEnvironment.getLocalGraphicsEnvironment();
            graphicsEnvironmentt.registerFont(redFont);
            mineCount.setFont(redFont);
            timer.setFont(redFont);
            mineCount.setForeground(Color.RED);
            timer.setForeground(Color.RED);
        }catch(IOException | FontFormatException e){}

        resetButton.addActionListener(this);

        scoreboardPanel.add(mineCount);
        scoreboardPanel.add(resetButton);
        scoreboardPanel.add(timer);

        topPanel = new JPanel(new GridLayout(2,1));
        topPanel.add(menuBar);
        topPanel.add(scoreboardPanel);

        frame.add(topPanel, BorderLayout.NORTH);

        setGame(mineNumber);

        time = new Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent e){
                timeElapsed++;
                if(timeElapsed < 10){
                    timer.setText("00" + timeElapsed);
                }
                else if(timeElapsed < 100){
                    timer.setText("0" + timeElapsed);
                }
                else
                    timer.setText(timeElapsed+"");
            }
        });

        frame.setVisible(true);
        
    }

    public void setGame(int mineNumber){
        if(mineNumber == 10){
            rows = 9;
            cols = 9;
        }
        else if(mineNumber == 40){
            rows = 16;
            cols = 16;
        }
        else if(mineNumber == 99){
            rows = 16;
            cols = 30;
        }
        if(gamePanel != null)
            frame.remove(gamePanel);
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(rows,cols));

        gameArr = new int[rows][cols];
        buttonArr = new JToggleButton[rows][cols];

        for(int x = 0; x < rows; x++){
            for(int y = 0; y < cols; y++){
                gameArr[x][y] = 0;
                JToggleButton space = new JToggleButton();
                buttonArr[x][y] = space;
                buttonArr[x][y].setBackground(null);
                buttonArr[x][y].putClientProperty("column", y);
                buttonArr[x][y].putClientProperty("row", x);
                buttonArr[x][y].putClientProperty("state", 0);
                buttonArr[x][y].addMouseListener(this);
                gamePanel.add(buttonArr[x][y]);
            }
        }

        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setSize(50 * cols,50 * rows);
        frame.revalidate();

    }

    public void fillMines(int mNumber, int row, int col){
        for(int i = 0; i < mNumber; i++){
            int randX = (int)(Math.random()*rows - 1);
            int randY = (int)(Math.random()*cols - 1);
            if(gameArr[randX][randY] == 0 && randX != row && randY != col && randX != row - 1 && randX != row+1 && randY != col - 1 && randY != col + 1){
                    gameArr[randX][randY] = -1;
                    buttonArr[randX][randY].putClientProperty("state", -1);
                    System.out.println(randX + "," + randY);
            }
            else    
                i--;
        }

        for(int x = 0; x < rows; x++){
            for(int y = 0; y < cols; y++){
                int counter = 0;
                if((int)(buttonArr[x][y].getClientProperty("state")) >= 0){
                    for(int i = x-1; i <= x+1; i++){
                        for(int j = y-1; j <= y+1; j++){
                            try{
                                if((int)(buttonArr[i][j].getClientProperty("state")) == -1)
                                    counter++;        
                            }catch(ArrayIndexOutOfBoundsException e){
                            }
                        }
                    }
                    buttonArr[x][y].putClientProperty("state", counter);
                }
                System.out.println(x + " " + y +" " + buttonArr[x][y].getClientProperty("state"));
                //buttonArr[x][y].setText(buttonArr[x][y].getClientProperty("state")  +"");
            }
        }
    }

    public void expand(int row, int col){

        if(buttonArr[row][col].isSelected() == false)
            buttonArr[row][col].setSelected(true);
        
        int state = Integer.parseInt("" + buttonArr[row][col].getClientProperty("state"));
        if(state > 0){
            buttonArr[row][col].setText(buttonArr[row][col].getClientProperty("state") + "");
            try{
                Font numberFont = Font.createFont(Font.TRUETYPE_FONT,new File("/Users/dhruv/Desktop/Mine/mine-sweeper.ttf"));
                numberFont = numberFont.deriveFont(Font.PLAIN,18);
                GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                graphicsEnvironment.registerFont(numberFont);
                buttonArr[row][col].setFont(numberFont);
            }catch(IOException | FontFormatException e){}
            buttonArr[row][col].setForeground(setFonts((int)(buttonArr[row][col].getClientProperty("state"))));
        }
        else{
            for(int i = row-1; i <= row+1; i++){
                for(int j = col-1; j <= col+1; j++){
                    if(!(i == row && j == col)) {
                        try{
                            state = Integer.parseInt("" + buttonArr[i][j].getClientProperty("state"));
                            if(!(buttonArr[i][j].isSelected()))
                                expand(i,j);
                        }catch(ArrayIndexOutOfBoundsException e){}
                   }
                }
            }
        }
    }

    public Color setFonts(int num){
        if(num == 1)
            return Color.BLUE;
        else if(num == 2)
            return Color.GREEN;
        else if(num == 3)
            return Color.RED;
        else if(num == 4)
            return Color.PINK;
        else if(num == 5)
            return new Color(128,0,0);
        else if(num == 6)
            return Color.CYAN;
        else if (num == 7)
            return Color.BLACK;
        else
            return Color.GRAY;

    }

    public void showMines(){
        for(int x = 0; x < rows; x++){
            for(int y = 0; y < cols; y++){
                if((int)(buttonArr[x][y].getClientProperty("state")) == -1){
                    if(original)
                        buttonArr[x][y].setIcon(mine);
                    else if(anime)
                        buttonArr[x][y].setIcon(sasuke);
                    else if(nintendo)
                        buttonArr[x][y].setIcon(bowser);
                }
            }
        }
        gameOver = true;
    }

    public void mouseReleased(MouseEvent e){
        if(gameOver){
            ((JToggleButton)e.getSource()).setSelected(!((JToggleButton)e.getSource()).isSelected());
            return;
        }
        int row = Integer.parseInt("" + ((JToggleButton)e.getComponent()).getClientProperty("row"));
        int col = Integer.parseInt("" + ((JToggleButton)e.getComponent()).getClientProperty("column"));
        if(e.getButton() == MouseEvent.BUTTON1  && ((JToggleButton)e.getSource()).getIcon() == null){
            System.out.print("CALLED");
            if(firstClick){
                time.start();
                fillMines(mineNumber, row, col);
                expand(row,col);
                firstClick = false;
            }
            int state = Integer.parseInt("" + ((JToggleButton)e.getComponent()).getClientProperty("state"));
            if(state != 0){
                if(state == -1){
                    ((JToggleButton)e.getSource()).setSelected(true);
                    //((JToggleButton)e.getSource()).setText("MINE");
                    //((JToggleButton)e.getSource()).setIcon(mine);
                    showMines();
                    if(original)
                        resetButton.setIcon(lose);
                    else if(anime)
                        resetButton.setIcon(angrySakura);
                    else if(anime)
                        resetButton.setIcon(angryPeach);
                    time.stop();
                }
                else{
                    ((JToggleButton)e.getSource()).setSelected(true);
                    ((JToggleButton)e.getSource()).setText("" + ((JToggleButton)e.getComponent()).getClientProperty("state"));
                    try{
                        Font numberFont = Font.createFont(Font.TRUETYPE_FONT,new File("/Users/dhruv/Desktop/Mine/mine-sweeper.ttf"));
                        numberFont = numberFont.deriveFont(Font.PLAIN,18);
                        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                        graphicsEnvironment.registerFont(numberFont);
                        ((JToggleButton)e.getSource()).setFont(numberFont);
                    }catch(IOException | FontFormatException ex){}
                    ((JToggleButton)e.getSource()).setForeground(setFonts((int)((JToggleButton)e.getComponent()).getClientProperty("state")));
                }             
            }
            else{
                ((JToggleButton)e.getSource()).setSelected(true);
                expand(row,col);
            }
        }
        else if(e.getButton() == MouseEvent.BUTTON3){
            if(((JToggleButton)e.getSource()).isSelected() == false && ((JToggleButton)e.getSource()).getIcon() == null){
                if(original)
                    ((JToggleButton)e.getSource()).setIcon(flag);
                else if(anime)
                    ((JToggleButton)e.getSource()).setIcon(naruto);
                else if(nintendo)
                    ((JToggleButton)e.getSource()).setIcon(mario);
                flagCounter++;
                if(mineNumber - flagCounter <= -10){
                    mineCount.setText("-" + Math.abs(mineNumber-flagCounter));
                }
                else if(mineNumber - flagCounter < 0){
                    mineCount.setText("-0" + Math.abs(mineNumber-flagCounter));
                    System.out.println(Math.abs(mineNumber-flagCounter));
                }
                else if(mineNumber -flagCounter < 10){
                    if(mineNumber - flagCounter == 0)
                        mineCount.setText("000");
                    mineCount.setText("00" + (mineNumber-flagCounter + ""));
                }
                else if(mineNumber - flagCounter < 100){
                    mineCount.setText("0" + (mineNumber-flagCounter + ""));
                }
                if(Integer.parseInt("" + ((JToggleButton)e.getComponent()).getClientProperty("state")) == - 1){
                    correctCount++;
                }
                System.out.println(correctCount);
            }
            else if(((JToggleButton)e.getSource()).getIcon().equals(flag) || ((JToggleButton)e.getSource()).getIcon().equals(naruto) || ((JToggleButton)e.getSource()).getIcon().equals(mario)){
                ((JToggleButton)e.getSource()).setIcon(null);
                ((JToggleButton)e.getSource()).setSelected(false);
                flagCounter--;
                if(Integer.parseInt("" + ((JToggleButton)e.getComponent()).getClientProperty("state")) == - 1){
                    correctCount--;
                }
                if(mineNumber - flagCounter <= -10){
                    mineCount.setText("-" + Math.abs(mineNumber-flagCounter));
                }
                else if(mineNumber - flagCounter < 0){
                    mineCount.setText("-0" + Math.abs(mineNumber-flagCounter));
                }
                else if(mineNumber - flagCounter < 10){
                    if(mineNumber - flagCounter == 0)
                        mineCount.setText("000");
                    mineCount.setText("00" + (mineNumber-flagCounter + ""));
                }
                else if(mineNumber - flagCounter < 100){
                    mineCount.setText("0" + (mineNumber-flagCounter + ""));
                }            
            }

            if(correctCount== mineNumber){
                gameOver = true;
                time.stop();
                if(original)
                    resetButton.setIcon(win);
                else if(anime)
                    resetButton.setIcon(happySakura);
                else if(nintendo)
                    resetButton.setIcon(happyPeach);
            }
            
            //((JToggleButton)e.getSource()).setText("flag");
        }
    }


    public void actionPerformed(ActionEvent e){
        //System.out.println(e.getSource());
        if(e.getSource() == beginnerItem){
            mineCount.setText("010");
            mineNumber = 10;
            flagCounter = 0;
            setGame(mineNumber);
            firstClick = true;
            gameOver = false;
            if(original)
                resetButton.setIcon(normal);
            else if(anime)
                resetButton.setIcon(sakura);
            else if(nintendo)
                resetButton.setIcon(peach);
            timeElapsed = 0;
            correctCount = 0;
            time.restart();
            timer.setText("000");
            time.stop();
        }
        else if(e.getSource() == intermediateItem){
            mineCount.setText("040");
            mineNumber = 40;
            flagCounter = 0;
            setGame(mineNumber);
            firstClick = true;
            gameOver = false;
            if(original)
                resetButton.setIcon(normal);
            else if(anime)
                resetButton.setIcon(sakura);
            else if(nintendo)
                resetButton.setIcon(peach);
            timeElapsed = 0;
            correctCount = 0;
            time.restart();
            timer.setText("000");
            time.stop();
        }
        else if(e.getSource() == expertItem){
            mineCount.setText("099");
            mineNumber = 99;
            flagCounter = 0;
            setGame(mineNumber);
            firstClick = true;
            gameOver = false;
            if(original)
                resetButton.setIcon(normal);
            else if(anime)
                resetButton.setIcon(sakura);
            else if(nintendo)
                resetButton.setIcon(peach);
            timeElapsed = 0;
            correctCount = 0;
            time.restart();
            timer.setText("000");
            time.stop();
        }
        else if(e.getSource() == resetButton){
            System.out.print("RESET");
            flagCounter = 0;
            mineCount.setText("0" + mineNumber);
            setGame(mineNumber);
            firstClick = true;
            gameOver = false;
            if(original)
                resetButton.setIcon(normal);
            else if(anime)
                resetButton.setIcon(sakura);
            else if(nintendo)
                resetButton.setIcon(peach);
            timeElapsed = 0;
            correctCount = 0;
            time.restart();
            timer.setText("000");
            time.stop();
        }
        else if(e.getSource() == marioIcons){
            original = false;
            anime = false;
            nintendo = true;
            if(gameOver)
                showMines();
            for(int x = 0; x < rows; x++){
                for(int y = 0; y < cols; y++){
                    if(buttonArr[x][y].getIcon() != null){
                        if(buttonArr[x][y].getIcon().equals(flag) || buttonArr[x][y].getIcon().equals(naruto))
                            buttonArr[x][y].setIcon(mario);
                    }
                }
            }
            resetButton.setIcon(peach);
        }
        else if(e.getSource() == weebIcons){
            original = false;
            anime = true;
            nintendo = false;
            if(gameOver)
                showMines();
            for(int x = 0; x < rows; x++){
                for(int y = 0; y < cols; y++){
                    if(buttonArr[x][y].getIcon() != null){
                        if(buttonArr[x][y].getIcon().equals(mario) || buttonArr[x][y].getIcon().equals(flag))
                            buttonArr[x][y].setIcon(naruto);
                    }
                }
            }
            resetButton.setIcon(sakura);
        }
        else if(e.getSource() == defaultIcons){
            original = true;
            anime = false;
            nintendo = false;
            if(gameOver)
                showMines();
            for(int x = 0; x < rows; x++){
                for(int y = 0; y < cols; y++){
                    if(buttonArr[x][y].getIcon() != null){
                        if(buttonArr[x][y].getIcon().equals(naruto) || buttonArr[x][y].getIcon().equals(mario))
                            buttonArr[x][y].setIcon(flag);
                    }
                }
            }
            resetButton.setIcon(normal);
        }
    }


    public void mousePressed(MouseEvent e){}    
    public void mouseExited(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}

    public static void main(String[]args){
        new Minesweeper();
    }
}

