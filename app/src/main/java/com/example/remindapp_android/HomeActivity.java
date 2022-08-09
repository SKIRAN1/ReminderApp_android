package com.example.remindapp_android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remindapp_android.Adapter.HomeAdapter;
import com.example.remindapp_android.Model.Reminders;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ImageView profile;
    TextView title;
    RecyclerView rcViewHome;
    FloatingActionButton btn_addReminder;
    ArrayList<Reminders> reminders;
    HomeAdapter homeAdapter;

    FirebaseFirestore myDatabase;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        title = findViewById(R.id.title);
        profile = (ImageView) findViewById(R.id.profile);
        myDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        rcViewHome = findViewById(R.id.rcViewHome);
        rcViewHome.setLayoutManager(new LinearLayoutManager(this));
        reminders = new ArrayList<>();
        homeAdapter = new HomeAdapter(this,reminders);
        homeAdapter.notifyDataSetChanged();
        rcViewHome.setAdapter(homeAdapter);
        rcViewHome.setHasFixedSize(true);
        btn_addReminder = (FloatingActionButton) findViewById(R.id.btn_createReminder);


        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btn_addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AddReminderActivity.class);
                startActivity(intent);
            }
        });

        GetReminders();
    }

    private void GetReminders() {
        myDatabase.collection("reminders").document(mAuth.getCurrentUser().getUid()).collection("userReminders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    Reminders reminder = documentSnapshot.toObject(Reminders.class);
                    reminders.add(reminder);
                    homeAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}