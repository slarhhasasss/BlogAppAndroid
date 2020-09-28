package ru.kolesnikovdmitry.blogapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.kolesnikovdmitry.blogapp.DAO.AccountsDao
import ru.kolesnikovdmitry.blogapp.models.Accounts

@Database(entities = [Accounts::class], version = 1)
abstract class AccountsDB : RoomDatabase() {
    abstract fun getAccountsDAO() : AccountsDao
}