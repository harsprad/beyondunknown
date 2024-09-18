package com.example.beyondunknown

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.BoxDataEntry
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.core.cartesian.series.Box
import com.example.beyondunknown.databinding.ActivitySettingsBinding
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var tempZipFile: File

    private val saveZipLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let{ uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    tempZipFile.inputStream().copyTo(outputStream)
                }
            }
            tempZipFile.delete()
        }
    }

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

        binding.btnExport.setOnClickListener{ createZip() }

    }

    private fun deleteCsvFiles(directory: File) {
        directory.listFiles()?.forEach {
            if (it.name.endsWith(".csv")) {
                it.delete()
            }
        }
    }

    private fun createZip() {
        val externalFilesDir = getExternalFilesDir(null) ?: return
        tempZipFile = File(externalFilesDir, "temp.zip")

        zipFiles(externalFilesDir, tempZipFile)

        saveZipLauncher.launch(createBlankZip(getBackupFileName()))
    }

    private fun createBlankZip(title: String): Intent {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            putExtra(Intent.EXTRA_TITLE, "$title.zip")
        }
        return intent
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

    private fun getBackupFileName(): String {
        val appName = getString(R.string.app_name_lc_hyphen)
        val todayDateStr = getDateAsString()
        return "$appName-backup-$todayDateStr"
    }
}