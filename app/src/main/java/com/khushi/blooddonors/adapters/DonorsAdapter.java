package com.khushi.blooddonors.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.khushi.blooddonors.Models.ModelUser;
import com.khushi.blooddonors.R;
import com.khushi.blooddonors.SharedPrefManager;
import com.khushi.blooddonors.ui.ActivityDonorProfile;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
        if (!donor.getImg().isEmpty()) {
            Picasso.get().load(donor.getImg()).into(holder.userImage);
        }

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
            }, 0);
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
        firestore.collection("Donors")
                .document(donor.getDonorID())
                .update("status", "Inactive")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        donor.setStatus("Inactive");
                        SharedPrefManager sharedPrefManager = new SharedPrefManager(mContext);
                        sharedPrefManager.saveDonor(donor);
                        notifyDataSetChanged();

                        // Fetch the FCM token at runtime
                        fetchFcmTokenAndSendNotification(donor);

                        Toast.makeText(mContext, "Donor status updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Failed to update donor status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchFcmTokenAndSendNotification(ModelUser donor) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String token = task.getResult();
                        firestore.collection("Donors")
                                .document(donor.getDonorID())
                                .update("donorDOB", token)
                                .addOnCompleteListener(task1 -> {
                                    if (task.isSuccessful()) {
                                    } else {
                                        Toast.makeText(mContext, "Failed to update donor status", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        sendNotificationToUser(token);
                    } else {
                        Log.e("FCM_DEBUG", "Failed to get FCM token", task.getException());
                    }
                });

    }

    private void sendNotificationToUser(String fcmToken) {
        String serverKey = "AAAAq3IgFZc:APA91bHvBiAEO4tgtt9q42JRRr4JT5OK-ou3Ds7NzdPaxA663uf-_HvUb9A8zo612oTqbloDV-jPw-fATmGDTzWskfxjMxQlEgZdLfgW9Ydk1sSzHh7CjR3bZzMMDaq1jU_lRAEZT01F";
        String fcmEndpoint = "https://fcm.googleapis.com/fcm/send";

        new Thread(() -> {
            try {
                // Create JSON payload for the notification
                JSONObject json = new JSONObject();
                json.put("to", fcmToken);
                JSONObject notification = new JSONObject();
                notification.put("title", "Donor Status Update");
                notification.put("body", "Your donor status has been updated to Inactive.");
                json.put("notification", notification);

                // Send the notification to FCM server
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
                Request request = new Request.Builder()
                        .url(fcmEndpoint)
                        .addHeader("Authorization", "key=" + serverKey)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("FCM Response", responseBody);

                // Check if the notification was sent successfully
                if (response.isSuccessful()) {
                    showSuccessToast();
                } else {
                    showErrorToast();
                }

            } catch (Exception e) {
                e.printStackTrace();
                showErrorToast();
            }
        }).start();
    }

    private void showSuccessToast() {
        runOnUiThread(() -> {
            Toast.makeText(mContext, "Notification sent successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void showErrorToast() {
        runOnUiThread(() -> {
            Toast.makeText(mContext, "Failed to send notification", Toast.LENGTH_SHORT).show();
        });
    }

    private void runOnUiThread(Runnable runnable) {
        new Handler(Looper.getMainLooper()).post(runnable);
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
