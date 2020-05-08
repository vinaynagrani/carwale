package com.vinay.covid;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

public class Utils {
    static final int FILTER_TOTAL_GREATER = 0;
    static final int FILTER_TOTAL_LESS = 1;
    static final int FILTER_RECOVERED_GREATER = 2;
    static final int FILTER_RECOVERED_LESS = 3;
    static final int FILTER_DEATH_GREATER = 4;
    static final int FILTER_DEATH_LESS = 5;

    static final int SORT_TOTAL_DSC = 0;
    static final int SORT_TOTAL_ASC = 1;
    static final int SORT_RECOVERED_ASC = 2;
    static final int SORT_RECOVERED_DES = 3;
    static final int SORT_DEATH_ASC = 4;
    static final int SORT_DEATH_DES = 5;
    static final int SORT_COUNTRY_ASC = 6;
    static final int SORT_COUNTRY_DES = 7;

    static final int ACS = 1;
    public static final int DES = 0;

    public static boolean isAboveKitkat() {
        int currentApiVersion = Build.VERSION.SDK_INT;
        return currentApiVersion >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean isStringNotNull(String object) {
        boolean isValid = true;
        try {
            if (object == null) {
                isValid = false;
            } else {
                if (object.trim().equals("")) {
                    isValid = false;
                }
                if (object.trim().equalsIgnoreCase("null")) {
                    isValid = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isValid;
    }

    public static boolean isNetworkAvailable(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return isConnected;
        }
    }

}
