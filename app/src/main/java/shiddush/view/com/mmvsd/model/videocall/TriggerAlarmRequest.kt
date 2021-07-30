package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class TriggerAlarmRequest : Serializable {

    @SerializedName("from_user")
    @Expose
    var fromUser: String? = ""
    @SerializedName("for_user")
    @Expose
    var forUser: String? = ""
    @SerializedName("sessionId")
    @Expose
    var sessionId: String? = ""
}