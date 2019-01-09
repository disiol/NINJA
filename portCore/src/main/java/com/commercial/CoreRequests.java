package com.commercial;

import android.os.AsyncTask;
import android.os.Bundle;

import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerLib;
import com.facebook.appevents.AppEventsConstants;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.yandex.metrica.YandexMetrica;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class CoreRequests {

    public static String address;

    public class CoreResult extends AsyncTask<Void, Void, JSONObject> {

        private RequestBody requestBody;

        public CoreResult(String application, String country, int tz, String os, String referrer) {
            requestBody = new FormBody.Builder()
                    .add("application", application)
                    .add("country", country)
                    .add("tz", String.valueOf(tz))
                    .add("os", os)
                    .add("referrer", referrer)
                    .build();
        }

        public CoreResult(int id, String country, int tz) {
            requestBody = new FormBody.Builder()
                    .add("id", String.valueOf(id))
                    .add("country", country)
                    .add("tz", String.valueOf(tz))
                    .build();
        }

        @Override
        protected JSONObject doInBackground(Void[] object) {
            try {
                return new JSONObject(new OkHttpClient().newCall(new Request.Builder().url(address).post(requestBody).build()).execute().body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class CoreSetClickId extends AsyncTask<Void, Void, Void> {

        private int id;
        private String click_id;

        public CoreSetClickId(int id, String click_id) {
            this.id = id;
            this.click_id = click_id;
        }

        @Override
        protected Void doInBackground(Void[] object) {
            try {
                new OkHttpClient().newCall(new Request.Builder().url(address + "?id=" + id + "&clickid=" + click_id).build()).execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class CoreEvents extends AsyncTask<Void, Void, Void> {

        private int id;

        public CoreEvents(int id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void[] object) {
            try {
                JSONArray eventsArray = new JSONArray(new OkHttpClient().newCall(new Request.Builder().url(address + "?id=" + id + "&getEvents=true").build()).execute().body().string());
                for (int i = 0; i < eventsArray.length(); i++) {
                    boolean presentGoal = true;
                    String goal = eventsArray.getJSONObject(i).getString("goal");
                    String offerid = eventsArray.getJSONObject(i).getString("offerid");
                    String sum = eventsArray.getJSONObject(i).getString("sum");
                    String depsum = eventsArray.getJSONObject(i).getString("depsum");
                    String status = eventsArray.getJSONObject(i).getString("status");
                    String currency = eventsArray.getJSONObject(i).getString("currency");
                    String pid = eventsArray.getJSONObject(i).getString("pid");

                    HitBuilders.EventBuilder googleEvents = new HitBuilders.EventBuilder();
                    String label = "";

                    Bundle firebaseParams = new Bundle();
                    Bundle facebookParams = new Bundle();
                    Map<String, Object> appsflyerValues = new HashMap<>();
                    Map<String, Object> appmetricaValues = new HashMap<>();

                    if (goal == null || goal.isEmpty()) {
                        goal = "conversion";
                        presentGoal = false;
                    }
                    label += "goal=" + goal + ";";

                    if (offerid != null && !offerid.isEmpty() && !offerid.equals("-1")) {
                        firebaseParams.putString("offerid", offerid);
                        facebookParams.putString("offerid", offerid);
                        appmetricaValues.put("offerid", offerid);
                        appsflyerValues.put("offerid", offerid);
                        label += "offerid=" + offerid + ";";
                    }
                    if (depsum != null && !depsum.isEmpty() && !depsum.equals("-1")) {
                        firebaseParams.putString("depsum", depsum);
                        appmetricaValues.put("depsum", depsum);
                        appsflyerValues.put(AFInAppEventParameterName.REVENUE, depsum);
                        googleEvents.setValue(Long.parseLong(depsum));
                        label += "depsum=" + depsum + ";";
                    }
                    if (status != null && !status.isEmpty() && !status.equals("-1")) {
                        firebaseParams.putString("status", status);
                        facebookParams.putString("status", status);
                        appmetricaValues.put("status", status);
                        appsflyerValues.put("status", status);
                        label += "status=" + status + ";";
                    }
                    if (currency != null && !currency.isEmpty() && !currency.equals("-1")) {
                        firebaseParams.putString("currency", currency);
                        facebookParams.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency);
                        appmetricaValues.put("currency", currency);
                        appsflyerValues.put(AFInAppEventParameterName.CURRENCY, currency);
                        label += "currency=" + currency + ";";
                    }
                    if (pid != null && !pid.isEmpty() && !pid.equals("-1")) {
                        googleEvents.setAction(String.valueOf(pid));
                        firebaseParams.putString("pid", pid);
                        facebookParams.putString("pid", pid);
                        appmetricaValues.put("pid", pid);
                        appsflyerValues.put("pid", pid);
                        label += "pid=" + pid + ";";
                    }
                    if (!presentGoal) {
                        if (sum != null && !sum.isEmpty() && !sum.equals("-1")) {
                            facebookParams.putString(AppEventsConstants.EVENT_PARAM_LEVEL, sum);
                        }
                        CoreApp.getAppEventsLogger().logEvent(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL, facebookParams);
                    } else {
                        switch (goal) {
                            case "reg":
                                if (sum != null && !sum.isEmpty() && !sum.equals("-1")) {
                                    facebookParams.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, sum);
                                }
                                CoreApp.getAppEventsLogger().logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, facebookParams);
                                break;
                            case "fd":
                                if (sum != null && !sum.isEmpty() && !sum.equals("-1")) {
                                    facebookParams.putString(AppEventsConstants.EVENT_PARAM_VALUE_TO_SUM, sum);
                                }
                                CoreApp.getAppEventsLogger().logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_TUTORIAL, facebookParams);
                                break;
                            case "dep":
                                if (sum != null && !sum.isEmpty() && !sum.equals("-1")) {
                                    facebookParams.putString(AppEventsConstants.EVENT_PARAM_VALUE_TO_SUM, sum);
                                }
                                CoreApp.getAppEventsLogger().logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, facebookParams);
                                break;
                            default:
                                if (sum != null && !sum.isEmpty() && !sum.equals("-1")) {
                                    facebookParams.putString(AppEventsConstants.EVENT_PARAM_LEVEL, sum);
                                }
                                CoreApp.getAppEventsLogger().logEvent(AppEventsConstants.EVENT_NAME_ACHIEVED_LEVEL, facebookParams);
                                break;
                        }
                    }
                    AppsFlyerLib.getInstance().trackEvent(CoreApp.getCoreApp(), goal, appsflyerValues);
                    YandexMetrica.reportEvent(goal, appmetricaValues);
                    CoreApp.getFirebaseAnalytics().logEvent(goal, firebaseParams);
                    googleEvents.setCategory(goal);
                    googleEvents.setLabel(label);
                    CoreApp.getCoreApp().getDefaultTracker().send(googleEvents.build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
