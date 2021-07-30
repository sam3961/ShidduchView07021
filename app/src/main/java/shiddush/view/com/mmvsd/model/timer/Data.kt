package shiddush.view.com.mmvsd.model.timer

import com.google.gson.annotations.SerializedName

data class Data(

    @field:SerializedName("timeForCountDown")
    val timeForCountDown: Int? = null,

    @field:SerializedName("dayOfWeek")
    val dayOfWeek: String? = "",

    @field:SerializedName("timeToText")
    val timeToText: String? = "",

    @field:SerializedName("youtube_class_list")
    val youtubeClassList: String? = "",

    @field:SerializedName("introduce_yourself")
    val introduceYourself: String? = "",

    @field:SerializedName("rejoin")
    val rejoin: String? = "",

    @field:SerializedName("best_match")
    val bestMatch: String? = "",

    @field:SerializedName("dating_event")
    val datingEvent: String? = "",

    @field:SerializedName("hadCall")
    val hadCall: Boolean? = null
)