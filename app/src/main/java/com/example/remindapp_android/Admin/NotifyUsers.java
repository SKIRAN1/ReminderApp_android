package com.example.remindapp_android.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.remindapp_android.Api.fcmSend;
import com.example.remindapp_android.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotifyUsers extends AppCompatActivity {

    TextView adminTitle;
    ImageView btn_notifyUser;
    FirebaseMessaging firebaseMessaging;
    FirebaseFirestore myDatabase;
    Button btn_notify;
    TextInputLayout et_notifyText;
    String text;
    FirebaseMessaging fm;
    long ID = 105710215396L;
    AtomicInteger msgId = new AtomicInteger();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nofity_users);

        adminTitle = findViewById(R.id.adminTitle);
        btn_notifyUser = findViewById(R.id.btn_notifyUser);
        firebaseMessaging = FirebaseMessaging.getInstance();
        myDatabase = FirebaseFirestore.getInstance();
        btn_notify = findViewById(R.id.btn_notify);
        et_notifyText = (TextInputLayout) findViewById(R.id.et_notificationText);
        fm = FirebaseMessaging.getInstance();

        adminTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyUsers.this, AdminHome.class);
                startActivity(intent);
            }
        });

        btn_notifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyUsers.this, NotifyUsers.class);
                startActivity(intent);
            }
        });

        btn_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Validation();
            }
        });

    }

    private void Validation() {
        if(!ValidateText()){
            return;
        }
        fcmSend.push(NotifyUsers.this, text);
//        PushNotification notification = new PushNotification(new NotificationData(text, "Remind"), TOPIC);
//        sendNotification(notification);
//        HashMap<String, Object> reminderdataMap = new HashMap<>();
//        reminderdataMap.put("message", text);
//        myDatabase.collection("notification").add(reminderdataMap);
//         RemoteMessage message = RemoteMessage.Builder()
//                .putData("title", "Remind Admin")
//                .putData("body", text)
//                .setTopic("Admin")
//                .build();
//        try {
//            FirebaseMessaging.getInstance().send(message);
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//        }



//        fm.send(new RemoteMessage.Builder(ID + "@fcm.googleapis.com")
//                .setMessageId(Integer.toString(msgId.incrementAndGet()))
//                .addData("my_message", "Hello World")
//                .addData("my_action","SAY_HELLO")
//                .build());

        Intent intent = new Intent(NotifyUsers.this, AdminHome.class);
        startActivity(intent);

    }


    private Boolean ValidateText() {
        text = et_notifyText.getEditText().getText().toString();
        if (TextUtils.isEmpty(text)) {
            et_notifyText.setError("Notification Text is Required");
            return false;
        }else{
            et_notifyText.setError(null);
            et_notifyText.setErrorEnabled(false);
            return true;
        }
    }
}