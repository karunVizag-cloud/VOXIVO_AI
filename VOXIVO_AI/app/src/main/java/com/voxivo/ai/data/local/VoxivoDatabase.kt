package com.voxivo.ai.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.voxivo.ai.data.local.dao.AppSettingsDao
import com.voxivo.ai.data.local.dao.ReminderDao
import com.voxivo.ai.data.local.dao.TaskDao
import com.voxivo.ai.data.local.dao.UserProfileDao
import com.voxivo.ai.data.local.entity.AppSettingsEntity
import com.voxivo.ai.data.local.entity.RecurrenceEntity
import com.voxivo.ai.data.local.entity.ReminderEntity
import com.voxivo.ai.data.local.entity.ReminderHistoryEntity
import com.voxivo.ai.data.local.entity.TaskEntity
import com.voxivo.ai.data.local.entity.UserProfileEntity
import com.voxivo.ai.data.local.entity.VoicePreferencesEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ReminderEntity::class,
        TaskEntity::class,
        UserProfileEntity::class,
        RecurrenceEntity::class,
        ReminderHistoryEntity::class,
        VoicePreferencesEntity::class,
        AppSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VoxivoDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao
    abstract fun taskDao(): TaskDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun appSettingsDao(): AppSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: VoxivoDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): VoxivoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VoxivoDatabase::class.java,
                    "voxivo_ai_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialDefaults(database)
                    }
                }
            }

            private suspend fun populateInitialDefaults(database: VoxivoDatabase) {
                val settingsDao = database.appSettingsDao()
                val profileDao = database.userProfileDao()

                settingsDao.saveSettings(AppSettingsEntity())
                settingsDao.saveVoicePreferences(VoicePreferencesEntity())
                profileDao.saveUserProfile(UserProfileEntity())
            }
        }
    }
}
