package bankernisarg.app.com.locationdemo.data.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.preference.PreferenceManager
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import bankernisarg.app.com.locationdemo.R
import bankernisarg.app.com.locationdemo.ui.MainActivity

import java.text.DateFormat
import java.util.Date

internal object Utils {

    val KEY_LOCATION_UPDATES_REQUESTED = "location-updates-requested"
    val KEY_LOCATION_UPDATES_RESULT = "location-update-result"
    val CHANNEL_ID = "channel_01"

    fun setRequestingLocationUpdates(context: Context, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(KEY_LOCATION_UPDATES_REQUESTED, value)
            .apply()
    }

    fun getRequestingLocationUpdates(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(KEY_LOCATION_UPDATES_REQUESTED, false)
    }

    fun sendNotification(context: Context, notificationDetails: String) {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra("from_notification", true)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java!!)
        stackBuilder.addNextIntent(notificationIntent)
        val notificationPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context)
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.ic_launcher
                )
            )
            .setColor(Color.RED)
            .setContentTitle("Location update")
            .setContentText(notificationDetails)
            .setContentIntent(notificationPendingIntent)
        builder.setAutoCancel(true)
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val mChannel =
                NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(mChannel)
            builder.setChannelId(CHANNEL_ID)
        }
        mNotificationManager.notify(0, builder.build())
    }

    fun getLocationResultTitle(context: Context, locations: List<Location>): String {
        val numLocationsReported = context.resources.getQuantityString(
            R.plurals.num_locations_reported, locations.size, locations.size
        )
        return numLocationsReported + ": " + DateFormat.getDateTimeInstance().format(Date())
    }

    private fun getLocationResultText(context: Context, locations: List<Location>): String {
        if (locations.isEmpty()) {
            return "Unknown Locations"
        }
        val sb = StringBuilder()
        for (location in locations) {
            sb.append("(")
            sb.append(location.latitude)
            sb.append(", ")
            sb.append(location.longitude)
            sb.append(")")
            sb.append("\n")
        }
        return sb.toString()
    }

    fun setLocationUpdatesResult(context: Context, locations: List<Location>) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(
                KEY_LOCATION_UPDATES_RESULT, getLocationResultTitle(context, locations)
                        + "\n" + getLocationResultText(context, locations)
            )
            .apply()
    }

    fun getLocationUpdatesResult(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(KEY_LOCATION_UPDATES_RESULT, "")!!
    }
}
