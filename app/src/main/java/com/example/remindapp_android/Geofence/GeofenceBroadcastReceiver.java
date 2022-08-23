package com.example.remindapp_android.Geofence;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.example.remindapp_android.Adapter.HomeAdapter;
import com.example.remindapp_android.NotificationHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    HomeAdapter homeAdapter;
    String title1,body1;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
       Toast.makeText(context, "Geofence Triggered", Toast.LENGTH_SHORT).show();
        title1 = intent.getStringExtra("title");
        body1 = intent.getStringExtra("body");
//        title1 = homeAdapter.title;
//        body1 = homeAdapter.body;
        NotificationHelper notificationHelper = new NotificationHelper(context);
//        notificationHelper.sendHighPriorityNotification("Hey","testing", HomeActivity.class);
       // notifyUser();
//
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
////
        if(geofencingEvent.hasError()){
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            return;
        }
////
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
//
        Location location = geofencingEvent.getTriggeringLocation();
//        Toast.makeText(context, location.toString(), Toast.LENGTH_SHORT).show();
        Log.e("Lcatnikf", location.toString());
        for(Geofence geofence: geofenceList){
            Log.e("TAG", "onReceive" + geofence.getRequestId());
        }
////
        int type = geofencingEvent.getGeofenceTransition();
//
        switch (type){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "Geofence Entered", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification(title1,body1);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "Geofence Inside", Toast.LENGTH_SHORT).show();
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "Geofence Exited", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}