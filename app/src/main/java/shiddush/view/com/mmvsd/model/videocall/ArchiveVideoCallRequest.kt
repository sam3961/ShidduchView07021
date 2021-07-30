package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class ArchiveVideoCallRequest : Serializable {

    @SerializedName("toId")
    @Expose
    var toId: String? = ""
    @SerializedName("fromId")
    @Expose
    var fromId: String? = ""
    @SerializedName("sessionId")
    @Expose
    var sessionId: String? = ""
    @SerializedName("count")
    @Expose
    var count: Int? = 0
}