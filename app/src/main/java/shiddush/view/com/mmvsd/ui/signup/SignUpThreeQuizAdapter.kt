package shiddush.view.com.mmvsd.ui.signup

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import shiddush.view.com.mmvsd.R
import shiddush.view.com.mmvsd.databinding.BibleQuizLayoutBinding
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpBibleQuizData
import shiddush.view.com.mmvsd.model.signupbiblequiz.SignUpBibleQuizPostRequest
import shiddush.view.com.mmvsd.utill.getBackImage60Size
import shiddush.view.com.mmvsd.utill.getFontSize
import shiddush.view.com.mmvsd.utill.getPercentHeightOfDevice

/**
 * Created by Sumit Kumar.
 */
class SignUpThreeQuizAdapter(
        private var quizListData: ArrayList<SignUpBibleQuizData>?,
        val context: FragmentActivity
) : RecyclerView.Adapter<SignUpThreeQuizAdapter.BindHolder>() {
    private lateinit var binding: BibleQuizLayoutBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = BibleQuizLayoutBinding.inflate(layoutInflater, parent, false)
        return BindHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizListData!!.size
    }

    override fun onBindViewHolder(holder: BindHolder, position: Int) {
        try {
            val binding = holder.binding

            if (quizListData != null) {
                binding.tvQuestion.text = quizListData!![position].getQuestions()
                binding.tvOptionOne.text = quizListData!![position].getOption1()
                binding.tvOptionQwo.text = quizListData!![position].getOption2()
                binding.tvOptionThree.text = quizListData!![position].getOption3()

                val size18 = getFontSize(context, 18)
                val size14 = getFontSize(context, 14)

                binding.tvQuestion.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size18)
                binding.tvOptionOne.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
                binding.tvOptionQwo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)
                binding.tvOptionThree.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size14)

                try{
                    val btnHeight = getPercentHeightOfDevice(context, 0.05F)
                    binding.tvOptionOne.layoutParams.height = btnHeight
                    binding.tvOptionQwo.layoutParams.height = btnHeight
                    binding.tvOptionThree.layoutParams.height = btnHeight
                }catch (e:Exception){
                    e.printStackTrace()
                }

                try {
                    val smallHeight = getPercentHeightOfDevice(context, 0.17F)
                    val largeHeight = getPercentHeightOfDevice(context, 0.19F)
                    if(quizListData!![position].getQuestions()!!.length >= 30){
                        binding.llBibleBack.layoutParams.height = largeHeight
                    }else{
                        binding.llBibleBack.layoutParams.height = smallHeight
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    //set Background Image
                    val bg60Image = getBackImage60Size(context)
                    binding.llBibleBack.background = ContextCompat.getDrawable(context, bg60Image)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                try {
                    binding.tvOptionOne.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                    binding.tvOptionQwo.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                    binding.tvOptionThree.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                    binding.tvOptionOne.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                    binding.tvOptionQwo.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                    binding.tvOptionThree.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                binding.tvOptionOne.setOnClickListener(View.OnClickListener {
                    try {
                        updateAns(quizListData!!.get(position).getId()!!, quizListData!!.get(position).getOption1()!!)
                        binding.tvOptionOne.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_filled)
                        binding.tvOptionQwo.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                        binding.tvOptionThree.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                        binding.tvOptionOne.setTextColor(ContextCompat.getColor(context, R.color.white))
                        binding.tvOptionQwo.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                        binding.tvOptionThree.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                binding.tvOptionQwo.setOnClickListener(View.OnClickListener {
                    try {
                        updateAns(quizListData!!.get(position).getId()!!, quizListData!!.get(position).getOption2()!!)
                        binding.tvOptionOne.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                        binding.tvOptionQwo.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_filled)
                        binding.tvOptionThree.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                        binding.tvOptionOne.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                        binding.tvOptionQwo.setTextColor(ContextCompat.getColor(context, R.color.white))
                        binding.tvOptionThree.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })

                binding.tvOptionThree.setOnClickListener(View.OnClickListener {
                    try {
                        updateAns(quizListData!!.get(position).getId()!!, quizListData!!.get(position).getOption3()!!)
                        binding.tvOptionOne.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                        binding.tvOptionQwo.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_border)
                        binding.tvOptionThree.background = ContextCompat.getDrawable(context, R.drawable.curved_shape_orange_filled)
                        binding.tvOptionOne.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                        binding.tvOptionQwo.setTextColor(ContextCompat.getColor(context, R.color.colorDarkGrayText))
                        binding.tvOptionThree.setTextColor(ContextCompat.getColor(context, R.color.white))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateAns(id: String, answer: String) {
        try {
            for (i in 0 until SignUpThreeFragment.Data.selectedAnswers.answer!!.size) {
                if (SignUpThreeFragment.Data.selectedAnswers.answer!!.get(i).qId!!.equals(id)) {
                    val ans = SignUpBibleQuizPostRequest.Answer()
                    ans.qId = id
                    ans.answerText = answer
                    SignUpThreeFragment.Data.selectedAnswers.answer!!.removeAt(i)
                    SignUpThreeFragment.Data.selectedAnswers.answer!!.add(i, ans)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class BindHolder(var binding: BibleQuizLayoutBinding) :
            RecyclerView.ViewHolder(binding.root) {}
}
