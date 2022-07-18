package com.example.remindapp_android.Model;

public class Reminders {
    String title,notes,date;

    public Reminders() {
    }

    public Reminders(String title, String notes, String date) {
        this.title = title;
        this.notes = notes;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
