package cz.cvut.fel.pda.bee_calendar.model

abstract class Action{
    abstract var name: String
    abstract var date: String
    abstract var remind: String
    abstract var notes: String?
    abstract var categoryId: Int
    abstract var userId: Int
}