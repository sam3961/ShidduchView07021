package shiddush.view.com.mmvsd.model.signupbiblequiz

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class SignUpBibleQuizPostRequest : Serializable {

    @SerializedName("user_id")
    @Expose
    var userId: String? = ""
    @SerializedName("terms_and_conditions")
    @Expose
    var termsAndConditions: Boolean? = false
    @SerializedName("answer")
    @Expose
    var answer: ArrayList<Answer>? = ArrayList<Answer>()

    class Answer{
        @SerializedName("q_id")
        @Expose
        var qId: String? = ""
        @SerializedName("answer_text")
        @Expose
        var answerText: String? = ""
    }

}