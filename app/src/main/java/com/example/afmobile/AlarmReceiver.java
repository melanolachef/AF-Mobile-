package com.example.afmobile;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String nome = intent.getStringExtra("NOME_REMEDIO");

        Intent i = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CANAL_REMEDIO")
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Hora do Remédio!")
                .setContentText("Tomar: " + nome)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(200, builder.build());
        }
    }
}