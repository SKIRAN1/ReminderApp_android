package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddReminderActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextInputLayout et_reminderDate,et_reminderTitle,et_reminderDescription;
    ImageView btn_calendar,profile;
    TextView tv_title;
    String date,title,description,email;
    FirebaseFirestore myDatabase;
    FirebaseAuth mAuth;
    Button btn_addReminder;
    Calendar calendar;
    SupportMapFragment supportMapFragment;
    String apikey;
    GoogleMap map;
    LatLng location;
    LatLng Montreal = new LatLng(45.480940, -73.624070);
    private ProgressDialog loadingBar;
//    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        apikey = "AIzaSyAtE3XUkS_CIw8lTVu-8bZXi_LtsvmO4jc";
        Places.initialize(getApplicationContext(), apikey);
        PlacesClient placesClient = Places.createClient(this);
        tv_title = findViewById(R.id.title);
        profile = findViewById(R.id.profile);
        btn_addReminder = findViewById(R.id.btn_addReminder);
        loadingBar = new ProgressDialog(this);

//        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setTypeFilter(TypeFilter.ADDRESS);
//        autocompleteSupportFragment.setLocationBias(RectangularBounds.newInstance(
//                new
//        ));
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(AddReminderActivity.this, "Error" + status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaceSelected(@NonNull Place place) {
//                AddPlace(place);
//              Montreal = place.getLatLng();
//              map.addMarker(new MarkerOptions()
//                      .position(Montreal)
//              );
//              supportMapFragment.getMapAsync(AddReminderActivity.this);
//                if (place.getLatLng() != null) {
////                    Montreal = place.getLatLng();
////                    supportMapFragment.getMapAsync(AddReminderActivity.this);
//////                    map.addMarker(new MarkerOptions()
//////                            .position(place.getLatLng()));
////                    Toast.makeText(AddReminderActivity.this, place.getAddress().toString(), Toast.LENGTH_SHORT).show();
//                }
            }
        });
        myDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        et_reminderDescription = (TextInputLayout) findViewById(R.id.et_reminderDescription);
        et_reminderTitle = (TextInputLayout) findViewById(R.id.et_reminderTitle);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);



        btn_calendar = findViewById(R.id.btn_calender);
//        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
//                .setTitleText("Select Date")
//                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                .build();
        et_reminderDate = (TextInputLayout) findViewById(R.id.et_reminderDate);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

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

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminderActivity.this,date,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


        tv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddReminderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddReminderActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                datePicker.show(getSupportFragmentManager(), "Material_Date_Picker");
//                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
//                    @Override
//                    public void onPositiveButtonClick(Object selection) {
//                        et_reminderDate.getEditText().setText(date);
//                    }
//                });
                datePickerDialog.show();

            }
        });

        btn_addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation();
            }
        });
    }

    private void AddPlace(Place place) {
//        map.addMarker(new MarkerOptions()
//                .position(place.getLatLng())
//        );
//        supportMapFragment.getMapAsync(AddReminderActivity.this);
    }

    private void updateCalendar() {
        String Format = "dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(Format , Locale.CANADA);

        et_reminderDate.getEditText().setText(dateFormat.format(calendar.getTime()));
    }

    private Boolean ValidateTitle() {
        title = et_reminderTitle.getEditText().getText().toString();
        if (TextUtils.isEmpty(title)) {
            et_reminderTitle.setError("Title is Required");
            return false;
        }else{
            et_reminderTitle.setError(null);
            et_reminderTitle.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean ValidateDescription() {
        description = et_reminderDescription.getEditText().getText().toString();
        if (TextUtils.isEmpty(description)) {
            et_reminderDescription.setError("Description is Required");
            return false;
        }else{
            et_reminderDescription.setError(null);
            et_reminderDescription.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean ValidateDate() {
        date = et_reminderDate.getEditText().getText().toString();
        if (TextUtils.isEmpty(date)) {
            et_reminderDate.setError("Date is Required");
            return false;
        }else{
            et_reminderDate.setError(null);
            et_reminderDate.setErrorEnabled(false);
            return true;
        }
    }

    private void Validation() {
        if(!ValidateTitle() | !ValidateDate()  | !ValidateDescription()){
            return;
        }
        loadingBar.setTitle("Adding Reminder");
        loadingBar.setMessage("Please wait until we are adding your reminder");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        AddReminder(title,date,description);

    }



    private void AddReminder(String title, String date, String description) {
        HashMap<String, Object> reminderdataMap = new HashMap<>();
        reminderdataMap.put("title", title);
        reminderdataMap.put("date", date);
        reminderdataMap.put("notes", description);
        reminderdataMap.put("location" , new GeoPoint(location.latitude, location.longitude));
        reminderdataMap.put("id",mAuth.getCurrentUser().getUid());

        myDatabase.collection("Reminders").document(mAuth.getCurrentUser().getUid()).collection("userReminders").add(reminderdataMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(AddReminderActivity.this, "Reminder Added", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                Intent intent = new Intent(AddReminderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions()
                .position(Montreal)
                .title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLng(Montreal));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Montreal , 15.3f));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng).title("Selected"));
                location = latLng;
            }
        });
    }

}