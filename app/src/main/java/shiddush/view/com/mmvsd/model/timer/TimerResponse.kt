package shiddush.view.com.mmvsd.model.timer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 */
class TimerResponse {

    @SerializedName("status")
    @Expose
    var status: String? = ""
    @SerializedName("message")
    @Expose
    var message: String? = ""
    @SerializedName("zones")
    @Expose
    var zones: DataZones? = null

    class DataZones {
        @SerializedName("countryCode")
        @Expose
        var countryCode: String? = ""
        @SerializedName("countryName")
        @Expose
        var countryName: String? = ""
        @SerializedName("zoneName")
        @Expose
        var zoneName: String? = ""
        @SerializedName("gmtOffset")
        @Expose
        var gmtOffset: String? = ""
        @SerializedName("timestamp")
        @Expose
        var timestamp: String? = ""
    }


}
