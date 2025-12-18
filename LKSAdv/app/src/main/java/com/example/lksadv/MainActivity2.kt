package com.example.lksadv

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lksadv.databinding.ActivityMain2Binding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.sql.Date
import java.text.SimpleDateFormat

class MainActivity2 : AppCompatActivity() {
    private lateinit var bind: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(bind.root)

        val hobi = listOf("Bersepeda", "Bermain", "Belajar")

        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, hobi)
        bind.atvHobi.setAdapter(adapter)

        bind.etTanggalLahir.setOnClickListener {
            val dtp = MaterialDatePicker.Builder.datePicker()
            dtp.setTitleText("Tanggal Lahir")
            val builderDate = dtp.build()
            builderDate.addOnPositiveButtonClickListener { lng ->
                val date = Date(lng)
                val formatter = SimpleDateFormat("dd-MM-yyyy")
                bind.etTanggalLahir.setText(formatter.format(date))
            }
            builderDate.show(supportFragmentManager, null)
        }

        bind.btnSubmit.setOnClickListener {
            val alert = MaterialAlertDialogBuilder(this)
            alert.setTitle("Konfirmasi")
            alert.setMessage("Apakah kamu yakin ingin menyimpan")
            alert.setPositiveButton("Ya") { dialog, which ->
                val intent = Intent().apply {
                    putExtra("nama", bind.etNama.text.toString())
                    putExtra("tanggalLahir", bind.etTanggalLahir.text.toString())
                }

                setResult(RESULT_OK, intent)

                finish()
            }
            alert.setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            alert.show()
        }
    }
}