package shiddush.view.com.mmvsd.ui.waitingscreen

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.AdapterBestMatchBinding
import shiddush.view.com.mmvsd.databinding.AdapterRejoinDateBinding
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.utill.AppInstance

/**
 * Created by Sumit Kumar.
 */
open class BestMatchAdapter(
    private var bestMatchListData: MutableList<OnlineOfflineResponse>,
    val context: WaitingActivity
) : RecyclerView.Adapter<BestMatchAdapter.BindHolder>() {
    private lateinit var binding: AdapterBestMatchBinding

    private lateinit var onBestMatchClickListeners: OnBestMatchClickListeners

    interface OnBestMatchClickListeners {
        fun onBestMatchClick(userID: String, userName: String)
        fun onRemoveUser(userID: String, rejoinType: String, userName: String)
    }

    fun onBestMatchClickListener(onBestMatchClickListeners: OnBestMatchClickListeners) {
        this.onBestMatchClickListeners = onBestMatchClickListeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = AdapterBestMatchBinding.inflate(layoutInflater, parent, false)
        return BindHolder(binding)
    }

    override fun getItemCount(): Int {
        return bestMatchListData.size
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        val binding = holder.binding

        var matchName = bestMatchListData.get(position).firstName
        if (matchName?.contains(" ")!!)
            matchName = matchName.split(" ")[0]

        binding.textViewName.text = matchName

        binding.imageViewRemove.setOnClickListener {
            onBestMatchClickListeners.onRemoveUser(
                bestMatchListData[position].userid!!,
                "best_match", bestMatchListData[position].firstName!!
            )
        }


        if (bestMatchListData.get(position).unreadCount != 0)
            binding.textViewChatCount.visibility = View.VISIBLE
        else
            binding.textViewChatCount.visibility = View.GONE

        binding.textViewChatCount.text = bestMatchListData.get(position).unreadCount.toString()

        if (AppInstance.userObj?.getGender()!! == 1) //Female
            binding.ivThumbnail.setImageResource(R.drawable.ic_male)
        else
            binding.ivThumbnail.setImageResource(R.drawable.ic_female)

        holder.itemView.setOnClickListener {
            onBestMatchClickListeners.onBestMatchClick(
                bestMatchListData.get(position).userid!!,
                bestMatchListData.get(position).firstName!!
            )
        }

    }

    open fun customNotify(arrayListUsers: MutableList<OnlineOfflineResponse>) {
        this.bestMatchListData = arrayListUsers
        notifyDataSetChanged()
    }


    inner class BindHolder(var binding: AdapterBestMatchBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}
