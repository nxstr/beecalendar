package cz.cvut.fel.pda.bee_calendar.model.enums

import androidx.room.TypeConverter

class RepeatConverter {

    @TypeConverter
    fun fromRepeat(value: RepeatEnum): Int{
        return value.value
    }

    @TypeConverter
    fun toRepeat(value: Int): RepeatEnum{
        return when(value){
            1-> RepeatEnum.EVERY_DAY
            2-> RepeatEnum.EVERY_WEEK
            3-> RepeatEnum.EVERY_MONTH
            else-> RepeatEnum.ONCE
        }
    }
}