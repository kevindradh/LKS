package com.example.kevindcinemaflix

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kevindcinemaflix.databinding.CardPopularFilmBinding
import com.example.kevindcinemaflix.databinding.CardRecentFilmBinding
import com.example.kevindcinemaflix.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        binding.etSearch.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val url = "http://192.168.227.133:5000/api/Movie"
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.getInputStream().bufferedReader().readText()
                val data = JSONObject(response).getJSONArray("data")

                lifecycleScope.launch(Dispatchers.Main) {
                    val popularAdapter = object : RecyclerView.Adapter<PopularViewHolder>() {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): PopularViewHolder {
                            return PopularViewHolder(CardPopularFilmBinding.inflate(inflater, parent, false))
                        }

                        override fun onBindViewHolder(
                            holder: PopularViewHolder,
                            position: Int
                        ) {
                            val movie = data.getJSONObject(position)
                            holder.binding.tvMovieTitle.text = movie.getString("title")
                            holder.binding.tvMovieDetails.text = "${movie.getString("releaseYear")} - ${movie.getString("genreName")}"

                            holder.itemView.setOnClickListener {
                                val intent = Intent(context, DetailActivity::class.java).apply {
                                    putExtra("movieId", movie.getInt("movieId"))
                                }
                                startActivity(intent)
                            }

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
                    val recentAdapter = object : RecyclerView.Adapter<RecentViewHolder>() {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): RecentViewHolder {
                            return RecentViewHolder(CardRecentFilmBinding.inflate(inflater, parent, false))
                        }

                        override fun onBindViewHolder(
                            holder: RecentViewHolder,
                            position: Int
                        ) {
                            val movie = data.getJSONObject(position)
                            holder.binding.tvMovieTitle.text = movie.getString("title")
                            holder.binding.tvMovieDetails.text = "${movie.getString("releaseYear")} - ${movie.getString("genreName")}"

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

                    binding.rvPopular.adapter = popularAdapter
                    binding.rvPopular.layoutManager = LinearLayoutManager(context,
                        LinearLayoutManager.HORIZONTAL, false)

                    binding.rvRecentRelease.adapter = recentAdapter
                    binding.rvRecentRelease.layoutManager = GridLayoutManager(context, 2)
                }
            }
        }

        return binding.root
    }

    class PopularViewHolder(val binding: CardPopularFilmBinding) : RecyclerView.ViewHolder(binding.root)
    class RecentViewHolder(val binding: CardRecentFilmBinding) : RecyclerView.ViewHolder(binding.root)
}