package com.example.lab1.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.example.lab1.R;
import com.example.lab1.data.GoldApiService;
import com.example.lab1.data.MetalRates;
import com.example.lab1.data.Record;
import com.example.lab1.data.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GoldWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_gold);
        views.setTextViewText(R.id.textGold, "Загрузка...");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String today = sdf.format(cal.getTime());

        GoldApiService api = RetrofitClient.getApi();
        api.getMetalRates(today, today).enqueue(new Callback<MetalRates>() {
            @Override
            public void onResponse(Call<MetalRates> call, Response<MetalRates> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MetalRates rates = response.body();
                    String goldPrice = "—";

                    for (Record record : rates.records) {
                        if (record.code == 1) {
                            goldPrice = record.buy.replace(",", ".");
                            break;
                        }
                    }

                    views.setTextViewText(R.id.textGold, "Золото: " + goldPrice + " ₽");
                    appWidgetManager.updateAppWidget(appWidgetId, views);

                    context.getSharedPreferences("gold_prefs", Context.MODE_PRIVATE)
                            .edit()
                            .putString("gold_rate", goldPrice)
                            .apply();
                } else {
                    showError(views, appWidgetManager, appWidgetId, context);
                }
            }

            @Override
            public void onFailure(Call<MetalRates> call, Throwable t) {
                showError(views, appWidgetManager, appWidgetId, context);
            }
        });
    }

    private void showError(RemoteViews views, AppWidgetManager appWidgetManager, int appWidgetId, Context context) {
        String cached = context.getSharedPreferences("gold_prefs", Context.MODE_PRIVATE)
                .getString("gold_rate", "...");
        views.setTextViewText(R.id.textGold, "Ошибка | " + cached);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, GoldWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(widget);
        new GoldWidgetProvider().onUpdate(context, manager, ids);
    }
}