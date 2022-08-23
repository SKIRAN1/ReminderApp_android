package com.example.remindapp_android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotificationService extends FirebaseMessagingService {
Context context;
NotificationManager manager;
NotificationCompat.Builder builder;
    //@SuppressLint("NewApi")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
       // super.onMessageReceived(message);
       if(message.getNotification() != null) {
           createNotificationChannel(message.getNotification().getTitle(), message.getNotification().getBody());
       }


      //  NotificationHelper notificationHelper = new NotificationHelper(this);
     //   notificationHelper.sendHighPriorityNotification(message.getNotification().getTitle(),message.getNotification().getBody());
   //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // createNotificationChannel(message.getNotification().getTitle() , message.getNotification().getBody());
   //     }
//
//
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivities(this,0,new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);
//        Notification notification;
//        notification = new NotificationCompat.Builder(this,"MyNotification")
//                .setContentTitle(message.getNotification().getTitle())
//                .setContentText(message.getNotification().getBody())
//                .setSmallIcon(R.drawable.logo)
//                .setAutoCancel(true)
//                .build();
//
//        manager.notify(notificationId,notification);


//        Toast.makeText(this, message.getNotification().getBody(), Toast.LENGTH_SHORT).show();
//
//            String title = message.getNotification().getTitle();
//            String body = message.getNotification().getBody();
//           // sendNotification(title, body);
//        NotificationManager manager = getSystemService(NotificationManager.class);
//        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, "MyNotification")
//                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(title)
//                .setContentText(body);
//        manager.notify(1, builder2.build());
//        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
//                .setTitle("Admin Alert")
//                .setMessage(body);
//        builder.setPositiveButton(
//                "Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//        final  String CHANNEL_ID = "NOTIFICATIONS_ADMIN";
//         NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"MyNotification", NotificationManager.IMPORTANCE_HIGH);
//        getSystemService(NotificationManager.class).createNotificationChannel(channel);
//
//        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID).setContentTitle(title).setContentText(body).setSmallIcon(R.drawable.logo).setAutoCancel(true);
//
//        NotificationManagerCompat.from(this).notify(1,notification.build());


    }

    private void createNotificationChannel(String title , String body) {

//        NotificationHelper notificationHelper = new NotificationHelper(context);
//        notificationHelper.sendHighPriorityNotification(title,body);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            manager = getSystemService(NotificationManager.class);
//            channel = new NotificationChannel(CHANNEL_ID, "AdminNotification", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("This is Admin Channel");
//            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, "Admin")
                    .setSmallIcon(R.drawable.logo)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(Notification.PRIORITY_HIGH);

        manager.notify(1, builder.build());

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

//    private void sendNotification(String title, String body) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            NotificationChannel channel2 = new NotificationChannel("Admin", "MyAdminNotification", NotificationManager.IMPORTANCE_HIGH);
//            channel2.setDescription("This is my admin Channel");
//            manager.createNotificationChannel(channel2);
//
//            NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, "Admin")
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentTitle(title)
//                    .setContentText(body);
//            manager.notify(20, builder2.build());
////        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
////                .setTitle("Admin Alert")
////                .setMessage(body);
////        builder.setPositiveButton(
////                "Yes",
////                new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        dialog.cancel();
////                    }
////                });
////        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
       // }
        }

//    private void sendNotification(title,body) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//            manager = getSystemService(NotificationManager.class);
//            channel = new NotificationChannel("MyNotification", "MyNotification", NotificationManager.IMPORTANCE_HIGH);
//            channel.setDescription("This is my Channel");
//            manager.createNotificationChannel(channel);
//
//            builder = new NotificationCompat.Builder(this, "MyNotification")
//                    .setSmallIcon(R.drawable.logo)
//                    .setContentTitle("Admin Notification")
//                    .setContentText("Hey guys");
//        }
//        manager.notify(1, builder.build());
//    }



