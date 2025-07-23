package uz.alien.test.todo.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectWithLifecycle(owner: LifecycleOwner, action: suspend (T) -> Unit) {
  owner.lifecycleScope.launch {
    collect { action(it) }
  }
}
