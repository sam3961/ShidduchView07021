package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class ReGenerateTokenResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int? = 0
    @SerializedName("message")
    @Expose
    var message: String? = ""
    @SerializedName("data")
    @Expose
    var data: SocketCallResponse? = SocketCallResponse()
}