package com.example.remindapp_android.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindapp_android.Model.Reminders;
import com.example.remindapp_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    Context context;
    ArrayList<Reminders> reminders;
    FirebaseFirestore myDatabase;
    FirebaseAuth mAuth;
    public HomeAdapter(Context context, ArrayList<Reminders> reminders) {
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Reminders reminder = reminders.get(position);
        holder.tv_reminderTitle.setText(reminder.getTitle());
        holder.tv_reminderDate.setText(reminder.getDate());
        holder.tv_reminderNotes.setText(reminder.getNotes());
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_reminderTitle,tv_reminderNotes,tv_reminderDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_reminderTitle = itemView.findViewById(R.id.tv_reminderTitle);
            tv_reminderNotes = itemView.findViewById(R.id.tv_reminderNotes);
            tv_reminderDate = itemView.findViewById(R.id.tv_reminderDate);
        }
    }
}
