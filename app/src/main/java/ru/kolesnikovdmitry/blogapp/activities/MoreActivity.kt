package ru.kolesnikovdmitry.blogapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_more.*
import ru.kolesnikovdmitry.blogapp.R

class MoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)

        setUpFields()
    }

    private fun setUpFields() {
        val title = intent.getStringExtra("title")
        val author = intent.getStringExtra("author")
        val text = intent.getStringExtra("text")
        val date = intent.getStringExtra("date")

        textViewTitleActMore.text = title
        textViewAuthorActMore.text = author
        textViewTextActMore.text = text
        textViewDateActMore.text = date
    }
}
