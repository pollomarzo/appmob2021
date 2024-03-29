package com.uni.pantry.data.items

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Item::class), version = 4, exportSchema = false)
abstract class ItemRoomDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    // this way, on create we populate the database
    private class ItemDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("TAGGED","insertin!")
            // once we keep the data through app restarts, we'll remove this
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.itemDao())
                }
            }
        }

        suspend fun populateDatabase(itemDao: ItemDao) {
            // Delete all content here.
            itemDao.deleteAll()

            // Add sample words.
            var item = Item("0100", "Default Element",
                1, "This is a default element. Feel free to play with it " +
                        "or delete it right away. It won't be here on your next restart!",
                1, "___")
            itemDao.insert(item)

        }
    }
    companion object {
        // singleton so that not multiple database openings
        @Volatile
        private var INSTANCE: ItemRoomDatabase? = null

        // since we'll repopulate the database away from UI thread we need a scope where it'll run
        fun getDatabase(context: Context, scope: CoroutineScope):ItemRoomDatabase{
            // if no instance, make it. if instance, return it
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemRoomDatabase::class.java,
                    "item_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(ItemDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}