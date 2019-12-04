package com.zhouzhou.locationgaode.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (null != intent){
            String action = intent.getAction();
            String test = intent.getStringExtra("test");
            if (true){
                Toast.makeText(context, test, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
