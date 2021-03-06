package com.example.samsung.p1211_listwidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.sql.Date;

/**
 * Created by samsung on 16.05.2017.
 */

public class MyProvider extends AppWidgetProvider {

    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private final String ACTION_ON_CLICK = "com.example.samsung.p1211_listwidget.itemonclick";
    final static String ITEM_POSITION = "item_position";
    //Для обхода получасового ограничения частоты обновлений
    private final String UPDATE_ALL_WIDGETS = "update_all_widgets";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent enableIntent = new Intent(context, MyProvider.class);
        enableIntent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, enableIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC,
                System.currentTimeMillis(),
                5000, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Intent disabledIntent = new Intent(context, MyProvider.class);
        disabledIntent.setAction(UPDATE_ALL_WIDGETS);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, disabledIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    //Standard functional
    @Override
    public void onUpdate(Context context,
                         AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    private void updateWidget(final Context context,
                              final AppWidgetManager appWidgetManager,
                              final int appWidgetID) {
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(),
                R.layout.widget
        );
        setUpdateTV(remoteViews, context, appWidgetID);
        setList(remoteViews, context, appWidgetID);
        setListClick(remoteViews, context, appWidgetID);

        appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
        //Обновление списка виджета
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetID, R.id.lvList);
    }

    private void setUpdateTV(RemoteViews remoteViews,
                             final Context context,
                             final int appWidgetID) {
        remoteViews.setTextViewText(R.id.tvUpdate,
                sdf.format(new Date(System.currentTimeMillis())));
        Intent updIntent = new Intent(context, MyProvider.class);
        updIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetID});
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, appWidgetID, updIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.tvUpdate, pendingIntent);
    }

    private void setList(RemoteViews remoteViews,
                         final Context context,
                         final int appWidgetID) {
        Intent adapter = new Intent(context, MyService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        //Для генерации уникальных данных к каждому создаваемому виджету
        Uri data = Uri.parse(adapter.toUri(Intent.URI_INTENT_SCHEME));
        adapter.setData(data);
        //Only for API 14 or more
        remoteViews.setRemoteAdapter(R.id.lvList, adapter);
        //For API 13 or less
//        remoteViews.setRemoteAdapter(appWidgetID, R.id.lvList, adapter);
        remoteViews.setEmptyView(R.id.lvList, R.id.tvEmpty);
    }

    private void setListClick(RemoteViews remoteViews,
                              final Context context,
                              final int appWidgetID) {
        Intent listClickIntent = new Intent(context, MyProvider.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, listClickIntent, 0);
        remoteViews.setPendingIntentTemplate(R.id.lvList, pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();

        if (action.equalsIgnoreCase(ACTION_ON_CLICK)) {
            int itemPos = intent.getIntExtra(ITEM_POSITION, -1);
            String message1 = "Clicked on item ",
                   message2 = " of widget "
                           + intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                                AppWidgetManager.INVALID_APPWIDGET_ID);
            if (itemPos != -1) {
                Toast.makeText(context,
                        message1 + itemPos + message2,
                        Toast.LENGTH_SHORT).show();
            }
        }
        //Для обхода получасового ограничения частоты обновлений
        if (action.equalsIgnoreCase(UPDATE_ALL_WIDGETS)) {
            ComponentName thisAppWidget = new ComponentName(
                    context.getPackageName(),
                    getClass().getName());
            AppWidgetManager manager = AppWidgetManager.getInstance(context);

            for (int appWidgetID : manager.getAppWidgetIds(thisAppWidget)) {
                updateWidget(context, manager, appWidgetID);
            }
        }
    }
}
