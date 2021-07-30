package shiddush.view.com.mmvsd.model.signup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class SignUpRequest : Serializable {

    @SerializedName("firstName")
    @Expose
    var firstName: String? = null
    @SerializedName("lastName")
    @Expose
    var lastName: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("phone_no")
    @Expose
    var phone_no: String? = null
    @SerializedName("gender")
    @Expose
    var gender: Int? = null
    @SerializedName("DOB")
    @Expose
    var dOB: String? = null
    @SerializedName("loginType")
    @Expose
    var loginType: Int? = null
    @SerializedName("facebookSocialId")
    @Expose
    var facebookSocialId: String? = null
    @SerializedName("googleSocialId")
    @Expose
    var googleSocialId: String? = null
    @SerializedName("device_type")
    @Expose
    var deviceType: String? = null
    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null
    @SerializedName("appversion")
    @Expose
    var appVersion: String? = null
    @SerializedName("deviceInfo")
    @Expose
    var deviceInfo: String? = ""
    @SerializedName("password")
    @Expose
    var password: String? = null
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