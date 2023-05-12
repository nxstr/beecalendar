package cz.cvut.fel.pda.bee_calendar.model

import androidx.room.*
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityConverter
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityEnum

@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    override val name: String,
    override val date: String,
    override val remind: String,
    override val notes: String? = null,
    override val categoryId: Int,
    override val userId: Int,

    val deadlineTime: String,
    val isActive: Boolean = true,

    @ColumnInfo(name = "priority_enum")
    @TypeConverters(PriorityConverter::class)
    val priority: PriorityEnum


) : Action()