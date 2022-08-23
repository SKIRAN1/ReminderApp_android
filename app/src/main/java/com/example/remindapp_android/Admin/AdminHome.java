package com.example.remindapp_android.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.remindapp_android.Adapter.HomeAdapter;
import com.example.remindapp_android.Admin.Adapter.AdminHomeAdapter;
import com.example.remindapp_android.HomeActivity;
import com.example.remindapp_android.LoginActivity;
import com.example.remindapp_android.Model.Reminders;
import com.example.remindapp_android.Model.Users;
import com.example.remindapp_android.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminHome extends AppCompatActivity {

    FirebaseAuth mAuth;
    Button btn_adminLogout;
    RecyclerView rcViewAdminHome;
    AdminHomeAdapter adminHomeAdapter;
    ArrayList<Users> users;
    TextView adminTitle;
    ImageView btn_notifyUser;

    FirebaseFirestore myDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        adminTitle = findViewById(R.id.adminTitle);
        btn_notifyUser = findViewById(R.id.btn_notifyUser);
        btn_adminLogout = findViewById(R.id.btn_adminLogout);
        mAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();
        rcViewAdminHome = findViewById(R.id.rcViewAdminHome);
        users = new ArrayList<>();
        adminHomeAdapter = new AdminHomeAdapter(this,users);
        rcViewAdminHome.setLayoutManager(new LinearLayoutManager(this));
        rcViewAdminHome.setAdapter(adminHomeAdapter);
        adminHomeAdapter.notifyDataSetChanged();
        rcViewAdminHome.setHasFixedSize(true);


        adminTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHome.this, AdminHome.class);
                startActivity(intent);
            }
        });

        btn_adminLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(AdminHome.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btn_notifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminHome.this, NotifyUsers.class);
                startActivity(intent);
            }
        });

        GetUsers();
    }

    private void GetUsers() {
        myDatabase.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    Users user = documentSnapshot.toObject(Users.class);
                    users.add(user);
                    adminHomeAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}