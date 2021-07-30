package shiddush.view.com.mmvsd.ui.waitingscreen

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.YoutubeListLayoutBinding
import shiddush.view.com.mmvsd.model.waitingscreenmodels.YoutubeLinksData
import shiddush.view.com.mmvsd.utill.dpToPxs
import shiddush.view.com.mmvsd.utill.getFontSize

/**
 * Created by Sumit Kumar.
 */
class YoutubeListAdapter(
        private var youtubeListData: ArrayList<YoutubeLinksData>?,
        val context: WaitingActivity
) : RecyclerView.Adapter<YoutubeListAdapter.BindHolder>() {
    private lateinit var binding: YoutubeListLayoutBinding
    var previousPosition: Int = -1
    var positionToPlay: Int = -1

    private lateinit var onClickPlayListeners: OnClickPlayListeners

    interface OnClickPlayListeners {
        fun onPlayClick(videoID: String)
    }

    fun OnPlayClickListener(onClickPlayListeners: OnClickPlayListeners) {
        this.onClickPlayListeners = onClickPlayListeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = YoutubeListLayoutBinding.inflate(layoutInflater, parent, false)
        return BindHolder(binding)
    }

    override fun getItemCount(): Int {
        return youtubeListData!!.size
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        try {
            val binding = holder.binding

            try {
                val size150 = getFontSize(context, 150)
                val size110= getFontSize(context, 110)
                val size22 = getFontSize(context, 22)
                val size14 = getFontSize(context, 16)
                val size12 = getFontSize(context, 14)

                binding.tvtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
                binding.tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size12)

                val thumbnailWidth = dpToPxs(size150.toInt())
                val thumbnailHeight = dpToPxs(size110.toInt())
                binding.ivThumbnail.layoutParams.width = thumbnailWidth
                binding.ivThumbnail.layoutParams.height = thumbnailHeight

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (positionToPlay == position) {
                    binding.ivPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_button))
                } else {
                    binding.ivPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_button))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val imageUrl = "https://img.youtube.com/vi/" + youtubeListData!!.get(position).getVideoId()!! + "/hqdefault.jpg"
            if (youtubeListData != null) {
                Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.video_thumbnail)
                        .error(R.drawable.video_thumbnail)
                        .into(binding.ivThumbnail)

                binding.tvtitle.text = youtubeListData!![position].getTitle()
                binding.tvSubtitle.text = youtubeListData!![position].getSubtitle()

                binding.llYoutubeMain.setOnClickListener {
                    try {
                        if (onClickPlayListeners != null) {
                            onClickPlayListeners.onPlayClick(youtubeListData!!.get(position).getVideoId()!!)
                            changePreviousIcon(position)
                            try {
                                binding.ivPlay.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause_button))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun changePreviousIcon(currentPosition: Int) {
        try {
            positionToPlay = -1
            if (previousPosition != currentPosition) {
                notifyItemChanged(previousPosition)
                previousPosition = currentPosition
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class BindHolder(var binding: YoutubeListLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {}
}
