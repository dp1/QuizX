package com.labmacc.quizx

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.labmacc.quizx.ui.Ranking
import kotlin.math.sqrt
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.labmacc.quizx.ui.Login
import com.labmacc.quizx.ui.ShowQuiz
import com.labmacc.quizx.ui.SplashScreen

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

        lastTrigger = System.currentTimeMillis()
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

//                Log.d(TAG, "accel $acceleration filtered $filteredAcceleration")

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

class RankingActivity : ComponentActivity() {
    companion object { const val TAG = "RankingA" }

    private val vm: RankingViewModel by viewModels { RankingViewModel.Factory }

    private lateinit var sensorManager: SensorManager
    private val shakeListener = ShakeListener(2_000) {
        if (vm.currentUser().value != null) {
            startActivity(Intent(this, CreateQuizActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Splash.route
            ) {
                composable(route = NavRoutes.Splash.route) {
                    SplashScreen(onFinish = {
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(0)
                        }
                    })
                }
                composable(route = NavRoutes.Home.route) {
                    Ranking(vm, doLogin = {
                        navController.navigate(NavRoutes.Login.route)
                    },
                    showChallenge = { quizId ->
                        vm.showQuizViewModel.loadQuiz(quizId)
                        navController.navigate(NavRoutes.ShowQuiz.route)
                    })
                }
                composable(route = NavRoutes.Login.route) {
                    Login(vm.loginViewModel, onComplete = {
                        vm.onLoggedIn()
                        navController.navigate(NavRoutes.Home.route) {
                            popUpTo(NavRoutes.Home.route)
                        }
                    })
                }
                composable(route = NavRoutes.ShowQuiz.route) {
                    ShowQuiz(vm.showQuizViewModel.quiz.value)
                }
            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
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
