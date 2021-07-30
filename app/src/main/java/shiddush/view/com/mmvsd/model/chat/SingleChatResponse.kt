package shiddush.view.com.mmvsd.model.chat

import com.google.gson.annotations.SerializedName

data class SingleChatResponse(

	@field:SerializedName("messages")
	val messages: Messages? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Messages(

	@field:SerializedName("opentok_session")
	var opentokSession: String? = null,

	@field:SerializedName("message_type")
	var messageType: String? = null,

	@field:SerializedName("datetime_tz")
	var datetimeTz: String? = null,

	@field:SerializedName("createdAt")
	var createdAt: String? = "",

	@field:SerializedName("_id")
	var id: String? = null,

	@field:SerializedName("message")
	var message: String? = null,

	@field:SerializedName("userid")
	var userid: String? = null,

	var noInternet: Boolean = false
)
