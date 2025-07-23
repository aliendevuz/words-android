package uz.alien.test.todo.presenter

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import uz.alien.test.databinding.TodoActivityMainBinding
import uz.alien.test.todo.domain.model.Todo
import uz.alien.test.todo.utils.collectWithLifecycle

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  private lateinit var binding: TodoActivityMainBinding
  private lateinit var adapter: TodoAdapter
  private val viewModel: TodoViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = TodoActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    initViews()
    observeViewModel()
  }

  private fun initViews () {
    setupRecyclerView()

    binding.fabAdd.setOnClickListener {
      DialogManager.showAddTodoDialog(this@MainActivity, layoutInflater) { title ->
        viewModel.add(Todo(title = title))
      }
    }
  }

  private fun setupRecyclerView() {
    adapter = TodoAdapter(
      onItemClick = { todo ->
        DialogManager.showEditTodoDialog(this@MainActivity, layoutInflater, todo) { title ->
          viewModel.update(todo.copy(title = title))
        }
      },
      onItemLongClick = { todo ->
        DialogManager.showConfirmDeleteDialog(this@MainActivity) {
          viewModel.delete(todo)
        }
      }
    )
    binding.rvTodo.layoutManager = LinearLayoutManager(this)
    binding.rvTodo.adapter = adapter
  }

  private fun observeViewModel() {
    viewModel.todos.collectWithLifecycle(this) { list ->
      adapter.submitList(list)
    }
  }
}
