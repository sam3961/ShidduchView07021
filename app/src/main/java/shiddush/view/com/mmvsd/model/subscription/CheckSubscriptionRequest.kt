package shiddush.view.com.mmvsd.model.subscription

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class CheckSubscriptionRequest : Serializable {

    @SerializedName("userid")
    @Expose
    var userId: String = ""

    @SerializedName("packageName")
    @Expose
    var packageName: String = ""

    @SerializedName("productId")
    @Expose
    var productId: String = ""

    @SerializedName("device_type")
    @Expose
    var deviceType: String = ""
}