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

    override var name: String,
    override var date: String,
    override var remind: String,
    override var notes: String?,
    override var categoryId: Int,
    override var userId: Int,

    var location: String?,
    var timeFrom: String,
    var timeTo: String,

    @ColumnInfo(name="repeat_enum")
    @TypeConverters(RepeatConverter::class)
    var repeatEnum: RepeatEnum,

    var repeatTo: String?

): Action(), java.io.Serializable