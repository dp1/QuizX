package com.labmacc.quizx

import android.app.Application

class QuizXApplication : Application() {
    companion object {
        lateinit var instance: QuizXApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}