package com.khushi.blooddonors.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.databinding.ActivityLoginBinding;
import com.khushi.blooddonors.databinding.ActivitySignUpBinding;

public class ActivityLogin extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore=FirebaseFirestore.getInstance();

        binding.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=binding.etMail.getText().toString();
                String password=binding.etPassword.getText().toString();
                if(email.isEmpty()|| password.isEmpty()){
                    Toast.makeText(ActivityLogin.this, "Please Enter Email or Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    loginAuth(email,password);
                }
            }
        });
        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLogin.this, ActivitySignUp.class));

            }
        });



    }

    private void loginAuth(String email, String password) {
        firestore.collection("Users").whereEqualTo("donorEmail",email).whereEqualTo("donorPassword",password).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size()>0){
                        Toast.makeText(ActivityLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ActivityLogin.this, MainActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(ActivityLogin.this, "Incorrect email or password Try Again", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityLogin.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });



    }
}