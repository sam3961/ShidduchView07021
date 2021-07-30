package shiddush.view.com.mmvsd.model.chat

import com.google.gson.annotations.SerializedName

data class ChatListResponse(

	@field:SerializedName("response")
	val response: Response? = null
)

data class Response(

	@field:SerializedName("opentok_session")
	val opentokSession: String? = null,

	@field:SerializedName("messages")
	val messages: MutableList<Messages>? = null
)

data class MessagesItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("__v")
	val V: Int? = null,

	@field:SerializedName("opentok_session")
	val opentokSession: String? = null,

	@field:SerializedName("message_type")
	val messageType: String? = null,

	@field:SerializedName("datetime_tz")
	val datetimeTz: String? = null,

	@field:SerializedName("_id")
	val id: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("userid")
	val userid: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
