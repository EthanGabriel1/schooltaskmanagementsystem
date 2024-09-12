package com.example.schooltaskmanagementsystem.Notes;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private String heading;
    private String description;

    public Note(String heading, String description) {
        this.heading = heading;
        this.description = description;
    }

    protected Note(Parcel in) {
        heading = in.readString();
        description = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(heading);
        dest.writeString(description);
    }
}