package com.example.dotacatalog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dotacatalog.data.User
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.concurrent.CountDownLatch

class RegisterActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val auth = Firebase.auth
        val buttonSignIn = findViewById<Button>(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        val error = findViewById<TextView>(R.id.regErrorText)
        val buttonReg = findViewById<Button>(R.id.buttonReg)
        buttonReg.setOnClickListener {
            val login = findViewById<EditText>(R.id.editTextRegLogin).text.toString()
            val password = findViewById<EditText>(R.id.editTextRegPassword).text.toString()
            val repPassword = findViewById<EditText>(R.id.editTextRepPassword).text.toString()
            if (password != repPassword) {
                error.text = "Different passwords"
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(login, password).addOnCompleteListener{task ->
                if (task.isSuccessful) {
                    createUser(auth)
                    setUser()
                    startActivity(Intent(this, CatalogActivity::class.java))
                } else {
                    val exception = task.exception
                    error.text = exception!!.message
                }
            }
        }
    }

    private fun createUser(auth: FirebaseAuth) {
        val db = Firebase.firestore
        val uid = auth.currentUser?.uid
        val users = db.collection("usrs")
        val fields = hashMapOf(
            "uid" to uid,
            "name" to "",
            "surname" to "",
            "fav_arcana" to "",
            "age" to "0",
            "sign_hero" to "",
            "mmr" to "0",
            "hated_hero" to "",
            "fav_persona" to "",
            "fav_team" to "",
            "nickname" to "",
            "favourites" to ArrayList<String>()
        )
        users.add(fields)
    }

    override fun onResume() {
        super.onResume()
        val error = findViewById<TextView>(R.id.regErrorText)
        error.text = ""
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