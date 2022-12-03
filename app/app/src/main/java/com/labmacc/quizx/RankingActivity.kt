package com.labmacc.quizx

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.labmacc.quizx.databinding.ActivityRankingBinding
import com.labmacc.quizx.databinding.RankingUserBinding
import kotlin.math.sqrt

class ShakeListener : SensorEventListener {
    private var filteredAcceleration = 0f
    private val alpha = 0.1f

    override fun onSensorChanged(event: SensorEvent) {
        val (x, y, z) = event.values
        val acceleration = sqrt(x * x + y * y + z * z)
        filteredAcceleration = filteredAcceleration * (1f - alpha) + acceleration * alpha

        Log.d("SHAKE", "accel $acceleration filteres $filteredAcceleration")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

class RankingActivity : AppCompatActivity() {
    companion object { const val TAG = "RankingA" }

    private lateinit var binding: ActivityRankingBinding
    private val vm: RankingViewModel by viewModels { RankingViewModel.Factory }

    private val shakeListener = ShakeListener()
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val table = binding.table

        vm.ranking.observe(this, Observer { users ->
            Log.i(TAG, "Received new ranking ${users.toString()}")

            table.removeAllViews()
            for ((i,user) in users.withIndex()) {
                val item = RankingUserBinding.inflate(layoutInflater)
                item.rankingPos.text = getString(R.string.ranking_pos, i+1)
                item.rankingName.text = user.displayName
                item.rankingScore.text = getString(R.string.ranking_score, user.score)
                table.addView(item.root)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            shakeListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(
            shakeListener,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        )
    }
}
