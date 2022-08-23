package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class UpdateReminderActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextInputLayout et_userReminderDate, et_userReminderTitle, et_userReminderDescription;
    ImageView btn_calendar, profile;
    TextView tv_title;
    String date, title, description, id;
    Button btn_updateReminder;
    Calendar calendar;
    SupportMapFragment supportMapFragment;

    GoogleMap map;
    Location location;
    LatLng reminderLocation;

    FirebaseFirestore myDatabase;
    FirebaseAuth mAuth;


    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);


        tv_title = findViewById(R.id.title);
        profile = findViewById(R.id.profile);
        btn_updateReminder = findViewById(R.id.btn_updateReminder);
        loadingBar = new ProgressDialog(this);
        et_userReminderDate = (TextInputLayout) findViewById(R.id.et_userReminderDate);
        et_userReminderDescription = (TextInputLayout) findViewById(R.id.et_userReminderDescription);
        et_userReminderTitle = (TextInputLayout) findViewById(R.id.et_userReminderTitle);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map2);
        supportMapFragment.getMapAsync(this);
        myDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        reminderLocation = new LatLng(45.480940, -73.624070);
//
        date = getIntent().getStringExtra("reminderDate");
        title = getIntent().getStringExtra("reminderTitle");
        description = getIntent().getStringExtra("reminderNotes");
        location = getIntent().getParcelableExtra("userLocation");
        id = getIntent().getStringExtra("reminderId");
//
        et_userReminderDate.getEditText().setText(date);
        et_userReminderTitle.getEditText().setText(title);
        et_userReminderDescription.getEditText().setText(description);

//
        btn_calendar = findViewById(R.id.btn_calender);
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

//
        calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }
        };
//
        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateReminderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateReminderActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
//
        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(UpdateReminderActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_updateReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation();
            }
        });
    }

    private void updateCalendar() {
        String Format = "dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(Format, Locale.CANADA);

        et_userReminderDate.getEditText().setText(dateFormat.format(calendar.getTime()));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        map.addMarker(new MarkerOptions()
//                .position(latLng)
//                .title("Reminder Location")
//        );
//        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        LatLng Montreal = new LatLng(45.480940, -73.624070);
        map.addMarker(new MarkerOptions()
                .position(Montreal)
                .title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLng(Montreal));
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Montreal , 15f));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng).title("Selected"));
                reminderLocation = latLng;
            }
        });
    }

    private Boolean ValidateTitle() {
        title = et_userReminderTitle.getEditText().getText().toString();
        if (TextUtils.isEmpty(title)) {
            et_userReminderTitle.setError("Title is Required");
            return false;
        }else{
            et_userReminderTitle.setError(null);
            et_userReminderTitle.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean ValidateDescription() {
        description = et_userReminderDescription.getEditText().getText().toString();
        if (TextUtils.isEmpty(description)) {
            et_userReminderDescription.setError("Description is Required");
            return false;
        }else{
            et_userReminderDescription.setError(null);
            et_userReminderDescription.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean ValidateDate() {
        date = et_userReminderDate.getEditText().getText().toString();
        if (TextUtils.isEmpty(date)) {
            et_userReminderDate.setError("Date is Required");
            return false;
        }else{
            et_userReminderDate.setError(null);
            et_userReminderDate.setErrorEnabled(false);
            return true;
        }
    }

//    private Boolean ValidateLocation() {
//        if (reminderLocation == null) {
//            et_userReminderDate.setError("Date is Required");
//            return false;
//        }else{
//            et_userReminderDate.setError(null);
//            et_userReminderDate.setErrorEnabled(false);
//            return true;
//        }

    private void Validation() {
        if(!ValidateTitle() | !ValidateDate()  | !ValidateDescription()){
            return;
        }
        loadingBar.setTitle("Updating Reminder");
        loadingBar.setMessage("Please wait until we are updating your reminder");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        UpdateReminder(title,date,description);
    }

    private void UpdateReminder(String title, String date, String description) {
        HashMap<String, Object> reminderUpdateMap = new HashMap<>();
        reminderUpdateMap.put("title", title);
        reminderUpdateMap.put("date", date);
        reminderUpdateMap.put("notes", description);
        reminderUpdateMap.put("location" , new GeoPoint(reminderLocation.latitude, reminderLocation.longitude));


        myDatabase.collection("Reminders").document(mAuth.getCurrentUser().getUid()).collection("userReminders").document(id).set(reminderUpdateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UpdateReminderActivity.this, "Reminder Updated", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                Intent intent = new Intent(UpdateReminderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


    }


}
