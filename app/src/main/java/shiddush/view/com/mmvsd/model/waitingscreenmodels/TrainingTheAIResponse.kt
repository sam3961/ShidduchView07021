package shiddush.view.com.mmvsd.model.waitingscreenmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
/**
 * Created by Sumit Kumar.
 */
class TrainingTheAIResponse {

    @SerializedName("code")
    @Expose
    private var code: Int? = 0
    @SerializedName("message")
    @Expose
    private var message: String? = ""
    @SerializedName("data")
    @Expose
    private var data: ArrayList<TrainingTheAIData>? = null

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

    fun getData(): ArrayList<TrainingTheAIData>? {
        return data
    }

    fun setData(data: ArrayList<TrainingTheAIData>) {
        this.data = data
    }


}
