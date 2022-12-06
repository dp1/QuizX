package com.labmacc.quizx

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.labmacc.quizx.databinding.ActivityRankingBinding
import com.labmacc.quizx.databinding.RankingUserBinding
import kotlin.math.sqrt

class ShakeListener(val triggerDelayMs: Long, val onTrigger: () -> Unit) : SensorEventListener {
    companion object { const val TAG = "SHAKE" }

    private var filteredAcceleration = 0f
    private val alpha = 0.1f
    private var gravity: FloatArray? = null

    private val threshold = 3
    private var lastTrigger: Long = 0

    init {
        lastTrigger = System.currentTimeMillis()
    }

    fun register(sensorManager: SensorManager) {
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unregister(sensorManager: SensorManager) {
        sensorManager.unregisterListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        )
        sensorManager.unregisterListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        )

        gravity = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                if (gravity == null)
                    return

                val x = event.values[0] - gravity!![0]
                val y = event.values[1] - gravity!![1]
                val z = event.values[2] - gravity!![2]

                val acceleration = sqrt(x * x + y * y + z * z)
                filteredAcceleration = filteredAcceleration * (1f - alpha) + acceleration * alpha

                Log.d(TAG, "accel $acceleration filtered $filteredAcceleration")

                if (filteredAcceleration >= threshold) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastTrigger >= triggerDelayMs) {
                        Log.i(TAG, "Triggered, filtered $filteredAcceleration")
                        lastTrigger = currentTime
                        onTrigger()
                    }
                }
            }
            Sensor.TYPE_GRAVITY -> {
                gravity = event.values.clone()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

class RankingActivity : AppCompatActivity() {
    companion object { const val TAG = "RankingA" }

    private lateinit var binding: ActivityRankingBinding
    private val vm: RankingViewModel by viewModels { RankingViewModel.Factory }

    private lateinit var sensorManager: SensorManager
    private val shakeListener = ShakeListener(10_000) {
        startActivity(Intent(this, CreateQuizActivity::class.java))
    }

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

                if (user.uuid == vm.currentUser()?.uuid) {
                    item.rankingName.text = "THIS IS YOU"
                }

                table.addView(item.root)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        shakeListener.register(sensorManager)
    }

    override fun onPause() {
        super.onPause()
        shakeListener.unregister(sensorManager)
    }
}
