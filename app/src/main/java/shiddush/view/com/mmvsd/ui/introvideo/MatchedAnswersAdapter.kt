package shiddush.view.com.mmvsd.ui.introvideo

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import shiddush.view.com.mmvsd.databinding.MatchedAnswersListBinding
import shiddush.view.com.mmvsd.model.videocall.MatchedAnswersData
import shiddush.view.com.mmvsd.utill.getFontSize

/**
 * Created by Sumit Kumar.
 */
class MatchedAnswersAdapter(
        private var data: ArrayList<MatchedAnswersData>?,
        val context: UserInfoBeforeCallActivity
) : RecyclerView.Adapter<MatchedAnswersAdapter.BindHolder>() {
    private lateinit var binding: MatchedAnswersListBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = MatchedAnswersListBinding.inflate(layoutInflater, parent, false)
        return BindHolder(binding)
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        try {
            val binding = holder.binding
            try {
                val size15 = getFontSize(context, 16)
                binding.tvtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)
                binding.tvSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size15)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            binding.tvtitle.text = data!![position].question
            binding.tvSubtitle.text = data!![position].answer
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class BindHolder(var binding: MatchedAnswersListBinding) :
            RecyclerView.ViewHolder(binding.root) {}
}
