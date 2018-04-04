package com.example.nakulnarwaria.courseregistration;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nakulNarwaria on 04-04-2018.
 */

public class RequestVolleyQueue {
    private static RequestQueue instance;
    private static Object obj = new Object();

    public static RequestQueue getInstance(Context context){
        synchronized (obj) {
            if(instance==null){
                instance = Volley.newRequestQueue(context.getApplicationContext());
            }
            return instance;
        }
    }

}
