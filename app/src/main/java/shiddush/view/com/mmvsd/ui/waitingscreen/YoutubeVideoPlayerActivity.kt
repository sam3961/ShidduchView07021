package shiddush.view.com.mmvsd.ui.waitingscreen

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityYoutubeVideoPlayerBinding
import shiddush.view.com.mmvsd.radioplayer.RadioPlayer
import shiddush.view.com.mmvsd.utill.*

class YoutubeVideoPlayerActivity : AppCompatActivity() {

    lateinit var binding: ActivityYoutubeVideoPlayerBinding
    private lateinit var youTubeVideoPlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
    private var currentVideoDutation: Float = 0F
    private var currentVideoState: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_youtube_video_player)

        try {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //To change navigation bar color
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val window = window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.navigationBarColor = ContextCompat.getColor(this, R.color.black)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val extras = intent.extras
            var videoId: String = ""
            var startSeconds: Float = 0F
            if (extras != null) {
                videoId = extras.getString(VIDEO_ID, "")
                startSeconds = extras.getFloat(VIDEO_DURATION, 0F)
                currentVideoState = extras.getBoolean(VIDEO_STATE, false)
                setYoutube(videoId, startSeconds)
            } else {
                this@YoutubeVideoPlayerActivity.finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setYoutube(videoId: String, startSeconds: Float) {
        binding.progressBar.visibility = View.VISIBLE
        binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer) {
                youTubeVideoPlayer = youTubePlayer
                youTubeVideoPlayer.loadVideo(videoId, startSeconds)
                binding.youtubePlayerView.enterFullScreen()
                binding.progressBar.visibility = View.GONE
                RadioPlayer.pauseRadio()
            }

            override fun onError(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                showToast(this@YoutubeVideoPlayerActivity, getString(R.string.youtube_error), Toast.LENGTH_SHORT)
            }

            override fun onStateChange(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, state: PlayerConstants.PlayerState) {
                super.onStateChange(youTubePlayer, state)
                if (state.name.equals(PLAYING)) {
                    currentVideoState = true
                } else if (state.name.equals(PAUSED)) {
                    currentVideoState = false
                }
                RadioPlayer.pauseRadio()
            }

            override fun onCurrentSecond(youTubePlayer: com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer, second: Float) {
                super.onCurrentSecond(youTubePlayer, second)
                currentVideoDutation = second
            }

        })

        binding.youtubePlayerView.addFullScreenListener(object : YouTubePlayerFullScreenListener {
            override fun onYouTubePlayerEnterFullScreen() {
                //binding.youtubePlayerView.enterFullScreen()
            }

            override fun onYouTubePlayerExitFullScreen() {
                goBackWithResult()
            }

        })

    }

    override fun onResume() {
        super.onResume()
        try {
            RadioPlayer.pauseRadio()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            binding.youtubePlayerView.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        goBackWithResult()
    }

    fun goBackWithResult() {
        try {
            val result = Intent()
            result.putExtra(VIDEO_DURATION, currentVideoDutation)
            result.putExtra(VIDEO_STATE, currentVideoState)
            setResult(Activity.RESULT_OK, result)
            this@YoutubeVideoPlayerActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
            setResult(Activity.RESULT_CANCELED)
            this@YoutubeVideoPlayerActivity.finish()
        }
    }

}
