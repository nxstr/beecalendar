package cz.cvut.fel.pda.bee_calendar.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.bee_calendar.utils.CategoryListAdapter
import cz.cvut.fel.pda.bee_calendar.R
import cz.cvut.fel.pda.bee_calendar.databinding.ActivityCategoryBinding
import cz.cvut.fel.pda.bee_calendar.model.Category
import cz.cvut.fel.pda.bee_calendar.utils.Vibrations
import cz.cvut.fel.pda.bee_calendar.viewmodels.CategoryViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.EventViewModel
import cz.cvut.fel.pda.bee_calendar.viewmodels.TaskViewModel
import kotlinx.coroutines.runBlocking

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
        supportActionBar?.title = "CATEGORIES"


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
            var exists = false
            runBlocking {
                if (binding.categoryName.text.toString() != "") {
                    if (categoryViewModel.getByName(binding.categoryName.text.toString()) != null) {
                        exists = true
                        Toast.makeText(this@CategoryActivity, "Category with same name already exists!", Toast.LENGTH_SHORT).show()
                        Vibrations.vibrate(this@CategoryActivity)
                    }
                }
            }
            if (!exists) {
                categoryViewModel.insert(
                    categoryName = binding.categoryName.text.toString()
                )
                binding.categoryName.text = null
            }
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
        Toast.makeText(this,
            "All the actions from this category will be moved to default category", Toast.LENGTH_SHORT).show()
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
