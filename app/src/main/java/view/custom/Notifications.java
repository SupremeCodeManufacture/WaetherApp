package view.custom;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.student.adminweather.R;

import data.App;
import data.GenericConstants;
import logic.helpers.MyLogs;
import view.activity.SplashActivity;

import static android.content.Context.NOTIFICATION_SERVICE;


public class Notifications {

    private static Context ctx = App.getAppCtx();
    private static String channelId = "id123";

    public static void showFCMNotification(int pid, String from, String messageBody, String tipText) {
        MyLogs.LOG("Notifications", "showFCMNotification", "messageBody: " + messageBody);
        NotificationManager notificationManager = setupNotifChannel();
        PendingIntent pendingIntent;

        Intent intent = new Intent(ctx, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(GenericConstants.EXTRA_TIP_KEY, tipText);
        pendingIntent = PendingIntent.getActivity(ctx, (int) System.currentTimeMillis() / 1000, intent, PendingIntent.FLAG_ONE_SHOT);

        if (from == null)
            from = ctx.getResources().getString(R.string.app_name);

        if (messageBody == null)
            messageBody = ctx.getResources().getString(R.string.txt_def_notific);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            mBuilder.setContentTitle(from);

        if (notificationManager != null)
            notificationManager.notify(pid, mBuilder.build());
    }

    private static NotificationManager setupNotifChannel() {
        NotificationManager notificationManager = (NotificationManager) App.getAppCtx().getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId,
                    "Pro channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Register the channel with the system
            if (notificationManager != null) {
                MyLogs.LOG("Notifications", "setupNotifChannel", "OK setup");
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        return notificationManager;
    }
}