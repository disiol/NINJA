package com.commercial;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class CommercialReceiver extends BroadcastReceiver implements View.OnClickListener {

    public static String referrer1 = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        referrer1 = intent.getStringExtra("referrer1");
    }

    @Override
    public void onClick(View view) {

    }
}
