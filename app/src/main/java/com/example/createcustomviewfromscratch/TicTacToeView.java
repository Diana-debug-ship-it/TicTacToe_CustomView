package com.example.createcustomviewfromscratch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import kotlin.random.Random;

interface OnFieldChangedListener {
    void onChanged(TicTacToeField field);
}

interface OnCellActionListener {
    void onActionListener(int row, int col, TicTacToeField field);
}

public class TicTacToeView extends View {

    private int currentRow = -1;
    private int currentColumn = -1;
    private Paint currentPaint;


    public static final int DEFAULT_PLAYER_1_COLOR = Color.GREEN;
    public static final int DEFAULT_PLAYER_2_COLOR = Color.RED;
    public static final int DEFAULT_GRID_COLOR = Color.GRAY;

    public static final int DESIRED_CELL_SIZE = 50;

    Context context;
    AttributeSet attrs;
    int defStyleAttr;
    int defStyleRes;

    private int playerOneColor;
    private int playerTwoColor;
    private int gridColor;

    private Rect fieldRect = new Rect();
    private float cellSize = 0f;
    private float cellPadding = 0f;

    private RectF cellRect = new RectF();

    private Paint player1Paint;
    private Paint player2Paint;
    private Paint gridPaint;

    private TicTacToeField field;

    public OnFieldChangedListener getListener() {
        return listener;
    }

    public void setListener(OnFieldChangedListener listener) {
        this.listener = listener;
    }

    public OnCellActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(OnCellActionListener actionListener) {
        this.actionListener = actionListener;
    }

    private OnFieldChangedListener listener = new OnFieldChangedListener() {
        @Override
        public void onChanged(TicTacToeField field) {
            invalidate();
        }
    };
    private OnCellActionListener actionListener;

    public TicTacToeField getField() {
        return field;
    }

    public void setField(TicTacToeField value) {
        if (this.field != null) {
            if (field.listeners != null) {
                field.listeners.remove(listener);
            }
        }
        field = value;
        field.listeners.add(listener);
        updateViewSizes();
        requestLayout();
        invalidate();
    }

    public TicTacToeView(Context context,
                         @Nullable AttributeSet attrs,
                         int defStyleAttr,
                         int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            setDefaultFocusHighlightEnabled(false);
        }

