package com.example.dotacatalog

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.dotacatalog.adapters.ImagePagerAdapter
import com.example.dotacatalog.data.Product
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

class DetailsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backButton = findViewById<Button>(R.id.detailsBackButton)
        backButton.setOnClickListener{
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val db = Firebase.firestore
        val id = intent.getStringExtra("id")
        db.collection("products").get()
            .addOnSuccessListener { documents ->
                for(document in documents) {
                    if (document.id == id) {
                        val product = document.toObject<Product>()
                        findViewById<TextView>(R.id.detailsHero).text = product.hero
                        findViewById<TextView>(R.id.detailsPrice).text = product.cost
                        findViewById<TextView>(R.id.detailsName).text = product.name
                        val adapter = ImagePagerAdapter(this, product.images)
                        findViewById<ViewPager>(R.id.imagesPager).adapter = adapter
                    }
                }
            }
    }

    @SuppressLint("WrongViewCast")
    private fun loadImages(imageUrls: List<String>) {
        val pager = findViewById<ViewPager2>(R.id.imagesPager)
        pager.removeAllViews()
        for(imageUrl in imageUrls) {
            val image = LayoutInflater.from(this).inflate(R.layout.image, null)
            Glide.with(this).load(imageUrl).into(image.findViewById<ImageView>(R.id.imageView))
            pager.addView(image)
        }
    }
}