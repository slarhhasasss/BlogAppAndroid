package ru.kolesnikovdmitry.blogapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kolesnikovdmitry.blogapp.MyApp
import ru.kolesnikovdmitry.blogapp.R
import ru.kolesnikovdmitry.blogapp.classes.HttpHelper
import ru.kolesnikovdmitry.blogapp.models.Accounts
import java.net.HttpURLConnection
import java.net.URL

class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnSubmitActSignIn.setOnClickListener {
            onClickSubmit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun onClickSubmit() {
        val strLogin = editTextLoginActSignIn.text.toString()
        val strPassword = editTextPasswordActSignIn.text.toString()

        btnSubmitActSignIn.isEnabled = false

        if(strLogin.equals("") || strPassword.equals("")) {
            Toast.makeText(applicationContext, "Empty fields", Toast.LENGTH_LONG).show()
            btnSubmitActSignIn.isEnabled = true
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val respCode = tryToSignIn(strLogin, strPassword)
            when {
                respCode.equals(HttpHelper.RESP_OK) -> {
                    addDataToDB(strLogin, strPassword)
                    MyApp.CUR_USER = strLogin
                    withContext(Dispatchers.Main) {
                        btnSubmitActSignIn.isEnabled = true
                        setResult(RESULT_OK)
                        finish()
                    }
                }
                respCode.equals(HttpHelper.RESP_WRONG_LOGIN) -> {
                    withContext(Dispatchers.Main) {
                        btnSubmitActSignIn.isEnabled = true
                        Toast.makeText(applicationContext, "Wrong Login!", Toast.LENGTH_LONG).show()
                    }
                }
                respCode == HttpHelper.RESP_WRONG_PASS -> {
                    withContext(Dispatchers.Main) {
                        btnSubmitActSignIn.isEnabled = true
                        Toast.makeText(applicationContext, "Wrong Password", Toast.LENGTH_LONG).show()
                    }
                }
                respCode == HttpHelper.RESP_INTERNAL_ERROR -> {
                    withContext(Dispatchers.Main) {
                        btnSubmitActSignIn.isEnabled = true
                        Toast.makeText(applicationContext, "Internal error in app!", Toast.LENGTH_LONG).show()
                    }
                }
                respCode == HttpHelper.RESP_NOT_FOUND -> {
                    withContext(Dispatchers.Main) {
                        btnSubmitActSignIn.isEnabled = true
                        Toast.makeText(applicationContext, "Server Not Found!", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        btnSubmitActSignIn.isEnabled = true
                        Toast.makeText(applicationContext, "Server Error!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    //Сохранение аккаунтов в локальной базе даннных
    private fun addDataToDB(strLogin: String, strPassword: String) {
        try{
            val accounts = MyApp.accountsDB.getAccountsDAO().getAll()
            //если такой логин  уже есть, то не добавляем его в баазу данных
            accounts.forEach{el ->
                if (el.login == strLogin) {
                    return
                }
            }
            val newAcc = Accounts(login = strLogin, password = strPassword, id = 0)
            MyApp.accountsDB.getAccountsDAO().add(newAcc)

            Log.e("myApp", "successfully added to database")
        } catch (th : Throwable) {
            Log.e("myApp", "Error: " + th.message.toString())
        }
    }

    private fun tryToSignIn(strLogin: String, strPassword: String): String {

        val hashMap = HashMap<String, Array<String>>()
        hashMap["login"] = arrayOf(strLogin)
        hashMap["password"] = arrayOf(strPassword)

        try {
            val queryStr = HttpHelper.getStringForQuery(hashMap)

            val strUrl = HttpHelper.getBASE_URL() + "/sign_in"

            val url = URL(strUrl)
            Log.e("myApp", strUrl)

            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true

            connection.connect()

            val os = connection.outputStream
            val byteReq = queryStr.toByteArray()
            os.write(byteReq)
            os.flush()
            os.close()

            val iStream = connection.inputStream
            val byteResp = iStream.readBytes()
            val respStr = String(byteResp)
            iStream.close()

            connection.disconnect()

            return respStr
        } catch (th : Throwable) {
            Log.e("myApp", "Error in sending message: ${th.message.toString()}")
            return "101"
        }
    }
}