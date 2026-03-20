/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tetris;

/**
 *
 * @author vm.alonsobarberan
 */
public class Shape {
    
    private Tetrominoes pieceShape;
    private int[][] coords;
    
    public Shape() {
        coords = new int[4][2];
        setRandomShape();
    }
    
    public Shape copy() {
        Shape shape = new Shape();
        shape.setShape(pieceShape);
        for (int i = 0; i < coords.length; i++) {
            int x = getX(i);
            int y = getY(i);
            shape.setX(i, x);
            shape.setY(i, y);
        }
        return shape;
    }
    
    public void rotateLeft() {
        for (int i = 0; i < coords.length; i++) {
            int x = getX(i);
            int y = getY(i);
            setX(i, y);
            setY(i, -x);
        }
    }

    private static final int[][][] coordsTable = new int[][][]{
        {{0, 0}, {0, 0}, {0, 0}, {0, 0}},
        {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},
        {{0, -1}, {0, 0}, {1, 0}, {1, 1}},
        {{0, -1}, {0, 0}, {0, 1}, {0, 2}},
        {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}},
        {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},
        {{1, -1}, {0, -1}, {0, 0}, {0, 1}}
    };

    public void setShape(Tetrominoes shapeType) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; j++) {
                coords[i][j] = coordsTable[shapeType.ordinal()][i][j];
            }
        }
        pieceShape = shapeType;
    }
    
    private void setX(int index, int x) {
        coords[index][0] = x;
    }
    
    private void setY(int index, int y) {
        coords[index][1] = y;
    }
    
    public int getX(int index) {
        return coords[index][0];
    }
    
    public int getY(int index) {
        return coords[index][1];
    }
    
    public Tetrominoes getShape() {
        return pieceShape;
    }
    
    public void setRandomShape() {
        int randNumber = (int) (Math.random() * 7) + 1;
        Tetrominoes t = Tetrominoes.values()[randNumber];
        setShape(t);
    }
    
    public int getMinX() {
        int minX = coords[0][0];
        for(int i = 1; i < coords.length; i++) {
            if (coords[i][0] < minX) {
                minX = coords[i][0];
            }
        }
        return minX;
    }
    
    public int getMaxX() {
        int maxX = coords[0][0];
        for(int i = 1; i < coords.length; i++) {
            if (coords[i][0] > maxX) {
                maxX = coords[i][0];
            }
        }
        return maxX;
    }
    
    public int getMinY() {
        int minY = coords[0][1];
        for(int i = 1; i < coords.length; i++) {
            if (coords[i][1] < minY) {
                minY = coords[i][1];
            }
        }
        return minY;
    }
    
    public int getMaxY() {
        int maxY = coords[0][1];
        for(int i = 1; i < coords.length; i++) {
            if (coords[i][1] > maxY) {
                maxY = coords[i][1];
            }
        }
        return maxY;
    }
    
    

}
