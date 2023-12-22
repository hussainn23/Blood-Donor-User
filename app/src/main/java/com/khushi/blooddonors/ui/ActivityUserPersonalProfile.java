package com.khushi.blooddonors.ui;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.Utils;
import com.khushi.blooddonors.databinding.ActivityUserPersonalProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

public class ActivityUserPersonalProfile extends AppCompatActivity {

    private ActivityUserPersonalProfileBinding binding;
    private Utils utils;
    private SharedPrefManager sharedPrefManager;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;
    private Uri selectedImageUri;
    private ModelUser modelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserPersonalProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        utils = new Utils(this);
        sharedPrefManager = new SharedPrefManager(this);
        firestore = FirebaseFirestore.getInstance();

        // Initialize Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();  // This is your default bucket

        // Load user data and set it to the UI
        loadUserData();

        // Set up the date of birth picker
        binding.tvDOB.setOnClickListener(v -> showDatePickerDialog());

        // Set up the image selection from the gallery
        binding.donorImage.setOnClickListener(v -> openGallery());

        // Set up the save changes button
        binding.btnEditProfile.setOnClickListener(v -> {
            // Save changes to the user profile
            saveUserProfile();
        });
    }

    private void loadUserData() {
        modelUser = sharedPrefManager.getUser();
        if (modelUser != null) {
            binding.tvName.setText(modelUser.getDonorName());
            binding.tvCNIC.setText(modelUser.getDonorCNIC());
            binding.tvEmail.setText(modelUser.getDonorEmail());
            binding.tvDOB.setText(modelUser.getDonorDOB());
            binding.tvBloodGroup.setText(modelUser.getDonorBloddGroup());
            binding.tvAddress.setText(modelUser.getDonorAddress());
            Picasso.get().load(sharedPrefManager.getUser().getImg()).into(binding.donorImage);

        }
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
            binding.donorImage.setImageURI(selectedImageUri);
        }
    }

    private void saveUserProfile() {
        if (modelUser != null) {
            // Update user data with changes
            modelUser.setDonorName(binding.tvName.getText().toString().trim());
            modelUser.setDonorCNIC(binding.tvCNIC.getText().toString().trim());
            modelUser.setDonorDOB(binding.tvDOB.getText().toString().trim());
            modelUser.setDonorEmail(binding.tvEmail.getText().toString().trim());
            modelUser.setDonorAddress(binding.tvAddress.getText().toString().trim());
            modelUser.setDonorBloddGroup(binding.tvBloodGroup.getText().toString().trim());
            modelUser.getStatus();
            modelUser.getDonorID();
            sharedPrefManager.getUser().getDonorPassword();

            if (selectedImageUri != null) {
                // Upload and update image
                uploadImageAndSaveUser();
            } else {
                saveUserToFirestore();
            }
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
                            utils.endLoadingAnimation();
                            // Image uploaded successfully
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Get the download URL
                                String downloadUrl = uri.toString();
                                // Update the user model with the image link
                                modelUser.setImg(downloadUrl);
                                // Save the updated user in Firestore
                                saveUserToFirestore();
                            });
                        } else {
                            utils.endLoadingAnimation();
                            Toast.makeText(ActivityUserPersonalProfile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            utils.endLoadingAnimation();
            saveUserToFirestore();
        }
    }

    private void saveUserToFirestore() {
        utils.startLoadingAnimation();
        firestore.collection("Donors")
                .document(modelUser.getDonorID())
                .set(modelUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        utils.endLoadingAnimation();
                        sharedPrefManager.saveDonor(modelUser);
                        Toast.makeText(ActivityUserPersonalProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        // Perform any other action or move to the next activity
                    } else {
                        utils.endLoadingAnimation();
                        Toast.makeText(ActivityUserPersonalProfile.this, "Failed to update user in Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePickerDialog() {
        // Implement your date picker logic here
        // You can use a DatePickerDialog or any other method to allow the user to select a date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1;
                    binding.tvDOB.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }
}
