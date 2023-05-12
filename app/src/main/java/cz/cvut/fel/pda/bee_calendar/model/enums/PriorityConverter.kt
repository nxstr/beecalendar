package cz.cvut.fel.pda.bee_calendar.model.enums

import androidx.room.TypeConverter

class PriorityConverter {

    @TypeConverter
    fun fromPriority(value: PriorityEnum): Int{
        return value.value
    }

    @TypeConverter
    fun toPriority(value: Int): PriorityEnum{
        return when(value){
            2-> PriorityEnum.MEDIUM
            3-> PriorityEnum.HIGH
            else-> PriorityEnum.LOW
        }
    }
}