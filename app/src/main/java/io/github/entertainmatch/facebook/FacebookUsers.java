package io.github.entertainmatch.facebook;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.facebook.AccessToken;

import javax.inject.Singleton;

import io.github.entertainmatch.model.Person;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Adrian Bednarz on 4/5/17.
 */

@Singleton
public class FacebookUsers {
    private final static String PREF_KEY = "facebook_users";
    private final static String USER_KEY = "user";

    private Person currentUser = null;

    public boolean isUserLoggedIn(Context ctx) {
        return currentUser != null || getCurrentUser(ctx) != null;
    }

    public Person getCurrentUser(Context ctx) {
        if (currentUser != null)
            return currentUser;

        SharedPreferences preferences = ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        Person candidate = getObject(preferences, USER_KEY, Person.class);
        currentUser = candidate == null || TextUtils.isEmpty(candidate.getFacebookId()) ? null : candidate;
        return currentUser;
    }

    public void setCurrentUser(Context ctx, Person user) {
        SharedPreferences preferences = ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        putObject(preferences, USER_KEY, user);

        currentUser = user;
    }

    public void removeCurrentUser(Context ctx) {
        SharedPreferences preferences = ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
        removeObject(preferences, USER_KEY, Person.class);

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
                    editor.putString(getFieldKey(key, field.getName()), field.get(o).toString());
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
