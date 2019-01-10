package com.commercial;

import android.view.View;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yandex.metrica.push.firebase.MetricaMessagingService;

public class CommercialMessagingService extends FirebaseMessagingService implements View.OnClickListener {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        new MetricaMessagingService().processPush(this, message);
    }

    @Override
    public void onClick(View view) {

    }
}
