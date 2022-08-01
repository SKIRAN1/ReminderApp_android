package com.example.remindapp_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AddReminderActivity extends AppCompatActivity {

    TextInputLayout et_reminderDate,et_reminderTitle,et_reminderDescription;
    ImageView btn_calendar,profile;
    TextView tv_title;
    String date,title,description,email;
    FirebaseFirestore myDatabase;
    FirebaseAuth mAuth;
    Button btn_addReminder;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);
        tv_title = findViewById(R.id.title);
        profile = findViewById(R.id.profile);
        btn_addReminder = findViewById(R.id.btn_addReminder);
        loadingBar = new ProgressDialog(this);
        myDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        et_reminderDescription = (TextInputLayout) findViewById(R.id.et_reminderDescription);
        et_reminderTitle = (TextInputLayout) findViewById(R.id.et_reminderTitle);
        btn_calendar = findViewById(R.id.btn_calender);
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        et_reminderDate = (TextInputLayout) findViewById(R.id.et_reminderDate);
        email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

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
                datePicker.show(getSupportFragmentManager(), "Material_Date_Picker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        et_reminderDate.getEditText().setText(datePicker.getHeaderText());
                    }
                });
            }
        });

        btn_addReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Validation();
            }
        });
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

        myDatabase.collection("reminders").document(mAuth.getCurrentUser().getUid()).collection("userReminders").add(reminderdataMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(AddReminderActivity.this, "Reminder Added", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
                Intent intent = new Intent(AddReminderActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }


}