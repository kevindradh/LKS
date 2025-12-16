package com.example.belajargawean

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.belajargawean.databinding.CardGaweanBinding
import com.example.belajargawean.databinding.FragmentBlankBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.log

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

        var location = ""

        binding.etSearchJob.doAfterTextChanged { text -> loadJobs(inflater, text.toString(), location) }

        binding.cgJobs.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    binding.chpAll.id -> {
                        location = ""
                        loadJobs(inflater, binding.etSearchJob.text.toString(), location)
                    }
                    binding.chpOnsite.id -> {
                        location = "Onsite"
                        loadJobs(inflater, binding.etSearchJob.text.toString(), location)
                    }
                    binding.chpRemote.id -> {
                        location = "Remote"
                        loadJobs(inflater, binding.etSearchJob.text.toString(), location)
                    }
                }
            }
        }

        loadJobs(inflater)

        return binding.root
    }

    private fun loadJobs(inflater: LayoutInflater, search: String = "", location: String = "") {
        lifecycleScope.launch(Dispatchers.IO) {
            val url = "http://10.0.2.2:5000/api/jobs?search=${search}&location=${location}"
            Log.d("CekURL", url)
            val request = URL(url).openConnection() as HttpURLConnection
            request.requestMethod = "GET"

            val inputStream = request.getInputStream().bufferedReader().readText()
            val responseCode = request.responseCode

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val convertJson = JSONObject(inputStream)
                val getData = convertJson.getJSONArray("data")
                lifecycleScope.launch(Dispatchers.Main) {
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
                            val job = getData.getJSONObject(position)
                            holder.binding.tvJobTitle.text = job.getString("name")
                            holder.binding.tvCompanyName.text = job.getJSONObject("company")
                                .getString("name")

                            for (i in 0 until Session.jobs.length()) {
                                val jobSecond = Session.jobs.getJSONObject(i)
                                if (jobSecond.getInt("id") == job.getInt("id")) {
                                    holder.binding.btnSaveJob.isChecked = true
                                }
                            }

                            holder.binding.btnSaveJob.setOnClickListener {
                                val shared = context?.getSharedPreferences("joblist", Activity.MODE_PRIVATE)
                                val editor = shared?.edit()

                                val dataJob = JSONArray(shared?.getString("joblist", "[]"))
                                dataJob.put(job)

                                editor?.putString("joblist", dataJob.toString())
                                editor?.commit()

//                                for (i in 0 until Session.jobs.length()) {
//                                    val jobSecond = Session.jobs.getJSONObject(i)
//                                    if (jobSecond.getInt("id") == job.getInt("id")) {
//                                        Toast.makeText(context, "Unable to add job", Toast.LENGTH_SHORT).show()
//                                        return@setOnClickListener
//                                    }
//                                }

//                                Session.jobs.put(job)
                                shared?.getString("joblist", "[]")?.let { msg -> Log.d("Job Tersimpan", msg) }
                            }

                            holder.itemView.setOnClickListener {
                                val intent =
                                    Intent(this@BlankFragment.context, MainActivity4::class.java).apply {
                                        putExtra("id", job.getInt("id"))
                                    }
                                startActivity(intent)
                            }
                        }

                        override fun getItemCount(): Int {
                            return getData.length()
                        }
                    }

                    binding.rvExplore.adapter = adapter
                    binding.rvExplore.layoutManager =
                        LinearLayoutManager(this@BlankFragment.context)
                }
            }
        }
    }

    class GaweanViewHolder(val binding: CardGaweanBinding) : RecyclerView.ViewHolder(binding.root)
}