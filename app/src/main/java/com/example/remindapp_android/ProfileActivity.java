package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    TextView title,tv_changePassword;
    TextInputLayout et_userAccountEmail,et_userAccountPhone,et_userAccountName;
    ImageView profile;
    Button btn_signOut;
    String email,phone,name;
    FirebaseAuth mAuth;
    FirebaseFirestore myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile = findViewById(R.id.profile);
        title = (TextView) findViewById(R.id.title);
        btn_signOut = findViewById(R.id.btn_signOut);
        mAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();
        tv_changePassword = findViewById(R.id.tv_changePassword);
        et_userAccountEmail = (TextInputLayout) findViewById(R.id.et_userAccountEmail);
        et_userAccountName = (TextInputLayout) findViewById(R.id.et_userAccountName);
        et_userAccountPhone = (TextInputLayout) findViewById(R.id.et_userAccountPhone);

        tv_changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ProfileActivity.this, "Change Password link sent to your email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        btn_signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogout();
            }
        });

        myDatabase.collection("users").document(mAuth.getCurrentUser().getUid()).addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                et_userAccountEmail.getEditText().setText(documentSnapshot.getString("email"));
                email = documentSnapshot.getString("email");
                et_userAccountName.getEditText().setText(documentSnapshot.getString("name"));
                et_userAccountPhone.getEditText().setText(documentSnapshot.getString("phone"));
            }
        });
    }

    private void UserLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}