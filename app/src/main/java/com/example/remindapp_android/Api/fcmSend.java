package com.example.remindapp_android.Api;

import static com.example.remindapp_android.Constants.BASE_URL;
import static com.example.remindapp_android.Constants.SERVER_KEY;
import static com.example.remindapp_android.Constants.TOPIC;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class fcmSend {

    private static String URL = "https://fcm.googleapis.com/fcm/send";

    public static void push(Context context, String message) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            JSONObject json = new JSONObject();
            json.put("to", TOPIC);
            JSONObject notification = new JSONObject();
            notification.put("title", "Remind Admin");
            notification.put("body", message);
            json.put("notification",notification);


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("FCM"+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String , String> params = new HashMap<>();
                    params.put("Content-Type","application/json");
                    params.put("Authorization", "key="+SERVER_KEY);
                    return params;
                }
            };

            queue.add(jsonObjectRequest);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

}

