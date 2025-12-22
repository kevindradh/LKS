package com.example.kevindcinemaflix

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kevindcinemaflix.HomeFragment.PopularViewHolder
import com.example.kevindcinemaflix.HomeFragment.RecentViewHolder
import com.example.kevindcinemaflix.databinding.ActivitySearchBinding
import com.example.kevindcinemaflix.databinding.CardPopularFilmBinding
import com.example.kevindcinemaflix.databinding.CardRecentFilmBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.tlSearch.setNavigationOnClickListener { finish() }

        binding.etSearch.doAfterTextChanged { text -> getMovies(text.toString()) }

        getMovies()
    }

    private fun getMovies(search: String = "") {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = "http://192.168.227.133:5000/api/Movie?search=${search}"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.getInputStream().bufferedReader().readText()
                val data = JSONObject(response).getJSONArray("data")

                lifecycleScope.launch(Dispatchers.Main) {
                    val recentAdapter = object : RecyclerView.Adapter<RecentViewHolder>() {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): RecentViewHolder {
                            return RecentViewHolder(
                                CardRecentFilmBinding.inflate(
                                    layoutInflater,
                                    parent,
                                    false
                                )
                            )
                        }

                        override fun onBindViewHolder(
                            holder: RecentViewHolder,
                            position: Int
                        ) {
                            val movie = data.getJSONObject(position)
                            holder.binding.tvMovieTitle.text = movie.getString("title")
                            holder.binding.tvMovieDetails.text =
                                "${movie.getString("releaseYear")} - ${movie.getString("genreName")}"

                            lifecycleScope.launch(Dispatchers.IO) {
                                val image = movie.getString("posterUrl")
                                val url = "http://192.168.227.133:5000/Poster/${image}"
                                val imageUrl = URL(url)
                                val getBitmap = BitmapFactory.decodeStream(imageUrl.openStream())
                                lifecycleScope.launch(Dispatchers.Main) {
                                    holder.binding.ivMoviePoster.setImageBitmap(getBitmap)
                                }
                            }
                        }

                        override fun getItemCount(): Int {
                            return data.length()
                        }
                    }

                    binding.rvSearch.adapter = recentAdapter
                    binding.rvSearch.layoutManager = GridLayoutManager(this@SearchActivity, 2)
                }
            }
        }
    }

    class RecentViewHolder(val binding: CardRecentFilmBinding) : RecyclerView.ViewHolder(binding.root)
}