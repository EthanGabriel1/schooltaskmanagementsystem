package com.example.schooltaskmanagementsystem.Notes;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schooltaskmanagementsystem.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<Note> notes;
    private final OnNoteDeleteListener onNoteDeleteListener;
    private final OnNoteEditListener onNoteEditListener; // Listener for edit

    public NoteAdapter(List<Note> notes, OnNoteDeleteListener onNoteDeleteListener, OnNoteEditListener onNoteEditListener) {
        this.notes = notes;
        this.onNoteDeleteListener = onNoteDeleteListener;
        this.onNoteEditListener = onNoteEditListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.headingTextView.setText(note.getHeading());
        holder.descriptionTextView.setText(note.getDescription());

        // Set long click listener for delete action
        holder.itemView.setOnLongClickListener(v -> {
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Trigger delete callback
                        onNoteDeleteListener.onNoteDelete(position);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        // Set click listener for edit action
        holder.itemView.setOnClickListener(v -> {
            onNoteEditListener.onNoteEdit(position);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView headingTextView;
        TextView descriptionTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            headingTextView = itemView.findViewById(R.id.note_heading);
            descriptionTextView = itemView.findViewById(R.id.note_description);
        }
    }

    // Interface for delete action
    public interface OnNoteDeleteListener {
        void onNoteDelete(int position);
    }

    // Interface for edit action
    public interface OnNoteEditListener {
        void onNoteEdit(int position);
    }
}