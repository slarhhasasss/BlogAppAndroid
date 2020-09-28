package ru.kolesnikovdmitry.blogapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Accounts(
       @PrimaryKey(autoGenerate = true)
       val id: Long,

       val login : String,

       val password : String
)