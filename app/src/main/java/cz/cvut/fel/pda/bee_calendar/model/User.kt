package cz.cvut.fel.pda.bee_calendar.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,

    var firstName: String,
    var lastName: String,
    var email: String,
    var password: String,

    var profileImg: String? = null
)