package com.example.remindapp_android;

import static com.example.remindapp_android.Constants.TOPIC;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindapp_android.Adapter.HomeAdapter;
import com.example.remindapp_android.Geofence.GeofenceHelper;
import com.example.remindapp_android.Model.Reminders;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeActivity extends AppCompatActivity {

    ImageView profile;
    TextView title;
    RecyclerView rcViewHome;
    FloatingActionButton btn_addReminder;
    ArrayList<Reminders> reminders;
    HomeAdapter homeAdapter;
    LatLng latLng;
    Location location2;
    FirebaseFirestore myDatabase;
    FirebaseAuth mAuth;
    NotificationCompat.Builder builder;
    NotificationChannel channel;
    NotificationManager manager;
    GeofencingClient geofencingClient;
    float GEOFENCE_RADIUS = 200;
    GeofenceHelper geofenceHelper;
    String GEOFENCE_ID = "GEOFENCE_ID";
    Calendar calendar;
    public String date;
    GeoPoint geoPoint;
    FusedLocationProviderClient fusedLocationProviderClient;
    int FINE_LOCATION_CODE = 1001;

    //    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        double lat = 45.480942;
        double lon = -73.624069;
        latLng = new LatLng(lat, lon);
        geofencingClient = LocationServices.getGeofencingClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geofenceHelper = new GeofenceHelper(this);
        calendar = Calendar.getInstance();
        String Format = "dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(Format, Locale.CANADA);
        date = dateFormat.format(calendar.getTime());
        title = findViewById(R.id.title);
        profile = (ImageView) findViewById(R.id.profile);
        myDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        rcViewHome = findViewById(R.id.rcViewHome);
        rcViewHome.setLayoutManager(new LinearLayoutManager(this));
        reminders = new ArrayList<>();
        homeAdapter = new HomeAdapter(this, reminders);
        homeAdapter.notifyDataSetChanged();
        rcViewHome.setAdapter(homeAdapter);
        rcViewHome.setHasFixedSize(true);
        btn_addReminder = (FloatingActionButton) findViewById(R.id.btn_createReminder);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rcViewHome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager = getSystemService(NotificationManager.class);
            channel = new NotificationChannel("Admin", "Admin Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("This is my Channel");
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, "Admin")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle("Admin Notification")
                    .setContentText("Hey guys");
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC);
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
        getLocation();
        GetReminders();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    location2 = location;
                }
            });
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "granted", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
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
                    Intent intent = new Intent(HomeActivity.this, UpdateReminderActivity.class);
                    intent.putExtra("reminderDate", reminders.get(position).getDate());
                    intent.putExtra("reminderNotes", reminders.get(position).getNotes());
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
                    .addSwipeLeftBackgroundColor(Color.rgb(232, 0, 0))
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
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Reminders reminder = documentSnapshot.toObject(Reminders.class);
                    reminder.setId(documentSnapshot.getId());
                    reminders.add(reminder);
                    homeAdapter.notifyDataSetChanged();
                }
            }
        });
        //GetNotification();
        // addGeofence(latLng, GEOFENCE_RADIUS);
        // createGeofence();
    }


//    private void createGeofence() {
//        int i;
////        for(Reminders reminder : reminders ){
////            if(reminder.getDate() == date){
////                Toast.makeText(this, reminder.getLocation().toString(), Toast.LENGTH_SHORT).show();
////            }
////        }
//        for (i = 0; i < reminders.size(); i++) {
//            if (reminders.get(i).getDate() == date) {
//                Toast.makeText(this, reminders.get(i).getTitle(), Toast.LENGTH_SHORT).show();
//                GeoPoint geo = reminders.get(i).getLocation();
//                double lat = geo.getLatitude();
//                double lon = geo.getLongitude();
//                LatLng latLng2 = new LatLng(lat, lon);
//                addGeofence(latLng2, GEOFENCE_RADIUS);
//            }
//        }
//    }
//
//    private void GetNotification() {
//        manager.notify(1, builder.build());
//    }
//
//
//    private void addGeofence(LatLng latLng, float radius) {
//
//        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL| Geofence.GEOFENCE_TRANSITION_EXIT);
//        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
//        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();
//
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Toast.makeText(geofenceHelper, "Geofence Added", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("GEOFENCE", e.toString());
//                        Toast.makeText(HomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(geofenceHelper, "Geofence failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
    // }
}