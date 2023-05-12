package cz.cvut.fel.pda.bee_calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cz.cvut.fel.pda.bee_calendar.dao.CategoryDao
import cz.cvut.fel.pda.bee_calendar.model.Category

class CategoryListAdapter : ListAdapter<Category, CategoryListAdapter.CategoryViewHolder>(WORDS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name)

        holder.itemView.findViewById<ImageButton>(R.id.catDelButton).setOnClickListener {
                println("***************************222222 "+ getItem(position).id)
                //зробити спінер і кнопку видалення одну, бо це якийсь треш((
        }
    }





    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryItemView: TextView = itemView.findViewById(R.id.textView)

        fun bind(text: String?) {
            categoryItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): CategoryViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycleview_item, parent, false)
                return CategoryViewHolder(view)
            }
        }
    }

    companion object {
        private val WORDS_COMPARATOR = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}