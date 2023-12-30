package com.khushi.blooddonors.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.Utils;
import com.khushi.blooddonors.adapters.DonorsAdapter;
import com.khushi.blooddonors.databinding.ActivityAllDonorsBinding;
import com.khushi.blooddonors.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class ActivityAllDonors extends AppCompatActivity {

    private ActivityAllDonorsBinding binding;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerView;
    private DonorsAdapter donorsAdapter;
    private List<ModelUser> userList = new ArrayList<>();

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllDonorsBinding .inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        sharedPrefManager = new SharedPrefManager(this);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        getUserList();
        setupBloodGroupSearch();
        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }


    private void getUserList() {
        utils.startLoadingAnimation();

        firestore.collection("Donors").addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                utils.endLoadingAnimation();
                return;
            }

            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                userList.clear();
                userList.addAll(queryDocumentSnapshots.toObjects(ModelUser.class));
                donorsAdapter = new DonorsAdapter(userList, ActivityAllDonors.this);
                recyclerView.setAdapter(donorsAdapter);
                utils.endLoadingAnimation();
            } else {
                utils.endLoadingAnimation();
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
