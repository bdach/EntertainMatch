package io.github.entertainmatch.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Adrian Bednarz on 6/2/17.
 */

public class SnackbarStatusHelper {
    private static final String PREF_KEY = "status";

    private static SharedPreferences getSharedPreferences(Context ctx) {
        if (ctx == null || ctx.getApplicationContext() == null) {
            return null;
        }
        return ctx.getApplicationContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    public static boolean shouldDisplaySnackbar(Context ctx, StageType stage) {
        return getSharedPreferences(ctx).getBoolean(stage.toString(), true);
    }

    public static void stopShowingSnackbar(Context ctx, StageType stage) {
        getSharedPreferences(ctx).edit().putBoolean(stage.toString(), false).apply();
    }
}