        if (attrs != null) {
            initAttributes(attrs, defStyleAttr, defStyleRes);
        } else {
            initDefaultColors();
        }
        initPaints();
        setFocusable(true);
        setClickable(true);
        if (isInEditMode()) {
            field = new TicTacToeField(8, 6);
        }
    }


    public TicTacToeView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.DefaultTicTacToeColor);
    }

    public TicTacToeView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.ticTacToeFieldStyle);
    }

    public TicTacToeView(Context context) {
        this(context, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!checkFieldsNotNull()) return;

        drawGrid(canvas);
        drawCurrentCell(canvas);
        drawCells(canvas);
    }

    private boolean checkFieldsNotNull() {
        if (field == null) return false;
        if (cellSize == 0) return false;
        if (fieldRect.width() <= 0) return false;
        if (fieldRect.height() <= 0) return false;
        return true;
    }

    private void drawCells(Canvas canvas) {
        for (int row = 0; row < field.getRows(); row++) {
            for (int col = 0; col < field.getColumns(); col++) {
                Cell cell = field.getCell(row, col);
                if (cell.equals(Cell.PLAYER_1)) {
                    drawPlayer1(canvas, row, col);
                } else if (cell.equals(Cell.PLAYER_2)) {
                    drawPlayer2(canvas, row, col);
                }
            }
        }
    }


    private void drawPlayer1(Canvas canvas, int row, int column) {
        getCellRect(row, column);
        canvas.drawLine(cellRect.left, cellRect.top, cellRect.right, cellRect.bottom, player1Paint);
        canvas.drawLine(cellRect.right, cellRect.top, cellRect.left, cellRect.bottom, player1Paint);
    }

    private void drawPlayer2(Canvas canvas, int row, int column) {
        getCellRect(row, column);
        canvas.drawCircle(cellRect.centerX(), cellRect.centerY(), cellRect.width() / 2, player2Paint);
    }

    private void drawCurrentCell(Canvas canvas) {
        if (currentRow == -1 || currentColumn == -1) {
            return;
        }
        RectF currentCell = getCellRect(currentRow, currentColumn);
        canvas.drawRect(currentCell.left - cellPadding,
                currentCell.top - cellPadding,
                currentCell.right + cellPadding,
                currentCell.bottom + cellPadding,
                currentPaint);
    }

    private RectF getCellRect(int row, int column) {
        cellRect.left = fieldRect.left + column * cellSize + cellPadding;
        cellRect.top = fieldRect.top + row * cellSize + cellPadding;
        cellRect.right = cellRect.left + cellSize - cellPadding * 2;
        cellRect.bottom = cellRect.top + cellSize - cellPadding * 2;
        return cellRect;
    }

    private void drawGrid(Canvas canvas) {
        int xStart = fieldRect.left;
        int xEnd = fieldRect.right;
        for (int i = 0; i <= field.getRows(); i++) {
            int y = (int) (fieldRect.top + cellSize * i);
            canvas.drawLine(xStart, y, xEnd, y, gridPaint);
        }
        int yStart = fieldRect.top;
        int yEnd = fieldRect.bottom;
        for (int i = 0; i <= field.getColumns(); i++) {
            int x = (int) (fieldRect.left + cellSize * i);
            canvas.drawLine(x, yStart, x, yEnd, gridPaint);
        }
    }

    private void initPaints() {
        player1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        player1Paint.setColor(playerOneColor);
        player1Paint.setStyle(Paint.Style.STROKE);
        player1Paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics()));

        player2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        player2Paint.setColor(playerTwoColor);
        player2Paint.setStyle(Paint.Style.STROKE);
        player2Paint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3f, getResources().getDisplayMetrics()));

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(gridColor);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, getResources().getDisplayMetrics()));

        currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentPaint.setColor(Color.rgb(230, 230, 230));
        currentPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (field != null && field.listeners != null) {
            field.listeners.add(listener);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (field != null && field.listeners != null) {
            field.listeners.remove(listener);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int minHeight = getSuggestedMinimumHeight() + getPaddingBottom() + getPaddingTop();

        int desiredCellSizeInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                DESIRED_CELL_SIZE,
                getResources().getDisplayMetrics());

        int rows = field.getRows();
        int columns = field.getColumns();

        int desiredWidth = Math.max(minWidth, columns * desiredCellSizeInPixels + getPaddingRight() + getPaddingLeft());
        int desiredHeight = Math.max(minHeight, rows * desiredCellSizeInPixels + getPaddingTop() + getPaddingBottom());

        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec),
                resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateViewSizes();
    }

    private void updateViewSizes() {
        if (this.field == null) return;

        int safeWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int safeHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if (field.getColumns() == 0 || field.getRows() == 0) {
            throw new RuntimeException("Division by zero");
        }
        float cellWidth = ((float) safeWidth) / field.getColumns();
        float cellHeight = ((float) safeHeight) / field.getRows();

        cellSize = Math.min(cellWidth, cellHeight);
        cellPadding = cellHeight * 0.2f;

        float fieldWidth = cellSize * field.getColumns();
        float fieldHeight = cellSize * field.getRows();

        fieldRect.left = (int) (getPaddingLeft() + (safeWidth - fieldWidth) / 2);
        fieldRect.top = (int) (getPaddingTop() + (safeHeight - fieldHeight) / 2);
        fieldRect.right = (int) (fieldRect.left + fieldWidth);
        fieldRect.bottom = (int) (fieldRect.top + fieldHeight);
    }


    private void initAttributes(AttributeSet attrs,
                                int defStyleAttr,
                                int defStyleRes) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.TicTacToeView,
                defStyleAttr,
                defStyleRes);

        playerOneColor = typedArray.getColor(R.styleable.TicTacToeView_playerOneColor, DEFAULT_PLAYER_1_COLOR);
        playerTwoColor = typedArray.getColor(R.styleable.TicTacToeView_playerTwoColor, DEFAULT_PLAYER_2_COLOR);
        gridColor = typedArray.getColor(R.styleable.TicTacToeView_gridColor, DEFAULT_GRID_COLOR);

        typedArray.recycle();
    }

    private void initDefaultColors() {
        playerOneColor = DEFAULT_PLAYER_1_COLOR;
        playerTwoColor = DEFAULT_PLAYER_2_COLOR;
        gridColor = DEFAULT_GRID_COLOR;
    }

    //processing touch events

    private int getRow(MotionEvent event) {
        return (int) ((event.getY() - fieldRect.top) / cellSize);
    }

    private int getColumn(MotionEvent event) {
        return (int) ((event.getX() - fieldRect.left) / cellSize);
    }

    private void updateCurrentCell(MotionEvent event) {
        TicTacToeField currentField = this.field;
        if (currentField == null) return;

        int row = getRow(event);
        int col = getColumn(event);
        if (row >= 0 && col >= 0 && row < currentField.getRows() && col < currentField.getColumns()) {
            if (currentRow != row || currentColumn != col) {
                this.currentRow = row;
                this.currentColumn = col;
                invalidate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TicTacToeField currentField = this.field;
        if (currentField == null) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                updateCurrentCell(event);
            case MotionEvent.ACTION_DOWN:
                updateCurrentCell(event);
                return true;
            case MotionEvent.ACTION_UP:
                return performClick();
            default:
                return false;
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        TicTacToeField currentField = this.field;
        if (currentField == null) return false;
        int row = currentRow;
        int col = currentColumn;
        if (row >= 0 && col >= 0 && row < currentField.getRows() && col < currentField.getColumns()) {
            actionListener.onActionListener(row, col, currentField);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN)
            moveCurrentCell(1, 0);
        else if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT)
            moveCurrentCell(0, -1);
        else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
            moveCurrentCell(0, 1);
        else if (keyCode==KeyEvent.KEYCODE_DPAD_UP)
            moveCurrentCell(-1, 0);
        return super.onKeyDown(keyCode, event);
    }

    private boolean moveCurrentCell(int rowDiff, int colDiff) {
        TicTacToeField currentField = this.field;
        if (currentField == null) return false;
        if (currentRow == -1 || currentColumn == -1) {
            currentRow = 0;
            currentColumn = 0;
            invalidate();
            return true;
        } else {
            if (currentColumn + colDiff < 0) return false;
            if (currentColumn + colDiff >= field.getColumns()) return false;
            if (currentRow + rowDiff < 0) return false;
            if (currentRow + rowDiff >= field.getRows()) return false;
            currentColumn += colDiff;
            currentRow += rowDiff;
            invalidate();
            return true;
        }
    }
}
