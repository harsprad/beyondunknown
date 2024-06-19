package com.example.beyondunknown

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.beyondunknown.databinding.ActivitySettingsBinding
import java.io.File

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val resetDataDialog = AlertDialog.Builder(this)
            .setTitle("Reset data")
            .setMessage("Are you sure you want to delete all your book data?")
            .setIcon(R.drawable.ic_delete_data)
            .setPositiveButton("No") { _, _ ->
                Toast.makeText(this, "Book database NOT reset", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Yes") { _, _ ->
                Toast.makeText(this, "Book database reset", Toast.LENGTH_SHORT).show()
                val externalFilesDir = getExternalFilesDir(null)
                externalFilesDir?.let {
                    deleteCsvFiles(it)
                    createEmptyDataCsv(it)
                }
                finish()
            }
            .create()

        binding.btnReset.setOnClickListener {
            resetDataDialog.show()
        }

        binding.btnGoBack.setOnClickListener{
            finish()
        }
    }

    private fun deleteCsvFiles(directory: File) {
        directory.listFiles()?.forEach {
            if (it.name.endsWith(".csv")) {
                it.delete()
            }
        }
    }

    private fun createEmptyDataCsv(directory: File) {
        val csvFile = File(directory, "_data.csv")
        csvFile.createNewFile()
    }
}