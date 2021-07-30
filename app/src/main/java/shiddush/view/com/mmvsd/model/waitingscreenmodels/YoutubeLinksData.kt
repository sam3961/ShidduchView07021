package shiddush.view.com.mmvsd.model.waitingscreenmodels

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Sumit Kumar.
 */
class YoutubeLinksData {

    @SerializedName("_id")
    @Expose
    private var id: String? = ""
    @SerializedName("youtube_url")
    @Expose
    private var youtubeUrl: String? = ""
    @SerializedName("title")
    @Expose
    private var title: String? = ""
    @SerializedName("subtitle")
    @Expose
    private var subtitle: String? = ""
    @SerializedName("videoId")
    @Expose
    private var videoId: String? = ""
    @SerializedName("thumbnail_url")
    @Expose
    private var thumbnailUrl: String? = ""

    fun getId(): String? {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getYoutubeUrl(): String? {
        return youtubeUrl
    }

    fun setYoutubeUrl(youtubeUrl: String) {
        this.youtubeUrl = youtubeUrl
    }

    fun getTitle(): String? {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getSubtitle(): String? {
        return subtitle
    }

    fun setSubtitle(subtitle: String) {
        this.subtitle = subtitle
    }

    fun getVideoId(): String? {
        return videoId
    }

    fun setVideoId(videoId: String) {
        this.videoId = videoId
    }

    fun getThumbnailUrl(): String? {
        return thumbnailUrl
    }

    fun setThumbnailUrl(thumbnailUrl: String) {
        this.thumbnailUrl = thumbnailUrl
    }


}
