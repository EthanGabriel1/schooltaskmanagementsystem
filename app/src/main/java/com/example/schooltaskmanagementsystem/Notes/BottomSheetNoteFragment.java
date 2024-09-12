package com.example.schooltaskmanagementsystem.Notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.schooltaskmanagementsystem.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetNoteFragment extends BottomSheetDialogFragment {

    private EditText editHeading;
    private EditText editDescription;
    private Button buttonSave;
    private NoteListener noteListener;
    private String initialHeading;
    private String initialDescription;

    public interface NoteListener {
        void onNoteAdded(Note note);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_note, container, false);

        editHeading = view.findViewById(R.id.edit_heading);
        editDescription = view.findViewById(R.id.edit_description);
        buttonSave = view.findViewById(R.id.button_save);

        // Set initial values if editing
        if (initialHeading != null && initialDescription != null) {
            editHeading.setText(initialHeading);
            editDescription.setText(initialDescription);
        }

        buttonSave.setOnClickListener(v -> {
            String heading = editHeading.getText().toString();
            String description = editDescription.getText().toString();

            if (!heading.isEmpty() && !description.isEmpty()) {
                Note note = new Note(heading, description);
                if (noteListener != null) {
                    noteListener.onNoteAdded(note);
                }
                dismiss(); // Close the bottom sheet after saving
            }
        });

        return view;
    }

    public void setNoteListener(NoteListener listener) {
        this.noteListener = listener;
    }

    // Set the note data for editing
    public void setNoteData(String heading, String description) {
        this.initialHeading = heading;
        this.initialDescription = description;
    }
}
