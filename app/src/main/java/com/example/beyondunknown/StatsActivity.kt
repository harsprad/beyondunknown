package com.example.beyondunknown

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.beyondunknown.databinding.ActivityStatsBinding

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoBack.setOnClickListener { finish() }

        binding.boxPlotView.setChart(GraphUtils.createChart(this))
        binding.boxPlotView.requestLayout() // Requests layout pass
        binding.boxPlotView.invalidate()
    }
}