package io.github.entertainmatch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * Class responsible for managing preferences.
 *
 * @author Adrian Bednarz
 * @since 6/2/17
 */
public class PreferencesHelper {

    /**
     * Retrieves {@link SharedPreferences} with the key of pref_key.
     * @param ctx The {@link Context} to use when fetching the preferences.
     * @return An instance of {@link SharedPreferences} with the key of pref_key
     */
    public static SharedPreferences getSharedPreferences(Context ctx, String pref_key) {
        if (ctx == null || ctx.getApplicationContext() == null) {
            return null;
        }
        return ctx.getApplicationContext().getSharedPreferences(pref_key, Context.MODE_PRIVATE);
    }

    /**
     * Helper method. Used to retrieve a Java object stored using the
     * {@link #putObject(SharedPreferences, String, Object)} method.
     * @param preferences The {@link SharedPreferences} instance to store the values into.
     * @param key The key to use when storing field values.
     * @param type The type of object to get.
     * @param <T> The type of object to retrieve.
     * @return An object of type {@link T} retrieved from {@link SharedPreferences}.
     */
    public static <T> T getObject(SharedPreferences preferences, String key, Class<T> type) {
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

    /**
     * Helper method. Used to store a Java object's field values into the supplied
     * {@link SharedPreferences} object.
     * @param preferences The {@link SharedPreferences} instance to store the values into.
     * @param key The key to use when storing field values.
     * @param o The object to store.
     */
    public static void putObject(SharedPreferences preferences, String key, Object o) {
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

    /**
     * Helper method. Used to remove an object stored using {@link SharedPreferences} from
     * the supplied {@link SharedPreferences} object.
     * @param preferences The {@link SharedPreferences} instance to remove the object from.
     * @param key The key under which the object was stored.
     * @param type The type of object stored.
     * @param <T> The type of object stored.
     */
    public static <T> void removeObject(SharedPreferences preferences, String key, Class<T> type) {
        SharedPreferences.Editor editor = preferences.edit();

        for (Field field : type.getDeclaredFields()) {
            if (field.getType().equals(String.class))
                editor.remove(getFieldKey(key, field.getName()));
        }

        editor.apply();
    }

    /**
     * Returns a key for the field with the given name.
     * @param objectKey The key used to store the whole object.
     * @param name The name of the field whose value is being stored.
     * @return A {@link String} to use when putting the value into {@link SharedPreferences}
     */
    private static String getFieldKey(String objectKey, String name) {
        return objectKey + "_" + name;
    }
}
