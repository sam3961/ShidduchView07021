package shiddush.view.com.mmvsd.ui.waitingscreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_sheet_drop_call.*
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.AdapterRejoinDateBinding
import shiddush.view.com.mmvsd.model.onlineOffline.OnlineOfflineResponse
import shiddush.view.com.mmvsd.utill.AppInstance
import shiddush.view.com.mmvsd.utill.PreferenceConnector
import shiddush.view.com.mmvsd.utill.getPrefString

/**
 * Created by Sumit Kumar.
 */
open class RejoinListAdapter(
    private var rejoinListData: MutableList<OnlineOfflineResponse>,
    val context: WaitingActivity
) : RecyclerView.Adapter<RejoinListAdapter.BindHolder>() {
    private lateinit var binding: AdapterRejoinDateBinding

    private lateinit var onRejoinClickListeners: OnRejoinClickListeners

    interface OnRejoinClickListeners {
        fun onRejoinClick(userID: String, userName: String, rejoinType: String)
        fun onRemoveUser(userID: String, rejoinType: String, userName: String)
    }

    fun OnRejoinClickListener(onRejoinClickListeners: OnRejoinClickListeners) {
        this.onRejoinClickListeners = onRejoinClickListeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = AdapterRejoinDateBinding.inflate(layoutInflater, parent, false)
        return BindHolder(binding)
    }

    override fun getItemCount(): Int {
        return rejoinListData.size!!
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        val binding = holder.binding

        var matchName = rejoinListData.get(position).firstName
        if (matchName?.contains(" ")!!)
            matchName = matchName.split(" ")[0]

        binding.textViewName.text = matchName
        if (rejoinListData.get(position).type!!.equals("rejoin")) {
            binding.textViewType.text = "Rejoin"
            binding.llType.background =
                ContextCompat.getDrawable(context, R.drawable.curved_gradient_dark_purple)
        } else {
            binding.textViewType.text = "Date Again"
            binding.llType.background =
                ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_filled)
        }

        if (AppInstance.userObj?.getGender()!! == 1) //Female
            binding.ivThumbnail.setImageResource(R.drawable.ic_male)
        else
            binding.ivThumbnail.setImageResource(R.drawable.ic_female)


        binding.llName.setOnClickListener {
            onRejoinClickListeners.onRejoinClick(
                rejoinListData.get(position).userid!!,
                rejoinListData.get(position).firstName!!, rejoinListData.get(position).type!!
            )
        }
        binding.cvAvatar.setOnClickListener {
            onRejoinClickListeners.onRejoinClick(
                rejoinListData.get(position).userid!!,
                rejoinListData.get(position).firstName!!, rejoinListData.get(position).type!!
            )
        }

        binding.imageViewRemove.setOnClickListener {
            onRejoinClickListeners.onRemoveUser(
                rejoinListData.get(position).userid!!,
                rejoinListData.get(position).type!!, rejoinListData.get(position).firstName!!
            )
        }
    }

    open fun customNotify(arrayListUsers: MutableList<OnlineOfflineResponse>) {
        this.rejoinListData = arrayListUsers
        notifyDataSetChanged()
    }

    inner class BindHolder(var binding: AdapterRejoinDateBinding) :
        RecyclerView.ViewHolder(binding.root) {}
}
