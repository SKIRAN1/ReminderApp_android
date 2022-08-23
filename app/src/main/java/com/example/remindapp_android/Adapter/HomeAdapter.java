package com.example.remindapp_android.Adapter;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindapp_android.Geofence.GeofenceBroadcastReceiver;
import com.example.remindapp_android.Geofence.GeofenceHelper;
import com.example.remindapp_android.HomeActivity;
import com.example.remindapp_android.Model.Reminders;
import com.example.remindapp_android.R;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    Context context;
    ArrayList<Reminders> reminders;
    Calendar calendar;
    String date;
    float GEOFENCE_RADIUS = 200;
    GeofenceHelper geofenceHelper;
    String GEOFENCE_ID = "GEOFENCE_ID2";
    GeofencingClient geofencingClient;
    public String title;
    public  String body;

    public HomeAdapter(Context context, ArrayList<Reminders> reminders) {
        this.context = context;
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Reminders reminder = reminders.get(position);
        holder.tv_reminderTitle.setText(reminder.getTitle());
        holder.tv_reminderDate.setText(reminder.getDate());
        holder.tv_reminderNotes.setText(reminder.getNotes());
        geofenceHelper = new GeofenceHelper(context);
        geofencingClient = LocationServices.getGeofencingClient(context);
        calendar = Calendar.getInstance();
        String Format = "dd-MM-YYYY";
        SimpleDateFormat dateFormat = new SimpleDateFormat(Format, Locale.CANADA);
        date = dateFormat.format(calendar.getTime());
        if(reminder.getDate().equals(date)){
            title = reminder.getTitle();
            body = reminder.getNotes();
            GeoPoint geo = reminder.getLocation();
            double lat = geo.getLatitude();
            double lon = geo.getLongitude();
            LatLng latLng2 = new LatLng(lat, lon);
            Toast.makeText(context, latLng2.toString(), Toast.LENGTH_SHORT).show();
            Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID,latLng2, GEOFENCE_RADIUS, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
            GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
            Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("body", body);
            PendingIntent pendingIntent = geofenceHelper.getPendingIntent(intent);


            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(geofenceHelper, "Geofence Added for " +title+" Reminder" , Toast.LENGTH_SHORT).show();
//                            Log.e("Fence", geofencingRequest.getGeofences().toString());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("GEOFENCE", e.toString());
                            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(geofenceHelper, "Geofence failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    private void userNotify() {

    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_reminderTitle,tv_reminderNotes,tv_reminderDate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_reminderTitle = itemView.findViewById(R.id.tv_reminderTitle);
            tv_reminderNotes = itemView.findViewById(R.id.tv_reminderNotes);
            tv_reminderDate = itemView.findViewById(R.id.tv_reminderDate);
        }
    }
}