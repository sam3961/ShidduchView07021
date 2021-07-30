package shiddush.view.com.mmvsd.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class AddReviewRequest : Serializable {

    @SerializedName("review_from")
    @Expose
    var reviewFrom: String? = ""
    @SerializedName("review_for")
    @Expose
    var reviewFor: String? = ""
    @SerializedName("isInterested")
    @Expose
    var isInterested: Boolean? = false
    @SerializedName("pleasant")
    @Expose
    var pleasant: Int? = 0
    @SerializedName("attractive")
    @Expose
    var attractive: Int? = 0
    @SerializedName("religious")
    @Expose
    var religious: Int? = 0
    @SerializedName("notes")
    @Expose
    var notes: String? = ""
}