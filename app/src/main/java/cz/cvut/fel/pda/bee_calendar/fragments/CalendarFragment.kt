package cz.cvut.fel.pda.bee_calendar.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.activities.EventDetailsActivity
import cz.cvut.fel.pda.bee_calendar.activities.MainActivity
import cz.cvut.fel.pda.bee_calendar.activities.TaskDetailsActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentCalendarBinding
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.utils.EventListAdapter
import cz.cvut.fel.pda.bee_calendar.utils.TaskListAdapter
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import java.time.LocalDate

class CalendarFragment: Fragment(), EventListAdapter.Listener, TaskListAdapter.Listener {
    private lateinit var binding: FragmentCalendarBinding
    lateinit var dateTV: TextView
    lateinit var calendarView: CalendarView
    lateinit var cardView2: LinearLayout
    private var actualDate: LocalDate = LocalDate.now()

    private val eventViewModel: EventViewModel by activityViewModels {
        EventViewModel.EventViewModelFactory(requireContext())
    }
    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskViewModel.TaskViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity).supportActionBar?.title = "CALENDAR"
    }

    private fun loadEvents(adapter: EventListAdapter){
        eventViewModel.getEventsByDate(actualDate).observe(viewLifecycleOwner) { words ->
            words.let {
                adapter.submitList(it)
            }
        }
    }

    private fun loadTasks(adapter: TaskListAdapter){
        taskViewModel.getTasksByDate(actualDate).observe(viewLifecycleOwner) { words ->
            words.let {
                adapter.submitList(it)
            }
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTV = binding.idTVDate
        calendarView = binding.calendarView
        cardView2 = binding.cardView2
        dateTV.setText(LocalDate.now().toString());
        val recyclerView = binding.recyclerview
        val adapter = EventListAdapter(this)
        val adapter1 = TaskListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        loadEvents(adapter)

        val schedule = binding.schedule
        schedule.setOnClickListener {
            recyclerView.adapter = adapter
            loadEvents(adapter)
        }

        val todo_list = binding.todoList
        todo_list.setOnClickListener {
            recyclerView.adapter = adapter1
            loadTasks(adapter1)
        }


        calendarView
            .setOnDateChangeListener(
                CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->

                    actualDate = LocalDate.of(year, month + 1, dayOfMonth)

                    dateTV.setText(actualDate.toString())
                    if (recyclerView.adapter == adapter) {
                        loadEvents(adapter)
                    } else if (recyclerView.adapter == adapter1) {
                        loadTasks(adapter1)
                    }
                })
    }

    override fun onClickItem(event: Event) {
        val intent = Intent(activity, EventDetailsActivity::class.java).apply {
            putExtra("event-detail", event)
        }
        startActivity(intent)
    }

    override fun onClickTask(task: Task) {
        val intent = Intent(activity, TaskDetailsActivity::class.java).apply {
            putExtra("task-detail", task)
        }
        startActivity(intent)
    }

    override fun onClickCheckbox(task: Task) {
        task.isActive = !task.isActive
        taskViewModel.updateTask(task)
    }

}