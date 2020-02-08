package bankernisarg.app.com.locationdemo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import bankernisarg.app.com.locationdemo.data.db.entities.Trip

@Database(
    entities = [Trip::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTripDao(): TripDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "MyDatabase.db"
            ).build()
    }
}