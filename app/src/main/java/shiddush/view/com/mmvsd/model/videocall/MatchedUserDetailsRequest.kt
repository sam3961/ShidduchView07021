package shiddush.view.com.mmvsd.model.videocall

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class MatchedUserDetailsRequest : Serializable {

    @SerializedName("fromId")
    @Expose
    var fromId: String? = ""
    @SerializedName("toId")
    @Expose
    var toId: String? = ""
}