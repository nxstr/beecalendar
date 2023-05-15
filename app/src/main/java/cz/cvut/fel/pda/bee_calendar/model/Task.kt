package cz.cvut.fel.pda.bee_calendar.model

import androidx.room.*
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityConverter
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityEnum

@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    override var name: String,
    override var date: String,
    override var remind: String,
    override var notes: String? = null,
    override var categoryId: Int,
    override var userId: Int,

    var deadlineTime: String,
    var isActive: Boolean = true,

    @ColumnInfo(name = "priority_enum")
    @TypeConverters(PriorityConverter::class)
    var priority: PriorityEnum


) : Action(), java.io.Serializable