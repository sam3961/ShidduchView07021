package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class VideoCallResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int? = 0
    @SerializedName("message")
    @Expose
    var message: String? = ""
    @SerializedName("data")
    @Expose
    var data: Data? = null

    class Data {
        @SerializedName("tokboxApi")
        @Expose
        val tokboxApi: String? = ""
        @SerializedName("fromId")
        @Expose
        val fromId: String? = ""
        @SerializedName("toId")
        @Expose
        val toId: String? = ""
        @SerializedName("fromUserSessionId")
        @Expose
        val fromUserSessionId: String? = ""
        @SerializedName("toUserSessionId")
        @Expose
        val toUserSessionId: String? = ""
        @SerializedName("matchFound")
        @Expose
        val matchFound: Boolean? = false
        @SerializedName("time")
        @Expose
        val time: Long? = 0
        @SerializedName("tokboxToken")
        @Expose
        val tokboxToken: String? = ""
        @SerializedName("count")
        @Expose
        val count: Int? = 0
    }

}