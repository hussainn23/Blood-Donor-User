package com.khushi.blooddonors.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.ui.ActivityDonorProfile;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DonorsAdapter extends RecyclerView.Adapter<DonorsAdapter.MyViewHolder> {

    private List<ModelUser> donorsList;
    private List<ModelUser> filteredDonorsList; // For filtering functionality
    private Context mContext;
    private FirebaseFirestore firestore;

    public DonorsAdapter(List<ModelUser> donorsList, Context mContext) {
        this.donorsList = donorsList;
        this.filteredDonorsList = new ArrayList<>(donorsList);
        this.mContext = mContext;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_all_donars, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ModelUser donor = filteredDonorsList.get(position);
        holder.tvName.setText(donor.getDonorName());
        holder.tvBloodGroup.setText(donor.getDonorBloddGroup());
        if (!donor.getImg().isEmpty()){
            Picasso.get().load(donor.getImg()).into(holder.userImage);
        }

        // Set the visibility of statusActiveImage based on donor's status
        if ("Inactive".equals(donor.getStatus())) {
            holder.statusActive.setVisibility(View.GONE);
            holder.statusInactive.setVisibility(View.VISIBLE);
        } else {
            holder.statusActive.setVisibility(View.VISIBLE);
            holder.statusInactive.setVisibility(View.GONE);
        }

        holder.cdContactDonor.setOnClickListener(v -> {
            if ("Inactive".equals(donor.getStatus())) {
                Toast.makeText(mContext, "Donor is not eligible ", Toast.LENGTH_LONG).show();
            } else {
                openGmail(donor.getDonorEmail(), donor.getDonorName(), donor);
            }
        });

        holder.cdDonor.setOnClickListener(v -> {
            String donorJson = new Gson().toJson(donor);
            Intent intent = new Intent(mContext, ActivityDonorProfile.class);
            intent.putExtra("donor", donorJson);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return filteredDonorsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView tvBloodGroup;
        public CardView cdContactDonor;
        public CardView cdDonor;
        public ImageView userImage;
        public ImageView statusActive;
        public ImageView statusInactive;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvBloodGroup = itemView.findViewById(R.id.tvBloodGroup);
            cdContactDonor = itemView.findViewById(R.id.cdContactDonor);
            userImage = itemView.findViewById(R.id.userImage);
            cdDonor = itemView.findViewById(R.id.cdDonor);
            statusActive = itemView.findViewById(R.id.statusActiveImage);
            statusInactive = itemView.findViewById(R.id.statusInactiveImage);
        }
    }

    private void openGmail(String email, String donorName, ModelUser donor) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Blood Donation Inquiry");
        intent.putExtra(Intent.EXTRA_TEXT, "Dear " + donorName + ",\n\n");

        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            new Handler().postDelayed(() -> {
                mContext.startActivity(Intent.createChooser(intent, "Send Email"));
                showDonorStatusDialog(donor);
            }, 0); // 0 milliseconds delay
        } else {
            Toast.makeText(mContext, "No email app found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDonorStatusDialog(ModelUser donor) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirm Donor Status");
        builder.setMessage("Is the donor ready?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            updateDonorStatus(donor);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
        });

        builder.create().show();
    }

    private void updateDonorStatus(ModelUser donor) {
        // Update donor status to "Inactive" in Firebase
        firestore.collection("Donors")
                .document(donor.getDonorID())
                .update("status", "Inactive")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update donor status in SharedPreference
                        donor.setStatus("Inactive");
                        SharedPrefManager sharedPrefManager = new SharedPrefManager(mContext);
                        sharedPrefManager.saveDonor(donor);
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "Donor status updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Failed to update donor status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void filterByBloodGroup(String bloodGroup) {
        filteredDonorsList.clear();
        if (bloodGroup.isEmpty()) {
            filteredDonorsList.addAll(donorsList);
        } else {
            for (ModelUser donor : donorsList) {
                if (donor.getDonorBloddGroup().toLowerCase().contains(bloodGroup.toLowerCase())) {
                    filteredDonorsList.add(donor);
                }
            }
        }
        notifyDataSetChanged();
    }
}
