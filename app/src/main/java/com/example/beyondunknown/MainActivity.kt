package com.example.beyondunknown

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beyondunknown.databinding.ActivityMainBinding
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val csvMain = File(getExternalFilesDir(null), "_data.csv")

        var booksData : List<String> = listOf()

        if (csvMain.exists()) {
            booksData = loadCsv(csvMain)
        } else {
            csvMain.createNewFile()
        }

        val bookList = booksData.map { str -> stringToBook(str) }.toMutableList()

        val adapter = BookListAdapter(bookList)
        binding.rvBookList.adapter = adapter
        binding.rvBookList.layoutManager = LinearLayoutManager(this)

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnAddBook.setOnClickListener {
            val title = binding.etAddBook.text.toString()
            val path = stringToPathName(title)
            val book = Book(title, "$path.csv")
            bookList.add(0,book)
            adapter.notifyItemInserted(0)

            val fileOutputStream = FileOutputStream(csvMain, true)
            val outputStreamWriter = OutputStreamWriter(fileOutputStream)
            BufferedWriter(outputStreamWriter).use { writer -> writer.append("$title,$path.csv\n") }
            fileOutputStream.close()

            binding.etAddBook.text.clear()
        }
    }

    private fun loadCsv(csvFile: File) : List<String> {
        val fileInputStream = FileInputStream(csvFile)
        val inputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader = BufferedReader(inputStreamReader)
        val result : MutableList<String> = mutableListOf()
        bufferedReader.useLines { lines -> lines.forEach { result.add(it) } }
        bufferedReader.close()
        inputStreamReader.close()
        fileInputStream.close()
        return result.toList()
    }

    private fun stringToBook(str: String) : Book {
        val bookData = str.split(",")
        return Book(bookData[0].replace("{comma}",","), bookData[1])
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
