package com.example.dotacatalog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.dotacatalog.data.Product
import com.example.dotacatalog.data.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class FavouritesActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favourites)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        findViewById<BottomNavigationView>(R.id.bottomNav).setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> {
                startActivity(Intent(this, CatalogActivity::class.java))
                return true
            }
            R.id.profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        findViewById<BottomNavigationView>(R.id.bottomNav).menu
            .findItem(R.id.fav)?.isChecked = true
        getCards()
    }

    private fun getCards() {
        val db = Firebase.firestore
        val scrollView = findViewById<ScrollView>(R.id.favScroll)
        db.collection("products").get()
            .addOnSuccessListener { documents ->
                scrollView.findViewById<LinearLayout>(R.id.favLinearLayout).removeAllViews()
                for (document in documents) {
                    val data = document.toObject(Product::class.java)
                    if (User.getCurrentUser().favourites.contains(document.id)) {
                        createCard(
                            document.id,
                            data.cost,
                            data.name,
                            data.hero,
                            data.images[0],
                            scrollView
                        )
                    }
                }
            }
    }
    private fun createCard(
        id: String,
        price: String,
        name: String,
        hero: String,
        imageUrl: String,
        scrollView: ScrollView
    ) {
        val cardView = LayoutInflater.from(this).inflate(R.layout.card_layout, null) as LinearLayout
        cardView.findViewById<TextView>(R.id.cardHero).text = hero
        cardView.findViewById<TextView>(R.id.cardName).text = name
        cardView.findViewById<TextView>(R.id.cardPrice).text = price
        val button = cardView.findViewById<Button>(R.id.favButton)
        button.background = ContextCompat.getDrawable(
            this,
            R.drawable.baseline_favorite_24
        )
        button.setOnClickListener {
            removeFromFavourites(id)
            scrollView.findViewById<LinearLayout>(R.id.favLinearLayout).removeView(cardView)
        }
        Glide.with(this).load(imageUrl).into(cardView.findViewById(R.id.cardImage))
        cardView.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        scrollView.findViewById<LinearLayout>(R.id.favLinearLayout).addView(cardView)
    }

    private fun removeFromFavourites(id: String) {
        val db = Firebase.firestore
        User.getCurrentUser().favourites.remove(id)
        db.collection("usrs").whereEqualTo("uid", User.getCurrentUser().uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    updateFav(document.id)
                }
            }
    }

    private fun updateFav(docId: String) {
        val db = Firebase.firestore
        val updates = hashMapOf<String, Any>(
            "favourites" to User.getCurrentUser().favourites
        )
        db.collection("usrs").document(docId).update(updates)
    }
}