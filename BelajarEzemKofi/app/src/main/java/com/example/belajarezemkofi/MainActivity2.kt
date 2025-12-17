package com.example.belajarezemkofi

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.belajarezemkofi.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnS.setOnClickListener {
            rotateImage(0.85f)
        }

        binding.btnM.setOnClickListener {
            rotateImage(1f)
        }

        binding.btnL.setOnClickListener {
            rotateImage(1.15f)
        }
    }

    private fun rotateImage(size: Float) {
        val animator = AnimatorSet()

        val scaleY = ObjectAnimator.ofFloat(binding.imageView, "scaleY", size)
        val scaleX = ObjectAnimator.ofFloat(binding.imageView, "scaleX", size)
        val rotation = ObjectAnimator.ofFloat(binding.imageView, "rotation", 0f, 360f)

        animator.playTogether(scaleX, scaleY, rotation)
        animator.duration = 600
        animator.start()


    }
}