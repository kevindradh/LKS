package com.example.belajargawean

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.belajargawean.databinding.ActivityMain4Binding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout

class MainActivity4 : AppCompatActivity() {
    private lateinit var binding: ActivityMain4Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val getId = intent.getIntExtra("id", 0)
        Toast.makeText(this, getId.toString(), Toast.LENGTH_SHORT).show()

        changeView(BlankFragment4())

        binding.tlDetail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> changeView(BlankFragment4())
                    1 -> changeView(BlankFragment5())
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

        })

        binding.btnShare.setOnClickListener {
            val bsd = BlankFragment6()
            bsd.show(supportFragmentManager, "")
        }
    }

    private fun changeView(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.fcvDetail.id, fragment)
        transaction.commit()
    }
}