package com.example.belajargawean

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.belajargawean.databinding.FragmentBlank3Binding

class BlankFragment3 : Fragment() {
    private lateinit var binding: FragmentBlank3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlank3Binding.inflate(inflater)

        return binding.root
    }
}