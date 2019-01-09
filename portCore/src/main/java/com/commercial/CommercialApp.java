package com.commercial;

import android.app.Application;

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

public class CommercialApp extends Application {

    private static CommercialApp commercialApp;
    private static AppEventsLogger appEventsLogger;
    private static FirebaseAnalytics firebaseAnalytics;
    private static GoogleAnalytics googleAnalytics;
    private static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        commercialApp = this;
        firebaseAnalytics = FirebaseAnalytics.getInstance(commercialApp);
        googleAnalytics = GoogleAnalytics.getInstance(commercialApp);

        FacebookSdk.sdkInitialize(commercialApp);
        AppEventsLogger.activateApp(commercialApp);
        appEventsLogger = AppEventsLogger.newLogger(commercialApp);

        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("bc4edd53-4781-4f03-864b-f19dbae0159f").build();
        YandexMetrica.activate(commercialApp, config);
        YandexMetrica.enableActivityAutoTracking(commercialApp);
        YandexMetricaPush.init(commercialApp);

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
        AppsFlyerLib.getInstance().init("XWEZ8VQyFtu3RrPpGX9Lg8", conversionDataListener, commercialApp);
        AppsFlyerLib.getInstance().startTracking(commercialApp);
    }

    public static FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    public static AppEventsLogger getAppEventsLogger() {
        return appEventsLogger;
    }

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            tracker = googleAnalytics.newTracker("UA-132064222-1");
        }
        return tracker;
    }

    public static CommercialApp getCommercialApp() {
        return commercialApp;
    }
}
