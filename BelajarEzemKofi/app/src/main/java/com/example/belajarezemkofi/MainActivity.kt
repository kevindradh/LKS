package com.example.belajarezemkofi

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.IO) {
            val url = "http://192.168.100.88:5000/api/coffee"
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
                            return CoffeViewHolder(CardCoffeeBinding.inflate(layoutInflater, parent, false))
                        }

                        override fun onBindViewHolder(
                            holder: CoffeViewHolder,
                            position: Int
                        ) {
                            coffees.getJSONObject(position).let { coffee ->
                                holder.bind.tvCoffeName.text = coffee.getString("name")
                                holder.bind.tvCoffeeRating.text = coffee.getString("rating")
                                holder.bind.tvPrice.text = "$${coffee.getString("price")}"

                                lifecycleScope.launch(Dispatchers.IO) {
                                    val urlHttp = "http://192.168.100.88:5000/images/${coffee.getString("imagePath")}"
                                    val imageUrl = URL(urlHttp).openStream()
                                    val bitmap = BitmapFactory.decodeStream(imageUrl)

                                    runOnUiThread {
                                        holder.bind.ivCoffeImage.setImageBitmap(bitmap)
                                    }
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