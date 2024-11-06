package com.example.project_mad;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class HealthFragment extends Fragment {

    public HealthFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_health, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout healthCard = view.findViewById(R.id.healthcard);
        LinearLayout dietCard = view.findViewById(R.id.dietcard);

        healthCard.setOnClickListener(v -> openFragment(new DoctorsAppointmentsFragment()));
        dietCard.setOnClickListener(v -> openFragment(new DietFragment()));
    }

    private void openFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Replace fragment_container with your actual container ID
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
