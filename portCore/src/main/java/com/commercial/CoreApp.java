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

public class CoreApp extends Application {

    private static CoreApp coreApp;
    private static AppEventsLogger appEventsLogger;
    private static FirebaseAnalytics firebaseAnalytics;
    private static GoogleAnalytics googleAnalytics;
    private static Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        coreApp = this;
        firebaseAnalytics = FirebaseAnalytics.getInstance(coreApp);
        googleAnalytics = GoogleAnalytics.getInstance(coreApp);

        FacebookSdk.sdkInitialize(coreApp);
        AppEventsLogger.activateApp(coreApp);
        appEventsLogger = AppEventsLogger.newLogger(coreApp);

        YandexMetricaConfig config = YandexMetricaConfig.newConfigBuilder("bc4edd53-4781-4f03-864b-f19dbae0159f").build();
        YandexMetrica.activate(coreApp, config);
        YandexMetrica.enableActivityAutoTracking(coreApp);
        YandexMetricaPush.init(coreApp);

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
        AppsFlyerLib.getInstance().init("XWEZ8VQyFtu3RrPpGX9Lg8", conversionDataListener, coreApp);
        AppsFlyerLib.getInstance().startTracking(coreApp);
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

    public static CoreApp getCoreApp() {
        return coreApp;
    }
}
