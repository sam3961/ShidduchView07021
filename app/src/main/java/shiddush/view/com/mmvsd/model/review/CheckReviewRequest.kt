package shiddush.view.com.mmvsd.model.review

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class CheckReviewRequest : Serializable {

    @SerializedName("userid")
    @Expose
    var userId: String? = ""
    @SerializedName("appversion")
    @Expose
    var appVersion: String? = ""
    @SerializedName("deviceInfo")
    @Expose
    var deviceInfo: String? = ""
    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = ""
    @SerializedName("lat")
    @Expose
    var lat: String? = ""
    @SerializedName("lng")
    @Expose
    var lng: String? = ""
    @SerializedName("country")
    @Expose
    var country: String? = ""
    @SerializedName("city")
    @Expose
    var city: String? = ""
    @SerializedName("countryCode")
    @Expose
    var countryCode: String? = ""
}