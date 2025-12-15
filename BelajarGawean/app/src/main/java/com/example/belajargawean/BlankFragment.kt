package com.example.belajargawean

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belajargawean.databinding.CardGaweanBinding
import com.example.belajargawean.databinding.FragmentBlankBinding

class BlankFragment : Fragment() {
    private lateinit var binding: FragmentBlankBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlankBinding.inflate(inflater)

        val adapter = object : RecyclerView.Adapter<GaweanViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): GaweanViewHolder {
                val view = CardGaweanBinding.inflate(inflater, parent, false)
                return GaweanViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: GaweanViewHolder,
                position: Int
            ) {

            }

            override fun getItemCount(): Int {
                return 6
            }
        }

        binding.rvExplore.adapter = adapter
        binding.rvExplore.layoutManager = LinearLayoutManager(parentFragment?.context)

        return binding.root
    }

    class GaweanViewHolder(val binding: CardGaweanBinding) : RecyclerView.ViewHolder(binding.root)
}