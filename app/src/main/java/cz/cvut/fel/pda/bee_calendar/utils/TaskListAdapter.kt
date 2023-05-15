package cz.cvut.fel.pda.bee_calendar.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.model.enums.PriorityEnum

class TaskListAdapter (private val listener: Listener): ListAdapter<Task, TaskListAdapter.TaskViewHolder>(
    WORDS_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(listener, current)

//        holder.itemView.findViewById<ImageButton>(R.id.catDelButton).setOnClickListener {
//            println("***************************222222 "+ getItem(position).id)
//            //зробити спінер і кнопку видалення одну, бо це якийсь треш((
//        }
    }





    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameText: TextView = itemView.findViewById(R.id.taskName)
        private val taskTime: TextView = itemView.findViewById(R.id.submitTime)
        private val taskPriority: ImageView = itemView.findViewById(R.id.priority_value)
        private val taskIsActive: CheckBox = itemView.findViewById(R.id.task_checkbox)

        fun bind(listener: Listener, task: Task) {
            taskNameText.text = task.name
            taskTime.text = task.deadlineTime
            if(task.priority==PriorityEnum.LOW){
                taskPriority.setBackgroundResource(R.drawable.ic_priority_green)
            }else if(task.priority==PriorityEnum.MEDIUM){
                taskPriority.setBackgroundResource(R.drawable.ic_priority_orange)
            } else if(task.priority==PriorityEnum.HIGH){
                taskPriority.setBackgroundResource(R.drawable.ic_priority_red)
            }
            taskIsActive.isChecked = !task.isActive

            taskNameText.setOnClickListener {
                println("clicked =================== " + taskNameText.text)
                listener.onClickTask(task)
            }
            taskIsActive.setOnClickListener {
                listener.onClickCheckbox(task)
            }
        }

        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.todo_list_fragment, parent, false)
                return TaskViewHolder(view)
            }
        }
    }

    companion object {
        private val WORDS_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    interface Listener {
        fun onClickTask(task: Task)
        fun onClickCheckbox(task: Task)
    }
}