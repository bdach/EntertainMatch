package io.github.entertainmatch.notifications;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.entertainmatch.facebook.FacebookUsers;
import io.github.entertainmatch.firebase.FirebaseUserController;

/**
 * Created by Adrian Bednarz on 5/10/17.
 */

public class NotificationService extends IntentService {
    public NotificationService() {
        super("NotificationServiceXDDD");

        String facebookId = FacebookUsers.getCurrentUser(this).getFacebookId();
        FirebaseUserController.getUser(facebookId).subscribe(user -> {
            Log.d("NotificationsService", "User data changed");
            if (user == null)
                return;

            user.getPolls().forEach((pollId, isNew) -> {
                if (isNew) {
                    Log.d("NotificationsService", pollId);
                    Notifications.notifyPollStarted(this, pollId);
                }
            });

            FirebaseUserController.makePollsOldForUser(facebookId, user);
        });

        FirebaseDatabase.getInstance().getReference("user_polls").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("NotificationsService", "CHANGED");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("NotificationsService", "onDestroy");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
