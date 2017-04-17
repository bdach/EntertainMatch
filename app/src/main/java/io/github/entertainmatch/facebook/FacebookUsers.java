package io.github.entertainmatch.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import io.github.entertainmatch.facebook.model.FacebookUser;

/**
 * Created by Adrian Bednarz on 4/5/17.
 */

public class FacebookUsers {
    private final static String PREF_KEY = "facebook_users";
    private final static String KEY_USER = "user";

    private static FacebookUser currentUser = null;

    public static boolean isUserLoggedIn(Context ctx) {
        return currentUser != null || getCurrentUser(ctx) != null;
    }

    public static FacebookUser getCurrentUser(Context ctx) {
        if (currentUser != null)
            return currentUser;

        SharedPreferences preferences = ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        FacebookUser candidate = getObject(preferences, KEY_USER, FacebookUser.class);
        currentUser = candidate == null || TextUtils.isEmpty(candidate.facebookId) ? null : candidate;
        return currentUser;
    }

    public static void setCurrentUser(Context ctx, FacebookUser user) {
        SharedPreferences preferences = ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        putObject(preferences, KEY_USER, user);

        currentUser = user;
    }

    public static void removeCurrentUser(Context ctx) {
        SharedPreferences preferences = ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        removeObject(preferences, KEY_USER, FacebookUser.class);

        currentUser = null;
    }

    private static <T> T getObject(SharedPreferences preferences, String key, Class<T> type) {
        T o  = null;

        try {
            o = type.getConstructor().newInstance();

            for (Field field : type.getDeclaredFields()) {
                try {
                    if (field.getType().equals(String.class))
                        field.set(o, preferences.getString(getFieldKey(key, field.getName()), ""));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return o;
    }

    private static void putObject(SharedPreferences preferences, String key, Object o) {
        SharedPreferences.Editor editor = preferences.edit();
        for (Field field : o.getClass().getDeclaredFields()) {
            try {
                if (field.getType().equals(String.class))
                    editor.putString(getFieldKey(KEY_USER, field.getName()), field.get(o).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        editor.apply();
    }

    private static <T> void removeObject(SharedPreferences preferences, String key, Class<T> type) {
        SharedPreferences.Editor editor = preferences.edit();

        for (Field field : type.getDeclaredFields()) {
            if (field.getType().equals(String.class))
                editor.remove(getFieldKey(key, field.getName()));
        }

        editor.apply();
    }

    private static String getFieldKey(String objectKey, String name) {
        return objectKey + "_" + name;
    }
}
