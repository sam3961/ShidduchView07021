package shiddush.view.com.mmvsd.ui.chat

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import shiddush.view.com.mmvsd.databinding.AdapterAnswersCommonBinding
import shiddush.view.com.mmvsd.model.matchedQuestions.QuestionsItem

/**
 * Created by Sumit Kumar.
 */
open class MatchedQuestionsAdapter(
    private var matchedQuestionList: MutableList<QuestionsItem>?,
    val context: Context
) : RecyclerView.Adapter<MatchedQuestionsAdapter.BindHolder>() {
    private lateinit var binding: AdapterAnswersCommonBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = AdapterAnswersCommonBinding.inflate(layoutInflater, parent, false)
        return BindHolder(binding)
    }

    override fun getItemCount(): Int {
        return matchedQuestionList?.size!!
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        val binding = holder.binding
        binding.textViewAnswer.text = matchedQuestionList!![position].answer
        binding.textViewQuestion.text = matchedQuestionList!![position].question

    }

    open fun customNotify(arrayListUsers: MutableList<QuestionsItem>) {
        this.matchedQuestionList = arrayListUsers
        notifyDataSetChanged()
    }

    inner class BindHolder(var binding: AdapterAnswersCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}
