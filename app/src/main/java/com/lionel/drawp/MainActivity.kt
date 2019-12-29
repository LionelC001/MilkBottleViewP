package com.lionel.drawp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        milkVolumeView.setData(2500)

        val dataChart = mutableListOf<Int?>().apply {
            for (i in 0..23) {
                when {
                    i % 5 == 0 -> add(i, i)
                    i == 3 -> add(i, 18)
                    i == 18 -> add(i, 3)
                    i == 1 -> add(i, 25)
                    i == 14 -> add(i, 14)
                    i == 23 -> add(i, 31)
                    else -> add(i, null)
                }
            }
        }

        chartView.setData(dataChart)
    }
}
