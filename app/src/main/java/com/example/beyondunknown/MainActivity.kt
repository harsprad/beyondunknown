package com.example.beyondunknown

import android.annotation.SuppressLint
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

    @SuppressLint("NotifyDataSetChanged")
    private val settingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Reload CSV data and refresh RecyclerView
            bookList = CsvUtils.loadCsvAsBook(this,"_data.csv")
            adapter.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookList = CsvUtils.loadCsvAsBook(this,"_data.csv")
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
            val newBook = Book(title, "$path.csv")
            val newBookIndex = 0
            bookList.add(newBookIndex,newBook)
            adapter.notifyItemInserted(newBookIndex)

            val csvFile = File(getExternalFilesDir(null), "_data.csv")
            csvFile.appendText("${replaceComma(title)},$path.csv\n")

            binding.etAddBook.text.clear()
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
