package cz.cvut.fel.pda.bee_calendar.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,

    var profileImg: String? = null
)