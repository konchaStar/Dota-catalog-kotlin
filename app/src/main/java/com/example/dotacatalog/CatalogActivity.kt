package com.example.dotacatalog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import java.util.concurrent.CountDownLatch

class CatalogActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catalog)
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
        val search = findViewById<EditText>(R.id.searchText)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                getCards(search.text.toString())
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.fav -> {
                startActivity(Intent(this, FavouritesActivity::class.java))
                return true
            }
            R.id.profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        val auth = Firebase.auth
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        findViewById<BottomNavigationView>(R.id.bottomNav).menu
            .findItem(R.id.home)?.isChecked = true
        val search = findViewById<EditText>(R.id.searchText)
        getCards(search.text.toString())
    }

    private fun getCards(search: String) {
        val db = Firebase.firestore
        val scrollView = findViewById<ScrollView>(R.id.arcanasContainer)
        db.collection("products").get()
            .addOnSuccessListener { documents ->
                scrollView.findViewById<LinearLayout>(R.id.linearLayout).removeAllViews()
                for (document in documents) {
                    val data = document.toObject(Product::class.java)
                    if (data.hero.toLowerCase().contains(search.toLowerCase())
                        || data.name.toLowerCase().contains(search.toLowerCase())
                    ) {
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
        if (User.getCurrentUser().favourites.contains(id)) {
            button.background = ContextCompat.getDrawable(
                this,
                R.drawable.baseline_favorite_24
            )
        } else {
            button.background = ContextCompat.getDrawable(
                this,
                R.drawable.baseline_favorite_border_24
            )
        }
        button.setOnClickListener {
            if (User.getCurrentUser().favourites.contains(id)) {
                removeFromFavourites(id)
                button.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.baseline_favorite_border_24
                )
            } else {
                addToFavourites(id)
                button.background = ContextCompat.getDrawable(
                    this,
                    R.drawable.baseline_favorite_24
                )
            }
        }
        Glide.with(this).load(imageUrl).into(cardView.findViewById(R.id.cardImage))
        cardView.setOnClickListener {
            val intent = Intent(this, DetailsActivity::class.java)
            intent.putExtra("id", id)
            startActivity(intent)
        }
        scrollView.findViewById<LinearLayout>(R.id.linearLayout).addView(cardView)
    }

    private fun addToFavourites(id: String) {
        val db = Firebase.firestore
        User.getCurrentUser().favourites.add(id)
        db.collection("usrs").whereEqualTo("uid", User.getCurrentUser().uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    updateFav(document.id)
                }
            }
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