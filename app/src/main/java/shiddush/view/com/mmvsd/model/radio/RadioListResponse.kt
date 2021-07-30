package shiddush.view.com.mmvsd.model.radio

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Sumit Kumar.
 */
class RadioListResponse : Serializable {

    @SerializedName("code")
    @Expose
    var code: Int? = 0
    @SerializedName("message")
    @Expose
    var message: String? = ""
    @SerializedName("data")
    @Expose
    var data: ArrayList<MusicUrl>? = ArrayList<MusicUrl>()

    class MusicUrl {
        @SerializedName("_id")
        @Expose
        var _id: String? = ""
        @SerializedName("music_url")
        @Expose
        var musicUrl: String? = ""
    }

}