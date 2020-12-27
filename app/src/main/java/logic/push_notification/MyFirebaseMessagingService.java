package logic.push_notification;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.student.adminweather.R;

import data.App;
import data.GenericConstants;
import logic.helpers.DataFormatConverter;
import logic.helpers.MyLogs;
import view.custom.Notifications;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            CloudDataObj cloudDataObj = DataFormatConverter.getObjFromRemoteMsg(remoteMessage);

            //show notification by custom data
            if (cloudDataObj != null && cloudDataObj.getUserNotification() != null) {
                Notifications.showFCMNotification(
                        cloudDataObj.getUserNotification().getNotificId(),
                        cloudDataObj.getUserNotification().getSenderName(),
                        cloudDataObj.getUserNotification().getText(),
                        cloudDataObj.getTipText());
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            MyLogs.LOG("MyFirebaseMessagingService", "onMessageReceived", "NOTIFICATION: " + remoteMessage.getNotification().getBody());
            Notifications.showFCMNotification(1,
                    App.getAppCtx().getResources().getString(R.string.app_name),
                    remoteMessage.getNotification().getBody(),
                    null);
        }
    }

    @Override
    public void onNewToken(String token) {
        //subscribe to notifications topic & send it to server
        MyLogs.LOG("MyFirebaseMessagingService", "onNewToken", "token: " + token);
        FirebaseMessaging.getInstance().subscribeToTopic(GenericConstants.TOPIC_ALL)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        MyLogs.LOG("MyFirebaseMessagingService", "onNewToken", "registered to topic: " + task.isSuccessful());
                    }
                });

        super.onNewToken(token);
    }
}