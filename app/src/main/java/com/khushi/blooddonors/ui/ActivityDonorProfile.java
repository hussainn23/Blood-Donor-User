package com.khushi.blooddonors.ui;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.Utils;
import com.khushi.blooddonors.databinding.ActivityDonorProfileBinding;
import com.squareup.picasso.Picasso;

public class ActivityDonorProfile extends AppCompatActivity {

    private ActivityDonorProfileBinding binding;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonorProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        utils = new Utils(this);

        if (getIntent().hasExtra("donor")) {
            String donorJson = getIntent().getStringExtra("donor");
            ModelUser donor = new Gson().fromJson(donorJson, ModelUser.class);

            binding.tvName.setText(donor.getDonorName());
            binding.tvCNIC.setText(donor.getDonorCNIC());
            binding.tvEmail.setText(donor.getDonorEmail());
            binding.tvDOB.setText(donor.getDonorDOB());
            binding.tvBloodGroup.setText(donor.getDonorBloddGroup());
            binding.tvAddress.setText(donor.getDonorAddress());
            if (donor.getImg() != null && !donor.getImg().isEmpty()) {
                Picasso.get().load(donor.getImg()).into(binding.donorImage);
            }

            binding.btnContactDonor.setOnClickListener(v -> {
                 if("Inactive".equals(donor.getStatus())){
                     Toast.makeText(this, "Donor is not eligible ", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     openGmail(donor.getDonorEmail(), donor.getDonorName(), donor);

                 }
            });
        } else {
        }

        binding.imgBack.setOnClickListener(v -> onBackPressed());
    }


    private void openGmail(String email, String donorName, ModelUser donor) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Blood Donation Inquiry");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear " + donorName + ",\n\n");

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                    // Call the showDonorStatusDialog after the delay
                    showDonorStatusDialog(donor);
                }
            }, 0); // 0 milliseconds delay
        } else {
            Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }



    private void showDonorStatusDialog(ModelUser donor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Donor Status");
        builder.setMessage("Is the donor ready?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateDonorStatus(donor);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }

    private void updateDonorStatus(ModelUser donor) {
        FirebaseFirestore.getInstance().collection("Donors")
                .document(donor.getDonorID())
                .update("status", "Inactive")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        donor.setStatus("Inactive");
                        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
                        sharedPrefManager.saveDonor(donor);
                        Toast.makeText(this, "Connected with donor", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update donor status", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
