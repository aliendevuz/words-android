package uz.alien.test.todo.presenter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.alien.test.R
import uz.alien.test.databinding.TodoItemTodoBinding
import uz.alien.test.todo.domain.model.Todo

class TodoAdapter(
  private val onItemClick: (Todo) -> Unit,
  private val onItemLongClick: (Todo) -> Unit
) : ListAdapter<Todo, TodoAdapter.ToDoViewHolder>(DiffCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item_todo, parent, false)
    return ToDoViewHolder(view)
  }

  override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
    val todo = getItem(position)
    holder.bind(todo)

    holder.itemView.setOnClickListener {
      onItemClick(todo)
    }

    holder.itemView.setOnLongClickListener {
      onItemLongClick(todo)
      true
    }
  }

  inner class ToDoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding = TodoItemTodoBinding.bind(itemView)
    private val tvTitle = binding.tvTitle

    fun bind(todo: Todo) {
      tvTitle.text = todo.title
    }
  }

  class DiffCallback : DiffUtil.ItemCallback<Todo>() {
    override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
      return oldItem == newItem
    }
  }
}
