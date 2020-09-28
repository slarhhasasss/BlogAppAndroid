package ru.kolesnikovdmitry.blogapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kolesnikovdmitry.blogapp.DAO.PostsDao
import ru.kolesnikovdmitry.blogapp.models.Posts

@Database(entities = arrayOf(Posts::class), version = 1)
abstract class PostsDB : RoomDatabase() {
    abstract fun getPostsDAO() : PostsDao
}