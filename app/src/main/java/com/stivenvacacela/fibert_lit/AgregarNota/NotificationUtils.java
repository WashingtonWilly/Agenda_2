package com.stivenvacacela.fibert_lit.AgregarNota;

import com.stivenvacacela.fibert_lit.AgregarNota.NotificationUtils;
import com.stivenvacacela.fibert_lit.R;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class NotificationUtils {

    private static final String CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";
    private static final String CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION = "Notification Channel Description";
    private static final int NOTIFICATION_ID = 1;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void showNotification(Context context, String title, String description) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        // Reemplaza "ic_notification" con el nombre de tu icono en res/drawable
        Notification.Builder builder = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_notification);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
