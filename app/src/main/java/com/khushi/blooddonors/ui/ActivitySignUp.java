package com.khushi.blooddonors.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.khushi.blooddonors.databinding.ActivitySignUpBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ActivitySignUp extends AppCompatActivity {


    private ActivitySignUpBinding binding;

    private TextView etDOB;
    private Spinner spBloodGroup;

  private FirebaseFirestore firestore;
  ModelUser modelUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore=FirebaseFirestore.getInstance();
        etDOB = findViewById(R.id.etDOB);
        spBloodGroup = findViewById(R.id.SpBloodGroup);


        // Set a click listener on etDOB to show the date picker
        etDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        // Set up the Spinner with blood group options
        setupBloodGroupSpinner();

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=binding.etName.getText().toString();
                String cnic=binding.etCNIC.getText().toString();
                String dob=binding.etDOB.getText().toString();
                String email=binding.etEmail.getText().toString();
                String address=binding.etAddress.getText().toString();
              //  String bloodGroup=binding.SpBloodGroup.toString();  will discuss it later spinner value can't use directly
                String password=binding.etPassword.getText().toString();
                String confirmPassword=binding.etConfirmPassword.getText().toString();

                //function to apload data user define fnction we are making to make easy code structure

                uploadData(name,cnic,dob,email,address,"A+",password,confirmPassword);


            }
        });












    }


    private void uploadData(String name, String cnic, String dob, String email, String address, String bloodGroup, String password, String confirmPassword) {
        

        modelUser=new ModelUser(name,cnic,dob,email,address,bloodGroup,password);
        firestore.collection("User").add(modelUser).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(ActivitySignUp.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ActivitySignUp.this, ActivityLogin.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivitySignUp.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });





    }








    private void showDatePickerDialog() {
        // Get the current date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new DatePickerDialog and show it
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
        // Create a list of blood groups
        List<String> bloodGroups = new ArrayList<>();
        bloodGroups.add("A+");
        bloodGroups.add("A-");
        bloodGroups.add("B+");
        bloodGroups.add("B-");
        bloodGroups.add("AB+");
        bloodGroups.add("AB-");
        bloodGroups.add("O+");
        bloodGroups.add("O-");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, bloodGroups);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spBloodGroup.setAdapter(adapter);

        // Set a listener to handle item selection
        spBloodGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Display a toast with the selected blood group
                String selectedBloodGroup = (String) parentView.getItemAtPosition(position);
                Toast.makeText(ActivitySignUp.this, "Selected Blood Group: " + selectedBloodGroup, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });
    }
}
