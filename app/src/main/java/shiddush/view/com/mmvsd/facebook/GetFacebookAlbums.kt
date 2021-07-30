package shiddush.view.com.mmvsd.facebook

import com.google.gson.annotations.SerializedName
/**
 * Created by Sumit Kumar.
 */
class GetFacebookAlbums {

    @SerializedName("data")
    var facebookAlbums: List<shiddush.view.com.mmvsd.facebook.GetFacebookAlbums.FacebookAlbums>? = null

    class FacebookAlbums {
        var name: String? = ""
        var id: String? = ""
    }
}