package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class MatchedAnswersData: Serializable {

    @SerializedName("_id")
    @Expose
    var id: String = ""
    @SerializedName("question")
    @Expose
    var question: String = ""
    @SerializedName("answer")
    @Expose
    var answer: String = ""
}
