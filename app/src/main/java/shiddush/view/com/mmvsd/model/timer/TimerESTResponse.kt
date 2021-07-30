package shiddush.view.com.mmvsd.model.timer

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 */
class TimerESTResponse {

    @SerializedName("currentDateTime")
    @Expose
    var currentDateTime: String? = ""
    @SerializedName("utcOffset")
    @Expose
    var utcOffset: String? = ""
    @SerializedName("isDayLightSavingsTime")
    @Expose
    var isDayLightSavingsTime: String? = ""



}
