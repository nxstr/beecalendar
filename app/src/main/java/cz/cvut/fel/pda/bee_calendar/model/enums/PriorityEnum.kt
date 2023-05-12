package cz.cvut.fel.pda.bee_calendar.model.enums

enum class PriorityEnum (
        val value: Int,
        val text: String
){
        LOW(1, "low"),
        MEDIUM(2, "medium"),
        HIGH(3, "high");
}