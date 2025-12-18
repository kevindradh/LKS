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
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
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

        val shared = getSharedPreferences("token", MODE_PRIVATE)

        lifecycleScope.launch(Dispatchers.IO) {
            val urlCategory = "http://192.168.100.88:5000/api/coffee-category"
            val urlMe = "http://192.168.100.88:5000/api/me"

            val connCategory = URL(urlCategory).openConnection() as HttpURLConnection
            val connMe = URL(urlMe).openConnection() as HttpURLConnection

            connCategory.requestMethod = "GET"
            connMe.requestMethod = "GET"
            connMe.setRequestProperty("Authorization", "Bearer ${shared.getString("token", "")}")

            val resCodeCategory = connCategory.responseCode
            val resCodeMe = connMe.responseCode

            if (resCodeCategory == HttpURLConnection.HTTP_OK && resCodeMe == HttpURLConnection.HTTP_OK) {
                runOnUiThread {
                    val inputStreamCategory = connCategory.getInputStream().bufferedReader().readText()
                    val categories = JSONArray(inputStreamCategory)
                    val me = JSONObject(connMe.getInputStream().bufferedReader().readText())

                    for (i in 0 until categories.length()) {
                        val category = binding.tlCategory.newTab()
                        category.text = categories.getJSONObject(i).getString("name")
                        category.tag = categories.getJSONObject(i).getInt("id")
                        binding.tlCategory.addTab(category)
                    }

                    binding.tvUsername.text = me.getString("fullName")

                    binding.tlCategory.addOnTabSelectedListener(object :
                        TabLayout.OnTabSelectedListener {
                        override fun onTabSelected(tab: TabLayout.Tab?) {
                            if (tab?.tag != null) {
                                val categoryId = tab.tag as Int
                                getCoffee(categoryId)
                            }
                        }

                        override fun onTabUnselected(p0: TabLayout.Tab?) {}

                        override fun onTabReselected(p0: TabLayout.Tab?) {}

                    })
                }
            }
        }

        getCoffee()
    }

    private fun getCoffee(categoryId: Int = 1) {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = "http://192.168.100.88:5000/api/coffee?coffeeCategoryID=${categoryId}"
            val conn = URL(url).openConnection() as HttpURLConnection

            conn.requestMethod = "GET"

            val resCode = conn.responseCode

            if (resCode == HttpURLConnection.HTTP_OK) {
                val inputStream = conn.getInputStream().bufferedReader().readText()

                val coffees = JSONArray(inputStream)

                runOnUiThread {
                    binding.rvCoffee.adapter = object : RecyclerView.Adapter<CoffeViewHolder>() {
                        override fun onCreateViewHolder(
                            parent: ViewGroup,
                            viewType: Int
                        ): CoffeViewHolder {
                            return CoffeViewHolder(
                                CardCoffeeBinding.inflate(
                                    layoutInflater,
                                    parent,
                                    false
                                )
                            )
                        }

                        override fun onBindViewHolder(
                            holder: CoffeViewHolder,
                            position: Int
                        ) {
                            val numFormat = NumberFormat.getCurrencyInstance(Locale.US)

                            coffees.getJSONObject(position).let { coffee ->
                                holder.bind.tvCoffeName.text = coffee.getString("name")
                                holder.bind.tvCoffeeRating.text = coffee.getString("rating")
                                holder.bind.tvPrice.text =
                                    numFormat.format(coffee.getDouble("price"))

                                // Show images from API
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val urlHttp =
                                        "http://192.168.100.88:5000/images/${coffee.getString("imagePath")}"
                                    val imageUrl = URL(urlHttp).openStream()
                                    val bitmap = BitmapFactory.decodeStream(imageUrl)

                                    runOnUiThread {
                                        holder.bind.ivCoffeImage.setImageBitmap(bitmap)
                                    }
                                }

                                holder.itemView.setOnClickListener {
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            MainActivity2::class.java
                                        )
                                    )
                                }
                            }
                        }

                        override fun getItemCount(): Int {
                            return coffees.length()
                        }

                    }
                    binding.rvCoffee.layoutManager = LinearLayoutManager(
                        this@MainActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                }
            }
        }
    }

    class CoffeViewHolder(val bind: CardCoffeeBinding) : RecyclerView.ViewHolder(bind.root)
}