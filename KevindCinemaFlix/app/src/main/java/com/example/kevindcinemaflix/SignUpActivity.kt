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
import com.example.kevindcinemaflix.databinding.ActivitySignUpBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSignUp.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val url = "http://192.168.227.133:5000/api/Authentication/SignUp"
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")

                val dataUser = JSONObject().apply {
                    put("fullName", binding.etFullName.text.toString())
                    put("username", binding.etUsername.text.toString())
                    put("email", binding.etEmail.text.toString())
                    put("password", binding.etPassword.text.toString())
                    put("confirmationPassword", binding.etConfirmPassword.text.toString())
                }

                val os = connection.getOutputStream()
                os.write(dataUser.toString().toByteArray())

                val responseCode = connection.responseCode

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    finish()
                } else {
                    runOnUiThread {
                        Toast.makeText(this@SignUpActivity, "Gagal melakukan registrasi", Toast.LENGTH_SHORT).show()
                        Log.d("Dari Sign Up", connection.errorStream.bufferedReader().readText())
                    }
                }
            }
        }
    }
}