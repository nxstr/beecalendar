package cz.cvut.fel.pda.bee_calendar.model

abstract class Action{
    abstract val name: String
    abstract val date: String
    abstract val remind: String
    abstract val notes: String?
    abstract val categoryId: Int
    abstract val userId: Int
}