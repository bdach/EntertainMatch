package io.github.entertainmatch.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import io.github.entertainmatch.R;
import io.github.entertainmatch.model.VoteCategoryStage;
import io.github.entertainmatch.utils.PollStageFactory;

/**
 * Created by Adrian Bednarz on 5/10/17.
 *
 * From this class we manage notifications.
 * They should be sent whenever event status changes.
 */

public class Notifications {
    /**
     * Notifications identifier used to reference notifications (to update them rather than showing new ones).
     */
    private static final int NOTIFICATION_ID = 2137;

    /**
     * Constructs and displays a notification for the newly updated weather for today.
     *
     * @param context Context used to query our ContentProvider and use various Utility methods
     */
    public static void notifyPollStarted(Context context, String pollId) {
        String title = "New poll just started!";
        String text = "New poll has just started! Come and see your friends' plans!";

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true); // hide on click

        Intent intent = PollStageFactory.get(VoteCategoryStage.class.toString(), pollId).getViewStageIntent(context);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = taskStackBuilder
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }
}
