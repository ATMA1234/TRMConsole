package com.example.ticketingtool_library.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;


import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.ticketingtool_library.LoginActivity;
import com.example.ticketingtool_library.R;
import com.example.ticketingtool_library.api.RegisterAPI;
import com.example.ticketingtool_library.api.RetroClient;
import com.example.ticketingtool_library.model.TicketDetails;
import com.example.ticketingtool_library.values.FunctionCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ticketingtool_library.values.constant.PREFS_TICKET_ID;
import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_FAILURE;
import static com.example.ticketingtool_library.values.constant.REQUEST_RESULT_SUCCESS;

public class New_Ticket_Notification extends BroadcastReceiver {
    Context Notification_context;
    FunctionCall functionCalls;
    List<TicketDetails> arrayList;
    SharedPreferences sharedPreferences;
    String SUBDIV_CODE, COMP_ID;
    public static int COUNT = 0;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == REQUEST_RESULT_SUCCESS) {
                SavePreferences(PREFS_TICKET_ID, arrayList.get(0).getTIC_ID());
                if (Integer.parseInt(arrayList.get(0).getTIC_ID()) >
                        Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString(PREFS_TICKET_ID, "")))) {
                    COUNT = Integer.parseInt(arrayList.get(0).getTIC_ID()) -
                            Integer.parseInt(Objects.requireNonNull(sharedPreferences.getString(PREFS_TICKET_ID, "")));
                    functionCalls.logStatus(COUNT + "");
                    notification(arrayList.get(0).getTITLE(), arrayList.get(0).getDESCRIPTION());
                }
            }
            return false;
        }
    });

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences = context.getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        Notification_context = context;
        functionCalls = new FunctionCall();
        arrayList = new ArrayList<>();

        if (intent != null) {
            SUBDIV_CODE = intent.getStringExtra("subdiv_code");
            COMP_ID = intent.getStringExtra("comp_id");
        } else SUBDIV_CODE = "0";
        ticketData(SUBDIV_CODE, COMP_ID);
    }

    private Context getContext() {
        return this.Notification_context;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    private void notification(String title, String description) {
        int uniqueId = (int) System.currentTimeMillis();
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(getContext(), LoginActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent2 = PendingIntent.getActivity(getContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            String channelID = "Your Channel ID";// The id of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelID, "My_Name", importance);

            Notification notification = getNotificationBuilder(title, description, defaultSoundUri, intent2)
                    .setChannelId(channelID)
                    .build();
            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(uniqueId, notification);


        } else if (notificationManager != null) {
            NotificationCompat.Builder notificationBuilder = getNotificationBuilder(title, description, defaultSoundUri, intent2);
            notificationManager.notify(uniqueId, notificationBuilder.build());

        }
    }

    //Calling Notification Builder which will pop up notifications to both below------------------------------------------------------------
    private NotificationCompat.Builder getNotificationBuilder(String title, String description, Uri defaultSoundUri, PendingIntent intent) {
        return new NotificationCompat.Builder(getContext())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getContext().getApplicationContext().getResources(), R.drawable.ticket))
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(intent)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);
    }

    //----------------------------------------------------------------------------------------------------------------------------------
    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //---------------------------------------------------------------------------------------------------------------------
    private void ticketData(String subdiv_code, String comp_id) {
        RetroClient retroClient = new RetroClient();
        RegisterAPI api = retroClient.getApiService();
        api.getTicketDetails(subdiv_code, comp_id).enqueue(new Callback<List<TicketDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<TicketDetails>> call, @NonNull Response<List<TicketDetails>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    arrayList = response.body();
                    handler.sendEmptyMessage(REQUEST_RESULT_SUCCESS);
                } else handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }

            @Override
            public void onFailure(@NonNull Call<List<TicketDetails>> call, @NonNull Throwable t) {
                handler.sendEmptyMessage(REQUEST_RESULT_FAILURE);
            }
        });
    }
}
