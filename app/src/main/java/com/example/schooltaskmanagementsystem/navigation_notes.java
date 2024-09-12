package com.example.schooltaskmanagementsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltaskmanagementsystem.Notes.BottomSheetNoteFragment;
import com.example.schooltaskmanagementsystem.Notes.Note;
import com.example.schooltaskmanagementsystem.Notes.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class navigation_notes extends Fragment implements NoteAdapter.OnNoteDeleteListener, NoteAdapter.OnNoteEditListener, BottomSheetNoteFragment.NoteListener {

    private static final String PREFS_NAME = "notes_prefs";
    private static final String KEY_NOTES = "notes";
    private List<Note> noteList = new ArrayList<>();
    private NoteAdapter noteAdapter;
    private Note noteToEdit;
    private int noteToEditPosition;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_notes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_notes);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        noteAdapter = new NoteAdapter(noteList, this, this); // Pass both listeners
        recyclerView.setAdapter(noteAdapter);

        FloatingActionButton fabAddNote = view.findViewById(R.id.fab_add_note);
        fabAddNote.setOnClickListener(v -> {
            noteToEdit = null; // Clear edit note
            BottomSheetNoteFragment bottomSheet = new BottomSheetNoteFragment();
            bottomSheet.setNoteListener(this); // Set the listener to handle the note
            bottomSheet.show(getFragmentManager(), bottomSheet.getTag());
        });

        loadNotes();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveNotes();
    }

    @Override
    public void onNoteAdded(Note note) {
        if (noteToEdit == null) {
            addNoteToRecyclerView(note);
        } else {
            noteList.set(noteToEditPosition, note);
            noteAdapter.notifyItemChanged(noteToEditPosition);
        }
    }

    @Override
    public void onNoteEdit(int position) {
        noteToEdit = noteList.get(position);
        noteToEditPosition = position;
        BottomSheetNoteFragment bottomSheet = new BottomSheetNoteFragment();
        bottomSheet.setNoteListener(this); // Set the listener to handle the note
        bottomSheet.setNoteData(noteToEdit.getHeading(), noteToEdit.getDescription()); // Pass the note data
        bottomSheet.show(getFragmentManager(), bottomSheet.getTag());
    }

    private void saveNotes() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        StringBuilder notesString = new StringBuilder();
        for (Note note : noteList) {
            notesString.append(note.getHeading()).append("|").append(note.getDescription()).append(",");
        }

        if (notesString.length() > 0) {
            notesString.setLength(notesString.length() - 1); // Remove trailing comma
        }

        editor.putString(KEY_NOTES, notesString.toString());
        editor.apply();
    }

    private void loadNotes() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String notesString = sharedPreferences.getString(KEY_NOTES, "");

        if (!notesString.isEmpty()) {
            String[] notesArray = notesString.split(",");
            for (String noteStr : notesArray) {
                String[] noteParts = noteStr.split("\\|");
                if (noteParts.length == 2) {
                    noteList.add(new Note(noteParts[0], noteParts[1]));
                }
            }
            noteAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNoteDelete(int position) {
        noteList.remove(position);
        noteAdapter.notifyItemRemoved(position);
    }

    public void addNoteToRecyclerView(Note note) {
        noteList.add(note);
        noteAdapter.notifyItemInserted(noteList.size() - 1);
    }
}

