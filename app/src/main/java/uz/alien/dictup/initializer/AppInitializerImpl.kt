package uz.alien.dictup.initializer

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import uz.alien.dictup.di.AppEntryPoint

class AppInitializerImpl : Initializer<Unit> {

    override fun create(context: Context) {
        val entryPoint = EntryPointAccessors.fromApplication(context, AppEntryPoint::class.java)
        val startupInitializer = entryPoint.getStartupInitializer()

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            startupInitializer.run()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}