package com.example.project_mad;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Handle Profile link click
        TextView profileLink = view.findViewById(R.id.profileLink);
        profileLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectedFragment("profile");
            }
        });

        // Handle Logout link click
        TextView logoutLink = view.findViewById(R.id.logoutLink);
        logoutLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectedFragment("logout");
            }
        });

        // Handle Reset Password link click
        TextView resetPasswordLink = view.findViewById(R.id.resetPasswordLink);
        resetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectedFragment("resetPassword");
            }
        });

        // Handle Add Account link click
        TextView addAccountLink = view.findViewById(R.id.addAccountLink);
        addAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectedFragment("addAccount");
            }
        });

        // Handle Delete Account link click
        TextView deleteAccountLink = view.findViewById(R.id.deleteAccountLink);
        deleteAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectedFragment("deleteAccount");
            }
        });

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
                    Toast.makeText(getActivity(), "Logout!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    // Navigate back to the login screen
                    Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                    startActivity(intent);
                    getActivity().finish(); // Finish current activity
                    return; // Exit to avoid trying to replace fragment
                }
 // Exit to avoid trying to replace fragment
            case "addAccount":
                // TODO: Implement add account functionality
                Toast.makeText(getActivity(), "Add Account Clicked", Toast.LENGTH_SHORT).show();
                return; // Exit to avoid trying to replace fragment
            case "deleteAccount":
                // TODO: Implement delete account functionality
                Toast.makeText(getActivity(), "Delete Account Clicked", Toast.LENGTH_SHORT).show();
                return; // Exit to avoid trying to replace fragment
            case "logout":
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Logout!", Toast.LENGTH_SHORT).show();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.apply();

                    // Navigate back to the login screen
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish(); // Finish current activity
                    return; // Exit to avoid trying to replace fragment
                }
            default:
                selectedFragment = new SettingsFragment(); // Default to SettingsFragment
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
}
