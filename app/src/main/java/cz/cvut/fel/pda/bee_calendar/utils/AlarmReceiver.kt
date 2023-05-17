package cz.cvut.fel.pda.bee_calendar.utils

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.activities.MainActivity
import java.time.LocalDateTime
import java.time.ZoneOffset


/**
 * Created by Praveen John on 11/12/2021
 * Alarm receiver for notifications
 * */
class AlarmReceiver : BroadcastReceiver() {

    /**
     * static object
     * */
    companion object {
        /**
         * Reminder unique id
         * */
        const val EXTRA_MESSAGE = "message"
    }

    /**
     *On Receive of Alarm event
     * */
    override fun onReceive(context: Context, intent: Intent) {
//        val todo = intent.getStringExtra(EXTRA_MESSAGE)
//        val notifId = System.currentTimeMillis().toInt() + (0..1000).random()

        val hashMap = intent.getSerializableExtra(EXTRA_MESSAGE) as ArrayList<String>
        val todo = hashMap.get(0)
        val notifId = hashMap.get(1).toInt()
        val title = "Bee Calendar " + hashMap.get(2)

        println("here------------------------ ")

        showReminderNotification(context, title, todo, notifId)
    }



    /**
     *setting the reminder
     * */


    fun setReminder(context: Context, dueDate: Long, message: String, name: String) {
        if(dueDate>=System.currentTimeMillis()) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val map = ArrayList<String>()
            map.add(message)
            map.add(dueDate.toInt().toString())
            map.add(name)
            intent.putExtra(EXTRA_MESSAGE, map)

            val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                dueDate.toInt(),
                intent,
                flag
            )
            alarmManager.set(AlarmManager.RTC_WAKEUP, dueDate, pendingIntent)
        }
    }

    fun cancelReminder(context: Context, dueDate: Long){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val myIntent = Intent(context, AlarmReceiver::class.java)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            dueDate.toInt(),
            myIntent,
            flag
        )

        alarmManager.cancel(pendingIntent)
    }

    /**
     *Method to show notification
     * */
    private fun showReminderNotification(
        context: Context,
        title: String,
        todo: String?,
        notifId: Int
    ) {
        val channeId = "events"
        val channelName = "Notification"
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder


        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent1 = PendingIntent.getActivity( context,0,tapResultIntent,flag)

            builder = NotificationCompat.Builder(context, channeId)
                .setChannelId(channeId)
                .setContentTitle(title)
                .setContentText(todo)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_honeycomb_black)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent1)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))

        notificationManager.notify(notifId, builder.build())
        
    }
}