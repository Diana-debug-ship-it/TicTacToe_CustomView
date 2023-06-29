package com.example.createcustomviewfromscratch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

enum Cell {
    PLAYER_1, //X
    PLAYER_2, //O
    EMPTY //empty cell
}



public class TicTacToeField {
    private final int rows;
    private final int columns;
    private final List<List<Cell>> cells;

    public Set<OnFieldChangedListener> listeners = new HashSet<OnFieldChangedListener>();

    private List<List<Cell>> generateMatrix(int rows, int columns) {
        List<List<Cell>> matrix = new ArrayList<>();
        List<Cell> temp;
        for (int i = 0; i < rows; i++) {
            temp = new ArrayList<>();
            for (int j = 0; j < columns; j++) {
                temp.add(Cell.EMPTY);
            }
            matrix.add(temp);
        }
        return matrix;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public TicTacToeField(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        generateMatrix(rows, columns);
        cells = generateMatrix(rows, columns);
    }

    public Cell getCell(int row, int column) {
        if (row < 0 || column < 0 || row >= rows || column >= columns) {
            return Cell.EMPTY;
        }
        return cells.get(row).get(column);
    }

    public void setCell(int row, int column, Cell cell) {
        if (row < 0 || column < 0 || row >= rows || column >= columns) {
            return;
        }
        if (getCell(row, column) != cell) {
            cells.get(row).set(column, cell);
            for (OnFieldChangedListener listener: listeners) {
                listener.onChanged(this);
            }
        }
    }


}
