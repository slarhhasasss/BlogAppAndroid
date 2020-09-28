package ru.kolesnikovdmitry.blogapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.activity_posts.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import ru.kolesnikovdmitry.blogapp.MyApp
import ru.kolesnikovdmitry.blogapp.R
import ru.kolesnikovdmitry.blogapp.classes.HttpHelper
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class PostsActivity : AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        supportActionBar?.title = MyApp.CUR_USER

        loadPosts()
    }



    private fun loadPosts() {
        scrollViewActPosts.visibility = View.INVISIBLE
        progressBarActPosts.visibility = View.VISIBLE

        linearLayActPosts.removeAllViews()

        val urlStr = HttpHelper.getBASE_URL() + "/get_posts"

        CoroutineScope(Dispatchers.IO).launch {
            val posts : JSONArray = getPostsFromServer(urlStr)
            when(posts.getJSONObject(posts.length() - 1).get("status").toString()) {
                HttpHelper.RESP_INTERNAL_ERROR -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error internal!", Toast.LENGTH_LONG).show()
                    }
                }
                HttpHelper.RESP_OK -> {
                    withContext(Dispatchers.Main) {
                        displayPages(posts)
                    }
                }
                else -> {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "Error :" +
                                posts.getJSONObject(0).get("status").toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    private fun displayPages(posts: JSONArray) {
        for (i in 0 until (posts.length() - 1)) {
            val view = createCard(posts.getJSONObject(i))
            linearLayActPosts.addView(view)
        }
        progressBarActPosts.visibility = View.INVISIBLE
        scrollViewActPosts.visibility = View.VISIBLE
    }

    private fun createCard(post: JSONObject?): View? {
        val cardView = CardView(applicationContext)
        layoutInflater.inflate(R.layout.post_layout, cardView)
        cardView.findViewById<TextView>(R.id.textViewPostAuthor).text = "Author: ${post?.getString("author")}"
        cardView.findViewById<TextView>(R.id.textViewPostDate).text =
                SimpleDateFormat("dd MMMM yyyy", Locale("ru")).format(Date(post?.getString("date")!!.toLong()))
        cardView.findViewById<TextView>(R.id.textViewPostTitle).text = post.getString("title")
        cardView.findViewById<TextView>(R.id.textViewPostText).text =
                if (post.getString("text").length > 50) {
                    cardView.findViewById<TextView>(R.id.textViewPostMore).visibility = View.VISIBLE
                    cardView.findViewById<TextView>(R.id.textViewPostMore).setOnClickListener {
                        val intent = Intent(applicationContext, MoreActivity::class.java)
                        intent.putExtra("title", post.getString("title"))
                        intent.putExtra("author", "Author: ${post.getString("author")}")
                        intent.putExtra("date", SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
                                .format(Date(post.getString("date").toLong())))
                        intent.putExtra("text", post.getString("text"))
                        startActivity(intent)
                    }
                    "${post.getString("text").substring(0, 50)}..."
                }
                else {
                    cardView.findViewById<TextView>(R.id.textViewPostMore).visibility = View.INVISIBLE
                    post.getString("text")
                }

        return cardView
    }

    private fun getPostsFromServer(strUrl : String): JSONArray {
        try {
            val url = URL(strUrl)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "POST"
            connection.doInput = true
            connection.doOutput = true

            connection.connect()

            val os = connection.outputStream
            val timeFrom : Long = getTimeFrom()
            val hm = HashMap<String, Array<String>>()
            hm["timeFrom"] = arrayOf(timeFrom.toString())
            val queryStr = HttpHelper.getStringForQuery(hm)
            val bytes = queryStr.toByteArray()
            os.write(bytes)
            os.flush()
            os.close()

            val iStream = connection.inputStream
            val bytesFromServer = iStream.readBytes()
            val respStr = String(bytesFromServer)
            iStream.close()

            connection.disconnect()

            val jsonArrAns = JSONArray(respStr)
            val jsonObjStatus = JSONObject()
            jsonObjStatus.put("status", HttpHelper.RESP_OK)
            jsonArrAns.put(jsonArrAns.length(), jsonObjStatus)

            Log.e("myApp", jsonArrAns.toString())

            return jsonArrAns
        } catch (th : Throwable) {
            val jsonObj = JSONObject()
            jsonObj.put("status", th.message.toString())
            return JSONArray().put(jsonObj)
        }
    }

    //Узнаем с какого времени загружать посты
    private fun getTimeFrom(): Long {
        return 0L
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post_act, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItemAddPostAct -> {
                val intent = Intent(applicationContext, AddPostActivity::class.java)
                startActivity(intent)
            }
            R.id.menuItemUploadPostsAct -> {
                loadPosts()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}