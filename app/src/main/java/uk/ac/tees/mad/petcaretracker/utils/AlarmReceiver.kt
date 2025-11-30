// Updated file: uk/ac/tees/mad/petcaretracker/utils/AlarmReceiver.kt

package uk.ac.tees.mad.petcaretracker.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import uk.ac.tees.mad.petcaretracker.R

class AlarmReceiver : BroadcastReceiver() {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.paw)
            .setContentTitle("Daily Pet Care Reminder")
            .setContentText("Time to check on your pets today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1, builder.build())

        // Reschedule the next daily notification
        scheduleDailyNotification(context, hour = 18, minute = 4)  // Use the same time
    }
}