package com.example.ticketingtool_library.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


import androidx.annotation.Nullable;

import com.example.ticketingtool_library.receiver.New_Ticket_Notification;
import com.example.ticketingtool_library.values.FunctionCall;

import java.util.Objects;

public class NewTicketService extends Service {
    FunctionCall functionCall;
    String SUBDIV_CODE, COMP_ID;

    public NewTicketService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        functionCall = new FunctionCall();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            SUBDIV_CODE = intent.getStringExtra("subdiv_code");
            COMP_ID = intent.getStringExtra("comp_id");
        }else SUBDIV_CODE="0";
        start_notification_check();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop_notification_check();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("ShortAlarm")
    private void start_notification_check() {
        functionCall.logStatus("Notification_Sending_Checking");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), New_Ticket_Notification.class);
        intent.putExtra("subdiv_code", SUBDIV_CODE);
        intent.putExtra("comp_id", COMP_ID);
        boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmRunning) {
            functionCall.logStatus("Notification_Sending_Started..");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            Objects.requireNonNull(alarmManager).setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), (1000), pendingIntent);
        } else functionCall.logStatus("Notification_Sending Already running..");
    }

    //---------------------------------------------------------------------------------------------------------------------------------
    private void stop_notification_check() {
        functionCall.logStatus("Notification_Sending_Checking");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), New_Ticket_Notification.class);
        intent.putExtra("subdiv_code", SUBDIV_CODE);
        intent.putExtra("comp_id", COMP_ID);
        boolean alarmRunning = (PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmRunning) {
            functionCall.logStatus("Notification_Sending_Stopping..");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        } else functionCall.logStatus("Notification_Sending Not yet Started..");
    }
}
