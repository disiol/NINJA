package com.commercial;

import android.app.Application;

import android.view.View;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

import java.util.Map;

public class CommercialApp extends Application implements View.OnClickListener {

    private static CommercialApp commercialApp1;
    private static AppEventsLogger appEventsLogger1;
    private static FirebaseAnalytics firebaseAnalytics1;
    private static GoogleAnalytics googleAnalytics1;
    private static Tracker tracker1;

    @Override
    public void onCreate() {
        super.onCreate();
        commercialApp1 = this;
        firebaseAnalytics1 = FirebaseAnalytics.getInstance(commercialApp1);
        googleAnalytics1 = GoogleAnalytics.getInstance(commercialApp1);

        FacebookSdk.sdkInitialize(commercialApp1);
        AppEventsLogger.activateApp(commercialApp1);
        appEventsLogger1 = AppEventsLogger.newLogger(commercialApp1);

        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("bc4edd53-4781-4f03-864b-f19dbae0159f").build();
        YandexMetrica.activate(commercialApp1, config);
        YandexMetrica.enableActivityAutoTracking(commercialApp1);
        YandexMetricaPush.init(commercialApp1);

        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {

            }

            @Override
            public void onInstallConversionFailure(String s) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {

            }
        };
        AppsFlyerLib.getInstance().init("XWEZ8VQyFtu3RrPpGX9Lg8", conversionDataListener, commercialApp1);
        AppsFlyerLib.getInstance().startTracking(commercialApp1);
    }

    public static FirebaseAnalytics getFirebaseAnalytics1() {
        return firebaseAnalytics1;
    }

    public static AppEventsLogger getAppEventsLogger1() {
        return appEventsLogger1;
    }

    synchronized public Tracker getDefaultTracker() {
        if (tracker1 == null) {
            tracker1 = googleAnalytics1.newTracker("UA-132064222-1");
        }
        return tracker1;
    }

    public static CommercialApp getCommercialApp1() {
        return commercialApp1;
    }

    @Override
    public void onClick(View view) {

    }
}
