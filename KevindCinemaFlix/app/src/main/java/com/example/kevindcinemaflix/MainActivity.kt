package com.example.kevindcinemaflix

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kevindcinemaflix.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val url = "http://192.168.227.133:5000/api/Authentication/SignIn"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")

                val dataLogin = JSONObject().apply {
                    put("email", binding.etEmail.text.toString())
                    put("password", binding.etPassword.text.toString())
                }

                val os = connection.getOutputStream()
                os.write(dataLogin.toString().toByteArray())

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val intent = Intent(this@MainActivity, ContainerActivity::class.java)
                    startActivity(intent)
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Gagal melakukan autentikasi", Toast.LENGTH_SHORT).show()
                        Log.d("Dari Sign In", connection.errorStream.bufferedReader().readText())
                    }
                }
            }
        }
    }
}