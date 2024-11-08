package com.example.project_mad;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.pm.PackageManager;

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the current user's UID from FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        if (userId != null) {
            // Retrieve reminders and appointments from the database for the current user
            checkReminders(context, userId);
            checkAppointments(context, userId);
        }
    }

    private void checkReminders(final Context context, final String userId) {
        DatabaseReference remindersRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("reminders");

        remindersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot reminderSnapshot : dataSnapshot.getChildren()) {
                    String title = reminderSnapshot.child("title").getValue(String.class);
                    // Show notification for reminders
                    if (title != null) {
                        showNotification(context, "Reminder", title, "reminder");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }

    private void checkAppointments(final Context context, final String userId) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("appointments");

        appointmentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                    String doctorName = appointmentSnapshot.child("doctorName").getValue(String.class);
                    String reason = appointmentSnapshot.child("reason").getValue(String.class);
                    // Show notification for appointments
                    if (doctorName != null && reason != null) {
                        showNotification(context, doctorName, reason, "appointment");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors here
            }
        });
    }

    private void showNotification(Context context, String title, String content, String type) {
        // Create notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder and Appointment Channel";
            String description = "Channel for reminder and appointment notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notification_channel", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an Intent to open the home page when clicked
        Intent homeIntent = new Intent(context, MainActivity.class); // Replace MainActivity with your home page activity
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Create a PendingIntent to trigger the activity when the notification is clicked
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification_channel")
                .setSmallIcon(R.drawable.ic_reminder) // Replace with your notification icon
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Dismiss the notification once it's clicked

        // Show the notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Handle missing permission
            return;
        }
        notificationManagerCompat.notify(type.equals("reminder") ? 1 : 2, builder.build()); // Different notification IDs for reminder and appointment
    }
}
