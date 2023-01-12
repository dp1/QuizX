package com.labmacc.quizx

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.labmacc.quizx.ui.Ranking
import kotlin.math.sqrt
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.labmacc.quizx.ui.Login
import com.labmacc.quizx.ui.ShowQuiz
import com.labmacc.quizx.ui.SplashScreen
import com.labmacc.quizx.vm.RankingViewModel

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

    private var currentDestination = ""
    private lateinit var sensorManager: SensorManager
    private val shakeListener = ShakeListener(2_000) {
        if (currentDestination == NavRoutes.Ranking.route && vm.currentUser().value != null) {
            startActivity(Intent(this, CreateQuizActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNotifications()

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = NavRoutes.Splash.route
            ) {
                composable(route = NavRoutes.Splash.route) {
                    SplashScreen(onComplete = {
                        navController.navigate(NavRoutes.Ranking.route) {
                            popUpTo(0)
                        }
                    })
                }
                composable(route = NavRoutes.Ranking.route) {
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
                        navController.navigate(NavRoutes.Ranking.route) {
                            popUpTo(NavRoutes.Ranking.route) {
                                inclusive = true
                            }
                        }
                    })
                }
                composable(route = NavRoutes.ShowQuiz.route) {
                    vm.currentUser().value?.let { user ->
                        ShowQuiz(
                            user,
                            vm.showQuizViewModel,
                            vm.showQuizViewModel.quiz.value,
                            vm.showQuizViewModel.author.value,
                            onComplete = {
                                vm.showQuizViewModel.resetAnswer()
                                navController.navigate(NavRoutes.Ranking.route) {
                                    popUpTo(NavRoutes.Ranking.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }

            navController.addOnDestinationChangedListener { _, destination, _ ->
                destination.route?.let {
                    currentDestination = it
                    if (it == NavRoutes.Login.route) {
                        vm.loginViewModel.prepare()
                    }
                }
            }
        }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        shakeListener.register(sensorManager)
    }

    override fun onPause() {
        super.onPause()
        shakeListener.unregister(sensorManager)
    }

    private fun setupNotifications() {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val interval = 60 * 1000L // milliseconds
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            interval,
            pendingIntent
        )
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.i(TAG, "Permission granted")
        } else {
            Log.i(TAG, "Permission denied :(")
        }
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission already granted")
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
            Log.i(TAG, "Showing permission rationale")
        } else {
            Log.i(TAG, "Launching permission request")
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
