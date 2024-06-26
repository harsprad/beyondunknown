package com.example.beyondunknown

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.beyondunknown.databinding.ActivitySecondaryBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.BufferedReader
import java.io.InputStreamReader

class SecondaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondaryBinding
    private var currentPage = 0
    private lateinit var csvFile: File
    private lateinit var csvPages: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondaryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val book: Book = intent.serializable("EXTRA_BOOK_TITLE")?: Book("Error", "_error.csv")
        binding.tvBookTitle.text = book.title

        csvFile = File(getExternalFilesDir(null), book.path)
        Log.d("FilePath", "CSV File path: ${csvFile.absolutePath}") // Log the file path

        csvPages = mutableListOf()

        if (csvFile.exists()) {
            loadCsv()
        } else {
            csvFile.createNewFile()
        }

        currentPage = csvPages.size
        updatePageNumber()

        binding.btnSave.setOnClickListener { saveToCsv() }
        binding.btnPrevious.setOnClickListener { navigatePage(-1) }
        binding.btnNext.setOnClickListener { navigatePage(1) }
    }

    private fun loadCsv() {
        csvFile.bufferedReader().useLines { lines -> lines.forEach { csvPages.add(it) } }
    }

    private fun updatePageNumber() {
        val pageFieldText = "Page: ${currentPage + 1}"
        binding.tvPageNumber.text = pageFieldText
        if (currentPage in csvPages.indices) {
            val page = csvPages[currentPage].split(",")
            binding.etNumberInput1.text.clear()
            binding.etNumberInput2.text.clear()
            binding.etNumberInput1.setHint(page[0])
            binding.etNumberInput2.setHint(page[1])
        } else {
            binding.etNumberInput1.text.clear()
            binding.etNumberInput2.text.clear()
            binding.etNumberInput1.setHint(R.string.hint_unknown_words)
            binding.etNumberInput2.setHint(R.string.hint_page_length)
        }
    }

    private fun saveToCsv() {
        if (saveToPage() == 1) {
            saveCsvFile()
            Toast.makeText(this, "Data saved to CSV", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCsvFile() {
        val fileOutputStream = FileOutputStream(csvFile)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream)
        outputStreamWriter.use { writer ->
            csvPages.forEach { writer.append("$it\n") }
        }
        fileOutputStream.close()
    }

    private fun navigatePage(direction: Int) {
        saveToPage()
        currentPage += direction
        if (currentPage < 0) {
            currentPage = 0
            Toast.makeText(this, "Already at the first page", Toast.LENGTH_SHORT).show()
        } else if (currentPage > csvPages.size) {
            currentPage = csvPages.size
            Toast.makeText(this, "Already at the latest page", Toast.LENGTH_SHORT).show()
        } else if (currentPage > 45) {
            currentPage = 45
            Toast.makeText(this, "Already at the final page", Toast.LENGTH_SHORT).show()
        }
        updatePageNumber()
    }

    private fun saveToPage(): Int {
        var number1 = binding.etNumberInput1.text.toString()
        var number2 = binding.etNumberInput2.text.toString()

        if (number1.isNotEmpty() || number2.isNotEmpty()) {

            // Only 1 is empty:
            if (number1.isEmpty()) {
                val hint1 = binding.etNumberInput1.hint.toString()
                if (hint1.isEmpty()) return 0
                number1 = hint1
            }
            if (number2.isEmpty()) {
                val hint2 = binding.etNumberInput2.hint.toString()
                if (hint2.isEmpty()) return 0
                number2 = hint2
            }

            val page = "$number1,$number2"
            if (currentPage in csvPages.indices) {
                csvPages[currentPage] = page
            } else {
                csvPages.add(page)
            }
        }

        // Both entries either non-empty or empty
        return 1
    }
}
