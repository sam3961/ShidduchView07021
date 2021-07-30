package shiddush.view.com.mmvsd.model.subscription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class SubscriptionRequest : Serializable {

    @SerializedName("userid")
    @Expose
    var userId: String = ""

    @SerializedName("productId")  //productId means "sku / subscription_id"
    @Expose
    var productId: String = ""

    @SerializedName("packageName")
    @Expose
    var packageName: String = ""

    @SerializedName("autoRenewing")
    @Expose
    var autoRenewing: Boolean = false

    @SerializedName("amount")
    @Expose
    var amount: String = ""

    @SerializedName("orderId")
    @Expose
    var orderId: String = ""

    @SerializedName("purchaseToken")
    @Expose
    var purchaseToken: String = ""

    @SerializedName("purchasedDate")
    @Expose
    var purchasedDate: Long = 0

    @SerializedName("device_type")
    @Expose
    var deviceType: String = ""
}