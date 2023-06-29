package com.example.createcustomviewfromscratch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.example.createcustomviewfromscratch.databinding.ActivityMainBinding;

import kotlin.random.Random;

public class MainActivity extends AppCompatActivity {

    private boolean isFirstPlayer = true;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ticTacToeView.setField(new TicTacToeField(10, 10));

        binding.ticTacToeView.setActionListener(new OnCellActionListener() {
            @Override
            public void onActionListener(int row, int col, TicTacToeField field) {
                Cell cell = field.getCell(row, col);
                if (cell.equals(Cell.EMPTY)) {
                    if (isFirstPlayer){
                        field.setCell(row, col, Cell.PLAYER_1);
                    } else {
                        field.setCell(row, col, Cell.PLAYER_2);
                    }
                    isFirstPlayer = !isFirstPlayer;
                }
            }
        });
    }
}