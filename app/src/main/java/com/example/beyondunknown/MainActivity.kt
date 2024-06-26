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
    private var bookList: MutableList<Book> = mutableListOf()

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
            csvFile.bufferedReader().useLines { lines ->
                lines.forEach { line -> bookList.add(stringToBook(line)) }
            }
        } else {
            csvFile.createNewFile()
        }
    }

    private fun stringToBook(str: String) : Book {
        val (title, path) = str.split(",", limit = 2)
        return Book(title.replace("{comma}", ","), path)
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
