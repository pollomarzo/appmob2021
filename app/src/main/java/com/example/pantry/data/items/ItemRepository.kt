package com.example.pantry.data.items

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.pantry.data.Result
import kotlinx.coroutines.flow.Flow

/**
 * Class that requests items and item information from the remote data source and
 * maintains an in-memory cache of items. Provides a clean API for data access to
 * the rest of the application
// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
 */

class ItemRepository(private val itemDao: ItemDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allItems: Flow<List<Item>> = itemDao.getAlphabetizedItems()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: Item) : Result<Item> {
        //TODO: can it fail?
        val res = itemDao.insert(item)
        Log.d("RETURNVALUE", res.toString())
        return Result.Success(item)
    }
    @WorkerThread
    suspend fun delete(item: Item) : Result<Item> {
        itemDao.delete(item)
        return Result.Success(item)
    }
    @WorkerThread
    suspend fun update(item: Item) : Result<Item> {
        itemDao.update(item)
        return Result.Success(item)
    }


//    // in-memory cache of the loggedInUser object
//    var user: LoggedInUser? = null
//        private set
//
//    val isLoggedIn: Boolean
//        get() = user != null
//
//    init {
//        // If user credentials will be cached in local storage, it is recommended it be encrypted
//        // @see https://developer.android.com/training/articles/keystore
//        user = null
//    }
//
//    fun logout() {
//        user = null
//        dataSource.logout()
//    }
//
//    fun addItem(username: String, password: String): Result<LoggedInUser> {
//        // handle login
//        val result = dataSource.addItem(username, password)
//
//        if (result is Result.Success) {
//            setLoggedInUser(result.data)
//        }
//
//        return result
//    }
//
//    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
//        this.user = loggedInUser
//        // If user credentials will be cached in local storage, it is recommended it be encrypted
//        // @see https://developer.android.com/training/articles/keystore
//    }
}