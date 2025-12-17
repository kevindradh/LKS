package com.example.belajarezemkofi

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belajarezemkofi.databinding.ActivityMainBinding
import com.example.belajarezemkofi.databinding.CardCoffeeBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

            }

            override fun getItemCount(): Int {
                return 10
            }

        }
        binding.rvCoffee.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    class CoffeViewHolder(val bind: CardCoffeeBinding) : RecyclerView.ViewHolder(bind.root)
}