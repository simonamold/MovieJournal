package com.example.moviejournal.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.moviejournal.Fragments.HomeFragment;
import com.example.moviejournal.Fragments.ToWatchFragment;
import com.example.moviejournal.Fragments.WatchedFragment;
import com.example.moviejournal.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment defaultFragment = new HomeFragment();
        loadFragment(defaultFragment);
        BottomNavigationView bottomNavigationViewMenu;
        bottomNavigationViewMenu = findViewById(R.id.bottomNavigationView);

        bottomNavigationViewMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home)
                {
                    Fragment fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                }
                else if (item.getItemId() == R.id.toWatch)
                {
                    Fragment fragment = new ToWatchFragment();
                    loadFragment(fragment);
                    return true;
                }
                else if (item.getItemId() == R.id.watched)
                {
                    Fragment fragment = new WatchedFragment();
                    loadFragment(fragment);
                    return true;
                }
                return false;
            }

        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}