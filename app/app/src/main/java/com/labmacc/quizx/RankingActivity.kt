package com.labmacc.quizx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.labmacc.quizx.databinding.ActivityRankingBinding
import com.labmacc.quizx.databinding.RankingUserBinding

class RankingActivity : AppCompatActivity() {
    companion object { const val TAG = "RankingA" }

    private lateinit var binding: ActivityRankingBinding
    private val vm: RankingViewModel by viewModels { RankingViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}
