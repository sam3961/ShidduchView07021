package shiddush.view.com.mmvsd.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class CheckReviewResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int? = 0

    @SerializedName("message")
    @Expose
    var message: String? = ""

    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data : Serializable {
        @SerializedName("isReviewDone")
        @Expose
        var isReviewDone: Boolean? = false

        @SerializedName("isInterested")
        @Expose
        var isInterested: Boolean? = false

        @SerializedName("modifiedDate")
        @Expose
        var modifiedDate: String? = ""

        @SerializedName("createdDate")
        @Expose
        var createdDate: String? = ""

        @SerializedName("_id")
        @Expose
        var id: String? = ""

        @SerializedName("review_from")
        @Expose
        var reviewFrom: String? = ""

        @SerializedName("updatedAt")
        @Expose
        var updatedAt: String? = ""

        @SerializedName("__v")
        @Expose
        var v: Int? = 0

        @SerializedName("createdAt")
        @Expose
        var createdAt: String? = ""

        @SerializedName("notes")
        @Expose
        var notes: String? = ""

        @SerializedName("religious")
        @Expose
        var religious: Int? = 0

        @SerializedName("attractive")
        @Expose
        var attractive: Int? = 0

        @SerializedName("pleasant")
        @Expose
        var pleasent: Int? = 0

        @SerializedName("review_for")
        @Expose
        var reviewFor: String? = ""

        @SerializedName("isSubscriptionDone")
        @Expose
        var isSubscriptionDone: Boolean? = false

        @SerializedName("isSubscriptionFree")
        @Expose
        var isSubscriptionFree: Boolean? = false

        //subscription related data
        @SerializedName("packageName")
        @Expose
        var packageName: String? = ""

        @SerializedName("productId")
        @Expose
        var productId: String? = ""

        @SerializedName("purchaseToken")
        @Expose
        var purchaseToken: String? = ""

        @SerializedName("purchasedDate")
        @Expose
        var purchasedDate: Long? = 0

        @SerializedName("autoRenewing")
        @Expose
        var autoRenewing: Boolean? = false

        @SerializedName("amount")
        @Expose
        var amount: String? = ""

        @SerializedName("orderId")
        @Expose
        var orderId: String? = ""

        @SerializedName("isLocationAlarmEnabled")
        @Expose
        var isLocationAlarmEnabled: Boolean = false

        @SerializedName("isGenderAlarmEnabled")
        @Expose
        var isGenderAlarmEnabled: Boolean = false

        @SerializedName("saidYesInReview")
        @Expose
        var saidYesInReview: Boolean = false

        @SerializedName("showSchedulingScreen")
        @Expose
        var showSchedulingScreen: Boolean = false

        @SerializedName("phone_no")
        @Expose
        var phone_no: String = ""

        @SerializedName("callData")
        @Expose
        var callData: CallData? = null

    }


}