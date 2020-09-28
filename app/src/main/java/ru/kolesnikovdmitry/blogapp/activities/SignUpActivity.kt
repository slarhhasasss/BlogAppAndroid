package ru.kolesnikovdmitry.blogapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
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

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSubmitActSignUp.setOnClickListener {
            onClickSubmit()
        }
    }

    private fun onClickSubmit() {
        val strLogin = editTextLoginActSignUp.text.toString()
        val strPassword = editTextPasswordActSignUp.text.toString()

        btnSubmitActSignUp.isEnabled = false

        if (strLogin == "" || strPassword == "") {
            Toast.makeText(applicationContext, "Empty fields", Toast.LENGTH_LONG).show()
            btnSubmitActSignUp.isEnabled = true
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val respCode = trySignUp(strLogin, strPassword)
            when(respCode) {
                HttpHelper.RESP_OK -> {
                    //добавляем пользователя в базу данных аккаунтов, если его там нет
                    addDataToDB(strLogin, strPassword)
                    //это наш пользователь текущего сеанса.
                    MyApp.CUR_USER = strLogin;
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Ok", Toast.LENGTH_LONG).show()
                        btnSubmitActSignUp.isEnabled = true
                        setResult(RESULT_OK)
                        finish()
                    }
                }
                HttpHelper.RESP_NOT_FOUND -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Page not found!", Toast.LENGTH_LONG).show()
                        btnSubmitActSignUp.isEnabled = true
                    }
                }
                HttpHelper.RESP_LOGIN_EXISTS -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "This login is already in use!", Toast.LENGTH_LONG).show()
                        btnSubmitActSignUp.isEnabled = true
                    }
                }
                HttpHelper.RESP_SERVER_ERROR -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Server Error!", Toast.LENGTH_LONG).show()
                        btnSubmitActSignUp.isEnabled = true
                    }
                }
                HttpHelper.RESP_INTERNAL_ERROR -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Internal error!!", Toast.LENGTH_LONG).show()
                        btnSubmitActSignUp.isEnabled = true
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Unusual error!!!", Toast.LENGTH_LONG).show()
                        btnSubmitActSignUp.isEnabled = true
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

    private fun trySignUp(strLogin: String, strPassword: String): String {
        try {
            val strUrl = HttpHelper.getBASE_URL() + "/sign_up"

            val hashMapReq = HashMap<String, Array<String>>()
            hashMapReq["login"] = arrayOf(strLogin)
            hashMapReq["password"] = arrayOf(strPassword)

            val reqStr = HttpHelper.getStringForQuery(hashMapReq)

            Log.e("myApp", reqStr)

            val url = URL(strUrl)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doInput = true
            connection.doOutput = true

            connection.connect()

            val os = connection.outputStream
            val byteArrRespReq = reqStr.toByteArray()
            os.write(byteArrRespReq)
            os.flush()
            os.close()

            val iStream = connection.inputStream
            val byteArrResp = iStream.readBytes()
            val strResp = String(byteArrResp)
            iStream.close()

            connection.disconnect()

            return strResp
        } catch (th : Throwable) {
            Log.e("myApp", th.message.toString())
            return "101"
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        finish()
    }
}