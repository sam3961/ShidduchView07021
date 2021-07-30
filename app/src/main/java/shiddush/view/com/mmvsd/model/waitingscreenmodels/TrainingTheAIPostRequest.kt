package shiddush.view.com.mmvsd.model.waitingscreenmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class TrainingTheAIPostRequest : Serializable {


    @SerializedName("question_id")
    @Expose
    var question_id: String? = ""
    @SerializedName("user_id")
    @Expose
    var user_id: String? = ""
    @SerializedName("answer_text")
    @Expose
    var answer_text: String? = ""


}
