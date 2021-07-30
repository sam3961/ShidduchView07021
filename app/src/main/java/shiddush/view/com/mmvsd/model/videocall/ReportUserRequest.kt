package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class ReportUserRequest : Serializable {

    @SerializedName("reported_by")
    @Expose
    var reportedBy: String? = ""
    @SerializedName("reported_for")
    @Expose
    var reportedFor: String? = ""
    @SerializedName("note")
    @Expose
    var note: String? = ""
    @SerializedName("openTok_sessionId")
    @Expose
    var openTokSessionId: String? = ""
}