package cz.cvut.fel.pda.bee_calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.bee_calendar.model.Event

class EventListAdapter (private val listener: Listener): ListAdapter<Event, EventListAdapter.EventViewHolder>(WORDS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(listener, current)

//        holder.itemView.findViewById<ImageButton>(R.id.catDelButton).setOnClickListener {
//            println("***************************222222 "+ getItem(position).id)
//            //зробити спінер і кнопку видалення одну, бо це якийсь треш((
//        }
    }





    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameText: TextView = itemView.findViewById(R.id.eventName)
        private val eventTimeFrom: TextView = itemView.findViewById(R.id.timeFrom)
        private val eventTimeTo: TextView = itemView.findViewById(R.id.timeTo)

        fun bind(listener: Listener, event: Event) {
            eventNameText.text = event.name
            eventTimeFrom.text = event.timeFrom
            eventTimeTo.text = event.timeTo

            eventNameText.setOnClickListener {
                println("clicked =================== " + eventNameText.text)
                listener.onClickItem(event)
            }
        }

        companion object {
            fun create(parent: ViewGroup): EventViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.schedule_fragment, parent, false)
                return EventViewHolder(view)
            }
        }
    }

    companion object {
        private val WORDS_COMPARATOR = object : DiffUtil.ItemCallback<Event>() {
            override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    interface Listener {
        fun onClickItem(event: Event)
    }
}