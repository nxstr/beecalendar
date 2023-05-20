package cz.cvut.fel.pda.bee_calendar.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.activities.EventDetailsActivity
import cz.cvut.fel.pda.bee_calendar.activities.MainActivity
import cz.cvut.fel.pda.bee_calendar.activities.TaskDetailsActivity
import cz.cvut.fel.pda.bee_calendar.databinding.FragmentSearchBinding
import cz.cvut.fel.pda.bee_calendar.model.Event
import cz.cvut.fel.pda.bee_calendar.model.Task
import cz.cvut.fel.pda.bee_calendar.utils.EventListAdapter
import cz.cvut.fel.pda.bee_calendar.utils.TaskListAdapter
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import kotlinx.coroutines.runBlocking

class SearchFragment: Fragment(), EventListAdapter.Listener, TaskListAdapter.Listener {

    private lateinit var binding: FragmentSearchBinding

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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (activity as MainActivity).supportActionBar?.title = "SEARCH"
        super.onCreate(savedInstanceState)
    }

    private fun loadEvents(adapter: EventListAdapter){
        runBlocking {
            adapter.submitList(eventViewModel.getByName(binding.actionName.text.toString()))
        }
    }

    private fun loadTasks(adapter: TaskListAdapter){
        runBlocking {
            adapter.submitList(taskViewModel.getByName(binding.actionName.text.toString()))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerview
        val adapter = EventListAdapter(this)
        val adapter1 = TaskListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val eventButton = binding.eventRadioButton
        val taskButton = binding.taskRadioButton
        if(binding.actionName.text.toString()!="enter action name") {
                eventButton.setOnClickListener {
                    recyclerView.adapter = adapter
                    loadEvents(adapter)
                }

                taskButton.setOnClickListener {
                    recyclerView.adapter = adapter1
                    loadTasks(adapter1)
                }
            }else{
                Toast.makeText(activity, "Enter action name!", Toast.LENGTH_LONG).show()
            }
        val back = binding.back
        back.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.main_fr, CalendarFragment())
            transaction.disallowAddToBackStack()
            transaction.commit()

        }
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