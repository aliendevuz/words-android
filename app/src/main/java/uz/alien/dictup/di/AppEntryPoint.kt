package uz.alien.dictup.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uz.alien.dictup.initializer.AppInitializer

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
    fun getStartupInitializer(): AppInitializer
}