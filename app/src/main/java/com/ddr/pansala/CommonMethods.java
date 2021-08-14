package com.ddr.pansala;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

public class CommonMethods {

    private static SharedPreferences prefs;

    public static void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    protected static void saveSession(Context context, UserRole userRole, String password) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getString("name", null) != null) {
            clearSession(context);
        }
        prefs.edit().putString("name", userRole.getName()).apply();
        prefs.edit().putString("email", userRole.getEmail()).apply();
        prefs.edit().putString("pw", password).apply();
        prefs.edit().putString("userId", userRole.getUserId()).apply();
        prefs.edit().putString("userType", userRole.getUserType()).apply();
        prefs.edit().putString("userStatus", userRole.getUserStatus()).apply();
    }

    /**
     * Method for get name from current user
     *
     * @return
     */
    protected static String getName() {
        String displayName = null;
        String fullName = prefs.getString("name", null);
        List<String> splitName = Arrays.asList(fullName.split(" "));
        if (splitName.size() != 0) {
            displayName = splitName.get(0);
        }
        return displayName;
    }

    protected static String getFullName() {
        return prefs.getString("name", null);
    }

    protected static String getUserIdFromSession() {
        return prefs.getString("userId", null);
    }

    protected static String getUserTypeFromSession() {
        return prefs.getString("userType", null);
    }

    protected static String getPasswordFromSession() {
        return prefs.getString("pw", null);
    }

    protected static String getEmailFromSession() {
        return prefs.getString("email", null);
    }

    protected static void clearSession(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().apply();
    }
}
