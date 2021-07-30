package shiddush.view.com.mmvsd.model.login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 * @Base LoginResponse class :  This class contain the all the fields having the  response of login api..
 */
class LoginResponse {

    @SerializedName("code")
    @Expose
    private var code: Int? = 0
    @SerializedName("message")
    @Expose
    private var message: String? = ""
    @SerializedName("data")
    @Expose
    private var data: LoginData? = null

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

    fun getData(): LoginData? {
        return data
    }

    fun setData(data: LoginData) {
        this.data = data
    }
}
