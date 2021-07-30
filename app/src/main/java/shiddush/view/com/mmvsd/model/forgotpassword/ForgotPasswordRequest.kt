package shiddush.view.com.mmvsd.model.forgotpassword

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
/**
 * Created by Sumit Kumar.
 */

class ForgotPasswordRequest {

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

}