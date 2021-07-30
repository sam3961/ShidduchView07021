package shiddush.view.com.mmvsd.ui.waitingscreen

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.TrainingTheAiListBinding
import shiddush.view.com.mmvsd.model.questions.Data
import shiddush.view.com.mmvsd.utill.getFontSize
import shiddush.view.com.mmvsd.utill.isNetworkAvailable
import shiddush.view.com.mmvsd.utill.showDialogNoInternet

/**
 * Created by Sumit Kumar.
 */
class TrainingTheAIAdapter(
        private var aiListData: ArrayList<Data>?,
        val context: WaitingActivity
) : RecyclerView.Adapter<TrainingTheAIAdapter.BindHolder>() {
    private lateinit var binding: TrainingTheAiListBinding

    private var delay = false

    private lateinit var onClickOptionListeners: OnClickCardListeners

    interface OnClickCardListeners {
        fun oncardOptionClick(questionId: String, answerId: String,isMain: Boolean)
        fun oncardSkipClick(questionId: String, option: String,isMain: Boolean)
    }

    fun OnCardClickListener(onClickOptionListeners: OnClickCardListeners) {
        this.onClickOptionListeners = onClickOptionListeners
    }

    fun customNotifyList(aiListData: ArrayList<Data>) {
        this.aiListData = aiListData
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = TrainingTheAiListBinding.inflate(layoutInflater, parent, false)
        return BindHolder(binding)
    }

    override fun getItemCount(): Int {
        return aiListData!!.size
    }

    public fun setDelay(delay: Boolean) {
        this.delay = delay
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        try {
            val binding = holder.binding

            try {
                val size18 = getFontSize(context, 18)
                val size16 = getFontSize(context, 16)
                val size12 = getFontSize(context, 14)

                binding.tvForthQuestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
                binding.tvSkipQuestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size16)
                binding.oneOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size12)
                binding.twoOption.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size12)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (aiListData != null) {

                binding.tvForthQuestion.text = aiListData!![position].question
                binding.oneOption.text = aiListData!![position].answers.get(0).answer
                binding.twoOption.text = aiListData!![position].answers.get(1).answer

                try {
                    binding.oneOption.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                    binding.twoOption.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                    binding.oneOption.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                    binding.twoOption.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.oneOption.setOnClickListener {
                    try {
                        if (isNetworkAvailable(context)) {
                            if (!delay) {
                                onClickOptionListeners.oncardOptionClick(aiListData!!.get(position).questionId, aiListData!!.get(position).answers.get(0).id,aiListData!!.get(position).IsMain())
                                binding.oneOption.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_filled)
                                binding.twoOption.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                                binding.oneOption.setTextColor(ContextCompat.getColor(context, R.color.white))
                                binding.twoOption.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                            }
                        } else {
                            showDialogNoInternet(context, context.getString(R.string.ooops), context.getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                binding.twoOption.setOnClickListener {
                    try {
                        if (isNetworkAvailable(context)) {
                            if (onClickOptionListeners != null && !delay) {
                                onClickOptionListeners.oncardOptionClick(aiListData!!.get(position).questionId, aiListData!!.get(position).answers.get(1).id,aiListData!!.get(position).IsMain())
                                binding.oneOption.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                                binding.twoOption.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_filled)
                                binding.oneOption.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                                binding.twoOption.setTextColor(ContextCompat.getColor(context, R.color.white))
                            }
                        } else {
                            showDialogNoInternet(context, context.getString(R.string.ooops), context.getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (aiListData!!.get(position).IsMain())
                    binding.tvSkipQuestion.visibility = View.GONE
                else
                    binding.tvSkipQuestion.visibility = View.VISIBLE

                binding.tvSkipQuestion.setOnClickListener {
                    try {
                        if (isNetworkAvailable(context)) {
                            if (onClickOptionListeners != null && !delay) {
                                onClickOptionListeners.oncardSkipClick(aiListData!!.get(position).questionId, "",aiListData!!.get(position).IsMain())
                            }
                        } else {
                            showDialogNoInternet(context, context.getString(R.string.ooops), context.getString(R.string.check_internet), R.drawable.ic_nointernet_icon)
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

    inner class BindHolder(var binding: TrainingTheAiListBinding) :
            RecyclerView.ViewHolder(binding.root) {}
}
