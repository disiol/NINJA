package com.commercial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CommercialReceiver extends BroadcastReceiver {

    public static String referrer = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        referrer = intent.getStringExtra("referrer");
    }
}
