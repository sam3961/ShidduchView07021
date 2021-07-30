package shiddush.view.com.mmvsd.model.sociallogin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class SocialLoginRequest : Serializable {

    @SerializedName("firstName")
    @Expose
    var firstName: String? = ""
    @SerializedName("lastName")
    @Expose
    var lastName: String? = ""
    @SerializedName("email")
    @Expose
    var email: String? = ""
    @SerializedName("gender")
    @Expose
    var gender: Int? = 0
    @SerializedName("DOB")
    @Expose
    var dOB: String? = ""
    @SerializedName("loginType")
    @Expose
    var loginType: Int? = 0
    @SerializedName("facebookSocialId")
    @Expose
    var facebookSocialId: String? = ""
    @SerializedName("googleSocialId")
    @Expose
    var googleSocialId: String? = ""
    @SerializedName("device_type")
    @Expose
    var deviceType: String? = ""
    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = ""
    @SerializedName("appversion")
    @Expose
    var appVersion: String? = ""
    @SerializedName("deviceInfo")
    @Expose
    var deviceInfo: String? = ""
    @SerializedName("isTandC")
    @Expose
    var isTandC: Boolean? = false
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