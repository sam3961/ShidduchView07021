package shiddush.view.com.mmvsd.tutorials.reviews

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_on_boarding.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.tutorials.NextClickListener
import shiddush.view.com.mmvsd.tutorials.adapters.ReviewPagerAdapter

class ReviewActivity : AppCompatActivity(), NextClickListener {

    private lateinit var reviewPagerAdapter: ReviewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        reviewPagerAdapter = ReviewPagerAdapter(supportFragmentManager)
        vp_onBoard.apply {
            scrollDuration = 500
            adapter = reviewPagerAdapter
        }
    }


    override fun onNextClick(next: Int) {
        if (next != 6)
            vp_onBoard.currentItem = next
        else {
            println("Step for next highlight")
        }
    }
}
