package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindapp_android.Adapter.HomeAdapter;
import com.example.remindapp_android.Model.Reminders;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeActivity extends AppCompatActivity {

    ImageView profile;
    TextView title;
    RecyclerView rcViewHome;
    FloatingActionButton btn_addReminder;
    ArrayList<Reminders> reminders;
    HomeAdapter homeAdapter;

    FirebaseFirestore myDatabase;
    FirebaseAuth mAuth;
    Location location;

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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcViewHome);



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

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
           switch (direction){
               case ItemTouchHelper.LEFT:
                   myDatabase.collection("Reminders").document(mAuth.getCurrentUser().getUid()).collection("userReminders").document(reminders.get(position).getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           Toast.makeText(HomeActivity.this, "Reminder deleted", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                           startActivity(intent);
                       }
                   });
                   break;
               case ItemTouchHelper.RIGHT:
                   Intent intent = new Intent(HomeActivity.this,UpdateReminderActivity.class);
                   intent.putExtra("reminderDate", reminders.get(position).getDate());
                   intent.putExtra("reminderNotes",reminders.get(position).getNotes());
                   intent.putExtra("reminderTitle", reminders.get(position).getTitle());
                   intent.putExtra("reminderId", reminders.get(position).getId());
//                   location.setLatitude(reminders.get(position).getLocation().getLatitude());
//                   location.setLongitude(reminders.get(position).getLocation().getLongitude());
//                   intent.putExtra("userLocation", location);
//                   intent.putExtra("reminderLatitude", reminders.get(position).getLocation().getLatitude());
//                   intent.putExtra("reminderLongitude", reminders.get(position).getLocation().getLongitude());
                  // intent.putExtra("reminderLocation" , (Parcelable) reminders.get(position).getLocation());
                   startActivity(intent);
                   break;
           }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(Color.rgb(232,0,0))
                    .addSwipeRightBackgroundColor(R.color.main)
                    .addSwipeLeftLabel("Delete")
                    .addSwipeRightLabel("Edit")
                    .create()
                    .decorate();


            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void GetReminders() {
        myDatabase.collection("Reminders").document(mAuth.getCurrentUser().getUid()).collection("userReminders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    Reminders reminder = documentSnapshot.toObject(Reminders.class);
                    reminder.setId(documentSnapshot.getId());
                    reminders.add(reminder);
                    homeAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}