package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class EndCallRequest : Serializable {

    @SerializedName("endcall_by")
    @Expose
    var endCallBy: String? = ""
    @SerializedName("endcall_for")
    @Expose
    var endCallFor: String? = ""
    @SerializedName("note")
    @Expose
    var note: String? = ""
    @SerializedName("openTok_sessionId")
    @Expose
    var openTokSessionId: String? = ""
}