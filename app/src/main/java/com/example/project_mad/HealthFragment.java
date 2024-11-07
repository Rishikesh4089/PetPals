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

        healthCard.setOnClickListener(v -> openInfoFragment(
                "Health Tips",
                "1. Regular Exercise: Keep your pet active to maintain a healthy weight.\n\n" +
                        "2. Routine Check-ups: Schedule regular vet appointments to monitor health.\n\n" +
                        "3. Proper Grooming: Brush and bathe your pet to maintain a healthy coat.\n\n" +
                        "4. Vaccinations: Keep vaccinations up-to-date to prevent diseases.\n\n" +
                        "5. Clean Environment: Keep living areas clean to avoid infections.",
                R.drawable.health_tips_image // Replace with actual drawable resource
        ));

        dietCard.setOnClickListener(v -> openInfoFragment(
                "Diet Tips",
                "1. Balanced Diet: Ensure your pet’s diet includes proteins, fats, and carbs.\n\n" +
                        "2. Fresh Water: Always provide fresh water for proper hydration.\n\n" +
                        "3. Portion Control: Avoid overfeeding to maintain a healthy weight.\n\n" +
                        "4. Nutritional Supplements: Consider supplements for added health benefits.\n\n" +
                        "5. Avoid Toxic Foods: Avoid chocolate, grapes, onions, and similar toxic foods.",
                R.drawable.diet_tips_image // Replace with actual drawable resource
        ));
    }

    private void openInfoFragment(String title, String message, int imageResId) {
        InfoDisplayFragment fragment = InfoDisplayFragment.newInstance(title, message, imageResId);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Replace fragment_container with your actual container ID
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
