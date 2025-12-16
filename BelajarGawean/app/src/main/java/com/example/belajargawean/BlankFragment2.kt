package com.example.belajargawean

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belajargawean.databinding.CardGawean2Binding
import com.example.belajargawean.databinding.CardGaweanBinding
import com.example.belajargawean.databinding.FragmentBlank2Binding
import com.google.android.material.tabs.TabLayout
import org.json.JSONArray

class BlankFragment2 : Fragment() {
    private lateinit var binding: FragmentBlank2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlank2Binding.inflate(inflater)

        binding.tlJob.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(p0: TabLayout.Tab?) {
                setRv()
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })


        setRv()
        return binding.root
    }

    private fun setRv() {
        val shared = context?.getSharedPreferences("joblist", Activity.MODE_PRIVATE)
        val editor = shared?.edit()
        val savedJob = JSONArray(shared?.getString("joblist", "[]"))

        binding.rvJob.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            inner class MainVH(val binding: CardGaweanBinding) : RecyclerView.ViewHolder(binding.root)
            inner class SecondVH(val binding: CardGawean2Binding) : RecyclerView.ViewHolder(binding.root)

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                return when(binding.tlJob.selectedTabPosition) {
                    0 -> {
                        val binding = CardGaweanBinding.inflate(layoutInflater, parent, false)
                        MainVH(binding)
                    }
                    1 -> {
                        val binding = CardGawean2Binding.inflate(layoutInflater, parent, false)
                        SecondVH(binding)
                    }
                    else -> throw IllegalArgumentException("Invalid view type")
                }
            }

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                when (holder) {
                    is MainVH -> {
//                        val savedJob = Session.jobs.getJSONObject(position)
                        val sJob = savedJob.getJSONObject(position)

                        holder.binding.btnSaveJob.setOnClickListener {
                            savedJob.remove(position)

                            editor?.putString("joblist", savedJob.toString())
                            editor?.commit()

                            notifyDataSetChanged()
                        }

                        holder.binding.tvJobTitle.text = sJob.getString("name")
                    }
                }
            }

            override fun getItemCount(): Int {
                return when (binding.tlJob.selectedTabPosition) {
                    0 -> savedJob.length()
                    else -> throw IllegalArgumentException("Invalid tab position")
                }
            }

        }

        binding.rvJob.layoutManager = LinearLayoutManager(this@BlankFragment2.context)
    }
}