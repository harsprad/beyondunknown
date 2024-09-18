package com.example.beyondunknown

import android.content.Context
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.BoxDataEntry
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.core.Chart

object GraphUtils {
    fun createChart(context: Context) : Chart {
        val boxChart = AnyChart.vertical()

        boxChart.title("Book Comprehensibility")

        boxChart.xAxis(0).labels(false)

        boxChart.labels(true)
        boxChart.labels().format("{%x}")
        boxChart.labels().textOverflow("...")
        boxChart.labels().anchor("right-bottom")
        boxChart.labels().position("q1")

        val data: MutableList<DataEntry> = mutableListOf(
            BoxDataEntry("Registered Nurse", 20000, 26000, 27000, 32000, 38000),
            BoxDataEntry("Dental Hygienist", 24000, 28000, 32000, 38000, 42000),
            BoxDataEntry("Computer Systems Analyst", 40000, 49000, 62000, 73000, 88000),
            BoxDataEntry("Physical Therapist", 52000, 59000, 65000, 74000, 83000),
            BoxDataEntry("Software Developer", 45000, 54000, 66000, 81000, 97000),
            BoxDataEntry("Information Security Analyst", 47000, 56000, 69000, 85000, 100000),
            BoxDataEntry("Physician Assistant", 67000, 72000, 84000, 95000, 110000),
            BoxDataEntry("Dentist", 75000, 99000, 123000, 160000, 210000),
            BoxDataEntry("Physician", 58000, 96000, 130000, 170000, 200000),
            BoxDataEntry("Registered Purse", 21000, 26000, 27000, 32000, 38000),
        )

        val box = boxChart.box(data)

        box.whiskerWidth("10%")

        return boxChart
    }
}