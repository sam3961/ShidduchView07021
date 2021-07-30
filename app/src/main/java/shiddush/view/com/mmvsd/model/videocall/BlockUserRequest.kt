package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class BlockUserRequest : Serializable {

    @SerializedName("blocked_by")
    @Expose
    var blockedBy: String? = ""
    @SerializedName("blocked_to")
    @Expose
    var blockedTo: String? = ""
    @SerializedName("openTok_sessionId")
    @Expose
    var openTokSessionId: String? = ""
}