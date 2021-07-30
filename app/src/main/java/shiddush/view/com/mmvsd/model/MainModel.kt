package shiddush.view.com.mmvsd.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 * @Base Model class :  This class contain the common response of every Api.
 **/


open class MainModel {
    @SerializedName("code")
    @Expose
    private var code: Int? = 0
    @SerializedName("message")
    @Expose
    private var message: String? = ""

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

}