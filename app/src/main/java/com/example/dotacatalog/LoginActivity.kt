package com.example.dotacatalog

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dotacatalog.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.concurrent.CountDownLatch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            setUser()
            startActivity(Intent(this, CatalogActivity::class.java))
        }
        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        buttonSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonLogin.setOnClickListener {
            val mail = findViewById<EditText>(R.id.editTextLogin).text.toString()
            val password = findViewById<EditText>(R.id.editTextPassword).text.toString()
            auth.signInWithEmailAndPassword(mail, password)
                .addOnSuccessListener {
                    setUser()
                    startActivity(Intent(this, CatalogActivity::class.java))
                }
                .addOnFailureListener {
                    val error = findViewById<TextView>(R.id.loginErrorText)
                    error.text = "Invalid credentials"
                }
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<TextView>(R.id.loginErrorText).text = ""
        val auth = Firebase.auth
        if (auth.currentUser != null) {
            setUser()
            startActivity(Intent(this, CatalogActivity::class.java))
        }
    }

    private fun setUser() {
        val db = Firebase.firestore
        val auth = Firebase.auth
        val latch = CountDownLatch(1)
        db.collection("usrs").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    if (user.uid.equals(auth.currentUser?.uid)) {
                        User.setCurrentUser(user)
                    }
                }
                latch.countDown()
            }
            .addOnFailureListener {
                latch.countDown()
            }
    }
}