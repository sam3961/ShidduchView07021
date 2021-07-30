package shiddush.view.com.mmvsd.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
/**
 * Created by Sumit Kumar.
 */

class LoginRequest {

    @SerializedName("email")
    @Expose
    var email: String? = ""
    @SerializedName("password")
    @Expose
    var password: String? = ""
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