package com.example.belajarezemkofi

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belajarezemkofi.databinding.ActivityMainBinding
import com.example.belajarezemkofi.databinding.CardCoffeeBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            val url = "http://192.168.100.88:5000/api/coffee"
            val urlCategory = "http://192.168.100.88:5000/api/coffee-category"
            val conn = URL(url).openConnection() as HttpURLConnection
            val connCategory = URL(urlCategory).openConnection() as HttpURLConnection

            conn.requestMethod = "GET"
            connCategory.requestMethod = "GET"

            val resCode = conn.responseCode
            val resCodeCategory = conn.responseCode

            if (resCode == HttpURLConnection.HTTP_OK && resCodeCategory == HttpURLConnection.HTTP_OK) {
                val inputStream = conn.getInputStream().bufferedReader().readText()
                val inputStreamCategory = connCategory.getInputStream().bufferedReader().readText()

                val coffees = JSONArray(inputStream)
                val categories = JSONArray(inputStreamCategory)

                runOnUiThread {
                    for (i in 0 until categories.length()) {
                        val chip = Chip(this@MainActivity)
                        chip.text = categories.getJSONObject(i).getString("name")
                        binding.cgCategory.addView(chip)
                    }

                    binding.rvCoffee.adapter = object : RecyclerView.Adapter<CoffeViewHolder>() {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): CoffeViewHolder {
                            return CoffeViewHolder(CardCoffeeBinding.inflate(layoutInflater, parent, false))
                        }

                        override fun onBindViewHolder(
                            holder: CoffeViewHolder,
                            position: Int
                        ) {
                            val numFormat = NumberFormat.getCurrencyInstance(Locale.US)

                            coffees.getJSONObject(position).let { coffee ->
                                holder.bind.tvCoffeName.text = coffee.getString("name")
                                holder.bind.tvCoffeeRating.text = coffee.getString("rating")
                                holder.bind.tvPrice.text = numFormat.format(coffee.getDouble("price"))

                                // Show images from API
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val urlHttp = "http://192.168.100.88:5000/images/${coffee.getString("imagePath")}"
                                    val imageUrl = URL(urlHttp).openStream()
                                    val bitmap = BitmapFactory.decodeStream(imageUrl)

                                    runOnUiThread {
                                        holder.bind.ivCoffeImage.setImageBitmap(bitmap)
                                    }
                                }

                                holder.itemView.setOnClickListener {
                                    startActivity(Intent(this@MainActivity, MainActivity2::class.java))
                                }
                            }
                        }

                        override fun getItemCount(): Int {
                            return coffees.length()
                        }

                    }
                    binding.rvCoffee.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                }
            }
        }
    }

    class CoffeViewHolder(val bind: CardCoffeeBinding) : RecyclerView.ViewHolder(bind.root)
}