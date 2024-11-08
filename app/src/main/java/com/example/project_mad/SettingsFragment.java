package com.example.project_mad;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private FirebaseAuth mAuth;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Handle Profile link click
        TextView profileLink = view.findViewById(R.id.profileLink);
        profileLink.setOnClickListener(v -> openSelectedFragment("profile"));

        // Handle Logout link click
        TextView logoutLink = view.findViewById(R.id.logoutLink);
        logoutLink.setOnClickListener(view1 -> openSelectedFragment("logout"));

        // Handle Reset Password link click
        TextView resetPasswordLink = view.findViewById(R.id.resetPasswordLink);
        resetPasswordLink.setOnClickListener(v -> openSelectedFragment("resetPassword"));

        // Handle Delete Account link click
        TextView deleteAccountLink = view.findViewById(R.id.deleteAccountLink);
        deleteAccountLink.setOnClickListener(v -> openSelectedFragment("deleteAccount"));

        return view;
    }

    private void openSelectedFragment(String fragmentName) {
        Fragment selectedFragment = null;

        switch (fragmentName) {
            case "profile":
                selectedFragment = new ProfileFragment();
                break;
            case "resetPassword":
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Reset Password!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    // Navigate to ResetPasswordActivity
                    Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                return;
            case "deleteAccount":
                // Show the delete confirmation dialog
                showDeleteAccountDialog();
                return;
            case "logout":
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Logout!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    // Navigate to LoginActivity
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                return;
            default:
                selectedFragment = new SettingsFragment();
                break;
        }

        // Replace the current fragment with the selected one
        if (selectedFragment != null) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, selectedFragment);
            transaction.addToBackStack(null); // Optionally, add to back stack for navigation
            transaction.commit();
        }
    }

    private void showDeleteAccountDialog() {
        if (getActivity() == null || getActivity().isFinishing()) {
            // Don't show the dialog if the activity is no longer valid
            Log.e(TAG, "Activity is not in a valid state to show the dialog.");
            return;
        }

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        // Create and show the dialog
        AlertDialog dialog = builder.create();

        Button btnConfirmDelete = dialogView.findViewById(R.id.btn_confirm_delete);
        btnConfirmDelete.setOnClickListener(view -> {
            deleteUserAccount();
            dialog.dismiss();  // Dismiss the dialog after confirming delete
        });

        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(view -> dialog.dismiss());  // Properly dismiss the dialog on cancel

        // Ensure the dialog is only shown if the fragment is still attached
        if (isAdded() && !getActivity().isFinishing()) {
            dialog.show();
        }
    }



    // Function to delete the current user's account
    private void deleteUserAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    // Navigate back to login screen or exit app
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish(); // Finish current activity
                } else {
                    Toast.makeText(getActivity(), "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No user is signed in.", Toast.LENGTH_SHORT).show();
        }
    }
}
