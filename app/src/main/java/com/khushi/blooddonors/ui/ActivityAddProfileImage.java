package com.khushi.blooddonors.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.Utils;
import com.khushi.blooddonors.databinding.ActivityAddProfileImageBinding;

public class ActivityAddProfileImage extends AppCompatActivity {

    private ActivityAddProfileImageBinding binding;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    Utils utils;

    private ModelUser modelUser;
    private SharedPrefManager sharedPrefManager;
    private Uri selectedImageUri; // To store the selected image URI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProfileImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        sharedPrefManager = new SharedPrefManager(this);
        utils=new Utils(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));


        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
        binding.btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        binding.btnAploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageAndSaveUser();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            binding.profileImage.setImageURI(selectedImageUri);
        }
    }

    private void uploadImageAndSaveUser() {
        if (selectedImageUri != null) {
            utils.startLoadingAnimation();
            // Upload image to Firebase Storage
            StorageReference imageRef = storageReference.child("profile_images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(selectedImageUri)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Image uploaded successfully
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String downloadUrl = uri.toString();
                                modelUser = sharedPrefManager.getUser();
                                if (modelUser != null) {
                                    modelUser.setImg(downloadUrl);
                                    // Save the updated user in Firestore
                                    firestore.collection("Donors")
                                            .document(modelUser.getDonorID())
                                            .set(modelUser)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    utils.endLoadingAnimation();
                                                    sharedPrefManager.saveDonor(modelUser);
                                                    Toast.makeText(ActivityAddProfileImage.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(ActivityAddProfileImage.this, MainActivity.class));

                                                    // Move to the next activity or perform other actions
                                                } else {
                                                    utils.endLoadingAnimation();
                                                    Toast.makeText(ActivityAddProfileImage.this, "Failed to update user in Firestore", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        } else {
                            utils.endLoadingAnimation();

                            Toast.makeText(ActivityAddProfileImage.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            utils.endLoadingAnimation();
            Toast.makeText(ActivityAddProfileImage.this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
    }
}
