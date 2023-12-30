package com.khushi.blooddonors.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;

public class ActivitySplash extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        firestore=FirebaseFirestore.getInstance();
        sharedPrefManager = new SharedPrefManager(this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPrefManager.isLoggedIn()) {
                    // If already logged in, move to the main activity
                    loginAuth(sharedPrefManager.getUser().getDonorEmail(),sharedPrefManager.getUser().getDonorPassword());
                } else {
                    // If not logged in, move to the login activity
                    Intent intent = new Intent(ActivitySplash.this, ActivityLogin.class);
                    startActivity(intent);
                    finish();
                }
                finish();
            }
        }, 4000);
    }



    private void loginAuth(String email, String password) {
        firestore.collection("Donors").whereEqualTo("donorEmail",email).whereEqualTo("donorPassword",password).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().size()>0){
                        Intent intent = new Intent(ActivitySplash.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        }
                        else{
                            startActivity(new Intent(ActivitySplash.this, ActivityLogin.class));

                        }
                        finish();
                    }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });



    }

}
