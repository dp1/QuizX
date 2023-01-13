package com.labmacc.quizx.data

import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.labmacc.quizx.QuizXApplication
import com.labmacc.quizx.data.model.SubmissionResult
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ApiDataSource {
    companion object { const val TAG = "ApiDS" }

    private val queue = Volley.newRequestQueue(QuizXApplication.instance)

    fun sendAnswer(user_id: String, quiz_id: String, answer: String, coveredArea: Float, onSuccess: (SubmissionResult) -> Unit) {
        val param = JSONObject()
        param.put("sender_id", user_id)
        param.put("quiz_id" , quiz_id)
        param.put("answer", answer)
        param.put("covered_area", coveredArea)
        val url = "https://quizx.dariopetrillo.it/submit"
        val stringRequest = JsonObjectRequest(
            Request.Method.POST, url, param, { r ->
                Log.i(TAG, "Api request was successful: $r")
                val elem = r as JSONObject
                val result = SubmissionResult(
                    score_obtained = elem.getInt("score_obtained"),
                    result = elem.getBoolean("result")
                )
                Log.i("created?", "result")
                onSuccess(result)
            }, {
                Log.e(TAG, "Api request failed: $it")
            }
        )
        queue.add(stringRequest)
    }

    suspend fun hasNewQuizzes(uuid: String) = suspendCoroutine<Boolean> { cont ->
        val param = JSONObject()
        param.put("uuid", uuid)
        val url = "https://quizx.dariopetrillo.it/check"
        val request = JsonObjectRequest(
            Request.Method.POST, url, param, { r->
                val elem = r as JSONObject
                cont.resume(elem.getBoolean("has_new"))
            }, {
                Log.e(TAG, "Api request failed: $it")
                cont.resume(false)
            }
        )
        queue.add(request)
    }
}