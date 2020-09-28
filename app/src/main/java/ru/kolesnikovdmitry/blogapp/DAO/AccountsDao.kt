package ru.kolesnikovdmitry.blogapp.DAO

import androidx.room.*
import ru.kolesnikovdmitry.blogapp.models.Accounts

@Dao
interface AccountsDao {

    @Insert
    fun add(account : Accounts)

    @Insert
    fun addAll(vararg  accounts : Accounts)

    @Update
    fun update(account : Accounts)

    @Delete
    fun delete(account : Accounts)

    @Query("SELECT * FROM accounts")
    fun getAll(): List<Accounts>

    @Query("DELETE FROM accounts WHERE login == :login  ")
    fun deleteOnLogin(login : String)

}