package com.vinay.covid.sharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingPreference {

    private SharedPreferences sharedPreferences;
    private static final String PREFERENCE_NAME = "SettingPreference";
    public static String KEY_SORT_OPTION = "sortOption";
    public static String KEY_COUNTRY_LAT = "countryLat";
    public static String KEY_COUNTRY_LONG = "countryLong";
    public static String KEY_COUNTRY_NAME = "countryName";


    public static String KEY_TOTAL_FILTER = "totalFilter";
    public static String KEY_DEATH_FILTER = "deathFilter";
    public static String KEY_RECOVERED_FILTER = "recoveredFilter";

    public static String KEY_TOTAL_SELECTED = "totalFilterSelected";
    public static String KEY_DEATH_SELECTED = "deathFilterSelected";
    public static String KEY_RECOVERED_SELECTED = "recoveredSelected";

    public static String KEY_TOTAL_NUMBER = "totalNumber";
    public static String KEY_DEATH_NUMBER = "deathNumber";
    public static String KEY_RECOVERED_NUMBER = "recoveredNumber";

    public static String DATA = "data";


    public static int getIntField(String fieldName, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(fieldName, 0);
    }

    public static void setIntField(Context context, String fieldName, int aesDes) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(fieldName, aesDes);
        editor.apply();
    }


    public static String getCountry(Context context, String fieldName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(fieldName, "");
    }

    public static void setCountry(Context context, String data, String fieldName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(fieldName, data);
        editor.apply();
    }

    public static boolean getBooleanField(String fieldName, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(fieldName, false);
    }

    public static void setBooleanField(Context context, String fieldName, boolean isSelected) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(fieldName, isSelected);
        editor.apply();
    }


}
