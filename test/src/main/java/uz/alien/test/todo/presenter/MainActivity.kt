package uz.alien.test.todo.presenter

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import uz.alien.test.R
import uz.alien.test.databinding.TodoActivityMainBinding
import uz.alien.test.databinding.TodoDialogAddTodoBinding
import uz.alien.test.todo.domain.model.Todo

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private lateinit var binding: TodoActivityMainBinding
  private lateinit var adapter: TodoAdapter
  private val viewModel: TodoViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = TodoActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupRecyclerView()
    observeViewModel()

    binding.fabAdd.setOnClickListener {
      showAddDialog()
    }
  }

  private fun setupRecyclerView() {
    adapter = TodoAdapter(
      onItemClick = { todo -> showEditDialog(todo) },
      onItemLongClick = { todo -> confirmDelete(todo) }
    )
    binding.rvTodo.layoutManager = LinearLayoutManager(this)
    binding.rvTodo.adapter = adapter
  }

  private fun observeViewModel() {
    viewModel.todos.collectWithLifecycle(this) { list ->
      adapter.submitList(list)
    }
  }

  private fun showEditDialog(todo: Todo) {
    val dialogView = LayoutInflater.from(this).inflate(R.layout.todo_dialog_add_todo, null)
    val editText = dialogView.findViewById<EditText>(R.id.etTodo)
    editText.setText(todo.title)

    AlertDialog.Builder(this)
      .setTitle("Edit ToDo")
      .setView(dialogView)
      .setPositiveButton("Update") { _, _ ->
        val updatedText = editText.text.toString()
        if (updatedText.isNotBlank()) {
          viewModel.update(todo.copy(title = updatedText))
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }


  private fun confirmDelete(todo: Todo) {
    AlertDialog.Builder(this)
      .setTitle("Delete ToDo")
      .setMessage("Are you sure you want to delete this task?")
      .setPositiveButton("Delete") { _, _ ->
        viewModel.delete(todo)
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  private fun showAddDialog() {
    val addTodoBinding = TodoDialogAddTodoBinding.inflate(layoutInflater)

    val dialogView = addTodoBinding.root
    val editText = dialogView.findViewById<EditText>(R.id.etTodo)

    AlertDialog.Builder(this)
      .setTitle("Add ToDo")
      .setView(dialogView)
      .setPositiveButton("Add") { _, _ ->
        val title = editText.text.toString()
        if (title.isNotBlank()) {
          viewModel.add(Todo(title = title))
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }
}

fun <T> Flow<T>.collectWithLifecycle(owner: LifecycleOwner, action: suspend (T) -> Unit) {
  owner.lifecycleScope.launch {
    collect { action(it) }
  }
}
