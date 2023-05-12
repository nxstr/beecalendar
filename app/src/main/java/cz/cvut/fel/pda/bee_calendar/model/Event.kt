package cz.cvut.fel.pda.bee_calendar.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatConverter
import cz.cvut.fel.pda.bee_calendar.model.enums.RepeatEnum

@Entity(tableName = "Events")
data class Event(

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    override val name: String,
    override val date: String,
    override val remind: String,
    override val notes: String?,
    override val categoryId: Int,
    override val userId: Int,

    val location: String?,
    val timeFrom: String,
    val timeTo: String,

    @ColumnInfo(name="repeat_enum")
    @TypeConverters(RepeatConverter::class)
    val repeatEnum: RepeatEnum,

    val repeatTo: String?

): Action()