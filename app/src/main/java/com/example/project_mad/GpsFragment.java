package com.example.project_mad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GpsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<Location> locationList; // Changed to use your Location class
    private LocationAdapter locationAdapter; // Adapter for RecyclerView
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gps, container, false);

        // Initialize the list of locations
        locationList = new ArrayList<>();

        // Set up the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Locations");

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Location location = snapshot.getValue(Location.class);
                    if (location != null) {
                        locationList.add(location);
                    }
                }
                // Set up adapter with the fetched data
                locationAdapter = new LocationAdapter(locationList, getContext(), GpsFragment.this::onLocationClick);
                recyclerView.setAdapter(locationAdapter);

                // Update the map with the new locations
                if (mMap != null) {
                    updateMapMarkers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    private void onLocationClick(Location location) {
        // Move the camera to the selected location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.getLatLng(), 15));
        mMap.addMarker(new MarkerOptions().position(location.getLatLng()).title(location.getName()));

        // Optionally replace RecyclerView with a detailed view of the selected item
        replaceWithLocationDetailFragment(location);
    }

    private void replaceWithLocationDetailFragment(Location location) {
        // Create a new fragment to show location details
        LocationDetailsFragment detailFragment = LocationDetailsFragment.newInstance(location);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment) // Assuming your main container ID is fragment_container
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        updateMapMarkers(); // Update the map with existing locations when ready
    }

    private void updateMapMarkers() {
        // Clear existing markers before adding new ones
        mMap.clear();
        for (Location location : locationList) {
            mMap.addMarker(new MarkerOptions().position(location.getLatLng()).title(location.getName()));
        }

        // Move the camera to the first location if the list is not empty
        if (!locationList.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationList.get(0).getLatLng(), 12));
        }
    }
}
