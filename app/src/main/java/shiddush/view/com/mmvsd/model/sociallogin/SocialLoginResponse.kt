package shiddush.view.com.mmvsd.model.sociallogin

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import shiddush.view.com.mmvsd.model.login.LoginData

/**
 * Created by Sumit Kumar.
 * @Base LoginResponse class :  This class contain the all the fields having the  response of login api..
 */
class SocialLoginResponse {

    @SerializedName("code")
    @Expose
    private var code: Int? = null
    @SerializedName("message")
    @Expose
    private var message: String? = null
    @SerializedName("data")
    @Expose
    private var data: SocialLoginData? = null

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

    fun getData(): SocialLoginData? {
        return data
    }

    fun setData(data: SocialLoginData) {
        this.data = data
    }
}
