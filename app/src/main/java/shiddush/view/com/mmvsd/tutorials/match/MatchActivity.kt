package shiddush.view.com.mmvsd.tutorials.match

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.tutorials.NextClickListener
import shiddush.view.com.mmvsd.tutorials.adapters.MatchPagerAdapter

class MatchActivity : AppCompatActivity(), NextClickListener {

    private lateinit var matchPagerAdapter: MatchPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        matchPagerAdapter = MatchPagerAdapter(supportFragmentManager)
        vp_onBoard.adapter = matchPagerAdapter
    }


    override fun onNextClick(next: Int) {
        if (next != 4)
            vp_onBoard.currentItem = next
        else {
            println("Step for next highlight")
        }
    }
}
