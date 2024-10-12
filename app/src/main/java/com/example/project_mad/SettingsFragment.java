package com.example.project_mad;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

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

    // Method to determine which fragment to open based on the selected link
    private void openSelectedFragment(String fragmentName) {
        Fragment selectedFragment = null;

        switch (fragmentName) {
            case "profile":
                selectedFragment = new ProfileFragment();
                break;
            case "resetPassword":
                break;
            case "addAccount":
                break;
            case "deleteAccount":
                break;
            default:
                selectedFragment = new SettingsFragment(); // Default to SettingsFragment
                break;
        }

        // Replace the current fragment with the selected one
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, selectedFragment);
        transaction.addToBackStack(null); // Optionally, add to back stack for navigation
        transaction.commit();
    }
}
