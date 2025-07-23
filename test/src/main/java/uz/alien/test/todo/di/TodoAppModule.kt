package uz.alien.test.todo.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uz.alien.test.todo.data.local.TodoDao
import javax.inject.Singleton
import uz.alien.test.todo.data.local.TodoDatabase
import uz.alien.test.todo.data.repository.TodoRepositoryImpl
import uz.alien.test.todo.domain.repository.TodoRepository
import uz.alien.test.todo.domain.usecase.AddTodoUseCase
import uz.alien.test.todo.domain.usecase.DeleteTodoUseCase
import uz.alien.test.todo.domain.usecase.GetTodosUseCase
import uz.alien.test.todo.domain.usecase.UpdateTodoUseCase

@Module
@InstallIn(SingletonComponent::class)
object TodoAppModule {

  @Provides
  @Singleton
  fun provideDatabase(@ApplicationContext context: Context): TodoDatabase {
    return Room.databaseBuilder(
      context,
      TodoDatabase::class.java,
      "todo_db"
    )
      .build()
  }


  @Provides
  fun provideTodoDao(db: TodoDatabase): TodoDao = db.todoDao()

  @Provides
  fun provideRepository(dao: TodoDao): TodoRepository = TodoRepositoryImpl(dao)


  @Provides
  fun provideAddTodoUseCase(repository: TodoRepository): AddTodoUseCase {
    return AddTodoUseCase(repository)
  }

  @Provides
  fun provideGetTodosUseCase(repository: TodoRepository): GetTodosUseCase {
    return GetTodosUseCase(repository)
  }

  @Provides
  fun provideUpdateTodoUseCase(repository: TodoRepository): UpdateTodoUseCase {
    return UpdateTodoUseCase(repository)
  }

  @Provides
  fun provideDeleteTodoUseCase(repository: TodoRepository): DeleteTodoUseCase {
    return DeleteTodoUseCase(repository)
  }
}
