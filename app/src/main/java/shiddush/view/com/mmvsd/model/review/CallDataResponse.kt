package shiddush.view.com.mmvsd.model.review

import com.google.gson.annotations.SerializedName

data class CallData(

	@field:SerializedName("toId")
	val toId: String? = null,

	@field:SerializedName("fromsocketid")
	val fromsocketid: String? = null,

	@field:SerializedName("fromUserSessionId")
	val fromUserSessionId: String? = null,

	@field:SerializedName("toSocketId")
	val toSocketId: String? = null,

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("toUserSessionId")
	val toUserSessionId: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("fromId")
	val fromId: String? = null,

	@field:SerializedName("tokboxApi")
	val tokboxApi: String? = null,

	@field:SerializedName("matchFound")
	val matchFound: Boolean? = null,

	@field:SerializedName("time")
	val time: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
