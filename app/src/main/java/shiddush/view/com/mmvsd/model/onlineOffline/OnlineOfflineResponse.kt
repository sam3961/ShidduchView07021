package shiddush.view.com.mmvsd.model.onlineOffline

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OnlineOfflineResponse {
    @SerializedName("userid")
    @Expose
    var userid: String? = null
    @SerializedName("firstName")
    @Expose
    var firstName: String? = null
    @SerializedName("type")
    @Expose
    var type: String? = ""

    var gender: String? = ""
    var unreadCount: Int? = 0


}