package com.example.schooltaskmanagementsystem.Notes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltaskmanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class NoteActivity extends AppCompatActivity implements NoteAdapter.OnNoteDeleteListener, NoteAdapter.OnNoteEditListener {

    private List<Note> noteList = new ArrayList<>();
    private NoteAdapter noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_navigation_notes);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(noteList, this, this); // Pass both listeners
        recyclerView.setAdapter(noteAdapter);

        findViewById(R.id.fab_add_note).setOnClickListener(v -> {
            BottomSheetNoteFragment bottomSheet = new BottomSheetNoteFragment();
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });
    }

    public void addNoteToRecyclerView(Note note) {
        noteList.add(note);
        noteAdapter.notifyItemInserted(noteList.size() - 1);
    }

    @Override
    public void onNoteDelete(int position) {
        noteList.remove(position);
        noteAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onNoteEdit(int position) {
        Note note = noteList.get(position);
        BottomSheetNoteFragment bottomSheet = new BottomSheetNoteFragment();
        bottomSheet.setNoteListener((BottomSheetNoteFragment.NoteListener) this); // Set the listener to handle the note
        bottomSheet.setNoteData(note.getHeading(), note.getDescription()); // Pass the note data
        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }
}