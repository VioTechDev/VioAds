package com.example.andmoduleads.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.andmoduleads.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    protected boolean isBackgroundRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }
}