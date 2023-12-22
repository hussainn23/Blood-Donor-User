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
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.Utils;
import com.khushi.blooddonors.databinding.ActivityLoginBinding;
import com.khushi.blooddonors.databinding.ActivitySignUpBinding;

public class ActivityLogin extends AppCompatActivity {
    private ActivityLoginBinding binding;
    FirebaseFirestore firestore;
    SharedPrefManager sharedPrefManager;
    Utils utils;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore=FirebaseFirestore.getInstance();
        sharedPrefManager = new SharedPrefManager(this);
        utils=new Utils(this);


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
        utils.startLoadingAnimation();
        firestore.collection("Donors").whereEqualTo("donorEmail",email).whereEqualTo("donorPassword",password).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    utils.endLoadingAnimation();
                    if(task.getResult().size()>0){
                        sharedPrefManager.setLogin(true);
                        sharedPrefManager.saveDonor(task.getResult().getDocuments().get(0).toObject(ModelUser.class));
                        Toast.makeText(ActivityLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        if (sharedPrefManager.getUser() != null && sharedPrefManager.getUser().getImg() != null
                                && !sharedPrefManager.getUser().getImg().isEmpty()){
                            startActivity(new Intent(ActivityLogin.this, MainActivity.class));

                        }
                        else{
                            startActivity(new Intent(ActivityLogin.this, ActivityAddProfileImage.class));

                        }
                        finish();
                    }
                    else {
                        utils.endLoadingAnimation();

                        Toast.makeText(ActivityLogin.this, "Incorrect email or password Try Again", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                utils.endLoadingAnimation();
                Toast.makeText(ActivityLogin.this, "Something went wrong", Toast.LENGTH_SHORT).show();

            }
        });



    }
}