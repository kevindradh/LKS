package com.example.edusparkblueprint

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.edusparkblueprint.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var bind: ActivityMainBinding
    private var index = 0
    private var dataQuiz = JSONArray().apply {
        put(JSONObject().apply {
            put("word", "Soekarno")
            put("image", "https://kompaspedia.kompas.id/wp-content/uploads/2020/02/Soekarno_sq-1-e1585486258865.jpg")
            put("score", 10)
        })
        put(JSONObject().apply {
            put("word", "Soeharto")
            put("image", "https://img.okezone.com/content/2023/10/17/337/2902748/kisah-pertemuan-tersembunyi-soeharto-dengan-israel-yang-berawal-dari-operasi-alpha-p1R6ZEj4Ne.jpg")
            put("score", 10)
        })
        put(JSONObject().apply {
            put("word", "Habibie")
            put("image", "https://el.iti.ac.id/wp-content/uploads/2025/08/BJ-Habibie-mantan-Presiden-Indonesia.jpg")
            put("score", 10)
        })
        put(JSONObject().apply {
            put("word", "Jokowi")
            put("image", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdU1km3yDYnnlZjJRQtiOxWjZcJmzOsxRzbQ&s")
            put("score", 10)
        })
    }
    private var userAnswer = JSONArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        changeQuiz()

        bind.btnNext.setOnClickListener {
            if (index == dataQuiz.length() - 2) {
                bind.btnNext.text = "Finish"
            }

            if (index == dataQuiz.length() - 1) {
                var nilai = 0

                for (i in 0 until userAnswer.length()) {
                    nilai += userAnswer.getJSONObject(i).getInt("score")
                }

                val alert = MaterialAlertDialogBuilder(this)
                alert.setTitle("Kuis Selesai")
                alert.setMessage("Kamu mendapatkan ${nilai} poin")
                alert.show()

                return@setOnClickListener
            }

            if (index < dataQuiz.length() - 1) {
                bind.btnPrev.isEnabled = true

                userAnswer.getJSONObject(index).apply {
                    put("answer", bind.etAnswer.text.toString())
                    put("score", if (bind.etAnswer.text.toString().equals(
                            dataQuiz.getJSONObject(
                                index
                            ).getString("word"), ignoreCase = true)
                    ) 10 else 0)
                }

                Log.d("Answer User", userAnswer.toString())

                index++
                changeQuiz()
                bind.etAnswer.setText(userAnswer.getJSONObject(index).getString("answer"))
            }
        }

        bind.btnPrev.setOnClickListener {
            bind.btnNext.text = "Next"
            index--

            changeQuiz()
            bind.etAnswer.setText(userAnswer.getJSONObject(index).getString("answer"))

            if (index == 0) {
                bind.btnPrev.isEnabled = false
                return@setOnClickListener
            }
        }
    }

    private fun changeQuiz() {
        lifecycleScope.launch(Dispatchers.IO) {
            val bmFac = BitmapFactory.decodeStream(URL(dataQuiz.getJSONObject(index).getString("image")).openStream())
            runOnUiThread {
                bind.ivImage.setImageBitmap(bmFac);
                bind.tvWord.text = shuffleWord(dataQuiz.getJSONObject(index).getString("word"))
                for (i in 0 until dataQuiz.length()) {
                    userAnswer.put(JSONObject().apply {
                        put("answer", "")
                        put("score", 0)
                    })
                }
            }
        }
    }

    private fun shuffleWord(word: String): String {
        return word.toList().shuffled().joinToString("").uppercase(Locale.getDefault())
    }
}