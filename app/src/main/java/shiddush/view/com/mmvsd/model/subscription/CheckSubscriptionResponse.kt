package shiddush.view.com.mmvsd.model.subscription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class CheckSubscriptionResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int = 0
    @SerializedName("message")
    @Expose
    var message: String = ""
    @SerializedName("data")
    @Expose
    var data: Data = Data()

    class Data : Serializable {
        @SerializedName("isSuccessful")
        @Expose
        var isSuccessful: Boolean = false

        @SerializedName("errorMessage")
        @Expose
        var errorMessage: String = ""

        @SerializedName("payload")
        @Expose
        var payload: Payload = Payload()
    }

    class Payload : Serializable {
        @SerializedName("kind")
        @Expose
        var kind: String = ""

        @SerializedName("startTimeMillis")
        @Expose
        var startTimeMillis: Long = 0

        @SerializedName("expiryTimeMillis")
        @Expose
        var expiryTimeMillis: Long = 0

        @SerializedName("autoRenewing")
        @Expose
        var autoRenewing: Boolean = false

        @SerializedName("priceCurrencyCode")
        @Expose
        var priceCurrencyCode: String = ""

        @SerializedName("priceAmountMicros")
        @Expose
        var priceAmountMicros: Long = 0

        @SerializedName("countryCode")
        @Expose
        var countryCode: String = ""

        @SerializedName("developerPayload")
        @Expose
        var developerPayload: String = ""

        @SerializedName("paymentState")
        @Expose
        var paymentState: Int = -1

        @SerializedName("cancelReason")
        @Expose
        var cancelReason: Int = -1

        @SerializedName("userCancellationTimeMillis")
        @Expose
        var userCancellationTimeMillis: Long = 0

        @SerializedName("cancelSurveyResult")
        @Expose
        var cancelSurveyResult: CancelSurveyResult = CancelSurveyResult()

        @SerializedName("orderId")
        @Expose
        var orderId: String = ""

        @SerializedName("priceChange")
        @Expose
        var priceChange: PriceChange = PriceChange()

        @SerializedName("acknowledgementState")
        @Expose
        var acknowledgementState: Int = -1
    }

    class CancelSurveyResult : Serializable {
        @SerializedName("cancelSurveyReason")
        @Expose
        var cancelSurveyReason: Int = -1
    }

    class PriceChange : Serializable {
        @SerializedName("newPrice")
        @Expose
        var newPrice: NewPrice = NewPrice()

        @SerializedName("state")
        @Expose
        var state: Int = -1
    }

    class NewPrice : Serializable {
        @SerializedName("priceMicros")
        @Expose
        var priceMicros: Long = 0

        @SerializedName("currency")
        @Expose
        var currency: String = ""
    }
}

/*
"paymentState": integer, (0,1,2,3)
"cancelReason": integer, (0,1,2,3)

1: paymentState-> The payment state of the subscription. Possible values are:
0 Payment pending
1 Payment received
2 Free trial
3 Pending deferred upgrade/downgrade


2: cancelReason->
The reason why a subscription was canceled or is not auto-renewing. Possible values are:
0 User canceled the subscription
1 Subscription was canceled by the system, for example because of a billing problem
2 Subscription was replaced with a new subscription
3 Subscription was canceled by the developer
*/