package cz.cvut.fel.pda.bee_calendar.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.AdapterView
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class EventActivityUtil {

    public fun times(time: Button, context: Context) {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                time.setText(LocalTime.of(hourOfDay, minute).format(DateTimeFormatter.ofPattern("HH:mm")))
            }
        }, hour, minute, true)
        mTimePicker.show()
    }

    public fun timeTextView(time: TextView, context: Context) {
        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(context, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                time.setText(LocalTime.of(hourOfDay, minute).format(DateTimeFormatter.ofPattern("HH:mm")))
            }
        }, hour, minute, true)
        mTimePicker.show()
    }

    public fun datePicker(sbd: TextView, context: Context){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context,
            { view, year, monthOfYear, dayOfMonth ->
                sbd.text = LocalDate.of(year, monthOfYear+1, dayOfMonth).toString()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}