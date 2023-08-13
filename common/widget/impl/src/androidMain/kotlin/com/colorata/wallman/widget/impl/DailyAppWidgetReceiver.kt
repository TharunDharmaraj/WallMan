package com.colorata.wallman.widget.impl

import android.content.Context
import androidx.annotation.Keep
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

@Keep
class DailyAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = createEverydayWidgetContent()
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        context?.let {
            WorkManager.getInstance(it).enqueue(
                PeriodicWorkRequestBuilder<WidgetUpdater>(
                    24,
                    TimeUnit.HOURS,
                    10,
                    TimeUnit.MINUTES
                ).addTag("DailyWidget").build()
            )
        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        context?.let {
            WorkManager.getInstance(it).cancelAllWorkByTag("DailyWidget")
        }
    }
}