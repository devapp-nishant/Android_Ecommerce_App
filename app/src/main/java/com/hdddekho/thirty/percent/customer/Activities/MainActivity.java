package com.hdddekho.thirty.percent.customer.Activities;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hdddekho.thirty.percent.customer.R;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNav;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);

        navController = findNavController(this, R.id.frame_layout);
        NavigationUI.setupWithNavController(bottomNav, navController);

    }
}