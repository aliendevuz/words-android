package uz.alien.test.todo.presenter

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import uz.alien.test.databinding.TodoDialogAddTodoBinding
import uz.alien.test.todo.domain.model.Todo

object DialogManager {
  fun showAddTodoDialog(
    context: Context,
    layoutInflater: LayoutInflater,
    onAdd: (String) -> Unit
  ) {
    val addTodoBinding = TodoDialogAddTodoBinding.inflate(layoutInflater)
    val dialogView = addTodoBinding.root
    val editText = addTodoBinding.etTodo

    AlertDialog.Builder(context)
      .setTitle("Add ToDo")
      .setView(dialogView)
      .setPositiveButton("Add") { _, _ ->
        val title = editText.text.toString()
        if (title.isNotBlank()) {
          onAdd(title)
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  fun showEditTodoDialog(
    context: Context,
    layoutInflater: LayoutInflater,
    todo: Todo,
    onEdit: (String) -> Unit
  ) {
    val addTodoBinding = TodoDialogAddTodoBinding.inflate(layoutInflater)
    val dialogView = addTodoBinding.root
    val editText = addTodoBinding.etTodo
    editText.setText(todo.title)

    AlertDialog.Builder(context)
      .setTitle("Edit ToDo")
      .setView(dialogView)
      .setPositiveButton("Update") { _, _ ->
        val updatedText = editText.text.toString()
        if (updatedText.isNotBlank()) {
          onEdit(updatedText)
        }
      }
      .setNegativeButton("Cancel", null)
      .show()
  }

  fun showConfirmDeleteDialog(
    context: Context,
    onDelete: () -> Unit
  ) {
    AlertDialog.Builder(context)
      .setTitle("Delete ToDo")
      .setMessage("Are you sure you want to delete this task?")
      .setPositiveButton("Delete") { _, _ -> onDelete() }
      .setNegativeButton("Cancel", null)
      .show()
  }
}