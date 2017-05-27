package io.github.entertainmatch.utils;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.github.entertainmatch.R;
import io.github.entertainmatch.firebase.models.FirebaseCompletedPoll;

/**
 * Created by Adrian Bednarz on 5/24/17.
 */

public class CalendarUtils {
    public static void addEventToCalendar(AppCompatActivity activity, FirebaseCompletedPoll poll) {
        ContentResolver cr = activity.getContentResolver();
        ContentValues values = new ContentValues();

        Calendar dt = Calendar.getInstance();
        dt.setTime(new Date(poll.getEventDate().getDate()));

        values.put(CalendarContract.Events.DTSTART, dt.getTimeInMillis());
        values.put(CalendarContract.Events.TITLE,
                activity.getString(R.string.calendar_event_title, poll.getEvent().getTitle(), poll.getLocation().getPlace()));
        values.put(CalendarContract.Events.DESCRIPTION, activity.getString(R.string.calendar_event_description));

        TimeZone timeZone = TimeZone.getDefault();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

        // default calendar
        values.put(CalendarContract.Events.CALENDAR_ID, 1);

        // for given duration
        dt.add(Calendar.MINUTE, poll.getEvent().getDuration());
        values.put(CalendarContract.Events.DTEND, dt.getTimeInMillis());

        // insert event to calendar
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // should be handled at higher level
            return;
        }

        Uri event = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        if (event == null) {
            // TODO: maybe snack?
            Toast.makeText(activity, R.string.calendar_failed, Toast.LENGTH_LONG).show();
            return;
        }

        // add reminder
        long id = Long.parseLong(event.getLastPathSegment());
        ContentValues valuesReminder = new ContentValues();
        valuesReminder.put(CalendarContract.Reminders.MINUTES, 24 * 60);
        valuesReminder.put(CalendarContract.Reminders.EVENT_ID, id);
        valuesReminder.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        cr.insert(CalendarContract.Reminders.CONTENT_URI, valuesReminder);
    }
}