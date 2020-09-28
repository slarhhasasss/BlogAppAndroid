package ru.kolesnikovdmitry.blogapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.kolesnikovdmitry.blogapp.MyApp
import ru.kolesnikovdmitry.blogapp.R
import ru.kolesnikovdmitry.blogapp.classes.HttpHelper
import java.net.HttpURLConnection
import java.net.URL

class AddPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        btnSubmitActAdd.setOnClickListener {
            onClickSubmit()
        }
    }


    private fun onClickSubmit() {
        val title = editTextTitleActAdd.text.toString()
        val author = MyApp.CUR_USER
        val text = editTextTextActAdd.text.toString()
        val likes = "0"

        btnSubmitActAdd.isEnabled = false

        if(title == "" || text == "") {
            Toast.makeText(applicationContext, "Empty fields", Toast.LENGTH_LONG).show()
            btnSubmitActAdd.isEnabled = true
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val respCode = tryToSendPost(title, author, text, likes)
            when (respCode) {
                HttpHelper.RESP_OK -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Send!", Toast.LENGTH_LONG).show()
                        btnSubmitActAdd.isEnabled = true
                        finish()
                    }
                }
                HttpHelper.RESP_SERVER_ERROR -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error on server!!!", Toast.LENGTH_LONG).show()
                        btnSubmitActAdd.isEnabled = true
                    }
                }
                HttpHelper.RESP_INTERNAL_ERROR -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error in sending on phone!", Toast.LENGTH_LONG).show()
                        btnSubmitActAdd.isEnabled = true
                    }
                }
            }
        }
    }

    private fun tryToSendPost(title: String, author: String?, text: String, likes: String): Any {
        val hm = HashMap<String, Array<String>>()
        hm.put("title", arrayOf(title))
        hm.put("author", arrayOf(author.toString()))
        hm.put("text", arrayOf(text))
        hm.put("likes", arrayOf(likes))

        val queryStr = HttpHelper.getStringForQuery(hm)

        val urlStr = HttpHelper.getBASE_URL() + "/get_posts/add_new_post"

        try{
            val url = URL(urlStr)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true
            connection.connect()

            val os = connection.outputStream
            os.write(queryStr.toByteArray())
            os.flush()
            os.close()

            val iStream = connection.inputStream
            val respStr = String(iStream.readBytes())
            iStream.close()

            connection.disconnect()
            return respStr
        } catch (th : Throwable) {
            Log.e("myApp", th.message.toString())
            return HttpHelper.RESP_INTERNAL_ERROR
        }
    }
}
