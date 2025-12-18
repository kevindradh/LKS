package com.example.belajarezemkofi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.belajarezemkofi.databinding.ActivityMain3Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity3 : AppCompatActivity() {
    private lateinit var bind: ActivityMain3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(bind.root)

        val shared = getSharedPreferences("token", MODE_PRIVATE)
        val editor = shared.edit()


        if (shared.getString("token", "") != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }


        bind.btnLogin.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                val url = "http://192.168.100.88:5000/api/auth"
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")

                val dataUser = JSONObject().apply {
                    put("username", bind.etUsername.text.toString())
                    put("password", bind.etPassword.text.toString())
                }

                val os = conn.outputStream
                os.write(dataUser.toString().toByteArray())
                os.flush()
                os.close()

                val resCode = conn.responseCode

                if (resCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.getInputStream().bufferedReader().readText()
                    Log.d("Hasil Login", inputStream)



                    editor.putString("token", inputStream)
                    editor.apply()

                    startActivity(Intent(this@MainActivity3, MainActivity::class.java))
                }
            }
        }
    }
}