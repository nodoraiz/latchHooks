package com.elevenpaths.latchhook.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.elevenpaths.latchhook.R;
import com.elevenpaths.latchhook.services.LatchService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ConfigurationManager {

    // this values can be hardcoded to use it by default (and modified later in the app)
    public final static String APPID = "";
    public final static String SECRET = "";

    private final static String PREFERENCE_FILE = "preferences";

    public final static String PREFERENCE_ACCOUNT = "account";
    public final static String PREFERENCE_PWD = "pwd";
    public final static String PREFERENCE_LATCHED_ACTIVITIES = "controlled_activities";
    public final static String PREFERENCE_APPID = "appid";
    public final static String PREFERENCE_SECRET = "secret";
    public final static String PREFERENCE_HOOKS_ENABLED = "hooks_enabled";
    public final static String PREFERENCE_MILIS_BETWEEN_CHECKS_IN_THREAD_MONITOR = "monitor_thread_milis";

    public final static String PACKAGE_NAME = "com.elevenpaths.latchhook";
    public final static String UNATHORIZED_ACTION_ACTIVITY_NAME = "com.elevenpaths.latchhook.activities.UnauthorizedAction";
    public final static String LATCH_SERVICE_ACTION = "com.elevenpaths.latchhook.LATCH_SERVICE";
    public final static String WATCHER_ACTION = "com.elevenpaths.latchhook.START_SERVICE";

    private static Gson gson;
    public static Gson getGson() {
        if(gson == null){
            gson = new Gson();
        }
        return gson;
    }

    private static int colorWhite = -1;
    private static int colorGreen = -1;

    public static int getColorWhite(Context context) {
        if(colorWhite == -1){
            colorWhite = context.getResources().getColor(R.color.white);
        }
        return colorWhite;
    }

    public static int getColorGreen(Context context) {
        if(colorGreen == -1){
            colorGreen = context.getResources().getColor(R.color.green);
        }
        return colorGreen;
    }

    public static boolean isLatcheable(Context context, Intent intent){

        Set<String> observedPackages = (Set<String>)ConfigurationManager.getObjectPreference(context, ConfigurationManager.PREFERENCE_LATCHED_ACTIVITIES, Set.class);

        return (intent.getComponent() != null && intent.getComponent().getPackageName() != null && observedPackages != null
                && observedPackages.contains(intent.getComponent().getPackageName()));
    }

    public static <T extends Object> T getObjectPreference(Context context, String key, Class<T> clazz){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            return getGson().fromJson(sharedPref.getString(key, null), clazz);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void setObjectPreference(Context context, String key, Object value){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key, getGson().toJsonTree(value).toString());
            editor.commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            return sharedPref.getBoolean(key, defaultValue);

        }catch (Exception e){
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void setBooleanPreference(Context context, String key, boolean value){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(key, value);
            editor.commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getStringPreference(Context context, String key){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            return sharedPref.getString(key, null);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void setStringPreference(Context context, String key, String value){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key, value);
            editor.commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static int getIntPreference(Context context, String key, int defaultValue){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            return sharedPref.getInt(key, defaultValue);

        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public static void setIntPreference(Context context, String key, int value){
        try {
            SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(key, value);
            editor.commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
