package com.example.samsung.p1211_listwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by samsung on 16.05.2017.
 */

public class MyFactory implements RemoteViewsFactory {

    private ArrayList<String> data;
    private Context context;
    private SimpleDateFormat sdf;
    private int widgetID;

    MyFactory(final Context context, final Intent intent) {
        this.context = context;
        this.sdf = new SimpleDateFormat("HH:mm:ss");
        this.widgetID = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        this.data = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return this.data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(
                this.context.getPackageName(),
                R.layout.item);
        remoteViews.setTextViewText(R.id.tvItemText, this.data.get(position));
        //Подключение обработчика нажатия пункта списка
        Intent clickIntent = new Intent();
        clickIntent.putExtra(MyProvider.ITEM_POSITION, position);
        clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        remoteViews.setOnClickFillInIntent(R.id.tvItemText, clickIntent);

        return remoteViews;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        this.data.clear();
        this.data.add(this.sdf.format(new Date(System.currentTimeMillis())));
        this.data.add(String.valueOf(hashCode()));
        this.data.add(String.valueOf(this.widgetID));

        for (int i = 3; i < 15; i++) {
            this.data.add("Item " + i);
        }
    }

    @Override
    public void onDestroy() {

    }
}
