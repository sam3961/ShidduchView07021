package shiddush.view.com.mmvsd.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import shiddush.view.com.mmvsd.model.review.AddReviewResponse.Data2
import shiddush.view.com.mmvsd.model.review.AddReviewResponse.Data1


/**
 * Created by Sumit Kumar.
 */
class AddReviewResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int? = 0
    @SerializedName("message")
    @Expose
    var message: String? = ""
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("data1")
        @Expose
        var data1: Data1? = null
        @SerializedName("data2")
        @Expose
        var data2: Data2? = null
    }

    class Data1 {
        @SerializedName("_id")
        @Expose
        var id: String? = null
        @SerializedName("review_from")
        @Expose
        var reviewFrom: String? = null
        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = null
        @SerializedName("__v")
        @Expose
        var v: Int? = null
        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = null
        @SerializedName("isReviewDone")
        @Expose
        var isReviewDone: Boolean? = null
        @SerializedName("notes")
        @Expose
        var notes: String? = null
        @SerializedName("religious")
        @Expose
        var religious: String? = null
        @SerializedName("attractive")
        @Expose
        var attractive: String? = null
        @SerializedName("pleasant")
        @Expose
        var pleasent: Int? = null
        @SerializedName("isInterested")
        @Expose
        var isInterested: Boolean? = null
        @SerializedName("review_for")
        @Expose
        var reviewFor: String? = null
        @SerializedName("queryStatus")
        @Expose
        var queryStatus: Boolean? = null
    }

    class Data2 {
        @SerializedName("_id")
        @Expose
        var id: String? = null
        @SerializedName("isvideoCallDone")
        @Expose
        var isvideoCallDone: Boolean? = null
        @SerializedName("queryStatus")
        @Expose
        var queryStatus: Boolean? = null
    }

}