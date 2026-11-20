package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        PostReactionEntity::class,
        CommentEntity::class,
        FollowEntity::class,
        ClanEntity::class,
        ChatThreadEntity::class,
        ChatMessageEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun postReactionDao(): PostReactionDao
    abstract fun commentDao(): CommentDao
    abstract fun followDao(): FollowDao
    abstract fun clanDao(): ClanDao
    abstract fun chatDao(): ChatDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE users ADD COLUMN profileBgStyle TEXT NOT NULL DEFAULT 'OBSIDIAN'")
                db.execSQL("ALTER TABLE users ADD COLUMN favoriteGamesCsv TEXT NOT NULL DEFAULT 'valorant'")
                db.execSQL("ALTER TABLE users ADD COLUMN bioMotto TEXT NOT NULL DEFAULT 'Sin límites en la arena.'")
                db.execSQL("ALTER TABLE users ADD COLUMN playSchedule TEXT NOT NULL DEFAULT 'Noches y fines de semana'")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "playforja_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
