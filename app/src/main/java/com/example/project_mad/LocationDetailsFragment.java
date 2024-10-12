package com.example.project_mad;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide; // Make sure to include Glide in your project

public class LocationDetailsFragment extends Fragment {

    private static final String ARG_LOCATION = "location_data";

    public static LocationDetailsFragment newInstance(Location location) {
        LocationDetailsFragment fragment = new LocationDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location); // Use Location class
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_location_details, container, false);

        // Get location data from arguments
        Location location = null;
        if (getArguments() != null) {
            location = getArguments().getParcelable(ARG_LOCATION); // Use your Location class
        }

        TextView nameTextView = view.findViewById(R.id.detailName);
        TextView addressTextView = view.findViewById(R.id.detailAddress);
        ImageView imageView = view.findViewById(R.id.detailImage);

        if (location != null) {
            nameTextView.setText(location.getName());
            addressTextView.setText(location.getAddress());

            // Load the image associated with the location using Glide
            // Assuming the photo field is a URL or resource ID; handle accordingly
            if (location.getPhoto() != null && !location.getPhoto().isEmpty()) {
                Glide.with(this)
                        .load(location.getPhoto()) // Use the photo URL
                        .placeholder(R.drawable.baseline_home_filled_24) // Optional placeholder image
                        .into(imageView);
            } else {
                imageView.setImageResource(R.drawable.baseline_home_filled_24); // Fallback image if no photo
            }
        } else {
            nameTextView.setText("Location not found");
            addressTextView.setText("");
            imageView.setImageResource(R.drawable.baseline_home_filled_24); // Fallback image if location is null
        }

        return view;
    }
}
