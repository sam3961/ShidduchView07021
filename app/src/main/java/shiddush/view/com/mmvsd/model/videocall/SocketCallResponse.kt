package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class SocketCallResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int? = 0

    @SerializedName("count")
    @Expose
    var count: Int? = 0

    @SerializedName("fromSocketID")
    @Expose
    var fromSocketID: String? = ""

    @SerializedName("toSocketID")
    @Expose
    var toSocketID: String? = ""

    @SerializedName("matchFound")
    @Expose
    var matchFound: Boolean? = false

    @SerializedName("tokboxApi")
    @Expose
    var tokboxApi: String? = ""

    @SerializedName("fromId")
    @Expose
    var fromId: String? = ""

    @SerializedName("toId")
    @Expose
    var toId: String? = ""

    @SerializedName("fromUserSessionId")
    @Expose
    var fromUserSessionId: String? = ""

    @SerializedName("toUserSessionId")
    @Expose
    var toUserSessionId: String? = ""

    @SerializedName("tokboxToken")
    @Expose
    var tokboxToken: String? = ""

    @SerializedName("message")
    @Expose
    var message: String? = ""

    @SerializedName("time")
    @Expose
    var time: Long? = 0L

    @SerializedName("status")
    @Expose
    var status: String? = ""

    @SerializedName("rejoin")
    @Expose
    var rejoin: Boolean? = false

}