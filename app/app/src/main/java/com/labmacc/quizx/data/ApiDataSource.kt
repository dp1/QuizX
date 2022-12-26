package com.labmacc.quizx.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.labmacc.quizx.QuizXApplication
import com.labmacc.quizx.data.model.SubmissionResult
import org.json.JSONObject

class ApiDataSource {

    val submissionResult = mutableStateOf<SubmissionResult?>(null)
    val queue = Volley.newRequestQueue(QuizXApplication.instance )


    fun sendAnswer(user_id : String, quiz_id : String, answer : String) {
        val param = JSONObject()
        param.put("sender_id", user_id)
        param.put("quiz_id" , quiz_id)
        param.put("answer", answer)
        val url = "https://quizx.dariopetrillo.it/submit"
        val stringRequest = JsonObjectRequest(
            Request.Method.POST, url, param, { r ->
                Log.i("NET", "All good! $r")
                val elem = r as JSONObject
                submissionResult.value = SubmissionResult(score_obtained = elem.getInt("score_obtained"),result = elem.getBoolean("result") )
                Log.i("created?", "${submissionResult.value}")
            }, {
                Log.e("NET", "Failed")
            }
        )
        queue.add(stringRequest)
    }



}