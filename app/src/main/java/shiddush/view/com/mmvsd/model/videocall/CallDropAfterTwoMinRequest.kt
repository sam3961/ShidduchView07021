package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class CallDropAfterTwoMinRequest : Serializable {

    @SerializedName("toId")
    @Expose
    var toId: String? = ""
    @SerializedName("fromUserSessionId")
    @Expose
    var fromUserSessionId: String? = ""
    @SerializedName("fromId")
    @Expose
    var fromId: String? = ""
    @SerializedName("toUserSessionId")
    @Expose
    var toUserSessionId: String? = ""
    @SerializedName("tokboxApi")
    @Expose
    var tokboxApi: String? = ""
}