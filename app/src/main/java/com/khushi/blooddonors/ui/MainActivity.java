package com.khushi.blooddonors.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.Utils;
import com.khushi.blooddonors.adapters.DonorsAdapter;
import com.khushi.blooddonors.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private DonorsAdapter donorsAdapter;
    private List<ModelUser> userList = new ArrayList<>();

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(this);

        sharedPrefManager = new SharedPrefManager(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        // Get the user list and set it to the RecyclerView
        getUserList();

        // Set up the blood group search functionality
        setupBloodGroupSearch();

        // Set up BottomNavigationView item click listener
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.profile) {
                    openProfileActivity();
                    return true;
                }
                else if(item.getItemId() == R.id.setting)
                {
                    Intent intent = new Intent(MainActivity.this, ActivitySetting.class);
                    startActivity(intent);
                    return true;
                }
                else if(item.getItemId() == R.id.home)
                {
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                else if(item.getItemId() == R.id.donate)
                {
                    Intent intent = new Intent(MainActivity.this, ActivityAllDonors.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                else {
                    // Add other cases for other BottomNavigationView items if needed
                }
                return false;
            }
        });
    }

    private void openProfileActivity() {
        // Open the ProfileActivity when "Profile" is clicked
        Intent intent = new Intent(MainActivity.this, ActivityUserPersonalProfile.class);
        startActivity(intent);
    }

    private void getUserList() {
        utils.startLoadingAnimation();

        // Listen for real-time updates
        firestore.collection("Donors").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                utils.endLoadingAnimation();
                // Handle errors
                return;
            }

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                // Clear the existing list
                userList.clear();

                // Add new data from the snapshot
                userList.addAll(queryDocumentSnapshots.toObjects(ModelUser.class));

                // Initialize and set the adapter
                donorsAdapter = new DonorsAdapter(userList, MainActivity.this);
                recyclerView.setAdapter(donorsAdapter);

                utils.endLoadingAnimation();
            } else {
                utils.endLoadingAnimation();
                // Handle empty snapshot
            }
        });
    }

    private void setupBloodGroupSearch() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the user list
                if (donorsAdapter != null) {
                    donorsAdapter.filterByBloodGroup(newText);
                }
                return true;
            }
        });
    }
}
