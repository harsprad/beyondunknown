package com.example.beyondunknown

import android.content.Context
import java.io.File

object CsvUtils {

    fun loadCsv(context: Context, location: String) : MutableList<String> {
        val csvFile = File(context.getExternalFilesDir(null), location)
        val target : MutableList<String> = mutableListOf()
        if (csvFile.exists()) {
            csvFile.bufferedReader().useLines { lines ->
                lines.forEach { target.add(it) }
            }
        } else {
            csvFile.createNewFile()
        }
        return target
    }

    private fun stringToBook(str: String) : Book {
        val (title, path) = str.split(",", limit = 2)
        return Book(title.replace("{comma}", ","), path)
    }

    fun loadCsvAsBook(context: Context, location: String) : MutableList<Book> {
        return loadCsv(context, location).map{ stringToBook(it) }.toMutableList()
    }

}