package ru.kolesnikovdmitry.blogapp.DAO

import androidx.room.*
import ru.kolesnikovdmitry.blogapp.models.Accounts
import ru.kolesnikovdmitry.blogapp.models.Posts

@Dao
interface PostsDao {
    @Insert
    fun add(post : Posts)

    @Insert
    fun addAll(vararg  posts : Posts)

    @Update
    fun update(post: Posts)

    @Delete
    fun delete(post: Posts)

    @Query("SELECT * FROM posts")
    fun getAll(): List<Posts>

    @Query("DELETE FROM posts WHERE id == :id  ")
    fun deleteOnLogin(id : String)
}