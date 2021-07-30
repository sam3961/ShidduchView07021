package shiddush.view.com.mmvsd.model.signupbiblequiz

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
/**
 * Created by Sumit Kumar.
 */
class SignUpNormalQuizPostRequest {

    @SerializedName("user_id")
    @Expose
    var userId: String? = ""
    @SerializedName("answer")
    @Expose
    var answer: ArrayList<Answer>? = ArrayList<Answer>()

    class Answer{
        @SerializedName("q_id")
        @Expose
        var qId: String? = ""
        @SerializedName("questions")
        @Expose
        var question: String? = ""
        @SerializedName("answer_text")
        @Expose
        var answerText: String? = ""
    }
}
