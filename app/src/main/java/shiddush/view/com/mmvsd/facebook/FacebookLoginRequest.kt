package shiddush.view.com.mmvsd.facebook

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 */
class FacebookLoginRequest {

    @SerializedName("facebook")
    @Expose
    var facebook: String? = ""
    @SerializedName("firstName")
    @Expose
    var firstName: String? = ""
    @SerializedName("lastName")
    @Expose
    var lastName: String? = ""
    @SerializedName("email")
    @Expose
    var email: String? = ""
    @SerializedName("profilePic")
    @Expose
    var profilePic: String? = ""
    @SerializedName("device_type")
    @Expose
    var deviceType: String? = ""
    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = ""
    @SerializedName("device_id")
    @Expose
    var deviceId: String? = ""
    @SerializedName("userName")
    @Expose
    var userName: String? = ""
    @SerializedName("gender")
    @Expose
    var gender: String? = ""
    @SerializedName("DOB")
    @Expose
    var DOB: String? = ""

}