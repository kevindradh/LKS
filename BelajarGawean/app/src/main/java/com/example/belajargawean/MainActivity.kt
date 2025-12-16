package com.example.belajargawean

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.belajargawean.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            lifecycleScope.launch(Dispatchers.IO) {
                val url = "http://10.0.2.2:5000/api/auth"
                val request = URL(url).openConnection() as HttpURLConnection
                request.requestMethod = "POST"
                request.setRequestProperty("Content-Type", "application/json")

                val dataUser = JSONObject().apply {
                    put("email", email)
                    put("password", password)
                }

                val os = request.getOutputStream()
                os.write(dataUser.toString().toByteArray())

                val responseCode = request.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val intent = Intent(this@MainActivity, MainActivity3::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}