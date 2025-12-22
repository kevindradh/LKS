package com.example.kevindcinemaflix

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.kevindcinemaflix.databinding.ActivityDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val movieId = intent.getIntExtra("movieId", 0)
        val shared = getSharedPreferences("savedMovie", MODE_PRIVATE)
        val saved = shared.getString("listSaved", "[]")
        val savedJson = JSONArray(saved)
        val editor = shared.edit()

        lifecycleScope.launch(Dispatchers.IO) {
            val url = "http://192.168.227.133:5000/api/Movie/${movieId}"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val resCode = connection.responseCode

            if (resCode == HttpURLConnection.HTTP_OK) {
                val inputS = connection.getInputStream().bufferedReader().readText()
                val data = JSONObject(inputS).getJSONObject("data")

                val image = data.getString("posterUrl")
                val url = "http://192.168.227.133:5000/Poster/${image}"
                val imageUrl = URL(url)
                val getBitmap = BitmapFactory.decodeStream(imageUrl.openStream())

                runOnUiThread {
                    binding.tlDetail.setNavigationOnClickListener { finish() }

                    binding.tvMovieTitle.text = data.getString("title")
                    binding.tvReleaseDate.text = data.getString("releaseYear")
                    binding.tvGenreName.text = data.getString("genreName")
                    binding.tvDuration.text = "Duration: ${data.getString("duration")}"
                    binding.tvRating.text = "Rating: ${data.getString("averageRating")}"
                    binding.tvDirector.text = data.getString("director")
                    binding.tvCast.text = data.getString("cast")
                    binding.tvSynopsis.text = data.getString("synopsis")
                    binding.ivMoviePoster.setImageBitmap(getBitmap)

                    binding.btnSaveMovie.setOnClickListener {
                        savedJson.put(data)
                        editor.putString("listSaved", savedJson.toString())
                        editor.apply()

                        binding.btnSaveMovie.text = "Saved"
                        Toast.makeText(this@DetailActivity, "Berhasil menambahkan ke list", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}