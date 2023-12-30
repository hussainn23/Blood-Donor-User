package com.khushi.blooddonors.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.Utils;
import com.khushi.blooddonors.databinding.ActivitySignUpBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivitySignUp extends AppCompatActivity {


    private ActivitySignUpBinding binding;

    private TextView etDOB;
    private Spinner spBloodGroup;

    Utils utils;

    private FirebaseFirestore firestore;
    String selectBloodGroup;
    ModelUser modelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utils=new Utils(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));




        //initliazation of firestore

        firestore = FirebaseFirestore.getInstance();

        etDOB = findViewById(R.id.etDOB);
        spBloodGroup = findViewById(R.id.SpBloodGroup);

        // Set up the Spinner with blood group options
        setupBloodGroupSpinner();


        // Set a click listener on etDOB to show the date picker
        etDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });


        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.etName.getText().toString();
                String cnic = binding.etCNIC.getText().toString();
                String dob = binding.etDOB.getText().toString();
                String email = binding.etEmail.getText().toString();
                String address = binding.etAddress.getText().toString();
                //  String bloodGroup=binding.SpBloodGroup.toString();  will discuss it later spinner value can't use directly
                String password = binding.etPassword.getText().toString();
                String confirmPassword = binding.etConfirmPassword.getText().toString();

                //function to apload data user define fnction we are making to make easy code structure
                if (name.isEmpty() || cnic.isEmpty() || dob.isEmpty() || email.isEmpty() || address.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
                    Toast.makeText(ActivitySignUp.this, "Enter Valid Data Please", Toast.LENGTH_SHORT).show();

                } else {
                    uploadData(name, cnic, dob, email, address, "A+", password, confirmPassword);

                }

            }
        });


    }


    private void uploadData(String name, String cnic, String dob, String email, String address, String bloodGroup, String password, String confirmPassword) {
        modelUser = new ModelUser(name, cnic, dob, email, address, selectBloodGroup, "Active","","",password);
        utils.startLoadingAnimation();

        firestore.collection("Donors").add(modelUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String documentId = task.getResult().getId();
                        modelUser.setDonorID(documentId);
                        firestore.collection("Donors").document(documentId).set(modelUser)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        utils.endLoadingAnimation();
                                        startActivity(new Intent(ActivitySignUp.this, ActivityLogin.class));
                                        finish();
                                        Toast.makeText(ActivitySignUp.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        utils.endLoadingAnimation();
                                        Toast.makeText(ActivitySignUp.this, "Not saved!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Update the EditText with the selected date
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;

                        etDOB.setText(selectedDate);
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }


    private void setupBloodGroupSpinner() {
        List<String> bloodGroups = new ArrayList<>();  /// array based implemenataion of list

        bloodGroups.add("A+");
        bloodGroups.add("A-");
        bloodGroups.add("B+");
        bloodGroups.add("B-");
        bloodGroups.add("AB+");
        bloodGroups.add("AB-");
        bloodGroups.add("O+");
        bloodGroups.add("O-");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodGroups);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBloodGroup.setAdapter(adapter);


        spBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int index, long id) {
                // Display a toast with the selected blood group
                selectBloodGroup= bloodGroups.get(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }
}



