package com.example.project_mad;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AddEventFragment extends Fragment {

    private EditText eventTitleEditText, eventDescriptionEditText;
    private Button saveButton;

    public interface OnEventAddedListener {
        void onEventAdded(String title, String description);
    }

    private OnEventAddedListener listener;

    public AddEventFragment(OnEventAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        eventTitleEditText = view.findViewById(R.id.eventTitleEditText);
        eventDescriptionEditText = view.findViewById(R.id.eventDescriptionEditText);
        saveButton = view.findViewById(R.id.saveButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eventTitleEditText.getText().toString();
                String description = eventDescriptionEditText.getText().toString();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                    Toast.makeText(getContext(), "Please enter title and description", Toast.LENGTH_SHORT).show();
                } else {
                    listener.onEventAdded(title, description);
                    getParentFragmentManager().popBackStack(); // Go back to the previous fragment
                }
            }
        });

        return view;
    }
}
