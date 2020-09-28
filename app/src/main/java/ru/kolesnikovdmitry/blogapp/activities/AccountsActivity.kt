package ru.kolesnikovdmitry.blogapp.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_accounts.*
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

class AccountsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)
       displayAccountList()
    }

    private fun createCard(elem: Accounts, context: Context?): View? {
        val txtView = TextView(context)
        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        params.setMargins(15, 15, 15, 15)
        txtView.layoutParams = params
        txtView.text = elem.login
        txtView.textSize = 20f
        txtView.setTextColor(Color.BLACK)

        txtView.setOnClickListener {
            tryToSignIn(elem)
        }

        txtView.setOnLongClickListener {
            showPopUpMenu(context, it, elem.login)
            return@setOnLongClickListener true
        }

        return txtView
    }

    private fun showPopUpMenu(context: Context?, view: View, login: String) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menu.add("Delete account").setOnMenuItemClickListener {
            deleteAccount(login)
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    private fun deleteAccount(login: String) {
        scrollViewActAccounts.visibility = ScrollView.INVISIBLE
        progressBarActAccounts.visibility = ProgressBar.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            MyApp.accountsDB.getAccountsDAO().deleteOnLogin(login)
            withContext(Dispatchers.Main) {
                displayAccountList()
            }
        }
        Toast.makeText(applicationContext, "deleted $login", Toast.LENGTH_LONG).show()
    }

    private fun displayAccountList() {
        try {
            linearLayActAccounts.removeAllViews()
            CoroutineScope(Dispatchers.IO).launch {
                val acc = MyApp.accountsDB.getAccountsDAO().getAll()
                withContext(Dispatchers.Main) {
                    if (acc.isEmpty()) {
                        textViewEmptyAccountsList.visibility = TextView.VISIBLE
                        progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                        scrollViewActAccounts.visibility = ScrollView.INVISIBLE
                        return@withContext
                    }
                    for (elem in acc) {
                        val card = createCard(elem, applicationContext)
                        linearLayActAccounts.addView(card)
                    }
                    scrollViewActAccounts.visibility = ScrollView.VISIBLE
                    progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                }
            }
        } catch (th: Throwable) {
            Log.e("myApp", "Error in AccountsActivity: " + th.message)
        }
    }

    private fun tryToSignIn(user: Accounts) {

        scrollViewActAccounts.visibility = ScrollView.INVISIBLE
        progressBarActAccounts.visibility = ProgressBar.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val respCode = sendRequest(user.login, user.password)
            when {
                respCode.equals(HttpHelper.RESP_OK) -> {
                    MyApp.CUR_USER = user.login
                    withContext(Dispatchers.Main) {
                        scrollViewActAccounts.visibility = ScrollView.VISIBLE
                        progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                        setResult(RESULT_OK)
                        finish()
                    }
                }
                respCode.equals(HttpHelper.RESP_WRONG_LOGIN) -> {
                    withContext(Dispatchers.Main) {
                        scrollViewActAccounts.visibility = ScrollView.VISIBLE
                        progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                        Toast.makeText(applicationContext, "Wrong Login!", Toast.LENGTH_LONG).show()
                    }
                }
                respCode == HttpHelper.RESP_WRONG_PASS -> {
                    withContext(Dispatchers.Main) {
                        scrollViewActAccounts.visibility = ScrollView.VISIBLE
                        progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                        Toast.makeText(applicationContext, "Wrong Password", Toast.LENGTH_LONG).show()
                    }
                }
                respCode == HttpHelper.RESP_INTERNAL_ERROR -> {
                    withContext(Dispatchers.Main) {
                        scrollViewActAccounts.visibility = ScrollView.VISIBLE
                        progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                        Toast.makeText(applicationContext, "Internal error in app!", Toast.LENGTH_LONG).show()
                    }
                }
                respCode == HttpHelper.RESP_NOT_FOUND -> {
                    withContext(Dispatchers.Main) {
                        scrollViewActAccounts.visibility = ScrollView.VISIBLE
                        progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                        Toast.makeText(applicationContext, "Server Not Found!", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        scrollViewActAccounts.visibility = ScrollView.VISIBLE
                        progressBarActAccounts.visibility = ProgressBar.INVISIBLE
                        Toast.makeText(applicationContext, "Server Error!", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun sendRequest(strLogin: String, strPassword: String): String {

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