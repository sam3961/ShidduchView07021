package shiddush.view.com.mmvsd.ui.chat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_full_screen.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.ActivityFullScreenBinding
import shiddush.view.com.mmvsd.socket.SocketCommunication
import shiddush.view.com.mmvsd.utill.FULL_SCREEN
import shiddush.view.com.mmvsd.utill.MESSAGE_IMAGE_URL
import shiddush.view.com.mmvsd.utill.WAITING_SCREEN

public class FullScreenActivity : AppCompatActivity(){

    lateinit var binding: ActivityFullScreenBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_screen)


        Glide.with(binding.imageView.context)
            .load(intent.getStringExtra(MESSAGE_IMAGE_URL))
            .placeholder(R.drawable.image_placeholder)
            .into(binding.imageView)

        backView.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        //to set app instance
        try {
            if (SocketCommunication.isSocketConnected()) {
                SocketCommunication.emitInScreenActivity(FULL_SCREEN)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}