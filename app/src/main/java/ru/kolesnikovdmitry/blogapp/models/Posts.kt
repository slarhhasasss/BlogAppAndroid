package ru.kolesnikovdmitry.blogapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
class Posts {

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
    var author: String? = null
    var date: String? = null
    var title: String? = null
    var text: String? = null
    var likes: String? = null
}