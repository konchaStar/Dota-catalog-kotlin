package com.example.dotacatalog

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dotacatalog.data.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore

class ProfileActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.buttonSignOut).setOnClickListener {
            val auth = Firebase.auth
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        findViewById<Button>(R.id.profileUpdate).setOnClickListener {
            updateFields()
        }
        findViewById<BottomNavigationView>(R.id.bottomNav).setOnNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        val auth = Firebase.auth
        if(auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        fillFields()
        findViewById<BottomNavigationView>(R.id.bottomNav).menu
            .findItem(R.id.profile)?.isChecked = true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.home -> {
                startActivity(Intent(this, CatalogActivity::class.java))
                return true
            }
            R.id.fav -> {
                startActivity(Intent(this, FavouritesActivity::class.java))
            }
        }
        return false
    }

    private fun fillFields() {
        val user = User.getCurrentUser()
        findViewById<EditText>(R.id.profileAge).setText(user.age)
        findViewById<EditText>(R.id.profileMmr).setText(user.mmr)
        findViewById<EditText>(R.id.profileHatedHero).setText(user.hated_hero)
        findViewById<EditText>(R.id.profileFavArcana).setText(user.fav_arcana)
        findViewById<EditText>(R.id.profileFavHero).setText(user.sign_hero)
        findViewById<EditText>(R.id.profileFavPersona).setText(user.fav_persona)
        findViewById<EditText>(R.id.profileFavTeam).setText(user.fav_team)
        findViewById<EditText>(R.id.profileName).setText(user.name)
        findViewById<EditText>(R.id.profileSurname).setText(user.surname)
        findViewById<EditText>(R.id.profileNickname).setText(user.nickname)
    }
    private fun updateFields() {
        val user = User.getCurrentUser()
        user.name = findViewById<EditText>(R.id.profileName).text.toString()
        user.surname = findViewById<EditText>(R.id.profileSurname).text.toString()
        user.nickname = findViewById<EditText>(R.id.profileNickname).text.toString()
        user.age = findViewById<EditText>(R.id.profileAge).text.toString()
        user.fav_team = findViewById<EditText>(R.id.profileFavTeam).text.toString()
        user.fav_persona = findViewById<EditText>(R.id.profileFavPersona).text.toString()
        user.sign_hero = findViewById<EditText>(R.id.profileFavHero).text.toString()
        user.fav_arcana = findViewById<EditText>(R.id.profileFavArcana).text.toString()
        user.mmr = findViewById<EditText>(R.id.profileMmr).text.toString()
        user.hated_hero = findViewById<EditText>(R.id.profileHatedHero).text.toString()
        User.setCurrentUser(user)
        val db = Firebase.firestore
        db.collection("usrs").whereEqualTo("uid", user.uid).get()
            .addOnSuccessListener {documents ->
                for(document in documents) {
                    updateUser(document.id)
                }
            }
    }

    private fun updateUser(docId: String) {
        val db = Firebase.firestore
        db.collection("usrs").document(docId).set(User.getCurrentUser(), SetOptions.merge())
    }
}