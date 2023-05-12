package cz.cvut.fel.pda.bee_calendar.model.enums

enum class RepeatEnum (
    val value: Int,
    val text: String
        ){
    ONCE(0, "once"),
    EVERY_DAY(1, "every day"),
    EVERY_WEEK(2, "every week"),
    EVERY_MONTH(3, "every_month");

}
