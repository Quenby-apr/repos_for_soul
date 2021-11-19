package com.example.lab_1;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.RemoteViews;

import business_logic.business_logics.MyObjectLogic;
import business_logic.interfaces.IObjectStorage;
import services.DbService;


public class CountWidget extends AppWidgetProvider {

    public static final String WIDGET_IDS_KEY ="ids";
    public static final String WIDGET_DATA_KEY ="data";

    public void onReceive(Context context, Intent intent) {
        int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
        if (intent.hasExtra(WIDGET_DATA_KEY)) {
            Object data = intent.getExtras().getString(WIDGET_DATA_KEY);
            this.update(context, AppWidgetManager.getInstance(context), ids, data);
        } else {
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        }
    }

    public void update(Context context, AppWidgetManager manager, int[] ids, Object data) {
        for (int widgetId : ids) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.count_widget);
            views.setTextViewText(R.id.appwidget_text, String.valueOf(data));

            manager.updateAppWidget(widgetId, views);
        }
    }
}