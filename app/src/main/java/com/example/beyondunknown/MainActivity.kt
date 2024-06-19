package com.example.beyondunknown

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beyondunknown.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookListAdapter
    private lateinit var bookList: MutableList<Book>

    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Reload CSV data and refresh RecyclerView
            loadCsv()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookList = mutableListOf()
        loadCsv()

        adapter = BookListAdapter(bookList)
        binding.rvBookList.adapter = adapter
        binding.rvBookList.layoutManager = LinearLayoutManager(this)

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            settingsLauncher.launch(intent)
        }

        binding.btnAddBook.setOnClickListener {
            val title = binding.etAddBook.text.toString()
            val path = stringToPathName(title)
            val book = Book(title, "$path.csv")
            bookList.add(0,book)
            adapter.notifyItemInserted(0)

            val csvFile = File(getExternalFilesDir(null), "_data.csv")
            csvFile.appendText("${replaceComma(title)},$path.csv\n")

            binding.etAddBook.text.clear()
        }
    }

    private fun loadCsv() {
        val csvFile = File(getExternalFilesDir(null), "_data.csv")
        bookList.clear()
        if (csvFile.exists()) {
            val lines = csvFile.bufferedReader().useLines { it.toList() }
            lines.forEach { line -> bookList.add(stringToBook(line)) }
        } else {
            csvFile.createNewFile()
        }
    }

    private fun stringToBook(str: String) : Book {
        val bookData = str.split(",")
        return if (bookData.size == 2) {
            Book(bookData[0].replace("{comma}", ","), bookData[1])
        } else {
            Book("Split error", "_error.csv")
        }
    }

    private fun stringToPathName(str: String) : String {
        val regex = Regex("""[^a-zA-z ]+""")
        return str.replace(regex, "")
            .replace(" ", "-")
    }

    private fun replaceComma(str: String) : String {
        return str.replace(",", "{comma}")
    }
}
