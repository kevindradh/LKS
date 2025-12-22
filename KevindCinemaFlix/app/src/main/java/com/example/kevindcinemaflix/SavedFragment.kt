package com.example.kevindcinemaflix

import android.content.Context.MODE_PRIVATE
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kevindcinemaflix.SearchActivity.RecentViewHolder
import com.example.kevindcinemaflix.databinding.CardRecentFilmBinding
import com.example.kevindcinemaflix.databinding.FragmentSavedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL


class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(inflater)

        val shared = context?.getSharedPreferences("savedMovie", MODE_PRIVATE)
        val saved = shared?.getString("listSaved", "[]")
        val data = JSONArray(saved)

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

        binding.rvSaved.adapter = recentAdapter
        binding.rvSaved.layoutManager = GridLayoutManager(context, 2)

        return binding.root
    }

    class RecentViewHolder(val binding: CardRecentFilmBinding) : RecyclerView.ViewHolder(binding.root)
}