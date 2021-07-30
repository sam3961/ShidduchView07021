package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class MatchedUserDetailsResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int? = 0
    @SerializedName("message")
    @Expose
    var message: String? = ""
    @SerializedName("data")
    @Expose
    var data: Data? = Data()

    class Data {
        @SerializedName("name")
        @Expose
        var name: String? = ""
        @SerializedName("age")
        @Expose
        var age: String? = ""
        @SerializedName("count")
        @Expose
        var count: Int? = 0
        @SerializedName("compatibilityrating")
        @Expose
        var compatibilityrating: Float = 0F
        @SerializedName("questionAnswer")
        @Expose
        var questionAnswer: List<QuestionAnswer>? = ArrayList<QuestionAnswer>()
    }

    class QuestionAnswer {
        @SerializedName("_id")
        @Expose
        var id: String? = ""
        @SerializedName("modifiedDate")
        @Expose
        var modifiedDate: String? = ""
        @SerializedName("createdDate")
        @Expose
        var createdDate: String? = ""
        @SerializedName("user_id")
        @Expose
        var userId: String? = ""
        @SerializedName("q_id")
        @Expose
        var qId: String? = ""
        @SerializedName("answer_text")
        @Expose
        var answerText: String? = ""
        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = ""
        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = ""
        @SerializedName("__v")
        @Expose
        var v: Int? = 0
        @SerializedName("matchedquestionAnswers")
        @Expose
        var matchedquestionAnswers: List<MatchedQuestionAnswer>? = ArrayList<MatchedQuestionAnswer>()
    }

    class MatchedQuestionAnswer {
        @SerializedName("_id")
        @Expose
        var id: String? = ""
        @SerializedName("modifiedDate")
        @Expose
        var modifiedDate: String? = ""
        @SerializedName("createdDate")
        @Expose
        var createdDate: String? = ""
        @SerializedName("questions")
        @Expose
        var questions: String? = ""
        @SerializedName("option1")
        @Expose
        var option1: String? = ""
        @SerializedName("option2")
        @Expose
        var option2: String? = ""
        @SerializedName("qType")
        @Expose
        var qType: Int? = 0
        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = ""
        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = ""
        @SerializedName("__v")
        @Expose
        var v: Int? = 0
        @SerializedName("isDeleted")
        @Expose
        var isDeleted: Boolean? = false
    }
}