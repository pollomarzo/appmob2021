package com.uni.pantry.data.items

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// using this to make sure only one instance of repo and DB
// In a different part of the doc: this approach is discouraged;
// a static singleton can provide the same functionality in a more modular way.
class ItemsApplication : Application(){
    // No need to cancel this scope as it'll be torn down with the process
    // unrelated to UI, so shouldn't be viewModelScope
    val applicationScope = CoroutineScope(SupervisorJob())


    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { ItemRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { ItemRepository(database.itemDao()) }

}