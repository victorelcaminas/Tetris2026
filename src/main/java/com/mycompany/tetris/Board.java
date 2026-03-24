/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.mycompany.tetris;

import com.mycompany.tetris.interfaces.GameOverInterface;
import com.mycompany.tetris.interfaces.Incrementer;
import com.mycompany.tetris.interfaces.InitGamer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.Timer;

/**
 *
 * @author vm.alonsobarberan
 */
public class Board extends javax.swing.JPanel implements InitGamer {

    class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (timer.isRunning() && canMove(currentRow, currentCol - 1, currentShape)) {
                        currentCol--;
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (timer.isRunning() && canMove(currentRow, currentCol + 1, currentShape)) {
                        currentCol++;
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (timer.isRunning()) {
                        rotate();
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (timer.isRunning() && canMove(currentRow + 1, currentCol, currentShape)) {
                        currentRow++;
                    }
                    break;
                default:
                    break;
            }
            repaint();
        }
    }

    private static final int NUM_ROWS = 22;
    private static final int NUM_COLS = 10;
    private static final int DELTA_TIME = 500; // in milliseconds

    private Shape currentShape;
    private int currentRow;
    private int currentCol;
    private Tetrominoes[][] squares;
    private Timer timer;
    private MyKeyAdapter keyAdapter;

    private Incrementer incrementer;
    private GameOverInterface gameOverInterface;

    /**
     * Creates new form Board
     */
    public Board() {
        initComponents();
        initBoard();
    }

    public void pause() {
        if (timer.isRunning()) {
            timer.stop();
        } else {
            timer.start();
        }
    }
    

    public void setIncrementer(Incrementer incrementer) {
        this.incrementer = incrementer;
    }
    
    public void setGameOverInterface(GameOverInterface gmInterface) {
        this.gameOverInterface = gmInterface;        
    }

    public void initGame() {
        if (incrementer != null) {
            incrementer.reset();
        }
        generateNewCurrentShape();
        squares = new Tetrominoes[NUM_ROWS][NUM_COLS];
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                squares[row][col] = Tetrominoes.NoShape;
            }
        }
        timer.start();
    }

    private void initBoard() {
        
        keyAdapter = new MyKeyAdapter();
        addKeyListener(keyAdapter);
        setFocusable(true);

        timer = new Timer(DELTA_TIME, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doGameLoop();
            }
        });
        initGame();
    }

    private void rotate() {
        if (currentShape.getShape() == Tetrominoes.SquareShape) {
            return;
        }
        Shape shape = currentShape.copy();
        shape.rotateLeft();
        if (canMove(currentRow, currentCol, shape)) {
            currentShape = shape;
        }
    }

    private boolean canMove(int row, int col, Shape shape) {
        // check bottom
        if (row + shape.getMaxY() >= NUM_ROWS) {
            return false;
        }
        // check left
        if (col + shape.getMinX() < 0) {
            return false;
        }
        // check right
        if (col + shape.getMaxX() >= NUM_COLS) {
            return false;
        }

        if (hitsSquares(row, col, shape)) {
            return false;
        }

        return true;
    }

    public boolean hitsSquares(int row, int col, Shape shape) {
        for (int point = 0; point < 4; point++) {
            int calcRow = shape.getY(point) + row;
            int calcCol = shape.getX(point) + col;
            if (calcRow >= 0 && calcRow < NUM_ROWS && calcCol >= 0 && calcCol < NUM_COLS) {
                if (squares[calcRow][calcCol] != Tetrominoes.NoShape) {
                    return true;
                }
            }
        }
        return false;
    }

    private void doGameLoop() { // VERY IMPORTAQNT METHOD
        if (canMove(currentRow + 1, currentCol, currentShape)) {
            currentRow++;
        } else { // Hit bottom or squares
            // copy shape to squares and generate new currentShape
            copyCurrentShapeToSquares(currentRow, currentCol, currentShape);
            boolean deletedLine = checkLine();
            if (currentRow == 0 && !deletedLine) {  
                processGameOver();
            }            
            generateNewCurrentShape();
        }

        repaint();
    }
    
    private void processGameOver() {
        timer.stop();
        gameOverInterface.setVisible(this);
    }

    private boolean checkLine() {
        boolean deletedLine = false;
        for (int row = 0; row < NUM_ROWS; row++) {
            if (isLineCompleted(row)) {
                deleteLine(row);
                deletedLine = true;
                fillRow0();
                incrementer.incrementScore(1);
            }
        }
        return deletedLine;
    }

    private void fillRow0() {
        for (int col = 0; col < NUM_COLS; col++) {
            squares[0][col] = Tetrominoes.NoShape;
        }
    }

    private void deleteLine(int row) {
        for (int r = row - 1; r >= 0; r--) {
            for (int col = 0; col < NUM_COLS; col++) {
                squares[r + 1][col] = squares[r][col];
            }
        }
    }

    private boolean isLineCompleted(int row) {
        for (int col = 0; col < NUM_COLS; col++) {
            if (squares[row][col] == Tetrominoes.NoShape) {
                return false;
            }
        }
        return true;
    }

    private void generateNewCurrentShape() {
        currentRow = 0;
        currentCol = NUM_COLS / 2;
        currentShape = new Shape();

    }

    private void copyCurrentShapeToSquares(int row, int col, Shape shape) {
        for (int i = 0; i < 4; i++) {
            int calcRow = row + shape.getY(i);
            int calcCol = col + shape.getX(i);
            if (calcRow >= 0 && calcRow < NUM_ROWS && calcCol >= 0 && calcCol < NUM_COLS) {
                squares[calcRow][calcCol] = shape.getShape();
            }
        }
    }

    private int squareWidth() {
        return getWidth() / NUM_COLS;
    }

    private int squareHeight() {
        return getHeight() / NUM_ROWS;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBackground(g);
        paintCurrentShape(g);
        Toolkit.getDefaultToolkit().sync();
    }

    private void paintBackground(Graphics g) {
        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < NUM_COLS; col++) {
                drawSquare(g, row, col, squares[row][col]);
            }
        }
    }

    public void paintCurrentShape(Graphics g) {
        for (int i = 0; i < 4; i++) {
            int row = currentRow + currentShape.getY(i);
            int col = currentCol + currentShape.getX(i);
            Tetrominoes t = currentShape.getShape();
            drawSquare(g, row, col, t);
        }
    }

    private void drawSquare(Graphics g, int row, int col,
            Tetrominoes shape) {
        Color colors[] = {new Color(0, 0, 0),
            new Color(204, 102, 102),
            new Color(102, 204, 102), new Color(102, 102, 204),
            new Color(204, 204, 102), new Color(204, 102, 204),
            new Color(102, 204, 204), new Color(218, 170, 0)
        };
        int x = col * squareWidth();
        int y = row * squareHeight();
        Color color = colors[shape.ordinal()];
        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2,
                squareHeight() - 2);
        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);
        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1,
                y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 232, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 399, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
