package com.example.beyondunknown

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.beyondunknown.databinding.ActivitySecondaryBinding
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class SecondaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondaryBinding
    private var currentPage = 0
    private lateinit var csvFile: File
    private var pageList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondaryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val book: Book = intent.serializable("EXTRA_BOOK_TITLE")?: Book("Error", "_error.csv")
        binding.tvBookTitle.text = book.title

        csvFile = File(getExternalFilesDir(null), book.path)
        Log.d("FilePath", "CSV File path: ${csvFile.absolutePath}") // Log the file path

        pageList = CsvUtils.loadCsv(this, book.path)

        currentPage = pageList.size
        updatePageNumber()


        binding.btnSave.setOnClickListener { saveToCsv(book.path, finished = false) }
        binding.btnSaveAndExit.setOnClickListener { saveToCsv(book.path, finished = true) }
        binding.btnPrevious.setOnClickListener { navigatePage(-1) }
        binding.btnNext.setOnClickListener { navigatePage(1) }


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                showExitConfirmationDialogue()
            }
        })
    }

    private fun showExitConfirmationDialogue() {
        AlertDialog.Builder(this)
            .setTitle("Exiting on possible unsaved data")
            .setMessage("Are you sure you want to leave?")
            .setIcon(R.drawable.ic_delete_data)
            .setPositiveButton("No") { dialogue, _ ->
                dialogue.dismiss()
            }
            .setNegativeButton("Yes") { _, _ ->
                finish()
            }
            .show()
    }

    private fun updatePageNumber() {
        val pageFieldText = "Page: ${currentPage + 1}"
        binding.tvPageNumber.text = pageFieldText
        binding.etNumberInput1.text.clear()
        binding.etNumberInput2.text.clear()

        if (currentPage in pageList.indices) {
            val page = pageList[currentPage].split(",")
            binding.etNumberInput1.setHint(page[0])
            binding.etNumberInput2.setHint(page[1])
        } else {
            binding.etNumberInput1.setHint(R.string.hint_unknown_words)
            binding.etNumberInput2.setHint(R.string.hint_page_length)
        }
    }

    private fun saveToCsv(location: String, finished: Boolean) {
        if (saveToPage() == 1) {
            CsvUtils.saveCsv(this, location, pageList)
            Toast.makeText(this, "Data saved to CSV", Toast.LENGTH_SHORT).show()
            if (finished) finish()
        } else {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigatePage(direction: Int) {
        if (saveToPage() == 0) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            return
        }
        currentPage += direction
        if (currentPage < 0) {
            currentPage = 0
            Toast.makeText(this, "Already at the first page", Toast.LENGTH_SHORT).show()
        } else if (currentPage > pageList.size) {
            currentPage = pageList.size
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

            // Only one field is empty:
            if (number1.isEmpty()) {
                val hint1 = binding.etNumberInput1.hint.toString()
                if (hint1 == getString(R.string.hint_unknown_words)) return 0
                number1 = hint1
            }
            if (number2.isEmpty()) {
                val hint2 = binding.etNumberInput2.hint.toString()
                if (hint2 == getString(R.string.hint_page_length)) return 0
                number2 = hint2
            }

            val page = "$number1,$number2"
            if (currentPage in pageList.indices) {
                pageList[currentPage] = page
            } else {
                pageList.add(page)
            }
        }

        // Both entries either empty or non-empty
        return 1
    }
}
