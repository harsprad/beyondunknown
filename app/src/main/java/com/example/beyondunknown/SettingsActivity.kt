package com.example.beyondunknown

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.beyondunknown.databinding.ActivitySettingsBinding
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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
                }
                setResult(RESULT_OK)
                finish()
            }
            .create()

        binding.btnReset.setOnClickListener { resetDataDialog.show() }

        binding.btnGoBack.setOnClickListener{ finish() }

        binding.btnExport.setOnClickListener{ createFile() }
    }

    private fun deleteCsvFiles(directory: File) {
        directory.listFiles()?.forEach {
            if (it.name.endsWith(".csv")) {
                it.delete()
            }
        }
    }

    private fun createFile() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")
        }
        startActivity(intent)
    }

    private fun zipFiles(sourceDir: File, outputFile: File) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputFile))).use { zos ->
            sourceDir.walkTopDown().forEach { file ->
                if (file.isFile) {
                    val entry = ZipEntry(file.relativeTo(sourceDir).path)
                    zos.putNextEntry(entry)
                    file.inputStream().copyTo(zos)
                    zos.closeEntry()
                }
            }
        }
    }
}