package com.digma.masquerade.digma.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by janidham on 18/01/16.
 */
public class PrefManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared pref file name
    private static final String PREF_NAME = "Masquerade";

    // All Shared Preferences Keys of User
    public static final String USER_AVATAR_URL = "USER_AVATAR_URL";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_GENDER = "USER_GENDER";
    public static final String USER_AGE = "USER_AGE";

    // Al Shared Preferences Keys of app
    public static final String DATA_SEND = "DATA_SEND";
    public static final String ACCEPT_TERMS = "ACCEPT_TERMS";

    // Constructor
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setAcceptTerms(Boolean accept) {
        editor.putBoolean(ACCEPT_TERMS, accept);

        editor.commit();
    }

    public void setDataSend(Boolean send) {
        editor.putBoolean(DATA_SEND, send);

        editor.commit();
    }

    public void setUserProfile(String avatarURL, String name, String email, String gender, String age) {
        editor.putString(USER_AVATAR_URL, avatarURL);
        editor.putString(USER_NAME, name);
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_GENDER, gender);
        editor.putString(USER_AGE, age);

        editor.commit();
    }

    public void setUserAvatarName(String avatarURL, String name) {
        editor.putString(USER_AVATAR_URL, avatarURL);
        editor.putString(USER_NAME, name);

        editor.commit();
    }

    public void setUserEmailGenderAge(String email, String gender, String age) {
        editor.putString(USER_EMAIL, email);
        editor.putString(USER_GENDER, gender);
        editor.putString(USER_AGE, age);

        editor.commit();
    }


    public boolean getAcceptedTerms() {
        return pref.getBoolean(ACCEPT_TERMS, false);
    }

    public boolean getDataSend() {
        return pref.getBoolean(DATA_SEND, false);
    }

    public String getUserAvatarUrl() {
        return pref.getString(USER_AVATAR_URL, "");
    }

    public String getUserName() {
        return pref.getString(USER_NAME, "");
    }

    public String getUserEmail() {
        return pref.getString(USER_EMAIL, "");
    }

    public String getUserAge() {
        return pref.getString(USER_AGE, "");
    }

    public String getUserGender() {
        return pref.getString(USER_GENDER, "");
    }

}
