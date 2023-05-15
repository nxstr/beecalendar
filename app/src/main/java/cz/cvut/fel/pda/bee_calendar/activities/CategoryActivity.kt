package cz.cvut.fel.pda.bee_calendar.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.bee_calendar.utils.CategoryListAdapter
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityCategoryBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel

class CategoryActivity: AppCompatActivity(), CategoryListAdapter.Listener {
    private lateinit var binding: ActivityCategoryBinding

    private var default: Int? = null

    private val categoryViewModel: CategoryViewModel by viewModels {
        CategoryViewModel.CategoryViewModelFactory(this)
    }

    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModel.TaskViewModelFactory(this)
    }

    private val eventViewModel: EventViewModel by viewModels {
        EventViewModel.EventViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = CategoryListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        categoryViewModel.categoriesLiveData.observe(this) { words ->
            // Update the cached copy of the words in the adapter.
            words.let {

                val arr = ArrayList<Category>()
                for(i in it){
                    if(!i.name.equals("default")) {
                        arr.add(i)
                    }else{
                        default = i.id
                    }
                }
                adapter.submitList(arr)
            }
        }

        binding.addCategoryButton.setOnClickListener {
            categoryViewModel.insert(
                categoryName = binding.categoryName.text.toString()
            )
            binding.categoryName.text = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun deleteCategory(catId: Int?) {
        if(catId!=null && default!=null){
            eventViewModel.getEventsByCatFlow(catId).observe(this){ events ->
                events.let {
                    for(i in it){
                        i.categoryId = default as Int
                        eventViewModel.updateEvent(i)
                    }
                }

            }

            taskViewModel.getTasksByCatFlow(catId).observe(this){ events ->
                events.let {
                    for(i in it){
                        i.categoryId = default as Int
                        taskViewModel.updateTask(i)
                    }
                }

            }
            categoryViewModel.delete(catId)
        }
    }
}
