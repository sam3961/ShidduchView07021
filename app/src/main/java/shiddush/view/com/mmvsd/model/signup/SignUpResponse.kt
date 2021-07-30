package shiddush.view.com.mmvsd.model.signup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import shiddush.view.com.mmvsd.model.login.LoginData
import shiddush.view.com.mmvsd.model.sociallogin.SocialLoginData

/**
 * Created by Sumit Kumar.
 * @Base LoginResponse class :  This class contain the all the fields having the  response of login api..
 */
class SignUpResponse {

    @SerializedName("code")
    @Expose
    private var code: Int? = 0
    @SerializedName("message")
    @Expose
    private var message: String? = ""
    @SerializedName("data")
    @Expose
    private var data: SignUpData? = null

    fun getCode(): Int? {
        return code
    }

    fun setCode(code: Int?) {
        this.code = code
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getData(): SignUpData? {
        return data
    }

    fun setData(data: SignUpData) {
        this.data = data
    }
}
