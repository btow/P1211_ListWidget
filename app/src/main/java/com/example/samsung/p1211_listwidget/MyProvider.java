package com.example.samsung.p1211_listwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by samsung on 16.05.2017.
 */

public class MyProvider extends AppWidgetProvider {

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
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
    }

    private void setList(final RemoteViews remoteViews,
                         final Context context,
                         final int appWidgetID) {
        Intent adapter = new Intent(context, MyService.class);
        adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
        remoteViews.setRemoteAdapter(R.id.lvList, adapter);
    }

    private void setListClick(final RemoteViews remoteViews,
                              final Context context,
                              final int appWidgetID) {
    }

    private void setUpdateTV(final RemoteViews remoteViews,
                             final Context context,
                             final int appWidgetID) {
        remoteViews.setTextViewText(R.id.tvUpdate, sdf.format(new Date(System.currentTimeMillis())));
        Intent updIntent = new Intent(context, MyProvider.class);
        updIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[] {appWidgetID});
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, appWidgetID, updIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.tvUpdate, pendingIntent);
    }
}
